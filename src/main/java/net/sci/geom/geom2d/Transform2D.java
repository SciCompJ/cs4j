/**
 * 
 */
package net.sci.geom.geom2d;

/**
 * General interface for transforms in 2D space. Input is a 2D point, and output
 * is a 2D point.
 *
 * @see AffineTransform2D
 * @see net.sci.geom.geom3d.Transform3D
 * 
 * @author dlegland
 *
 */
public interface Transform2D
{
    /**
     * Transforms a points and returns the result as a new 3D point.
     * 
     * @param point
     *            the point to transform.
     * @return the transformed point.
     */
	public Point2D transform(Point2D point);
	
    /**
     * Convenience method that allow to compute transformed point by specifying
     * coordinates instead of a Point2D instance.
     * 
     * Some implementations may avoid to create the temporary point.
     * 
     * @param x
     *            the x-coordinate of the point to transform
     * @param y
     *            the y-coordinate of the point to transform
     * @return the transformed Point
     */
    public default Point2D transform(double x, double y)
    {
        return transform(new Point2D(x, y));
    }
}
