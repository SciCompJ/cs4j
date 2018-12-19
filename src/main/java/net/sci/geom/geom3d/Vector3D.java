/**
 * 
 */
package net.sci.geom.geom3d;

import static java.lang.Math.abs;
import net.sci.array.Dimensional;
import net.sci.geom.geom3d.transform.AffineTransform3D;

/**
 * @author dlegland
 *
 */
public class Vector3D implements Dimensional
{
    // ===================================================================
    // constants

    private final static double DEFAULT_TOL = 1e-12;
    
    
    // ===================================================================
    // Static methods

    public static boolean isParallel(Vector3D v1, Vector3D v2)
    {
        return isParallel(v1, v2, DEFAULT_TOL);
    }
    
    public static boolean isParallel(Vector3D v1, Vector3D v2, double tol)
    {
        v1 = v1.normalize();
        v2 = v2.normalize();
        return crossProduct(v1, v2).norm() < tol;
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
    public static boolean isPerpendicular(Vector3D v1, Vector3D v2)
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
    public static boolean isPerpendicular(Vector3D v1, Vector3D v2, double tol)
    {
        v1 = v1.normalize();
        v2 = v2.normalize();
        return abs(v1.x * v2.x + v1.y * v2.y + v1.z * v2.z) < tol;
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
    public static double dotProduct(Vector3D v1, Vector3D v2)
    {
        return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
    }

    /**
     * Computes the 3D cross product of the two vectors.
     * 
     * Cross product is zero for colinear vectors. 
     * 
     * @param v1
     *            the first vector
     * @param v2
     *            the second vector
     * @return the cross product of <code>v1</code> and <code>v2</code>.
     */
    public static Vector3D crossProduct(Vector3D v1, Vector3D v2)
    {
        return new Vector3D(
                v1.y * v2.z - v1.z * v2.y, 
                v1.z * v2.x - v1.x * v2.z, 
                v1.x * v2.y - v1.y * v2.x);
    }
    

    // ===================================================================
	// class variables

	/** x coordinate of the vector */
	final double x;

	/** y coordinate of the vector */
	final double y;

	/** z coordinate of the vector */
	final double z;

	
	// ===================================================================
	// constructors

	/** Empty constructor, similar to Vector3d(0,0,0) */
	public Vector3D()
	{
		this(0, 0, 0);
	}

	/**
     * New Vector3d given by its coordinates
     * 
     * @param the
     *            x-coordinate of the vector
     * @param the
     *            y-coordinate of the vector
     * @param the
     *            z-coordinate of the vector
     */
	public Vector3D(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
     * Constructs a new vector with the same coordinates as the given point.
     * 
     * @param point
     *            the point used to initialize the new vector
     */
	public Vector3D(Point3D point)
	{
		this(point.x, point.y, point.z);
	}

	/**
     * Constructs a new vector between two points
     * 
     * @param p1
     *            the origin of the vector
     * @param p2
     *            the destination of the vector
     */
	public Vector3D(Point3D p1, Point3D p2)
	{
		this(p2.x - p1.x, p2.y - p1.y, p2.z - p1.z);
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
	 * @return the z coordinate of this vector
	 */
	public double getZ()
	{
		return z;
	}

	/**
     * Returns the sum of current vector with vector given as parameter. Inner
     * fields are not modified.
     * 
     * @param v
     *            the vector to add
     * @return the result of the addition of the two vectors
     */
	public Vector3D plus(Vector3D v)
	{
		return new Vector3D(this.x + v.x, this.y + v.y, this.z + v.z);
	}

	/**
	 * Returns the subtraction of current vector with vector given as parameter.
	 * Inner fields are not modified.
     * 
     * @param v
     *            the vector to subtract
     * @return the result of the subtraction of the two vectors
	 */
	public Vector3D minus(Vector3D v)
	{
		return new Vector3D(this.x - v.x, this.y - v.y, this.z - v.z);
	}

	/**
	 * Multiplies the vector by a scalar amount. Inner fields are not
	 * 
	 * @param k
	 *            the scale factor
	 * @return the scaled vector
	 */
	public Vector3D times(double k)
	{
		return new Vector3D(this.x * k, this.y * k, this.z * k);
	}

	/**
	 * Returns the opposite vector v2 of this, such that the sum of this and v2
	 * equals the null vector.
	 * 
	 * @return the vector opposite to <code>this</code>.
	 */
	public Vector3D opposite()
	{
		return new Vector3D(-x, -y, -z);
	}

	/**
	 * Computes the norm of the vector.
	 * 
	 * @return the norm of this vector
	 */
	public double norm()
	{
		return Math.hypot(Math.hypot(x, y), z);
	}

	/**
	 * Returns the normalized vector, with same direction but with norm equal to
	 * 1.
	 * 
	 * @return the normalized vector with same direction as this vector
	 */
	public Vector3D normalize()
	{
		double n = this.norm();
		return new Vector3D(x / n, y / n, z / n);
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
     *            the vector to process
     * @return the dot product of the two vectors
     */
	public double dotProduct(Vector3D v)
	{
		return x * v.x + y * v.y + z * v.z;
	}

    /**
     * Computes the cross product with vector <code>v</code>. 
     * @param v
     *            the vector to process
     * @return the cross product of the two vectors
     */
	public Vector3D crossProduct(Vector3D v)
	{
		return new Vector3D(
				this.y * v.z - this.z * v.y, 
				this.z * v.x - this.x * v.z, 
				this.x * v.y - this.y * v.x);
	}
	
	public Vector3D transform(AffineTransform3D trans)
	{
	    return trans.transform(this);
	}
	

    // ===================================================================
    // Implements Dimensional interface

    /**
     * Returns a dimensionality equals to 3.
     * 
     * @return the value 3
     */
    @Override
    public int dimensionality()
    {
        return 3;
    }
	
}
