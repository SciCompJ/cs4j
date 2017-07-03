/**
 * 
 */
package net.sci.geom.geom2d;

import static java.lang.Double.isInfinite;
import net.sci.geom.Box;
import net.sci.geom.geom2d.polygon.Polygon2D;
import net.sci.geom.geom2d.polygon.SimplePolygon2D;

/**
 * Contains the bounds of a planar geometry.
 * 
 * @author dlegland
 *
 */
public class Box2D implements Box
{
    // ===================================================================
    // class variables

    double xmin;
    double ymin;
    double xmax;
    double ymax;
    
    
    // ===================================================================
    // constructors

    /** Empty constructor (size and position zero) */
    public Box2D()
    {
        this(0, 0, 0, 0);
    }
    
    /**
     * Main constructor, given bounds for x coord, then bounds for y coord.
     */
    public Box2D(double xmin, double xmax, double ymin, double ymax)
    {
        this.xmin = xmin;
        this.xmax = xmax;
        this.ymin = ymin;
        this.ymax = ymax;
    }
    
    /**
     * Constructor from 2 points, giving extreme coordinates of the box.
     */
    public Box2D(Point2D p1, Point2D p2)
    {
        double x1 = p1.getX();
        double y1 = p1.getY();
        double x2 = p2.getX();
        double y2 = p2.getY();
        this.xmin = Math.min(x1, x2);
        this.xmax = Math.max(x1, x2);
        this.ymin = Math.min(y1, y2);
        this.ymax = Math.max(y1, y2);
    }
    
    
    // ===================================================================
    // General methods
    
    /**
     * Converts this bounding box to a rectangular polyon.
     * @return
     */
    public Polygon2D getRectangle()
    {
    	Point2D p1 = new Point2D(this.xmin, this.ymin);
    	Point2D p2 = new Point2D(this.xmax, this.ymin);
    	Point2D p3 = new Point2D(this.xmax, this.ymax);
    	Point2D p4 = new Point2D(this.xmin, this.ymax);
    	Polygon2D poly = new SimplePolygon2D(p1, p2, p3, p4);
    	return poly;
    }

    
    // ===================================================================
    // Tests of inclusion
    
    /**
     * Checks if this box contains the given point.
     */
    public boolean contains(Point2D point)
    {
        double x = point.getX();
        double y = point.getY();
        if (x < xmin)
            return false;
        if (y < ymin)
            return false;
        if (x > xmax)
            return false;
        if (y > ymax)
            return false;
        return true;
    }
    
    /**
     * Checks if this box contains the point defined by the given coordinates.
     */
    public boolean contains(double x, double y)
    {
        if (x < xmin)
            return false;
        if (y < ymin)
            return false;
        if (x > xmax)
            return false;
        if (y > ymax)
            return false;
        return true;
    }

    // ===================================================================
    // Accessors to Box2D fields
    
    public double getMinX()
    {
        return xmin;
    }
    
    public double getMaxX()
    {
        return xmax;
    }
    
    public double getMinY()
    {
        return ymin;
    }
    
    public double getMaxY()
    {
        return ymax;
    }
    
    public double getWidth()
    {
        return xmax - xmin;
    }
    
    public double getHeight()
    {
        return ymax - ymin;
    }
    
    /** Returns true if all bounds are finite. */
    public boolean isBounded()
    {
        if (isInfinite(xmin))
            return false;
        if (isInfinite(ymin))
            return false;
        if (isInfinite(xmax))
            return false;
        if (isInfinite(ymax))
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
        default: throw new IllegalArgumentException("Dimension index must be eithre 0 or 1, not " + d);
        }
    }
    
    public double getMax(int d)
    {
        switch(d)
        {
        case 0: return this.xmax;
        case 1: return this.ymax;
        default: throw new IllegalArgumentException("Dimension index must be eithre 0 or 1, not " + d);
        }
    }
    
    public boolean almostEquals(Box2D box, double eps)
    {
        if (Math.abs(box.xmin - xmin) > eps) return false;
        if (Math.abs(box.xmax - xmax) > eps) return false;
        if (Math.abs(box.ymin - ymin) > eps) return false;
        if (Math.abs(box.ymax - ymax) > eps) return false;
        return true;
    }
}
