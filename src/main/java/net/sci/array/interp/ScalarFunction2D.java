/**
 * 
 */
package net.sci.array.interp;

import net.sci.geom.geom2d.Point2D;

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
	 * 
	 * @param x
	 *            the x-coordinate of the position
	 * @param y
	 *            the y-coordinate of the position
	 * @return the function evaluated at the given position
	 */
	public double evaluate(double x, double y);
	
	/**
	 * Evaluates a position given as a point.
	 * 
	 * @param p
	 *            the position for evaluating the function
	 * @return the function evaluated at the given position
	 */
	public default double evaluate(Point2D p)
	{
		return this.evaluate(p.getX(), p.getY());
	}
}
