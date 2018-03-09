package net.sci.geom.geom2d;

import static java.lang.Math.abs;

import net.sci.array.Dimensional;
import net.sci.geom.geom2d.transform.AffineTransform2D;

public class Vector2D implements Dimensional
{
    // ===================================================================
    // constants

	private final static double DEFAULT_TOL = 1e-12;
	
	
    // ===================================================================
    // Static methods

	public static boolean isParallel(Vector2D v1, Vector2D v2)
	{
		return isParallel(v1, v2, DEFAULT_TOL);
	}
	
	public static boolean isParallel(Vector2D v1, Vector2D v2, double tol)
	{
		v1 = v1.normalize();
		v2 = v2.normalize();
		return abs(v1.x * v2.y - v1.y * v2.x) < tol;
	}

	/**
	 * Tests if the two vectors are perpendicular
	 * 
	 * @param v1
	 *            the first vector to test
	 * @param v2
	 *            the second vector to test
	 * @return true if the vectors are perpendicular
	 */
	public static boolean isPerpendicular(Vector2D v1, Vector2D v2)
	{
		return isPerpendicular(v1, v2, DEFAULT_TOL);
	}
	
	/**
	 * Tests if the two vectors are perpendicular
	 * 
	 * @param v1
	 *            the first vector
	 * @param v2
	 *            the second vector
	 * @param tol
	 *            the tolerance used for testing the product
	 * @return true if the vectors are perpendicular
	 */
	public static boolean isPerpendicular(Vector2D v1, Vector2D v2, double tol)
	{
		v1 = v1.normalize();
		v2 = v2.normalize();
		return abs(v1.x * v2.x + v1.y * v2.y) < tol;
	}

	/**
	 * Get the dot product of the two vectors, defined by :
	 * <p>
	 * <code> dx1*dy2 + dx2*dy1</code>
	 * <p>
	 * Dot product is zero if the vectors defined by the 2 vectors are
	 * orthogonal. It is positive if vectors are in the same direction, and
	 * negative if they are in opposite direction.
	 * 
	 * @param v1
	 *            the first vector
	 * @param v2
	 *            the second vector
	 * @return the dot product of <code>v1</code> and <code>v2</code>.
	 */
	public static double dotProduct(Vector2D v1, Vector2D v2)
	{
		return v1.x * v2.x + v1.y * v2.y;
	}

	/**
	 * Get the cross product of the two vectors, defined by :
	 * <p>
	 * <code> dx1*dy2 - dx2*dy1</code>
	 * <p>
	 * Cross product is zero for colinear vectors. It is positive if angle
	 * between vector 1 and vector 2 is comprised between 0 and PI, and negative
	 * otherwise.
	 * 
	 * @param v1
	 *            the first vector
	 * @param v2
	 *            the second vector
	 * @return the cross product of <code>v1</code> and <code>v2</code>.
	 */
	public static double crossProduct(Vector2D v1, Vector2D v2)
	{
		return v1.x * v2.y - v2.x * v1.y;
	}
    
	// ===================================================================
	// class variables

	/** x coordinate of the vector */
	final double x;

	/** y coordinate of the vector */
	final double y;

	
	// ===================================================================
	// constructors

	/** Empty constructor, similar to Vector2d(0,0) */
	public Vector2D()
	{
		this(0, 0);
	}

	/**
	 * New Vector2d given by its coordinates.
	 * 
	 * @param x
	 *            the x-coordinate of the vector
	 * @param y
	 *            the y-coordinate of the vector
	 */
	public Vector2D(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	/**
	 * Constructs a new vector with the same coordinates as the given point.
	 * 
	 * @param point
	 *            the point used to build the vector
	 */
	public Vector2D(Point2D point)
	{
		this(point.x, point.y);
	}

	/**
	 * Constructs a new vector between two points
	 * 
	 * @param p1
	 *            the origin of the vector
	 * @param p2
	 *            the destination of the vector
	 */
	public Vector2D(Point2D p1, Point2D p2)
	{
		this(p2.x - p1.x, p2.y - p1.y);
	}


	// ===================================================================
    // Methods specific to Vector2D

    /**
     * Returns the result of the given transformation applied to this vector.
     * 
     * @param trans
     *            the transformation to apply
     * @return the transformed vector
     */
    public Vector2D transform(AffineTransform2D trans)
    {
        return trans.transform(this);
    }
    
	/**
	 * Returns the sum of current vector with vector given as parameter. Inner
	 * fields are not modified.
	 * 
	 * @param v
	 *            the vector to add
	 * @return the result of the addition of this vector with <code>v</code>
	 */
	public Vector2D plus(Vector2D v)
	{
		return new Vector2D(this.x + v.x, this.y + v.y);
	}

	/**
	 * Returns the subtraction of current vector with vector given as parameter.
	 * Inner fields are not modified.
	 * 
	 * @param v
	 *            the vector to subtract
	 * @return the result of the subtraction of this vector with <code>v</code>
	 */
	public Vector2D minus(Vector2D v)
	{
		return new Vector2D(this.x - v.x, this.y - v.y);
	}

	/**
	 * Multiplies the vector by a scalar amount. Inner fields are not
	 * 
	 * @param k
	 *            the scale factor
	 * @return the scaled vector
	 */
	public Vector2D times(double k)
	{
		return new Vector2D(this.x * k, this.y * k);
	}

	/**
	 * Returns the opposite vector v2 of this, such that the sum of this and v2
	 * equals the null vector.
	 * 
	 * @return the vector opposite to <code>this</code>.
	 */
	public Vector2D opposite()
	{
		return new Vector2D(-x, -y);
	}

	/**
	 * @return the norm of this vector
	 */
	public double norm()
	{
		return Math.hypot(x, y);
	}

	/**
	 * Returns the normalized vector, with same direction but with norm equal to
	 * 1.
	 * 
	 * @return the vector with same direction and with norm equal to 1.
	 */
	public Vector2D normalize()
	{
		double n = Math.hypot(x, y);
		return new Vector2D(x / n, y / n);
	}

	
    // ===================================================================
	// operations between vectors

	/**
	 * Computes the dot product with vector <code>v</code>. The dot product is
	 * defined by:
	 * <p>
	 * <code> x1*y2 + x2*y1</code>
	 * <p>
	 * Dot product is zero if the vectors are orthogonal. It is positive if
	 * vectors are in the same direction, and negative if they are in opposite
	 * direction.
	 * 
	 * @param v
	 *            the vector for dot product
	 * @return the dot product of this vector with <code>v</code>
	 */
	public double dotProduct(Vector2D v)
	{
		return x * v.x + y * v.y;
	}

	/**
	 * Computes the cross product with vector <code>v</code>. The cross product
	 * is defined by :
	 * <p>
	 * <code> x1*y2 - x2*y1</code>
	 * <p>
	 * Cross product is zero for colinear vector. It is positive if angle
	 * between vector 1 and vector 2 is comprised between 0 and PI, and negative
	 * otherwise.
	 * 
	 * @param v
	 *            the vector for cross product
	 * @return the cross product of this vector with <code>v</code>
	 */
	public double crossProduct(Vector2D v)
	{
		return x * v.y - v.x * y;
	}

    public boolean almostEquals(Vector2D vect, double eps)
    {
        if (Math.abs(vect.x - x) > eps) return false;
        if (Math.abs(vect.y - y) > eps) return false;
        return true;
    }
    
    
    // ===================================================================
    // Accessors

    /**
     * @return the x coordinate of this vector
     */
    public double getX()
    {
        return x;
    }

    /**
     * @return the y coordinate of this vector
     */
    public double getY()
    {
        return y;
    }


	// ===================================================================
    // Implements Dimensional interface

	/**
	 * Returns a dimensionality equals to 2.
	 */
    @Override
    public int dimensionality()
    {
        return 2;
    }

//	/**
//	 * Transform the vector, by using only the first 4 parameters of the
//	 * transform. Translation of a vector returns the same vector.
//	 * 
//	 * @param trans
//	 *            an affine transform
//	 * @return the transformed vector.
//	 */
//	public Vector2d transform(AffineTransform2D trans)
//	{
//		return trans.transform(this);
//	}

}
