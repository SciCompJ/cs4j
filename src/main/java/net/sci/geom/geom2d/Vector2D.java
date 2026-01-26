package net.sci.geom.geom2d;

import static java.lang.Math.abs;

import java.util.Locale;

import net.sci.array.Dimensional;
import net.sci.array.numeric.Numeric;

/**
 * Implementation of a two-dimensional vector.
 */
public class Vector2D implements Dimensional, Numeric<Vector2D>
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
	 * Tests if the two vectors are perpendicular.
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
     * Get the dot product of two vectors. If the two vectors are given by
     * {@code v1 = (vx1,vy1)} and {@code v2 = (vx2,vy2)}, then their dot product
     * is given by:
     * <p>
     * {@code vx1 * vx2 + vy2 * vy2}
     * <p>
     * 
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
     * Get the dot product of two vectors given by their coordinates. This
     * method is a low-level version of the method with same name that accepts
     * two vectors as input arguments. If the two vectors are given by
     * {@code v1 = (vx1,vy1)} and {@code v2 = (vx2,vy2)}, then their dot product
     * is given by:
     * <p>
     * {@code vx1 * vx2 + vy2 * vy2}
     * <p>
     * 
     * Dot product is zero if the vectors defined by the 2 vectors are
     * orthogonal. It is positive if vectors are in the same direction, and
     * negative if they are in opposite direction.
     * 
     * @see #dotProduct(Vector2D, Vector2D)
     * 
     * @param vx1
     *            the x-coordinate of the first vector
     * @param vy1
     *            the y-coordinate of the first vector
     * @param vx2
     *            the x-coordinate of the second vector
     * @param vy2
     *            the y-coordinate of the second vector
     * @return the dot product of <code>v1</code> and <code>v2</code>.
     */
    public static double dotProduct(double vx1, double vy1, double vx2, double vy2)
    {
        return vx1 * vx2 + vy1 * vy2;
    }
    
	/**
     * Get the cross product of two vectors. If the two vectors are given by
     * {@code v1 = (vx1,vy1)} and {@code v2 = (vx2,vy2)}, then their cross
     * product is given by:
     * <p>
     * {@code vx1 * vy2 - vx2 * vy1}
     * <p>
     * 
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
    
    /**
     * Get the cross product of two vectors given by their coordinates. This
     * method is a low-level version of the method with same name that accepts
     * two vectors as input arguments. If the two vectors are given by
     * {@code v1 = (vx1,vy1)} and {@code v2 = (vx2,vy2)}, then their cross
     * product is given by:
     * <p>
     * {@code vx1 * vy2 - vx2 * vy1}
     * <p>
     * 
     * Cross product is zero for colinear vectors. It is positive if angle
     * between vector 1 and vector 2 is comprised between 0 and PI, and negative
     * otherwise.
     * 
     * @see #crossProduct(Vector2D, Vector2D)
     * 
     * @param vx1
     *            the x-coordinate of the first vector
     * @param vy1
     *            the y-coordinate of the first vector
     * @param vx2
     *            the x-coordinate of the second vector
     * @param vy2
     *            the y-coordinate of the second vector
     * @return the cross product of the vectors given by their coordinates.
     */
    public static double crossProduct(double vx1, double vy1, double vx2, double vy2)
    {
        return vx1 * vy2 - vx2 * vy1;
    }
    
    
	// ===================================================================
	// class variables

	/** x coordinate of the vector */
	final double x;

	/** y coordinate of the vector */
	final double y;

	
	// ===================================================================
	// constructors

	/** Empty constructor, resulting in a null vector with coordinates (0,0). */
	public Vector2D()
	{
		this(0, 0);
	}

	/**
	 * New Vector2D given initialized with its coordinates.
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
	 * Applies <em> rotations</em> by 90 degrees in counter-clockwise
	 * orientation.
	 * 
	 * @param n
	 *            the number of 90-degrees rotations to apply.
	 * @return the rotated vector.
	 */
    public Vector2D rotate90(int n)
    {
    	n = ((n % 4) + 4) % 4;
    	switch(n)
    	{
    	case 0: return this;
    	case 1: return new Vector2D(-y,  x);
    	case 2: return new Vector2D(-x, -y);
    	case 3: return new Vector2D( y, -x);
    	default: return this; // should never happen...
    	}
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

    // ===================================================================
    // Accessors
    
    /**
     * @return the x coordinate of this vector
     */
    public double x()
    {
        return x;
    }

    /**
     * @return the y coordinate of this vector
     */
    public double y()
    {
        return y;
    }

    // ===================================================================
    // Implementation of the Numeric interface
    
    @Override
    public Vector2D one()
    {
        return new Vector2D(1, 0);
    }

    @Override
    public Vector2D zero()
    {
        return new Vector2D(0, 0);
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
     * Returns the opposite vector v2 of this, such that the sum of this and v2
     * equals the null vector.
     * 
     * @return the vector opposite to <code>this</code>.
     */
    @Override
    public Vector2D opposite()
    {
        return new Vector2D(-x, -y); 
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
     * Divides the vector by a scalar amount. Inner fields are not
     * 
     * @param k
     *            the scale factor
     * @return the scaled vector
     */
    @Override
    public Vector2D divideBy(double k)
    {
        return new Vector2D(this.x / k, this.y / k);
    }

    // ===================================================================
    // Geometry methods
    
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

    public boolean almostEquals(Vector2D vect, double eps)
    {
        if (Math.abs(vect.x - x) > eps) return false;
        if (Math.abs(vect.y - y) > eps) return false;
        return true;
    }
    
    
    public Vector2D duplicate()
    {
        return new Vector2D(x, y);
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


    // ===================================================================
    // Override Object interface

    @Override
    public String toString()
    {
        return String.format(Locale.ENGLISH, "Point2D(%g,%g)", this.x, this.y);
    }

}
