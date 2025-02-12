/**
 * 
 */
package net.sci.geom.geom3d;

import static java.lang.Double.isInfinite;

import net.sci.geom.Bounds;

/**
 * Contains the bounds of a 3D geometry.
 * 
 * @author dlegland
 *
 */
public class Bounds3D implements Bounds
{
    // ===================================================================
    // Static factories
    
    /**
     * Computes the bounds of a collection of points. If the collection is
     * empty, returns infinite bounds.
     * 
     * @param points
     *            the points to bound
     * @return the bounds of the points.
     */
    public static final Bounds3D of(Iterable<Point3D> points)
    {
        // init bounds
        double xmin = Double.POSITIVE_INFINITY;
        double xmax = Double.NEGATIVE_INFINITY;
        double ymin = Double.POSITIVE_INFINITY;
        double ymax = Double.NEGATIVE_INFINITY;
        double zmin = Double.POSITIVE_INFINITY;
        double zmax = Double.NEGATIVE_INFINITY;
        
        // compute bounds by iterating over points
        for (Point3D p : points)
        {
            xmin = Math.min(p.x, xmin);
            xmax = Math.max(p.x, xmax);
            ymin = Math.min(p.y, ymin);
            ymax = Math.max(p.y, ymax);
            zmin = Math.min(p.z, zmin);
            zmax = Math.max(p.z, zmax);
        }

        return new Bounds3D(xmin, xmax, ymin, ymax, zmin, zmax);        
    }
    
    
    // ===================================================================
    // class variables

    double xmin;
    double xmax;
    double ymin;
    double ymax;
    double zmin;
    double zmax;
    
    
    // ===================================================================
    // constructors

    /** Empty constructor (size and position zero) */
    public Bounds3D()
    {
        this(0, 0, 0, 0, 0, 0);
    }
    
    /**
     * Main constructor, given bounds for x coord, then bounds for y coord, then
     * bounds for the z coords.
     * 
     * @param xmin
     *            the minimum value of the x coordinate of the bounds
     * @param xmax
     *            the maximum value of the x coordinate of the bounds
     * @param ymin
     *            the minimum value of the y coordinate of the bounds
     * @param ymax
     *            the maximum value of the y coordinate of the bounds
     * @param zmin
     *            the minimum value of the z coordinate of the bounds
     * @param zmax
     *            the maximum value of the z coordinate of the bounds
     */
    public Bounds3D(double xmin, double xmax, double ymin, double ymax, double zmin, double zmax)
    {
        this.xmin = xmin;
        this.xmax = xmax;
        this.ymin = ymin;
        this.ymax = ymax;
        this.zmin = zmin;
        this.zmax = zmax;
    }
    
    /**
     * Constructor from 2 points, giving extreme coordinates of the bounds.
     * 
     * @param p1
     *            first corner of the box
     * @param p2
     *            the corner of the box opposite to the first corner
     */
    public Bounds3D(Point3D p1, Point3D p2)
    {
        double x1 = p1.x();
        double y1 = p1.y();
        double z1 = p1.z();
        double x2 = p2.x();
        double y2 = p2.y();
        double z2 = p2.z();
        this.xmin = Math.min(x1, x2);
        this.xmax = Math.max(x1, x2);
        this.ymin = Math.min(y1, y2);
        this.ymax = Math.max(y1, y2);
        this.zmin = Math.min(z1, z2);
        this.zmax = Math.max(z1, z2);
    }
    
    
    // ===================================================================
    // General methods
    
    /**
     * Computes the bounds corresponding to the union of this bounds and the
     * input bounds. The maximal extend in each direction is kept.
     * 
     * @param that
     *            the Bounds3D to combine with
     * @return the union of the two bounds
     */
    public Bounds3D union(Bounds3D that)
    {
        double xmin = Math.min(this.xmin, that.xmin);
        double xmax = Math.max(this.xmax, that.xmax);
        double ymin = Math.min(this.ymin, that.ymin);
        double ymax = Math.max(this.ymax, that.ymax);
        double zmin = Math.min(this.zmin, that.zmin);
        double zmax = Math.max(this.zmax, that.zmax);
        return new Bounds3D(xmin, xmax, ymin, ymax, zmin, zmax);
    }
    

    // ===================================================================
    // Accessors to Bounds3D fields
    
    public double xMin()
    {
        return xmin;
    }
    
    public double xMax()
    {
        return xmax;
    }
    
    public double yMin()
    {
        return ymin;
    }
    
    public double yMax()
    {
        return ymax;
    }
    
    public double zMin()
    {
        return zmin;
    }
    
    public double zMax()
    {
        return zmax;
    }
    
    public double xExtent()
    {
        return xmax - xmin;
    }
    
    public double yExtent()
    {
        return ymax - ymin;
    }
    
    public double zExtent()
    {
        return zmax - zmin;
    }
    
    /**
     * Checks if this bounds contains the given point.
     * 
     * @param point
     *            the point to evaluate
     * @return true if the 3D point is within this 3D bounds
     */
    public boolean contains(Point3D point)
    {
        return contains(point.x(), point.y(), point.z());
    }

    /**
     * Checks if this bounds contains the given point.
     * 
     * @param x
     *            the x-coordinate of the point to evaluate
     * @param y
     *            the y-coordinate of the point to evaluate
     * @param z
     *            the z-coordinate of the point to evaluate
     * @return true if the 3D point is within this 3D bounds
     */
    public boolean contains(double x, double y, double z)
    {
        if (x < xmin) return false;
        if (y < ymin) return false;
        if (z < zmin) return false;
        if (x > xmax) return false;
        if (y > ymax) return false;
        if (z > zmax) return false;
        return true;
    }


    // ===================================================================
    // Implementation of the Bounds interface
    
    /**
     * Checks if the bounds are finite.
     *
     * @return true if all the bounding limits have finite values.
     */
    public boolean isFinite()
    {
        if (isInfinite(xmin)) return false;
        if (isInfinite(ymin)) return false;
        if (isInfinite(zmin)) return false;
        if (isInfinite(xmax)) return false;
        if (isInfinite(ymax)) return false;
        if (isInfinite(zmax)) return false;
        return true;
    }
    
    @Override
    public double minCoord(int d)
    {
        switch(d)
        {
        case 0: return this.xmin;
        case 1: return this.ymin;
        case 2: return this.zmin;
        default: throw new IllegalArgumentException("Dimension index must be between 0 and 2, not " + d);
        }
    }
    
    @Override
    public double maxCoord(int d)
    {
        switch(d)
        {
        case 0: return this.xmax;
        case 1: return this.ymax;
        case 2: return this.zmax;
        default: throw new IllegalArgumentException("Dimension index must be between 0 and 2, not " + d);
        }
    }
    
    @Override
    public double size(int d)
    {
        switch(d)
        {
        case 0: return this.xmax - this.xmin;
        case 1: return this.ymax - this.ymin;
        case 2: return this.zmax - this.zmin;
        default: throw new IllegalArgumentException("Dimension index must be between 0 and 2, not " + d);
        }
    }

    @Override
    public int dimensionality()
    {
        return 3;
    }
}
