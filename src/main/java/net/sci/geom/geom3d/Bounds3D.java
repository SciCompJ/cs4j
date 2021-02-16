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
        double x1 = p1.getX();
        double y1 = p1.getY();
        double z1 = p1.getZ();
        double x2 = p2.getX();
        double y2 = p2.getY();
        double z2 = p2.getZ();
        this.xmin = Math.min(x1, x2);
        this.xmax = Math.max(x1, x2);
        this.ymin = Math.min(y1, y2);
        this.ymax = Math.max(y1, y2);
        this.zmin = Math.min(z1, z2);
        this.zmax = Math.max(z1, z2);
    }
    

    // ===================================================================
    // Accessors to Box2D fields
    
    public double getXMin()
    {
        return xmin;
    }
    
    public double getXMax()
    {
        return xmax;
    }
    
    public double getYMin()
    {
        return ymin;
    }
    
    public double getYMax()
    {
        return ymax;
    }
    
    public double getZMin()
    {
        return zmin;
    }
    
    public double getZMax()
    {
        return zmax;
    }
    
    public double getSizeX()
    {
        return xmax - xmin;
    }
    
    public double getSizeY()
    {
        return ymax - ymin;
    }
    
    public double getSizeZ()
    {
        return zmax - zmin;
    }
    
    /** 
     * Returns true if all bounds are finite. 
     *
     * @return true is the bounds has finite values.
     */
    public boolean isBounded()
    {
        if (isInfinite(xmin))
            return false;
        if (isInfinite(ymin))
            return false;
        if (isInfinite(zmin))
            return false;
        if (isInfinite(xmax))
            return false;
        if (isInfinite(ymax))
            return false;
        if (isInfinite(zmax))
            return false;
        return true;
    }
    

    // ===================================================================
    // generic accessors
    
    public double getMin(int d)
    {
        switch(d)
        {
        case 0: return this.xmin;
        case 1: return this.ymin;
        case 2: return this.zmin;
        default: throw new IllegalArgumentException("Dimension index must be between 0 and 2, not " + d);
        }
    }
    
    public double getMax(int d)
    {
        switch(d)
        {
        case 0: return this.xmax;
        case 1: return this.ymax;
        case 2: return this.zmax;
        default: throw new IllegalArgumentException("Dimension index must be between 0 and 2, not " + d);
        }
    }
    

    // ===================================================================
    // tests of inclusion
    
    public double getSize(int d)
    {
        switch(d)
        {
        case 0: return this.xmax - this.xmin;
        case 1: return this.ymax - this.ymin;
        case 2: return this.zmax - this.zmin;
        default: throw new IllegalArgumentException("Dimension index must be between 0 and 2, not " + d);
        }
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
        double x = point.getX();
        double y = point.getY();
        double z = point.getZ();
        
        if (x < xmin)
            return false;
        if (y < ymin)
            return false;
        if (z < zmin)
            return false;
        if (x > xmax)
            return false;
        if (y > ymax)
            return false;
        if (z > zmax)
            return false;
        return true;
    }
}
