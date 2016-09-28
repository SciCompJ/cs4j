/**
 * 
 */
package net.sci.register.image;

import java.util.Collection;

import net.sci.geom.geom2d.Point2d;
import net.sci.interp.ScalarFunction2D;

/**
 * @author dlegland
 *
 */
public class SumOfSquaredDifferencesMetric2d extends ImageToImageMetric2d
{

	/* (non-Javadoc)
	 * @see net.sci.register.image.ImageToImageMetric2d#evaluate()
	 */
	@Override
	public double evaluate(ScalarFunction2D img1, ScalarFunction2D img2,
			Collection<Point2d> points)
	{
		double accum = 0;
		
		for (Point2d p : points)
		{
			double v1 = img1.evaluate(p.getX(), p.getY());
			double v2 = img2.evaluate(p.getX(), p.getY());
			double d = v1 - v2;
			accum += d*d;
		}
		
		return accum;
	}

}
