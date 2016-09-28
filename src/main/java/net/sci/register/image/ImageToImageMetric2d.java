/**
 * 
 */
package net.sci.register.image;

import java.util.Collection;

import net.sci.geom.geom2d.Point2d;
import net.sci.interp.ScalarFunction2D;

/**
 * Base class for measuring how much two images can differ. 
 * 
 * @author dlegland
 *
 */
public abstract class ImageToImageMetric2d
{
	
//	BivariateFunction img1;
//	BivariateFunction img2;
//	
//	Collection<Point2d> points;
//	
//	/**
//	 * Evaluates the difference between two images, and returns a scalar state.
//	 */
//	public abstract double evaluate();
	
	/**
	 * Evaluates the difference between two images, using the specified positions.
	 */
	public abstract double evaluate(ScalarFunction2D img1, ScalarFunction2D img2, Collection<Point2d> points);
}
