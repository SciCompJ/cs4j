/**
 * 
 */
package net.sci.register.image;

import java.util.Collection;

import net.sci.array.interp.ScalarFunction2D;
import net.sci.geom.geom2d.Point2d;
import net.sci.optim.ScalarFunction;
import net.sci.register.transform.ParametricTransform2d;

/**
 * @author dlegland
 *
 */
public class SimpleImageRegistrationEvaluator implements ScalarFunction
{
	ScalarFunction2D refImage;

	TransformedImage2d transformedImage;
	
	ImageToImageMetric2d metric;
	
	Collection<Point2d> points;
	
	public SimpleImageRegistrationEvaluator(ScalarFunction2D refImage,
			TransformedImage2d transformedImage, ImageToImageMetric2d metric,
			Collection<Point2d> points)
	{
		this.refImage = refImage;
		this.transformedImage = transformedImage;
		this.metric = metric;
		this.points = points;
	}
	
	/* (non-Javadoc)
	 * @see net.sci.optim.ScalarFunction#evaluate(double[])
	 */
	@Override
	public double evaluate(double[] theta)
	{
		// TODO: check class conversion
		ParametricTransform2d transform = (ParametricTransform2d) this.transformedImage.getTransform();
		transform.setParameters(theta);
		
		return metric.evaluate(refImage, transformedImage, points);
	}

}
