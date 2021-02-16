/**
 * 
 */
package net.sci.geom.geom3d;

import net.sci.geom.Geometry;

/**
 * Interface for all geometries in a 3D Euclidean space.
 * 
 * @author dlegland
 *
 */
public interface Geometry3D extends Geometry
{
    // ===================================================================
    // Static variables
    
    /**
     * The origin of the basis, equal to (0,0,0).
     */
    public static final Point3D ORIGIN = new Point3D(0,0,0);
    
        
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
    public boolean contains(Point3D point, double eps);
    
    /**
     * Computes the distance between this geometry and the given point.
     * 
     * @param point
     *            a point in the 2D plane
     * @return the Euclidean distance between this geometry and the specified point
     */
    public default double distance(Point3D point)
    {
        return distance(point.getX(), point.getY(), point.getZ());
    }
    
    /**
     * Computes the distance between this geometry and the point given by the
     * couple of coordinates.
     * 
     * @param x
     *            the x-coordinate of the point to test
     * @param y
     *            the y-coordinate of the point to test
     * @param z
     *            the z-coordinate of the point to test
     * @return the Euclidean distance between this geometry and the specified
     *         point
     */
    public double distance(double x, double y, double z);
    
    /**
     * Returns dimensionality equals to 3.
     */
    @Override
    public default int dimensionality()
    {
        return 3;
    }
    
    public Bounds3D bounds();
}
