/**
 * 
 */
package net.sci.geom.geom3d;

import java.util.ArrayList;
import java.util.Collection;


/**
 * The geometry obtained by the union of several points.
 * 
 * @author dlegland
 *
 */
public class MultiPoint3D implements Geometry3D
{
    // ===================================================================
    // Static factories
    
    /** Empty constructor. */
    public static final MultiPoint3D create()
    {
        return new MultiPoint3D(10);
    }
    
    /**
     * Empty constructor that specifies the initial capacity of the underlying
     * buffer.
     */
    public static final MultiPoint3D create(int initialCapacity)
    {
        return new MultiPoint3D(initialCapacity);
    }
    
    /** Creates a new MultiPoint3D from a collection of points. */
    public static final MultiPoint3D create(Collection<Point3D> points)
    {
        return new MultiPoint3D(10);
    }

    
    // ===================================================================
    // Class variables

    /**
     * The inner array of points.
     */
    ArrayList<Point3D> points;

    
    // ===================================================================
    // Constructors

    /** Empty constructor. */
    private MultiPoint3D(int initialCapacity)
    {
        this.points = new ArrayList<Point3D>(initialCapacity);
    }

    /** Constructor from a collection of points. */
    private MultiPoint3D(Collection<Point3D> points)
    {
        this.points = new ArrayList<Point3D>(points.size());
        this.points.addAll(points);
    }


    // ===================================================================
    // New methods
    
    public Point3D centroid()
    {
        double sx = 0;
        double sy = 0;
        double sz = 0;
        int n = 0;
        for (Point3D p : points)
        {
            sx += p.x;
            sy += p.y;
            sz += p.z;
            n++;
        }
        
        // normalize by point count
        return new Point3D(sx / n, sy / n, sz / n);
    }
    
    public PrincipalAxes3D principalAxes()
    {
        return PrincipalAxes3D.fromPoints(points);
    }
    
    
    // ===================================================================
    // Point management methods
    
    public void addPoint(Point3D p)
    {
        this.points.add(p);
    }
    
    public int pointCount()
    {
        return this.points.size();
    }
    

    // ===================================================================
    // Methods implementing the Geometry3D interface
    
    /**
     * Return true by definition.
     */
    @Override
    public boolean isBounded()
    {
        return true;
    }

    @Override
    public boolean contains(Point3D q, double eps)
    {
        for (Point3D p : this.points)
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
    public double distance(double x, double y, double z)
    {
        double minDist = Double.POSITIVE_INFINITY;
        for (Point3D p : this.points)
        {
            minDist = Math.min(minDist, p.distance(x, y, z));
        }
        return minDist;
    }

    @Override
    public Bounds3D bounds()
    {
        return Bounds3D.of(points);
    }

    @Override
    public MultiPoint3D duplicate()
    {
       return new MultiPoint3D(points);
    }
}
