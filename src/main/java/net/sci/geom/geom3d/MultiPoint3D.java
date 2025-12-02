/**
 * 
 */
package net.sci.geom.geom3d;

import java.util.Collection;

import net.sci.geom.MultiPoint;
import net.sci.geom.geom3d.impl.ArrayListMultiPoint3D;


/**
 * The geometry obtained by the union of several points.
 * 
 * @author dlegland
 *
 */
public interface MultiPoint3D extends MultiPoint, Geometry3D
{
    // ===================================================================
    // Static factories
    
    /** Empty constructor. */
    public static MultiPoint3D create()
    {
        return new ArrayListMultiPoint3D(10);
    }
    
    /**
     * Empty constructor that specifies the initial capacity of the underlying
     * buffer.
     */
    public static MultiPoint3D create(int initialCapacity)
    {
        return new ArrayListMultiPoint3D(initialCapacity);
    }
    
    /**
     * Creates a new MultiPoint3D from a collection of points.
     *
     * @param points
     *            the collection of points that compose the geometry
     * @return a new instance of MultiPoint3D
     */
    public static MultiPoint3D create(Collection<Point3D> points)
    {
        return new ArrayListMultiPoint3D(points);
    }


    // ===================================================================
    // New methods
    
    public default Point3D centroid()
    {
        double sx = 0;
        double sy = 0;
        double sz = 0;
        int n = 0;
        for (Point3D p : points())
        {
            sx += p.x;
            sy += p.y;
            sz += p.z;
            n++;
        }
        
        // normalize by point count
        return new Point3D(sx / n, sy / n, sz / n);
    }
    
    public default PrincipalAxes3D principalAxes()
    {
        return PrincipalAxes3D.fromPoints(points());
    }
    
    
    // ===================================================================
    // Point management methods
    
    public Collection<Point3D> points();
    
    public void addPoint(Point3D p);
    
    public default int pointCount()
    {
        return this.points().size();
    }
    

    // ===================================================================
    // Methods implementing the Geometry3D interface
    
    /**
     * Return true by definition.
     */
    @Override
    public default boolean isBounded()
    {
        return true;
    }

    @Override
    public default boolean contains(Point3D q, double eps)
    {
        for (Point3D p : this.points())
        {
            if (p.contains(q, eps))
            {
               return true;
            }
        }
        return false;
    }

    /**
     * Returns the distance to the closest point in the set, or +infinity if the
     * point does not contain any point.
     */
    @Override
    public default double distance(double x, double y, double z)
    {
        double minDist = Double.POSITIVE_INFINITY;
        for (Point3D p : points())
        {
            minDist = Math.min(minDist, p.distance(x, y, z));
        }
        return minDist;
    }

    @Override
    public default Bounds3D bounds()
    {
        return Bounds3D.of(points());
    }

    @Override
    public MultiPoint3D duplicate();
}
