/**
 * 
 */
package net.sci.interp;

import net.sci.geom.geom2d.Point2d;

/**
 * A scalar function of two variables, used to represent interpolated images.
 * 
 * @author dlegland
 *
 */
public interface ScalarFunction2D
{
	/**
	 * Evaluates a position given as a couple of coordinates.
	 */
	public double evaluate(double x, double y);
	
	/**
	 * Evaluates a position given as a point.
	 */
	public default double evaluate(Point2d p)
	{
		return this.evaluate(p.getX(), p.getY());
	}
}
