/**
 * 
 */
package net.sci.geom.geom3d;

import static java.lang.Math.abs;

import java.util.Locale;

import net.sci.array.Dimensional;
import net.sci.array.numeric.Numeric;

/**
 * Implementation of a three-dimensional vector.
 */
public class Vector3D implements Dimensional, Numeric<Vector3D>
{
    // ===================================================================
    // Private constants

    private final static double DEFAULT_TOL = 1e-12;
    
    
    // ===================================================================
    // Public constants

    /**
     * The first (normalized) spanning vector of the 3-dimensional space,
     * corresponding to the (1,0,0) vector. Usually associated to the "x"
     * direction.
     */
    public final static Vector3D E_1 = new Vector3D(1, 0, 0);
    
    /**
     * The second (normalized) spanning vector of the 3-dimensional space,
     * corresponding to the (0,1,0) vector. Usually associated to the "y"
     * direction.
     */
    public final static Vector3D E_2 = new Vector3D(0, 1, 0);

    /**
     * The third (normalized) spanning vector of the 3-dimensional space,
     * corresponding to the (0,0,1) vector. Usually associated to the "z"
     * direction.
     */
    public final static Vector3D E_3 = new Vector3D(0, 0, 1);
    
    
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
     * Get the dot product of two vectors. If the two vectors are given by
     * {@code v1 = (vx1,vy1,vz1)} and {@code v2 = (vx2,vy2,vz2)}, then their dot product
     * is given by:
     * <p>
     * {@code vx1 * vx2 + vy2 * vy2 + vz1 * vz2}
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
     * Get the dot product of two vectors given by their coordinates. This
     * method is a low-level version of the method with same name that accepts
     * two vectors as input arguments. If the two vectors are given by
     * {@code v1 = (vx1,vy1,vz1)} and {@code v2 = (vx2,vy2,vz2)}, then their dot
     * product is given by:
     * <p>
     * {@code vx1 * vx2 + vy2 * vy2 + vz1 * vz2}
     * <p>
     * Dot product is zero if the vectors defined by the 2 vectors are
     * orthogonal. It is positive if vectors are in the same direction, and
     * negative if they are in opposite direction.
     * 
     * @see #dotProduct(Vector3D, Vector3D)
     * 
     * @param vx1
     *            the x-coordinate of the first vector
     * @param vy1
     *            the y-coordinate of the first vector
     * @param vz1
     *            the z-coordinate of the first vector
     * @param vx2
     *            the x-coordinate of the second vector
     * @param vy2
     *            the y-coordinate of the second vector
     * @param vz2
     *            the z-coordinate of the second vector
     * @return the dot product of the two vectors
     */
    public static double dotProduct(double vx1, double vy1, double vz1, double vx2, double vy2, double vz2)
    {
        return vx1 * vx2 + vy1 * vy2 + vz1 * vz2;
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
    
    /**
     * Computes the 3D cross product of two vectors given by their coordinates. This
     * method is a low-level version of the method with same name that accepts
     * two vectors as input arguments.
     * 
     * Cross product is zero for colinear vectors. 
     * 
     * @see #crossProduct(Vector3D, Vector3D)
     * 
     * @param vx1
     *            the x-coordinate of the first vector
     * @param vy1
     *            the y-coordinate of the first vector
     * @param vz1
     *            the z-coordinate of the first vector
     * @param vx2
     *            the x-coordinate of the second vector
     * @param vy2
     *            the y-coordinate of the second vector
     * @param vz2
     *            the z-coordinate of the second vector
     * @return the cross product of the two vectors
     */
    public static Vector3D crossProduct(double vx1, double vy1, double vz1, double vx2, double vy2, double vz2)
    {
        return new Vector3D(
                vy1 * vz2 - vz1 * vy2, 
                vz1 * vx2 - vx1 * vz2, 
                vx1 * vy2 - vy1 * vx2);
    }
    
    /**
     * Computes the angle between two 3D vectors. The result is given between 0
     * and PI.  
     *
     * @param v1 the first vector
     * @param v2 the second vector
     * @return the angle between the two vectors, in radians
     */
    public static final double angle(Vector3D v1, Vector3D v2)
    {
        // compute angle using arc-tangent to get better precision for angles
        // near zero, see the discussion in: 
        // http://www.mathworks.com/matlabcentral/newsreader/view_thread/151925#381952
        double norm = Vector3D.crossProduct(v1, v2).norm();
        double denom = Vector3D.dotProduct(v1, v2);
        return Math.atan2(norm, denom);
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


	/**
	 * @return the z coordinate of this vector
	 */
	public double z()
	{
		return z;
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
     * 
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

    
    // ===================================================================
    // Implementation of the Numeric interface
    
    @Override
    public Vector3D one()
    {
        return E_1;
    }

    @Override
    public Vector3D zero()
    {
        return new Vector3D(0, 0, 0);
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
     * Returns the opposite vector v2 of this, such that the sum of this and v2
     * equals the null vector.
     * 
     * @return the vector opposite to <code>this</code>.
     */
    @Override
    public Vector3D opposite()
    {
        return new Vector3D(-x, -y, -z); 
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
     * Divides the vector by a scalar amount. Inner fields are not
     * 
     * @param k
     *            the scale factor
     * @return the scaled vector
     */
    @Override
    public Vector3D divideBy(double k)
    {
        return new Vector3D(this.x / k, this.y / k, this.z / k);
    }
    

    // ===================================================================
    // General geometry methods

    public Vector3D transform(AffineTransform3D trans)
    {
        return trans.transform(this);
    }

    public Vector3D duplicate()
    {
        return new Vector3D(x, y, z);
    }
    
    public boolean almostEquals(Vector3D that, double tol)
    {
        if (Math.abs(this.x - that.x) > tol) return false;
        if (Math.abs(this.y - that.y) > tol) return false;
        if (Math.abs(this.z - that.z) > tol) return false;
        return true;
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
	
    // ===================================================================
    // Override Object's methods

    @Override
    public String toString()
    {
        return String.format(Locale.ENGLISH, "Vector3D(%f, %f, %f)", x, y, z);
    }
}
