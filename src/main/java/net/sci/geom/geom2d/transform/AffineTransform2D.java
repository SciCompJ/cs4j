/**
 * 
 */
package net.sci.geom.geom2d.transform;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.Vector2D;

/**
 * @author dlegland
 *
 */
public interface AffineTransform2D extends Transform2D
{
	// ===================================================================
	// static methods

	/**
	 * Creates a translation by the given vector.
	 * 
	 * @param vect
	 *            the vector of the translation transform
	 * @return a new instance of AffineTransform2D representing a translation
	 */
	public static AffineTransform2D createTranslation(Vector2D vect)
	{
		return new MatrixAffineTransform2D(1, 0, vect.getX(), 0, 1, vect.getY());
	}

	/**
	 * Creates a translation by the given vector.
	 * 
	 * @param dx
	 *            the x-component of the translation transform
	 * @param dy
	 *            the y-component of the translation transform
	 * @return a new instance of AffineTransform2D representing a translation
	 */
	public static AffineTransform2D createTranslation(double dx, double dy)
	{
		return new MatrixAffineTransform2D(1, 0, dx, 0, 1, dy);
	}

	/**
	 * Creates a scaling by the given coefficients, centered on the origin.
	 * 
	 * @param sx
	 *            the scaling along the x direction
	 * @param sy
	 *            the scaling along the y direction
	 * @return a new instance of AffineTransform2D representing a translation
	 */
	public static AffineTransform2D createScaling(double sx, double sy)
	{
		return new MatrixAffineTransform2D(sx, 0, 0, 0, sy, 0);
	}

	/**
	 * Creates a scaling by the given coefficients, centered on the point given
	 * by (x0,y0).
	 * 
	 * @param center
	 * 			  the center of the scaling
	 * @param sx
	 *            the scaling along the x direction
	 * @param sy
	 *            the scaling along the y direction
	 * @return a new instance of AffineTransform2D representing a centered scaling
	 */
	public static AffineTransform2D createScaling(Point2D center, double sx,
			double sy)
	{
		return new MatrixAffineTransform2D(
				sx, 0, (1 - sx) * center.getX(), 
				0, sy, (1 - sy) * center.getY());
	}

	/**
	 * Creates a rotation around the origin, with angle in radians.
	 * 
	 * @param angle
	 *            the angle of the rotation, in radians
	 * @return a new instance of AffineTransform2D representing a centered rotation
	 */
	public static AffineTransform2D createRotation(double angle)
	{
		return createRotation(0, 0, angle);
	}

	/**
	 * Creates a rotation around the specified point, with angle in radians.
	 * 
	 * @param center
	 * 			  the center of the rotation
	 * @param angle
	 *            the angle of the rotation, in radians
	 * @return a new instance of AffineTransform2D representing a centered rotation
	 */
	public static AffineTransform2D createRotation(Point2D center, double angle)
	{
		return createRotation(center.getX(), center.getY(), angle);
	}

	/**
	 * Creates a rotation around the specified point, with angle in radians.
	 * 
	 * @param cx
	 * 			  the x-coordinate of the rotation center
	 * @param cy
	 * 			  the y-coordinate of the rotation center
	 * @param angle
	 *            the angle of the rotation, in radians
	 * @return a new instance of AffineTransform2D representing a centered rotation
	 */
	public static AffineTransform2D createRotation(double cx, double cy,
			double angle)
	{
		// pre-compute trigonometric functions
		double cot = cos(angle);
		double sit = sin(angle);

		// init coef of the new AffineTransform.
		return new MatrixAffineTransform2D(
				cot, -sit, (1 - cot) * cx + sit * cy, 
				sit,  cot, (1 - cot) * cy - sit * cx);
	}

	/**
	 * Creates a rotation composed of the given number of rotations by 90
	 * degrees around the origin.
	 * 
	 * @param numQuadrant
	 *            the quadrant number
	 * @return a new instance of AffineTransform representing a rotation by a
	 *         multiple of 90 degrees
	 */
	public static AffineTransform2D createQuadrantRotation(int numQuadrant)
	{
		int n = ((numQuadrant % 4) + 4) % 4;
		switch (n) {
		case 0:
			return new MatrixAffineTransform2D(1, 0, 0, 0, 1, 0);
		case 1:
			return new MatrixAffineTransform2D(0, -1, 0, 1, 0, 0);
		case 2:
			return new MatrixAffineTransform2D(-1, 0, 0, 0, -1, 0);
		case 3:
			return new MatrixAffineTransform2D(0, 1, 0, -1, 0, 0);
		default:
			throw new RuntimeException("Error in integer rounding...");
		}
	}

	/**
	 * Creates a rotation composed of the given number of rotations by 90
	 * degrees around the given point.
	 * 
	 * @param center
	 *            the rotation center
	 * @param numQuadrant
	 *            the quadrant number
	 * @return a new instance of AffineTransform representing a rotation by a
	 *         multiple of 90 degrees
	 */
	public static MatrixAffineTransform2D createQuadrantRotation(Point2D center,
			int numQuadrant)
	{
		return createQuadrantRotation(center.getX(), center.getY(), numQuadrant);
	}

	/**
	 * Creates a rotation composed of the given number of rotations by 90
	 * degrees around the point given by (x0,y0).
	 * 
	 * @param x0
	 *            the x-coordinate of the rotation center
	 * @param y0
	 *            the y-coordinate of the rotation center
	 * @param numQuadrant
	 *            the quadrant number
	 * @return a new instance of AffineTransform representing a rotation by a
	 *         multiple of 90 degrees
	 */
	public static MatrixAffineTransform2D createQuadrantRotation(double x0,
			double y0, int numQuadrant)
	{
		int n = ((numQuadrant % 4) + 4) % 4;
		int m00 = 0, m01 = 0, m10 = 0, m11 = 0;
		
		switch (n) {
		case 0:
			m00 = 1; m11 = 1; break;
		case 1:
			m01 = -1; m10 = 1; break;
		case 2:
			m00 = -1; m11 = -1; break;
		case 3:
			m01 = 1; m10 = -1; break;
		default:
			throw new RuntimeException("Error in integer rounding...");
		}
		
		double m02 = (1 - m00) * x0 - m01 * y0;
		double m12 = (1 - m11) * y0 - m10 * x0;

		return new MatrixAffineTransform2D(m00, m01, m02, m10, m11, m12);
	}


//	/**
//	 * Creates a reflection by the given line. The resulting transform is
//	 * indirect.
//	 */
//	public static MatrixAffineTransform2d createLineReflection(
//			math.jg.geom2d.line.LinearShape2D line)
//	{
//		// origin and direction of line
//		Point2d origin = line.getOrigin();
//		Vector2d vector = line.getDirection();
//
//		// extract direction vector coordinates
//		double dx = vector.getX();
//		double dy = vector.getY();
//		double x0 = origin.getX();
//		double y0 = origin.getY();
//
//		// pre-compute some terms
//		double dx2 = dx * dx;
//		double dy2 = dy * dy;
//		double dxy = dx * dy;
//		double delta = dx2 + dy2;
//
//		// creates the new transform
//		return new MatrixAffineTransform2d((dx2 - dy2) / delta, 2 * dxy / delta, 2
//				* (dy2 * x0 - dxy * y0) / delta, 2 * dxy / delta, (dy2 - dx2)
//				/ delta, 2 * (dx2 * y0 - dxy * x0) / delta);
//	}

	/**
	 * Returns a center reflection around a point. The resulting transform is
	 * equivalent to a rotation by 180 around this point.
	 * 
	 * @param center
	 *            the center of the reflection
	 * @return an instance of MatrixAffineTransform2d representing a point reflection
	 */
	public static AffineTransform2D createPointReflection(Point2D center)
	{
		return createScaling(center, -1, -1);
	}

	// ===================================================================
	// specific methods

	/**
	 * @return the affine matrix of the coefficients corresponding to this transform 
	 */
	public double[][] getMatrix();

	public AffineTransform2D invert();
	
    // ===================================================================
    // default methods

	/**
     * Returns the affine transform created by applying first the affine
     * transform given by <code>that</code>, then this affine transform. 
     * This is the equivalent method of the 'concatenate' method in
     * java.awt.geom.AffineTransform.
     * 
     * @param that
     *            the transform to apply first
     * @return the composition this * that
     */
    public default AffineTransform2D concatenate(AffineTransform2D that)
    {
        double[][] m1 = this.getMatrix();
        double[][] m2 = that.getMatrix();
        double n00 = m1[0][0] * m2[0][0] + m1[0][1] * m2[1][0];
        double n01 = m1[0][0] * m2[0][1] + m1[0][1] * m2[1][1];
        double n02 = m1[0][0] * m2[0][2] + m1[0][1] * m2[1][2] + m1[0][2];
        double n10 = m1[1][0] * m2[0][0] + m1[1][1] * m2[1][0];
        double n11 = m1[1][0] * m2[0][1] + m1[1][1] * m2[1][1];
        double n12 = m1[1][0] * m2[0][2] + m1[1][1] * m2[1][2] + m1[1][2];
        return new MatrixAffineTransform2D(n00, n01, n02, n10, n11, n12);
    }

    /**
     * Returns the affine transform created by applying first this affine
     * transform, then the affine transform given by <code>that</code>. This the
     * equivalent method of the 'preConcatenate' method in
     * java.awt.geom.AffineTransform. <code><pre>
     * shape = shape.transform(T1.preConcatenate(T2).preConcatenate(T3));
     * </pre></code> is equivalent to the sequence: <code><pre>
     * shape = shape.transform(T1);
     * shape = shape.transform(T2);
     * shape = shape.transform(T3);
     * </pre></code>
     * 
     * @param that
     *            the transform to apply in a second step
     * @return the composition that * this
     */
    public default AffineTransform2D preConcatenate(AffineTransform2D that) 
    {
        double[][] m1 = that.getMatrix();
        double[][] m2 = this.getMatrix();
        double n00 = m1[0][0] * m2[0][0] + m1[0][1] * m2[1][0];
        double n01 = m1[0][0] * m2[0][1] + m1[0][1] * m2[1][1];
        double n02 = m1[0][0] * m2[0][2] + m1[0][1] * m2[1][2] + m1[0][2];
        double n10 = m1[1][0] * m2[0][0] + m1[1][1] * m2[1][0];
        double n11 = m1[1][0] * m2[0][1] + m1[1][1] * m2[1][1];
        double n12 = m1[1][0] * m2[0][2] + m1[1][1] * m2[1][2] + m1[1][2];
        return new MatrixAffineTransform2D(n00, n01, n02, n10, n11, n12);
    }


	/**
	 * Applies this transformation to the given point.
	 * 
	 * @param point
	 *            the point to transform
	 * @return the transformed point
	 */
	public default Point2D transform(Point2D point)
	{
		double[][] mat = this.getMatrix();
		double x = point.getX();
		double y = point.getY();
		
		double xt = x * mat[0][0] + y * mat[0][1] + mat[0][2]; 
		double yt = x * mat[1][0] + y * mat[1][1] + mat[1][2];
		
		return new Point2D(xt, yt);
	}
	
	/**
	 * Transforms a vector, by using only the linear part of this transform.
	 * 
	 * @param v
	 *            the vector to transform
	 * @return the transformed vector
	 */
	public default Vector2D transform(Vector2D v)
	{
		double vx = v.getX();
		double vy = v.getY();
		double[][] mat = this.getMatrix();
		return new Vector2D(
				vx * mat[0][0] + vy * mat[0][1], 
				vx * mat[1][0] + vy * mat[1][1]);
	}

}
