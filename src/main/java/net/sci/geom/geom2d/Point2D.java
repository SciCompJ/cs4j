/**
 * 
 */
package net.sci.geom.geom2d;

import net.sci.geom.Point;

/**
 * @author dlegland
 *
 */
public class Point2D implements Geometry2D, Point
{
	// ===================================================================
	// class variables

	/** x coordinate of the point */
	final double x;

	/** y coordinate of the point */
	final double y;

	
	// ===================================================================
	// constructors

	/** Empty constructor, similar to Point(0,0) */
	public Point2D()
	{
		this(0, 0);
	}

	/** New point given by its coordinates */
	public Point2D(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	
	
	// ===================================================================
	// accessors

	/**
	 * @return the x coordinate of this point
	 */
	public double getX() {
		return x;
	}

	/**
	 * @return the y coordinate of this point
	 */
	public double getY() {
		return y;
	}
	
	
	// ===================================================================
	// generic methods

	/**
	 * Adds the specified vector to the point, and returns the result.
	 */
	public Point2D add(Vector2D v)
	{
		return new Point2D(this.x + v.getX(), this.y + v.getY());
	}

	/**
	 * Subtracts the specified vector from the point, and returns the result.
	 */
	public Point2D subtract(Vector2D v)
	{
		return new Point2D(this.x - v.getX(), this.y - v.getY());
	}

	public boolean almostEquals(Point2D point, double eps)
	{
        if (Math.abs(point.x - x) > eps) return false;
        if (Math.abs(point.y - y) > eps) return false;
        return true;
	}
	
	
	// ===================================================================
    // Implementation of the Point interface

    @Override
    public double get(int dim)
    {
        switch(dim)
        {
        case 0: return this.x;
        case 1: return this.y;
        default:
            throw new IllegalArgumentException("Dimension should be comprised between 0 and 1");
        }
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
     * Computes the distance between this and the point <code>point</code>.
     * 
     * @param point another point
     * @return the distance between the two points
     */
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

    /**
     * Returns true, as a point is bounded by definition.
     */
    public boolean isBounded()
    {
        return true;
    }

    @Override
    public Box2D boundingBox()
    {
        return new Box2D(this.x, this.x, this.y, this.y);
    }


//	public Point2d transform(Transform2d trans) 
//	{
//		return trans.transform(this);
//	}

}
