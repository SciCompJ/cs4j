/**
 * 
 */
package net.sci.geom.geom3d;

import net.sci.geom.Point;
import net.sci.geom.geom3d.transform.AffineTransform3D;

/**
 * A three-dimensional point.
 * 
 * @author dlegland
 *
 */
public class Point3D implements Point, Geometry3D
{
	// ===================================================================
	// class variables

	/** x coordinate of the point */
	final double x;

	/** y coordinate of the point */
	final double y;

	/** z coordinate of the point */
	final double z;

	
	// ===================================================================
	// constructors

	/** Empty constructor, similar to Point(0,0,0) */
	public Point3D()
	{
		this(0, 0, 0);
	}

	/** New point given by its coordinates */
	public Point3D(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
    /** Convert a vector to a point */
    public Point3D(Vector3D vect)
    {
        this(vect.getX(), vect.getY(), vect.getZ());
    }

	
	// ===================================================================
	// accessors

	/**
     * @return the x coordinate of this point
     */
    public double getX()
    {
        return x;
    }
    
    /**
     * @return the y coordinate of this point
     */
    public double getY()
    {
        return y;
    }
    
    /**
     * @return the z coordinate of this point
     */
    public double getZ()
    {
        return z;
	}
	
	
	// ===================================================================
	// Methods specific to Point3D

	/**
	 * Adds the specified vector to the point, and returns the new point.
	 */
	public Point3D plus(Vector3D v)
	{
		return new Point3D(this.x + v.getX(), this.y + v.getY(), this.z + v.getZ());
	}

	/**
	 * Subtracts the specified vector from the point, and returns the new point.
	 */
	public Point3D minus(Vector3D v)
	{
		return new Point3D(this.x - v.getX(), this.y - v.getY(), this.z - v.getZ());
	}

	public Point3D transform(AffineTransform3D trans)
    {
        return trans.transform(this);
    }
    
	
    // ===================================================================
    // Implements Geometry3D methods

    @Override
    public boolean contains(Point3D point, double eps)
    {
        return distance(point) <= eps;
    }

    /**
     * Computes the distance between current point and point with coordinate
     * <code>(x,y)</code>. Uses the <code>Math.hypot()</code> function for
     * better robustness than simple square root.
     */
    public double distance(double x, double y, double z)
    {
        return Math.hypot(Math.hypot(this.x - x, this.y - y), this.z - z);
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
        case 2: return this.z;
        default:
            throw new IllegalArgumentException("Dimension should be comprised between 0 and 2");
        }
    }


    // ===================================================================
    // Implements Geometry interface

    /**
     * Returns true, as a point is bounded by definition.
     */
    public boolean isBounded()
    {
        return true;
    }

    @Override
    public Box3D boundingBox()
    {
        return new Box3D(this.x, this.x, this.y, this.y, this.z, this.z);
    }
}
