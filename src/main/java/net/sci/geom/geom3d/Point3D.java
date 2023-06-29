/**
 * 
 */
package net.sci.geom.geom3d;

import java.util.Locale;

import net.sci.geom.Point;
import net.sci.geom.geom2d.Point2D;

/**
 * A three-dimensional point.
 * 
 * @author dlegland
 *
 */
public class Point3D implements Point, Geometry3D
{
    // ===================================================================
    // Static methods
    
    public static final Point3D centroid(Point3D... points)
    {
        double xc = 0;
        double yc = 0;
        double zc = 0;
        int np = points.length;
        for (Point3D p : points)
        {
            xc += p.x;
            yc += p.y;
            zc += p.z;
        }
        
        return new Point3D(xc / np, yc / np, zc / np);
    }

    /**
     * Converts a 2D point into a 3D point by adding a z-coordinate
     * 
     * @see #projectXY()
     * 
     * @param point
     *            the point to convert
     * @param z
     *            the amount of translation in the z direction
     * @return the new 3D point
     */
    public static final Point3D from2d(Point2D point, double z)
    {
        return new Point3D(point.getX(), point.getY(), z);
    }
    
    
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

	/**
     * New point given by its coordinates
     * 
     * @param x
     *            the x coordinate of the new point
     * @param y
     *            the y coordinate of the new point
     * @param z
     *            the z coordinate of the new point
     */
	public Point3D(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
    /**
     * Convert a vector to a point
     *
     * @param vect
     *            the vector to convert
     */
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
     * 
     * @param v
     *            the 3D vector to add
     * @return the result of the translation of this point by the given vector
     */
	public Point3D plus(Vector3D v)
	{
		return new Point3D(this.x + v.getX(), this.y + v.getY(), this.z + v.getZ());
	}

	/**
     * Subtracts the specified vector from the point, and returns the new point.
     * 
     * @param v
     *            the 3D vector to subtract
     * @return the result of the translation of this point by the opposite of
     *         the given vector
     */
	public Point3D minus(Vector3D v)
	{
		return new Point3D(this.x - v.getX(), this.y - v.getY(), this.z - v.getZ());
	}
	
    /**
     * Projects this point onto the XY plane and converts into a 2D point.
     * 
     * @see #projectXY()
     * 
     * @return the 2D projection of the point onto the XY plane.
     */
	public Point2D projectXY()
	{
	    return new Point2D(this.x, this.y);
	}

	public Point3D transform(AffineTransform3D trans)
    {
        return trans.transform(this);
    }
    
	public boolean almostEquals(Point3D point, double eps)
	{
	    if (Math.abs(point.x - x) > eps) return false;
        if (Math.abs(point.y - y) > eps) return false;
        if (Math.abs(point.z - z) > eps) return false;
	    return true;
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
    public Bounds3D bounds()
    {
        return new Bounds3D(this.x, this.x, this.y, this.y, this.z, this.z);
    }
    
    public Point3D duplicate()
    {
        return new Point3D(x, y, z);
    }
    
    // ===================================================================
    // Override Object's methods

    @Override
    public String toString()
    {
        return String.format(Locale.ENGLISH, "Point3D(%f, %f, %f)", x, y, z);
    }
}
