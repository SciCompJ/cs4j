/**
 * 
 */
package net.sci.register.image;

import java.util.Collection;

import net.sci.array.interp.ScalarFunction2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.optim.ScalarFunction;
import net.sci.register.transform.ParametricTransform2D;

/**
 * @author dlegland
 *
 */
public class SimpleImageRegistrationEvaluator implements ScalarFunction
{
	ScalarFunction2D refImage;

	TransformedImage2D transformedImage;
	
	ImageToImageMetric2D metric;
	
	Collection<Point2D> points;
	
	public SimpleImageRegistrationEvaluator(ScalarFunction2D refImage,
			TransformedImage2D transformedImage, ImageToImageMetric2D metric,
			Collection<Point2D> points)
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
		ParametricTransform2D transform = (ParametricTransform2D) this.transformedImage.getTransform();
		transform.setParameters(theta);
		
		return metric.evaluate(refImage, transformedImage, points);
	}

}
