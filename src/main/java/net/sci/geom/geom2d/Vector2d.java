package net.sci.geom.geom2d;

import static java.lang.Math.abs;

public class Vector2d
{
    // ===================================================================
    // constants

	private final static double DEFAULT_TOL = 1e-12;
	
	
    // ===================================================================
    // Static methods

	public static boolean isParallel(Vector2d v1, Vector2d v2)
	{
		return isParallel(v1, v2, DEFAULT_TOL);
	}
	
	public static boolean isParallel(Vector2d v1, Vector2d v2, double tol)
	{
		v1 = v1.normalize();
		v2 = v2.normalize();
		return abs(v1.x * v2.y - v1.y * v2.x) < tol;
	}

	/**
	 * Tests if the two vectors are perpendicular
	 * 
	 * @return true if the vectors are perpendicular
	 */
	public static boolean isPerpendicular(Vector2d v1, Vector2d v2)
	{
		return isPerpendicular(v1, v2, DEFAULT_TOL);
	}
	
	/**
	 * Tests if the two vectors are perpendicular
	 * 
	 * @return true if the vectors are perpendicular
	 */
	public static boolean isPerpendicular(Vector2d v1, Vector2d v2, double tol)
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
	 */
	public static double dotProduct(Vector2d v1, Vector2d v2)
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
	 */
	public static double crossProduct(Vector2d v1, Vector2d v2)
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
	public Vector2d()
	{
		this(0, 0);
	}

	/** New Vector2d given by its coordinates */
	public Vector2d(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	/**
	 * Constructs a new vector with the same coordinates as the given point.
	 */
	public Vector2d(Point2d point)
	{
		this(point.x, point.y);
	}

	/**
	 * Constructs a new vector between two points
	 */
	public Vector2d(Point2d p1, Point2d p2)
	{
		this(p2.x - p1.x, p2.y - p1.y);
	}

	
	// ===================================================================
	// base operations

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

	/**
	 * Returns the sum of current vector with vector given as parameter. Inner
	 * fields are not modified.
	 */
	public Vector2d add(Vector2d v)
	{
		return new Vector2d(this.x + v.x, this.y + v.y);
	}

	/**
	 * Returns the subtraction of current vector with vector given as parameter.
	 * Inner fields are not modified.
	 */
	public Vector2d subtract(Vector2d v)
	{
		return new Vector2d(this.x - v.x, this.y - v.y);
	}

	/**
	 * Multiplies the vector by a scalar amount. Inner fields are not
	 * 
	 * @param k
	 *            the scale factor
	 * @return the scaled vector
	 */
	public Vector2d multiply(double k)
	{
		return new Vector2d(this.x * k, this.y * k);
	}

	/**
	 * Returns the opposite vector v2 of this, such that the sum of this and v2
	 * equals the null vector.
	 * 
	 * @return the vector opposite to <code>this</code>.
	 */
	public Vector2d opposite()
	{
		return new Vector2d(-x, -y);
	}

	/**
	 * Computes the norm of the vector
	 */
	public double norm()
	{
		return Math.hypot(x, y);
	}

	/**
	 * Returns the normalized vector, with same direction but with norm equal to
	 * 1.
	 */
	public Vector2d normalize()
	{
		double n = Math.hypot(x, y);
		return new Vector2d(x / n, y / n);
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
	 */
	public double dotProduct(Vector2d v)
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
	 */
	public double crossProduct(Vector2d v)
	{
		return x * v.y - v.x * y;
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
