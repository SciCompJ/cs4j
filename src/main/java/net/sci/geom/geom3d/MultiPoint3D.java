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
    // Methods
    
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
        if (this.points.isEmpty())
        {
            return new Bounds3D();
        }
        
        // init bounds
        double xmin = Double.POSITIVE_INFINITY;
        double xmax = Double.NEGATIVE_INFINITY;
        double ymin = Double.POSITIVE_INFINITY;
        double ymax = Double.NEGATIVE_INFINITY;
        double zmin = Double.POSITIVE_INFINITY;
        double zmax = Double.NEGATIVE_INFINITY;
        
        // compute bounds by iterating over points
        for (Point3D p : this.points)
        {
            xmin = Math.min(p.x, xmin);
            ymin = Math.min(p.y, ymin);
            zmin = Math.min(p.z, zmin);
            xmax = Math.max(p.x, xmax);
            ymax = Math.max(p.y, ymax);
            zmax = Math.max(p.z, zmax);
        }

        return new Bounds3D(xmin, xmax, ymin, ymax, zmin, zmax);
    }
}
