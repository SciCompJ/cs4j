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

	
	// ===================================================================
	// Implements Shape2D methods

	/**
	 * Computes the distance between this and the point <code>point</code>.
	 */
	public double distance(Point2D point)
	{
		return distance(point.x, point.y);
	}

	/**
	 * Computes the distance between current point and point with coordinate
	 * <code>(x,y)</code>. Uses the <code>Math.hypot()</code> function for
	 * better robustness than simple square root.
	 */
	public double distance(double x, double y)
	{
		return Math.hypot(this.x - x, this.y - y);
	}


    // ===================================================================
    // Implements Point interface

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

//	@Override
//	public boolean contains(Point2d p)
//	{
//		return this.equals(p);
//	}

//	@Override
//	public Box2D boundingBox()
//	{
//		return new Box2D(this.x, this.x, this.y, this.y);
//	}

//	@Override
//	public Collection<Point2d> clip(Box2D box)
//	{
//		ArrayList<Point2d> set = new ArrayList<Point2d>(1);
//		if (box.contains(this))
//			set.add(this);
//		return set;
//	}


//	public Point2d transform(Transform2d trans) 
//	{
//		return trans.transform(this);
//	}

}