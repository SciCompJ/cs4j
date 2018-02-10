/**
 * 
 */
package net.sci.geom.geom3d.transform;

import net.sci.geom.geom2d.Vector2D;
import net.sci.geom.geom3d.Point3D;

/**
 * Concrete implementation of a 3D affine transform, that stores the twelve
 * coefficients.
 * 
 * @author dlegland
 *
 */
public class MatrixAffineTransform3D implements AffineTransform3D
{
	// ===================================================================
	// class members

	// coefficients for x coordinate.
	protected double m00, m01, m02, m03;

	// coefficients for y coordinate.
	protected double m10, m11, m12, m13;

    // coefficients for y coordinate.
    protected double m20, m21, m22, m23;
	
    
	// ===================================================================
	// Constructors

	/**
	 * Empty constructor, that creates an instance of the identity transform.
	 */
	public MatrixAffineTransform3D()
	{
		m00 = 1;
		m01 = 0;
        m02 = 0;
        m03 = 0;
        m10 = 0;
        m11 = 1;
        m12 = 0;
        m13 = 0;
        m20 = 0;
        m21 = 0;
        m22 = 1;
        m23 = 0;
	}

	public MatrixAffineTransform3D(
            double xx, double yx, double zx, double tx, 
            double xy, double yy, double zy, double ty, 
            double xz, double yz, double zz, double tz)
	{
		m00 = xx;
		m01 = yx;
        m02 = zx;
        m03 = tx;
        m10 = xy;
        m11 = yy;
        m12 = zy;
        m13 = ty;
        m20 = xz;
        m21 = yz;
        m22 = zz;
        m23 = tz;
	}


	// ===================================================================
	// general methods

//	/**
//	 * Returns the affine transform created by applying first the affine
//	 * transform given by <code>that</code>, then this affine transform.
//	 * 
//	 * @param that
//	 *            the transform to apply first
//	 * @return the composition this * that
//	 */
//	public MatrixAffineTransform2d concatenate(MatrixAffineTransform2d that)
//	{
//		double n00 = this.m00 * that.m00 + this.m01 * that.m10;
//		double n01 = this.m00 * that.m01 + this.m01 * that.m11;
//		double n02 = this.m00 * that.m02 + this.m01 * that.m12 + this.m02;
//		double n10 = this.m10 * that.m00 + this.m11 * that.m10;
//		double n11 = this.m10 * that.m01 + this.m11 * that.m11;
//		double n12 = this.m10 * that.m02 + this.m11 * that.m12 + this.m12;
//		return new MatrixAffineTransform2d(n00, n01, n02, n10, n11, n12);
//	}
//
//	/**
//	 * Return the affine transform created by applying first this affine
//	 * transform, then the affine transform given by <code>that</code>.
//	 * 
//	 * @param that
//	 *            the transform to apply in a second step
//	 * @return the composition that * this
//	 */
//	public MatrixAffineTransform2d preConcatenate(MatrixAffineTransform2d that)
//	{
//		return new MatrixAffineTransform2d(that.m00 * this.m00 + that.m01 * this.m10,
//				that.m00 * this.m01 + that.m01 * this.m11, that.m00 * this.m02
//						+ that.m01 * this.m12 + that.m02, that.m10 * this.m00
//						+ that.m11 * this.m10, that.m10 * this.m01 + that.m11
//						* this.m11, that.m10 * this.m02 + that.m11 * this.m12
//						+ that.m12);
//	}

	public Point3D transform(Point3D p)
	{
		double x = p.getX();
        double y = p.getY();
        double z = p.getZ();
		return new Point3D(
                x * m00 + y * m01 + z * m02 + m03, 
                x * m10 + y * m11 + z * m12 + m13, 
                x * m20 + y * m21 + z * m22 + m23);
	}

//	public Point2d[] transform(Point2d[] src, Point2d[] dst)
//	{
//		if (dst == null)
//			dst = new Point2d[src.length];
//
//		double x, y;
//		for (int i = 0; i < src.length; i++)
//		{
//			x = src[i].getX();
//			y = src[i].getY();
//			dst[i] = new Point2d(x * m00 + y * m01 + m02, x * m10 + y * m11
//					+ m12);
//		}
//		return dst;
//	}

//	/**
//	 * Transforms each point in the collection and returns a new collection
//	 * containing the transformed points.
//	 */
//	public Collection<Point2d> transform(Collection<Point2d> points)
//	{
//		// Allocate memory
//		ArrayList<Point2d> res = new ArrayList<Point2d>(points.size());
//
//		// transform each point in the input image
//		double x, y;
//		for (Point2d p : points)
//		{
//			x = p.getX();
//			y = p.getY();
//			res.add(new Point2d(x * m00 + y * m01 + m02, x * m10 + y * m11 + m12));
//		}
//		return res;
//	}

	/**
	 * Transforms a vector, by using only the linear part of this transform.
	 * 
	 * @param v
	 *            the vector to transform
	 * @return the transformed vector
	 */
	public Vector2D transform(Vector2D v)
	{
		double vx = v.getX();
		double vy = v.getY();
		return new Vector2D(
				vx * m00 + vy * m01, 
				vx * m10 + vy * m11);
	}

	/**
	 * Returns the inverse transform. If the transform is not invertible, throws
	 * a new NonInvertibleTransform2DException.
	 */
	public MatrixAffineTransform3D invert()
	{
        double det = this.determinant();

        // check invertibility
        if (Math.abs(det) < 1e-12)
            throw new RuntimeException("Non-invertible matrix");
        
        return new MatrixAffineTransform3D(
                (m11 * m22 - m21 * m12) / det,
                (m21 * m02 - m01 * m22) / det,
                (m01 * m12 - m11 * m02) / det,
                (m01 * (m22 * m13 - m12 * m23) + m02 * (m11 * m23 - m21 * m13) 
                        - m03 * (m11 * m22 - m21 * m12)) / det, 
                (m20 * m12 - m10 * m22) / det, 
                (m00 * m22 - m20 * m02) / det, 
                (m10 * m02 - m00 * m12) / det, 
                (m00 * (m12 * m23 - m22 * m13) - m02 * (m10 * m23 - m20 * m13) 
                        + m03 * (m10 * m22 - m20 * m12)) / det, 
                (m10 * m21 - m20 * m11) / det, 
                (m20 * m01 - m00 * m21) / det,
                (m00 * m11 - m10 * m01) / det, 
                (m00 * (m21 * m13 - m11 * m23) + m01 * (m10 * m23 - m20 * m13) 
                        - m03 * (m10 * m21 - m20 * m11))    / det);
	}


    /**
     * Computes the determinant of this affine transform. Can be zero.
     * 
     * @return the determinant of the transform.
     */
    private double determinant()
    {
        return m00 * (m11 * m22 - m12 * m21) - m01 * (m10 * m22 - m20 * m12)
                + m02 * (m10 * m21 - m20 * m11);
    }

//	@Override
//	public boolean equals(Object obj)
//	{
//		if (this == obj)
//			return true;
//
//		if (!(obj instanceof MatrixAffineTransform2d))
//			return false;
//
//		MatrixAffineTransform2d that = (MatrixAffineTransform2d) obj;
//
//		if (!math.jg.util.EqualUtils.areEqual(this.m00, that.m00))
//			return false;
//		if (!EqualUtils.areEqual(this.m01, that.m01))
//			return false;
//		if (!EqualUtils.areEqual(this.m02, that.m02))
//			return false;
//		if (!EqualUtils.areEqual(this.m00, that.m00))
//			return false;
//		if (!EqualUtils.areEqual(this.m01, that.m01))
//			return false;
//		if (!EqualUtils.areEqual(this.m02, that.m02))
//			return false;
//
//		return true;
//	}

    @Override
    public double[][] getMatrix()
    {
        return new double[][] {
                { this.m00, this.m01, this.m02, this.m03 },
                { this.m10, this.m11, this.m12, this.m13 },
                { this.m20, this.m21, this.m22, this.m23 },
                { 0, 0, 0, 1 } };
    }
}
