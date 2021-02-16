/**
 * 
 */
package net.sci.geom.geom2d;

import net.sci.geom.Geometry;

/**
 * A shape embedded into a 2-dimensional space.
 * 
 * @author dlegland
 *
 */
public interface Geometry2D extends Geometry
{
    // ===================================================================
    // Static variables
    
    /**
     * The origin of the basis, equal to (0,0).
     */
    public static final Point2D ORIGIN = new Point2D(0,0);
    
    /**
     * The minimum norm for which a vector can be assimilated to null vector.
     */
    public static final double MIN_VECTOR_NORM = 1e-12;
        
    // ===================================================================
    // Methods declaration
    
    /**
	 * Checks if the geometry contains the given point, with a given precision.
	 * 
	 * @param point
	 *            the point to test
	 * @param eps
	 *            the tolerance to use for distance comparison
	 * @return true if the point is inside this geometry, with respect to the
	 *         given tolerance
	 */
    public boolean contains(Point2D point, double eps);
    
    /**
     * Computes the distance between this geometry and the given point.
     * 
     * @param point
     *            a point in the 2D plane
     * @return the Euclidean distance between this geometry and the specified point
     */
    public default double distance(Point2D point)
    {
        return distance(point.getX(), point.getY());
    }
    
    /**
     * Computes the distance between this geometry and the point given by the
     * couple of coordinates.
     * 
     * @param x
     *            the x-coordinate of the point to test
     * @param y
     *            the y-coordinate of the point to test
     * @return the Euclidean distance between this geometry and the specified
     *         point
     */
    public double distance(double x, double y);
    
    /**
     * @return a dimensionality value equals to 2.
     */
    @Override
    public default int dimensionality()
    {
        return 2;
    }

    /**
     * @return the bounds of this geometry.
     */
    @Override
    public Bounds2D bounds();
}
