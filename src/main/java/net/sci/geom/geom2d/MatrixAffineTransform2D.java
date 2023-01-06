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

	public Point2D transform(Point2D p)
	{
		double x = p.getX();
		double y = p.getY();
		return new Point2D(
				x * m00 + y * m01 + m02, 
				x * m10 + y * m11 + m12);
	}
	
    @Override
    public Point2D transform(double x, double y)
    {
        return new Point2D(
                x * m00 + y * m01 + m02, 
                x * m10 + y * m11 + m12);
    }


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
