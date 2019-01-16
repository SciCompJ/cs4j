/**
 * 
 */
package net.sci.array.interp;

/**
 * A scalar function of three variables, used to represent interpolated 3D images.
 * 
 * @author dlegland
 *
 */
public interface ScalarFunction3D
{
	/**
	 * Evaluates a position given as a triplet of coordinates.
	 * 
	 * @param x
	 *            the x-coordinate of the position
	 * @param y
	 *            the y-coordinate of the position
	 * @param z
	 *            the y-coordinate of the position
	 * @return the function evaluated at the given position
	 */
	public double evaluate(double x, double y, double z);
}
