/**
 * 
 */
package net.sci.interp;

import net.sci.geom.geom3d.Point3d;

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
	 */
	public double evaluate(double x, double y, double z);
	
	/**
	 * Evaluates a position given as a point.
	 */
	public default double evaluate(Point3d p)
	{
		return this.evaluate(p.getX(), p.getY(), p.getZ());
	}
}