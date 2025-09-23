/**
 * 
 */
package net.sci.geom.geom2d;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import net.sci.geom.MultiPoint;

/**
 * The geometry obtained by the union of several points.
 * 
 * @author dlegland
 *
 */
public class MultiPoint2D implements MultiPoint, PointShape2D
{
    // ===================================================================
    // Static factories
    
    /** Empty constructor. */
    public static final MultiPoint2D create()
    {
        return new MultiPoint2D(10);
    }
    
    /**
     * Empty constructor that specifies the initial capacity of the underlying
     * buffer.
     */
    public static final MultiPoint2D create(int initialCapacity)
    {
        return new MultiPoint2D(initialCapacity);
    }
    
    /**
     * Creates a new MultiPoint2D from a collection of points.
     *
     * @param points
     *            the collection of points that compose the geometry
     * @return a new instance of MultiPoint2D
     */
    public static final MultiPoint2D create(Collection<Point2D> points)
    {
        return new MultiPoint2D(points);
    }

    
    // ===================================================================
    // Class variables

    /**
     * The inner array of points.
     */
    ArrayList<Point2D> points;

    
    // ===================================================================
    // Constructors

    /** Empty constructor. */
    private MultiPoint2D(int initialCapacity)
    {
        this.points = new ArrayList<Point2D>(initialCapacity);
    }

    /**
     * Constructor from a collection of points.
     * 
     * @param points
     *            the collection of points that compose the geometry
     */
    private MultiPoint2D(Collection<Point2D> points)
    {
        this.points = new ArrayList<Point2D>(points.size());
        this.points.addAll(points);
    }


    // ===================================================================
    // New methods
    
    public Point2D centroid()
    {
        double sx = 0;
        double sy = 0;
        int n = 0;
        for (Point2D p : points)
        {
            sx += p.x;
            sy += p.y;
            n++;
        }
        return new Point2D(sx / n, sy / n);
    }
    
    public PrincipalAxes2D principalAxes()
    {
        return PrincipalAxes2D.fromPoints(points);
    }
    
    
    // ===================================================================
    // Methods
    
    public void addPoint(Point2D p)
    {
        this.points.add(p);
    }
    
    // ===================================================================
    // Methods implementing the PointShape2D interface
    
    public int pointCount()
    {
        return this.points.size();
    }
    
    @Override
    public Collection<? extends Point2D> points()
    {
        return Collections.unmodifiableList(this.points);
    }


    // ===================================================================
    // Methods implementing the Geometry2D interface
    
    /**
     * Return true by definition.
     */
    @Override
    public boolean isBounded()
    {
        return true;
    }

    @Override
    public boolean contains(Point2D q, double eps)
    {
        for (Point2D p : this.points)
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
    public double distance(double x, double y)
    {
        double minDist = Double.POSITIVE_INFINITY;
        for (Point2D p : this.points)
        {
            minDist = Math.min(minDist, p.distance(x, y));
        }
        return minDist;
    }

    @Override
    public Bounds2D bounds()
    {
        return Bounds2D.of(points);
    }

    @Override
    public MultiPoint2D duplicate()
    {
        MultiPoint2D pts = new MultiPoint2D(points.size());
        pts.points.addAll(points);
        return pts;
    }
}
