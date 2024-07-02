/**
 * 
 */
package net.sci.register.image;

import java.util.Collection;

import net.sci.array.numeric.interp.ScalarFunction2D;
import net.sci.geom.geom2d.Point2D;

/**
 * For each point, compute state in both images, compute the squared difference,
 * sum over all points, and divides by number of points.
 * 
 * @author dlegland
 *
 */
public class MeanSquaredDifferencesMetric2D extends ImageToImageMetric2D
{
	public MeanSquaredDifferencesMetric2D()
	{
		
	}
//	public MeanSquaredDifferencesMetric2d(BivariateFunction img1, BivariateFunction img2, Collection<Point2d> points)
//	{
//		this.img1 = img1;
//		this.img2 = img2;
//		this.points = points;
//	}
	
	/* (non-Javadoc)
	 * @see net.sci.register.image.ImageToImageMetric2d#evaluate()
	 */
	@Override
	public double evaluate(ScalarFunction2D img1, ScalarFunction2D img2,
			Collection<Point2D> points)
	{
		double accum = 0;
		
		for (Point2D p : points)
		{
			double v1 = img1.evaluate(p.x(), p.y());
			if (Double.isNaN(v1))
				v1 = 0;
			double v2 = img2.evaluate(p.x(), p.y());
			if (Double.isNaN(v2))
				v1 = 0;
			
			double d = v1 - v2;
			accum += d * d;
		}
		
		return accum / points.size();
	}

}
