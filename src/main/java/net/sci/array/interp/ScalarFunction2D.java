/**
 * 
 */
package net.sci.array.interp;

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
}
