/**
 * 
 */
package net.sci.register.image;

import java.util.Collection;

import net.sci.array.interp.ScalarFunction2D;
import net.sci.geom.geom2d.Point2D;

/**
 * Base class for measuring how much two images can differ. 
 * 
 * @author dlegland
 *
 */
public abstract class ImageToImageMetric2D
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
     * Evaluates the difference between two images, using the specified
     * positions.
     * 
     * @param img1
     *            the first function to evaluate
     * @param img2
     *            the second function to evaluate
     * @param points
     *            the collection of points for sampling the function
     * @return the result of the metric evaluated at the sampling points
     */
	public abstract double evaluate(ScalarFunction2D img1, ScalarFunction2D img2, Collection<Point2D> points);
}
