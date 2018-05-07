/**
 * 
 */
package net.sci.geom.geom3d.transform;

import net.sci.geom.geom3d.Point3D;
import net.sci.geom.geom3d.Vector3D;

/**
 * @author dlegland
 *
 */
public interface AffineTransform3D extends Transform3D
{
	// ===================================================================
	// static methods

	/**
	 * Creates a translation by the given vector.
	 * 
	 * @param vect
	 *            the vector of the translation transform
	 * @return a new instance of AffineTransform3D representing a translation
	 */
	public static AffineTransform3D createTranslation(Vector3D vect)
	{
		return new MatrixAffineTransform3D(1, 0, 0, vect.getX(), 0, 1, 0, vect.getY(), 0, 0, 1, vect.getZ());
	}

	/**
	 * Creates a translation by the given vector.
	 * 
	 * @param dx
	 *            the x-component of the translation transform
     * @param dy
     *            the y-component of the translation transform
     * @param dz
     *            the z-component of the translation transform
	 * @return a new instance of AffineTransform3D representing a translation
	 */
	public static AffineTransform3D createTranslation(double dx, double dy, double dz)
	{
        return new MatrixAffineTransform3D(1, 0, 0, dx, 0, 1, 0, dy, 0, 0, 1, dz);
	}

	/**
	 * Creates a scaling by the given coefficients, centered on the origin.
	 * 
	 * @param sx
	 *            the scaling along the x direction
     * @param sy
     *            the scaling along the y direction
     * @param sz
     *            the scaling along the z direction
	 * @return a new instance of AffineTransform3D representing a translation
	 */
	public static AffineTransform3D createScaling(double sx, double sy, double sz)
	{
		return new MatrixAffineTransform3D(sx, 0, 0, 0,  0, sy, 0, 0,   0, 0, sz, 0);
	}

	/**
	 * Creates a scaling by the given coefficients, centered on the given point.
	 * 
	 * @param center
	 * 			  the center of the scaling
	 * @param sx
	 *            the scaling along the X direction
     * @param sy
     *            the scaling along the Y direction
     * @param sz
     *            the scaling along the Z direction
	 * @return a new instance of AffineTransform3D representing a centered scaling
	 */
	public static AffineTransform3D createScaling(Point3D center, double sx,
			double sy, double sz)
	{
		return new MatrixAffineTransform3D(
				sx, 0, 0, (1 - sx) * center.getX(), 
                0, sy, 0, (1 - sy) * center.getY(),
                0, 0, sz, (1 - sz) * center.getZ());
	}

	/**
     * Creates a rotation around the X axis.
     * 
     * @param theta
     *            the angle of rotation, in radians
     * @return a new instance of AffineTransform3D representing the rotation
     */
    public static AffineTransform3D createRotationOx(double theta)
    {
        double cot = Math.cos(theta);
        double sit = Math.sin(theta);
        return new MatrixAffineTransform3D(1, 0, 0, 0, 0, cot, -sit, 0, 0, sit, cot,
                0);
    }

    /**
     * Creates a rotation around the Y axis.
     * 
     * @param theta
     *            the angle of rotation, in radians
     * @return a new instance of AffineTransform3D representing the rotation
     */
    public static AffineTransform3D createRotationOy(double theta)
    {
        double cot = Math.cos(theta);
        double sit = Math.sin(theta);
        return new MatrixAffineTransform3D(cot, 0, sit, 0, 0, 1, 0, 0, -sit, 0, cot,
                0);
    }

    /**
     * Creates a rotation around the Z axis.
     * 
     * @param theta
     *            the angle of rotation, in radians
     * @return a new instance of AffineTransform3D representing the rotation
     */
    public static AffineTransform3D createRotationOz(double theta)
    {
        double cot = Math.cos(theta);
        double sit = Math.sin(theta);
        return new MatrixAffineTransform3D(cot, -sit, 0, 0, sit, cot, 0, 0, 0, 0, 1,
                0);
    }


	// ===================================================================
	// Interface declaration

	/**
	 * @return the affine matrix of the coefficients corresponding to this transform 
	 */
	public double[][] getMatrix();

	public AffineTransform3D invert();
	
	/**
	 * Applies this transformation to the given point.
	 * 
	 * @param point
	 *            the point to transform
	 * @return the transformed point
	 */
	public default Point3D transform(Point3D point)
	{
		double[][] mat = this.getMatrix();
		double x = point.getX();
        double y = point.getY();
        double z = point.getZ();
		
        double xt = x * mat[0][0] + y * mat[0][1] + z * mat[0][2] +  mat[0][3]; 
        double yt = x * mat[1][0] + y * mat[1][1] + z * mat[1][2] +  mat[1][3]; 
        double zt = x * mat[2][0] + y * mat[2][1] + z * mat[2][2] +  mat[2][3]; 
		
		return new Point3D(xt, yt, zt);
	}
	
	/**
	 * Transforms a vector, by using only the linear part of this transform.
	 * 
	 * @param v
	 *            the vector to transform
	 * @return the transformed vector
	 */
	public default Vector3D transform(Vector3D v)
	{
		double vx = v.getX();
        double vy = v.getY();
        double vz = v.getZ();
		double[][] mat = this.getMatrix();
		return new Vector3D(
				vx * mat[0][0] + vy * mat[0][1] + vz * mat[0][2], 
                vx * mat[1][0] + vy * mat[1][1] + vz * mat[1][2], 
                vx * mat[2][0] + vy * mat[2][1] + vz * mat[2][2]);
	}

}
