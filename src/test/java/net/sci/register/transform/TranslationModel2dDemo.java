/**
 * 
 */
package net.sci.register.transform;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import net.sci.array.data.scalar2d.ScalarArray2D;
import net.sci.array.interp.LinearInterpolator2D;
import net.sci.geom.geom2d.Point2d;
import net.sci.image.io.TiffImageReader;
import net.sci.optim.NelderMeadSimplexOptimizer;
import net.sci.optim.Optimizer;
import net.sci.optim.ScalarFunction;
import net.sci.register.image.MeanSquaredDifferencesMetric2d;
import net.sci.register.image.SimpleImageRegistrationEvaluator;
import net.sci.register.image.TransformedImage2d;

/**
 * @author dlegland
 *
 */
public class TranslationModel2dDemo
{

	public static final void main(String[] args) throws IOException
	{
		//		File file0 = new File(".");
		//		System.out.println(file0.getAbsolutePath());

		// Read input images
		
		File file1 = new File("files/rat_BF_LipNor552/rat57_LipNor552_080.tif");
		System.out.println("file exists: " + file1.exists());

		TiffImageReader reader = new TiffImageReader(file1);
		ScalarArray2D<?> image1 = (ScalarArray2D<?>) reader.readImage().getData();
//		Array2d array1 = (Array2d) reader.readImage().getImageArray();

		File file2 = new File("files/rat_BF_LipNor552/rat58_LipNor552_080.tif");
		System.out.println("file exists: " + file2.exists());

		reader = new TiffImageReader(file2);
		ScalarArray2D<?> image2 = (ScalarArray2D<?>) reader.readImage().getData();
//		Array2d array2 = (Array2d) reader.readImage().getImageArray();

		// Create interpolators
		
		LinearInterpolator2D interp1 = new LinearInterpolator2D(image1);
		
		LinearInterpolator2D interp2 = new LinearInterpolator2D(image2);

		
		// Create transform model		

		double[] params = new double[2];
		
		ParametricTransform2d transfo = new TranslationModel2d(params);
		TransformedImage2d tim = new TransformedImage2d(interp2, transfo);
		
		
		// Create the metric 
		
		Collection<Point2d> grid = new ArrayList<Point2d>(100 * 80);
		for (int y = 0; y < 80; y++)
		{
			for (int x = 0; x < 100; x++)
			{
				grid.add(new Point2d(x, y));
			}
		}
		
		// Create registration evaluator
		MeanSquaredDifferencesMetric2d metric = new MeanSquaredDifferencesMetric2d();
		
		SimpleImageRegistrationEvaluator evaluator = new SimpleImageRegistrationEvaluator(
				interp1, tim, metric, grid);
		
		double refValue = evaluator.evaluate(params);
		
		System.out.println("ref Value: " + refValue);
		
		for (int ty = -15; ty < 10; ty++)
		{
			params[1] = ty;
//			transfo.setParameters(params);
//			double state = metric.evaluate(interp1, tim, grid);
			double value = evaluator.evaluate(params);
			
			System.out.println("ty = " + ty + "; metric = " + value);
		}
		
		
		// Create optimizer
		
		double[] params0 = new double[]{0, 0};
		double[] deltas = new double[]{1, 1};
		
		Optimizer optimizer = new NelderMeadSimplexOptimizer(evaluator, params0, deltas);
		
		System.out.println("Start optimization...");
		ScalarFunction.EvaluationResult eval = optimizer.startOptimization();
		double[] pos = eval.getPosition();
		System.out.println("Optimised position: " + pos[0] + ", " + pos[1]);
		System.out.println("Optimised state: " + eval.getValue());
	}
}
