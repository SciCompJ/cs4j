/**
 * 
 */
package net.sci.geom.geom2d;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import net.sci.geom.Point;

/**
 * A point in the two-dimensional plane. 
 * 
 * @author dlegland
 *
 */
public class Point2D implements PointShape2D, Point
{
    // ===================================================================
    // Static methods
    
    /**
     * Computes the centroid of a collection of points.
     * 
     * @param points
     *            the points to consider.
     * @return the centroid of the input points.
     */
    public static final Point2D centroid(Point2D... points)
    {
        double xc = 0;
        double yc = 0;
        int np = points.length;
        for (Point2D p : points)
        {
            xc += p.x;
            yc += p.y;
        }
        
        return new Point2D(xc / np, yc / np);
    }
    
    /**
     * Computes the centroid of a collection of points identified by two
     * coordinate arrays with the same length.
     * 
     * @param xCoords
     *            the x-coordinates of the points
     * @param yCoords
     *            the y-coordinates of the points
     * @return the centroid of the input points.
     */
    public static final Point2D centroid(double[] xCoords, double[] yCoords)
    {
        if (yCoords.length != xCoords.length)
        {
            throw new IllegalArgumentException("Coordinate arrays must have same length");
        }
        
        double xc = 0;
        double yc = 0;
        int np = xCoords.length;
        for (int i = 0; i < np; i++)
        {
            xc += xCoords[i];
            yc += yCoords[i];
        }
        
        return new Point2D(xc / np, yc / np);
    }
    
    /**
     * Interpolates the position of a new Point2D between the two points.
     * 
     * @param p1
     *            the first point to interpolate
     * @param p2
     *            the second point to interpolate
     * @param t
     *            the relative position of the new point, between 0 and 1. If t
     *            is outside the [0,1] range, its value is clamped to enforce
     *            the resulting point to be between the two extremity points.
     * @return the interpolated point
     */
    public static final Point2D interpolate(Point2D p1, Point2D p2, double t)
    {
        if (t <= 0) return p1;
        if (t >= 1) return p2;
        double x = p1.x() * (1.0 - t) + p2.x() * t;
        double y = p1.y() * (1.0 - t) + p2.y() * t;
        return new Point2D(x, y);
    }
    
    /**
     * Returns a new sorted list of the points in the collection given as
     * parameter. The points are sorted in lexicographic order: first with
     * increasing X order, and in case of equality by increasing Y order.
     * 
     * @param points
     *            a collection of points to sort
     * @return a new collection containing points sorted wrt their coordinates.
     */
    public static final ArrayList<Point2D> sortPoints(Collection<Point2D> points)
    {
        // create result array
        ArrayList<Point2D> res = new ArrayList<Point2D>(points.size());
        res.addAll(points);
        
        // create the comparator
        Comparator<Point2D> comparator = new Comparator<Point2D>()
        {
            @Override
            public int compare(Point2D p0, Point2D p1)
            {
                // compare X first
                double dx = p0.x() - p1.x();
                if (dx < 0) return -1;
                if (dx > 0) return +1;
                // add Y comparison
                double dy = p0.y() - p1.y();
                if (dy < 0) return -1;
                if (dy > 0) return +1;
                // point with same coordinates
                return 0;
            }
        };
        
        // sort the array of points
        Collections.sort(res, comparator);
        return res;
    }


    // ===================================================================
    // class variables

	/** x coordinate of the point */
	final double x;

	/** y coordinate of the point */
	final double y;

	
	// ===================================================================
	// Constructors

	/** Empty constructor, similar to Point(0,0) */
	public Point2D()
	{
		this(0, 0);
	}

	/** 
	 * New point given by its coordinates 
	 * 
	 * @param x the x coordinate of the new point
	 * @param y the y coordinate of the new point
	 */
	public Point2D(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	
	
	// ===================================================================
	// Specific methods

	/**
     * Applies the translation defined by the two components to this point and
     * returns the translated point.
     * 
     * @param dx
     *            the x-component of the translation
     * @param dy
     *            the y-component of the translation
     * @return the translated point
     */
	public Point2D translate(double dx, double dy)
	{
	    return new Point2D(this.x + dx, this.y + dy);
	}
	
	/**
	 * Adds the specified vector to the point, and returns the result.
	 * 
	 * @param v
	 *            the vector to add
	 * @return the result of the addition of<code>v</code> to this point
	 */
	public Point2D plus(Vector2D v)
	{
		return new Point2D(this.x + v.x(), this.y + v.y());
	}

	/**
	 * Subtracts the specified vector from the point, and returns the result.
	 * 
	 * @param v
	 *            the vector to subtract
	 * @return the result of the subtraction of<code>v</code> to this point
	 */
	public Point2D minus(Vector2D v)
	{
		return new Point2D(this.x - v.x(), this.y - v.y());
	}
	

    // ===================================================================
    // Implements vector space structure for Point2D

    /**
     * Adds the coordinates of the specified point to those of this point, and
     * returns the point with new coordinates
     * 
     * @param p
     *            the 2D point to add
     * @return the result of the translation of this point by the given vector
     */
    public Point2D plus(Point2D p)
    {
        return new Point2D(this.x + p.x(), this.y + p.y());
    }

    /**
     * Subtracts the coordinates of the specified point from those of this
     * point, and returns the point with new coordinates
     * 
     * @param p
     *            the 2D point to subtract
     * @return the result of the translation of this point by the opposite of
     *         the given vector
     */
    public Point2D minus(Point2D p)
    {
        return new Point2D(this.x - p.x(), this.y - p.y());
    }

    /**
     * Multiplies the coordinates of this point by the given factor, and returns
     * the point with new coordinates.
     * 
     * @param k
     *            the scaling factor
     * @return the point with new coordinates
     */
    public Point2D times(double k)
    {
        return new Point2D(this.x * k, this.y * k);
    }

    /**
     * Divides the coordinates of this point by the given factor, and returns
     * the point with new coordinates.
     * 
     * @param k
     *            the scaling factor
     * @return the point with new coordinates
     */
    public Point2D divideBy(double k)
    {
        return new Point2D(this.x / k, this.y / k);
    }
    
    
	// ===================================================================
    // Specific methods
    
    /**
     * Returns the result of the given transformation applied to this point.
     * 
     * @param trans
     *            the transformation to apply
     * @return the transformed point
     */
    public Point2D transform(AffineTransform2D trans)
    {
        return trans.transform(this);
    }

    /**
     * Checks if the two points are equal up to the absolute tolerance value
     * given as parameter. The tolerance is used for each of the x and y
     * coordinates.
     * 
     * @param point
     *            the point to compare with
     * @param eps
     *            the (absolute) tolerance on coordinates
     * @return true if all the coordinates of the points are within the
     *         <code>eps</code> interval.
     */
	public boolean almostEquals(Point2D point, double eps)
	{
        if (Math.abs(point.x - x) > eps) return false;
        if (Math.abs(point.y - y) > eps) return false;
        return true;
	}
	
	
    // ===================================================================
    // accessors

    /**
     * @return the x coordinate of this point
     */
    public double x()
    {
        return x;
    }
    
    /**
     * @return the y coordinate of this point
     */
    public double y()
    {
        return y;
    }
    
    
    // ===================================================================
    // Implementation of the Point interface

    @Override
    public double get(int dim)
    {
        return switch (dim)
        {
            case 0 -> this.x;
            case 1 -> this.y;
            default -> throw new IllegalArgumentException(
                    "Dimension should be either 0 or 1");
        };
    }
    
    
    // ===================================================================
    // Implementation of the PointShape2D interface

    @Override
    public int pointCount()
    {
        return 1;
    }
    
    @Override
    public Collection<? extends Point2D> points()
    {
        return List.of(this);
    }


    // ===================================================================
    // Implementation of the Geometry2D interface

    @Override
    public boolean contains(Point2D point, double eps)
    {
        if (Math.abs(this.x - point.x) > eps) return false;
        if (Math.abs(this.y - point.y) > eps) return false;
        return true;
    }

    /**
     * Computes the distance between this point and the point
     * <code>point</code>.
     *
     * @param point
     *            another point
     * @return the distance between the two points
     */
    /* 
     * Overrides the default implementation in Geometry2D interface to directly
     * compare point coordinates.
     */ 
    @Override
    public double distance(Point2D point)
    {
        return distance(point.x, point.y);
    }

    /**
     * Computes the distance between current point and point with coordinate
     * <code>(x,y)</code>. Uses the <code>Math.hypot()</code> function for
     * better robustness than simple square root.
     * 
     * @param x the x-coordinate of the other point
     * @param y the y-coordinate of the other point
     * @return the distance between the two points
     */
    public double distance(double x, double y)
    {
        return Math.hypot(this.x - x, this.y - y);
    }


    // ===================================================================
    // Implementation of the Geometry interface

    @Override
    public Bounds2D bounds()
    {
        return new Bounds2D(this.x, this.x, this.y, this.y);
    }

    @Override
    public Point2D duplicate()
    {
        return new Point2D(x, y);
    }


    // ===================================================================
    // Override Object interface

    @Override
    public String toString()
    {
        return String.format(Locale.ENGLISH, "Point2D(%g,%g)", this.x, this.y);
    }

}
