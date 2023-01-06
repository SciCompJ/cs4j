/**
 * 
 */
package net.sci.geom.geom3d;

/**
 * General interface for transforms in 3D space. Input is a 3D point, and output
 * is a 3D point.
 *
 * @see AffineTransform3D
 * @see net.sci.geom.geom2d.Transform2D
 * 
 * @author dlegland
 */
public interface Transform3D
{
    /**
     * Transforms a points and returns the result as a new 3D point.
     * 
     * @param point
     *            the point to transform.
     * @return the transformed point.
     */
	public Point3D transform(Point3D point);
	
    /**
     * Convenience method that allow to compute transformed point by specifying
     * coordinates instead of a Point3D instance.
     * 
     * Some implementations may avoid to create the temporary point.
     * 
     * @param x
     *            the x-coordinate of the point to transform
     * @param y
     *            the y-coordinate of the point to transform
     * @param z
     *            the z-coordinate of the point to transform
     * @return the transformed Point
     */
	public default Point3D transform(double x, double y, double z)
	{
	    return transform(new Point3D(x, y, z));
	}
}
