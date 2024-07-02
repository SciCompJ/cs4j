/**
 * 
 */
package net.sci.register.image;

import java.util.Collection;

import net.sci.array.numeric.interp.ScalarFunction2D;
import net.sci.geom.geom2d.Point2D;

/**
 * @author dlegland
 *
 */
public class SumOfSquaredDifferencesMetric2D extends ImageToImageMetric2D
{

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
			double v2 = img2.evaluate(p.x(), p.y());
			double d = v1 - v2;
			accum += d*d;
		}
		
		return accum;
	}

}
