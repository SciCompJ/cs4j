/**
 * 
 */
package net.sci.geom.geom2d;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * @author dlegland
 *
 */
public interface AffineTransform2d extends Transform2d
{
	// ===================================================================
	// static methods

	/**
	 * Creates a translation by the given vector.
	 */
	public static AffineTransform2d createTranslation(Vector2d vect)
	{
		return new MatrixAffineTransform2d(1, 0, vect.getX(), 0, 1, vect.getY());
	}

	/**
	 * Creates a translation by the given vector.
	 */
	public static AffineTransform2d createTranslation(double dx, double dy)
	{
		return new MatrixAffineTransform2d(1, 0, dx, 0, 1, dy);
	}

	/**
	 * Creates a scaling by the given coefficients, centered on the origin.
	 */
	public static AffineTransform2d createScaling(double sx, double sy)
	{
		return new MatrixAffineTransform2d(sx, 0, 0, 0, sy, 0);
	}

	/**
	 * Creates a scaling by the given coefficients, centered on the point given
	 * by (x0,y0).
	 */
	public static AffineTransform2d createScaling(Point2d center, double sx,
			double sy)
	{
		return new MatrixAffineTransform2d(
				sx, 0, (1 - sx) * center.getX(), 
				0, sy, (1 - sy) * center.getY());
	}

	/**
	 * Creates a rotation around the origin, with angle in radians.
	 */
	public static AffineTransform2d createRotation(double angle)
	{
		return createRotation(0, 0, angle);
	}

	/**
	 * Creates a rotation around the specified point, with angle in radians.
	 */
	public static AffineTransform2d createRotation(Point2d center, double angle)
	{
		return createRotation(center.getX(), center.getY(), angle);
	}

	/**
	 * Creates a rotation around the specified point, with angle in radians.
	 */
	public static AffineTransform2d createRotation(double cx, double cy,
			double angle)
	{
		// pre-compute trigonometric functions
		double cot = cos(angle);
		double sit = sin(angle);

		// init coef of the new AffineTransform.
		return new MatrixAffineTransform2d(
				cot, -sit, (1 - cot) * cx + sit * cy, 
				sit,  cot, (1 - cot) * cy - sit * cx);
	}

	/**
	 * Creates a rotation composed of the given number of rotations by 90
	 * degrees around the origin.
	 */
	public static MatrixAffineTransform2d createQuadrantRotation(int numQuadrant)
	{
		int n = ((numQuadrant % 4) + 4) % 4;
		switch (n) {
		case 0:
			return new MatrixAffineTransform2d(1, 0, 0, 0, 1, 0);
		case 1:
			return new MatrixAffineTransform2d(0, -1, 0, 1, 0, 0);
		case 2:
			return new MatrixAffineTransform2d(-1, 0, 0, 0, -1, 0);
		case 3:
			return new MatrixAffineTransform2d(0, 1, 0, -1, 0, 0);
		default:
			throw new RuntimeException("Error in integer rounding...");
		}
	}

	/**
	 * Creates a rotation composed of the given number of rotations by 90
	 * degrees around the given point.
	 */
	public static MatrixAffineTransform2d createQuadrantRotation(Point2d center,
			int numQuadrant)
	{
		return createQuadrantRotation(center.getX(), center.getY(), numQuadrant);
	}

	/**
	 * Creates a rotation composed of the given number of rotations by 90
	 * degrees around the point given by (x0,y0).
	 */
	public static MatrixAffineTransform2d createQuadrantRotation(double x0,
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

		return new MatrixAffineTransform2d(m00, m01, m02, m10, m11, m12);
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
	public static AffineTransform2d createPointReflection(Point2d center)
	{
		return createScaling(center, -1, -1);
	}

	// ===================================================================
	// static methods

	public double[][] getMatrix();

	public AffineTransform2d invert();
	
	public default Point2d transform(Point2d point)
	{
		double[][] mat = this.getMatrix();
		double x = point.getX();
		double y = point.getY();
		
		double xt = x * mat[0][0] + y * mat[0][1] + mat[0][2]; 
		double yt = x * mat[1][0] + y * mat[1][1] + mat[1][2];
		
		return new Point2d(xt, yt);
	}
}
