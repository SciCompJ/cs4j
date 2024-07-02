/**
 * 
 */
package net.sci.register.transform;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import net.sci.array.interp.LinearInterpolator2D;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.image.io.TiffImageReader;
import net.sci.optim.NelderMeadSimplexOptimizer;
import net.sci.optim.Optimizer;
import net.sci.optim.ScalarFunction;
import net.sci.register.image.MeanSquaredDifferencesMetric2D;
import net.sci.register.image.SimpleImageRegistrationEvaluator;
import net.sci.register.image.TransformedImage2D;

/**
 * @author dlegland
 *
 */
public class TranslationModel2DDemo
{
    public static final void main(String... args) throws IOException
	{
		// Read input images
		String fileName = TranslationModel2DDemo.class.getResource("/images/rat_BF_LipNor552/rat57_LipNor552_080.tif").getFile();
		File file1 = new File(fileName);
		System.out.println("file exists: " + file1.exists());
		TiffImageReader reader = new TiffImageReader(file1);
		ScalarArray2D<?> image1 = (ScalarArray2D<?>) reader.readImage().getData();

		String fileName2 = TranslationModel2DDemo.class.getResource("/images/rat_BF_LipNor552/rat58_LipNor552_080.tif").getFile();
		File file2 = new File(fileName2);
		System.out.println("file exists: " + file2.exists());

		reader = new TiffImageReader(file2);
		ScalarArray2D<?> image2 = (ScalarArray2D<?>) reader.readImage().getData();

		// Create interpolators
		LinearInterpolator2D interp1 = new LinearInterpolator2D(image1);
		LinearInterpolator2D interp2 = new LinearInterpolator2D(image2);

		
		// Create parametric transform model		
		double[] params = new double[2];
		ParametricTransform2D transfo = new TranslationModel2D(params);
		TransformedImage2D tim = new TransformedImage2D(interp2, transfo);
		
		
		// Create the metric 
		Collection<Point2D> grid = new ArrayList<Point2D>(100 * 80);
		for (int y = 0; y < 80; y++)
		{
			for (int x = 0; x < 100; x++)
			{
				grid.add(new Point2D(x, y));
			}
		}
		MeanSquaredDifferencesMetric2D metric = new MeanSquaredDifferencesMetric2D();
		
        // Create registration evaluator
		SimpleImageRegistrationEvaluator evaluator = new SimpleImageRegistrationEvaluator(
				interp1, tim, metric, grid);
		
		// evaluate for initial parameters
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
