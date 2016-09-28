/**
 * 
 */
package net.sci.geom.geom3d;



/**
 * @author dlegland
 *
 */
public class Vector3d
{
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
	public Vector3d()
	{
		this(0, 0, 0);
	}

	/** New Vector3d given by its coordinates */
	public Vector3d(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Constructs a new vector with the same coordinates as the given point.
	 */
	public Vector3d(Point3d point)
	{
		this(point.x, point.y, point.z);
	}

	/**
	 * Constructs a new vector between two points
	 */
	public Vector3d(Point3d p1, Point3d p2)
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
	 */
	public Vector3d add(Vector3d v)
	{
		return new Vector3d(this.x + v.x, this.y + v.y, this.z + v.z);
	}

	/**
	 * Returns the subtraction of current vector with vector given as parameter.
	 * Inner fields are not modified.
	 */
	public Vector3d subtract(Vector3d v)
	{
		return new Vector3d(this.x - v.x, this.y - v.y, this.z - v.z);
	}

	/**
	 * Multiplies the vector by a scalar amount. Inner fields are not
	 * 
	 * @param k
	 *            the scale factor
	 * @return the scaled vector
	 */
	public Vector3d multiply(double k)
	{
		return new Vector3d(this.x * k, this.y * k, this.z * k);
	}

	/**
	 * Returns the opposite vector v2 of this, such that the sum of this and v2
	 * equals the null vector.
	 * 
	 * @return the vector opposite to <code>this</code>.
	 */
	public Vector3d opposite()
	{
		return new Vector3d(-x, -y, -z);
	}

	/**
	 * Computes the norm of the vector
	 */
	public double norm()
	{
		return Math.hypot(Math.hypot(x, y), z);
	}

	/**
	 * Returns the normalized vector, with same direction but with norm equal to
	 * 1.
	 */
	public Vector3d normalize()
	{
		double n = this.norm();
		return new Vector3d(x / n, y / n, z / n);
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
	public double dotProduct(Vector3d v)
	{
		return x * v.x + y * v.y + z * v.z;
	}

	public Vector3d crossProduct(Vector3d v)
	{
		return new Vector3d(
				this.y * v.z - this.z * v.y, 
				this.z * v.x - this.x * v.z, 
				this.x * v.y - this.y * v.x);
	}
}
