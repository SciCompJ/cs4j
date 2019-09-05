/**
 * 
 */
package net.sci.geom.geom2d;

/**
 * Concrete implementation of an affine transform, that stores the six coefficients.
 *  
 * @author dlegland
 *
 */
public class MatrixAffineTransform2D implements AffineTransform2D
{
	// ===================================================================
	// class members

	// coefficients for x coordinate.
	protected double m00, m01, m02;

	// coefficients for y coordinate.
	protected double m10, m11, m12;

	
	// ===================================================================
	// Constructors

	/**
	 * Empty constructor, that creates an instance of the identity transform.
	 */
	public MatrixAffineTransform2D()
	{
		m00 = 1;
		m01 = 0;
		m02 = 0;
		m10 = 0;
		m11 = 1;
		m12 = 0;
	}

	public MatrixAffineTransform2D(double xx, double yx, double tx, double xy,
			double yy, double ty)
	{
		m00 = xx;
		m01 = yx;
		m02 = tx;
		m10 = xy;
		m11 = yy;
		m12 = ty;
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

	public Point2D transform(Point2D p)
	{
		double x = p.getX();
		double y = p.getY();
		return new Point2D(
				x * m00 + y * m01 + m02, 
				x * m10 + y * m11 + m12);
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
	public MatrixAffineTransform2D inverse()
	{
		// compute determinant
		double det = m00 * m11 - m10 * m01;

		// check invertibility
		if (Math.abs(det) < 1e-12)
			throw new RuntimeException("Non-invertible matrix");

		// create matrix
		return new MatrixAffineTransform2D(
				m11 / det, -m01 / det, (m01 * m12 - m02 * m11) / det, 
				-m10 / det, m00 / det, (m02 * m10 - m00 * m12) / det);
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
	public double[][] affineMatrix()
	{
		return new double[][] {
				{this.m00, this.m01, this.m02}, 
				{this.m10, this.m11, this.m12}, 
				{0, 0, 1}
		};
	}
}
