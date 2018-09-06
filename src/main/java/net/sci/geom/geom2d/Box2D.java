/**
 * 
 */
package net.sci.geom.geom2d;

import static java.lang.Double.isInfinite;

import net.sci.geom.Box;
import net.sci.geom.geom2d.polygon.Polygon2D;

/**
 * Contains the bounds of a planar geometry.
 * 
 * @author dlegland
 *
 */
public class Box2D implements Box, Geometry2D
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
	 * Main constructor, given bounds for x coordinate, then bounds for y
	 * coordinate.
	 * 
	 * @param xmin
	 *            the minimum value along the first dimension
	 * @param xmax
	 *            the maximum value along the first dimension
	 * @param ymin
	 *            the minimum value along the second dimension
	 * @param ymax
	 *            the maximum value along the second dimension
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
	 * 
	 * @param p1
	 *            a point corresponding to a corner of the box
	 * @param p2
	 *            a point corresponding to a corner of the box, opposite of p1
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
	 * 
	 * @return the polygon corresponding to this bounding box
	 */
    public Polygon2D getRectangle()
    {
    	Point2D p1 = new Point2D(this.xmin, this.ymin);
    	Point2D p2 = new Point2D(this.xmax, this.ymin);
    	Point2D p3 = new Point2D(this.xmax, this.ymax);
    	Point2D p4 = new Point2D(this.xmin, this.ymax);
    	Polygon2D poly = Polygon2D.create(p1, p2, p3, p4);
    	return poly;
    }

    
    // ===================================================================
    // Tests of inclusion
    
    /**
	 * Checks if this box contains the given point.
	 * 
	 * @param point
	 *            the test point
	 * @return true if the point is inside the bounding box
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
	 * @param x
	 *            the x-coordinate of the test point
	 * @param y
	 *            the y-coordinate of the test point
	 * @return true if the point is inside the bounding box
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
    
    public double getSizeX()
    {
        return xmax - xmin;
    }
    
    public double getSizeY()
    {
        return ymax - ymin;
    }
    
    /** @return true if all bounds are finite. */
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
        default: throw new IllegalArgumentException("Dimension index must be either 0 or 1, not " + d);
        }
    }
    
    public double getMax(int d)
    {
        switch(d)
        {
        case 0: return this.xmax;
        case 1: return this.ymax;
        default: throw new IllegalArgumentException("Dimension index must be either 0 or 1, not " + d);
        }
    }
    
    public double getSize(int d)
    {
        switch(d)
        {
        case 0: return this.xmax - this.xmin;
        case 1: return this.ymax - this.ymin;
        default: throw new IllegalArgumentException("Dimension index must be either 0 or 1, not " + d);
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

    @Override
    public boolean contains(Point2D point, double eps)
    {
        if (point.x < xmin || point.x > xmax) return false;
        if (point.y < ymin || point.y > ymax) return false;
        return true;
    }

    @Override
    public double distance(double x, double y)
    {
        return getRectangle().distance(x, y);
    }

    @Override
    public Box2D boundingBox()
    {
        return this;
    }
}
