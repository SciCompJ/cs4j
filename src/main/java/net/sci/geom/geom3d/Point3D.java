/**
 * 
 */
package net.sci.geom.geom3d;

import java.util.Locale;

import net.sci.geom.Point;
import net.sci.geom.geom2d.Point2D;

/**
 * A three-dimensional point.
 * 
 * @author dlegland
 *
 */
public class Point3D implements Point, Geometry3D
{
    // ===================================================================
    // Static methods

    /**
     * Computes the Euclidean distance between two points.
     * 
     * @param p1
     *            the first point
     * @param p2
     *            the second point
     * @return the distance between the two points
     */
    public static final double distance(Point3D p1, Point3D p2)
    {
        double h = Math.hypot(p2.x - p1.x, p2.y - p1.y);
        return Math.hypot(p2.z - p1.z, h);
    }
    
    /**
     * Computes the squared Euclidean distance between two points. AS it does
     * not involve square root computation, the squared distance may be faster
     * to compute than the classical Euclidean distance.
     * 
     * @param p1
     *            the first point
     * @param p2
     *            the second point
     * @return the squared distance between the two points
     */
    public static final double squaredDistance(Point3D p1, Point3D p2)
    {
        double dx = p2.x - p1.x;
        double dy = p2.y - p1.y;
        double dz = p2.z - p1.z;
        return dx * dx + dy * dy + dz * dz;
    }
    
    /**
     * Computes the centroid of a collection of points. The coordinates of the
     * centroid are the mean of the coordinates of the points.
     * 
     * @param points
     *            the collection of points.
     * @return the centroid of the points
     */
    public static final Point3D centroid(Point3D... points)
    {
        double xc = 0;
        double yc = 0;
        double zc = 0;
        int np = points.length;
        for (Point3D p : points)
        {
            xc += p.x;
            yc += p.y;
            zc += p.z;
        }

        return new Point3D(xc / np, yc / np, zc / np);
    }

    /**
     * Converts a 2D point into a 3D point by adding a z-coordinate
     * 
     * @see #projectXY()
     * 
     * @param point
     *            the point to convert
     * @param z
     *            the amount of translation in the z direction
     * @return the new 3D point
     */
    public static final Point3D from2d(Point2D point, double z)
    {
        return new Point3D(point.x(), point.y(), z);
    }

    /**
     * Interpolates the position of a new Point3D between the two points.
     * 
     * @param p1
     *            the first point to interpolate
     * @param p2
     *            the second point to interpolate
     * @param t
     *            the relative position of the new point, between 0 and 1. If t
     *            is outside the [0,1] range, its value is clamped to enforce
     *            the resulting point to be between the two extremity points.
     * @return the interpolated point
     */
    public static final Point3D interpolate(Point3D p1, Point3D p2, double t)
    {
        if (t <= 0) return p1;
        if (t >= 1) return p2;
        double x = p1.x * (1.0 - t) + p2.x * t;
        double y = p1.y * (1.0 - t) + p2.y * t;
        double z = p1.z * (1.0 - t) + p2.z * t;
        return new Point3D(x, y, z);
    }
    

    // ===================================================================
    // class variables

    /** x coordinate of the point */
    final double x;

    /** y coordinate of the point */
    final double y;

    /** z coordinate of the point */
    final double z;

    
    // ===================================================================
    // constructors

    /** Empty constructor, similar to Point(0,0,0) */
    public Point3D()
    {
        this(0, 0, 0);
    }

    /**
     * Creates a new point from its coordinates.
     * 
     * @param x
     *            the x coordinate of the new point
     * @param y
     *            the y coordinate of the new point
     * @param z
     *            the z coordinate of the new point
     */
    public Point3D(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Convert a vector to a point.
     *
     * @param vect
     *            the vector to convert
     */
    public Point3D(Vector3D vect)
    {
        this(vect.x(), vect.y(), vect.z());
    }
    

    // ===================================================================
    // Accessors

    /**
     * @return the x coordinate of this point
     */
    public double x()
    {
        return x;
    }

    /**
     * @return the y coordinate of this point
     */
    public double y()
    {
        return y;
    }

    /**
     * @return the z coordinate of this point
     */
    public double z()
    {
        return z;
    }
    

    // ===================================================================
    // Methods specific to Point3D

    /**
     * Applies the translation defined by the three components to this point and
     * returns the translated point.
     * 
     * @param dx
     *            the x-component of the translation
     * @param dy
     *            the y-component of the translation
     * @param dz
     *            the z-component of the translation
     * @return the translated point
     */
    public Point3D translate(double dx, double dy, double dz)
    {
        return new Point3D(this.x + dx, this.y + dy, this.z + dz);
    }

    /**
     * Adds the specified vector to the point, and returns the new point.
     * 
     * @param v
     *            the 3D vector to add
     * @return the result of the translation of this point by the given vector
     */
    public Point3D plus(Vector3D v)
    {
        return new Point3D(this.x + v.x, this.y + v.y, this.z + v.z);
    }

    /**
     * Subtracts the specified vector from the point, and returns the new point.
     * 
     * @param v
     *            the 3D point to subtract
     * @return the result of the translation of this point by the opposite of
     *         the given vector
     */
    public Point3D minus(Vector3D v)
    {
        return new Point3D(this.x - v.x, this.y - v.y, this.z - v.z);
    }
    

    // ===================================================================
    // Implements vector space structure for Point3D

    /**
     * Adds the coordinates of the specified point to those of this point, and
     * returns the point with new coordinates
     * 
     * @param p
     *            the 3D point to add
     * @return the result of the translation of this point by the given vector
     */
    public Point3D plus(Point3D p)
    {
        return new Point3D(this.x + p.x, this.y + p.y, this.z + p.z);
    }

    /**
     * Subtracts the coordinates of the specified point from those of this
     * point, and returns the point with new coordinates
     * 
     * @param p
     *            the 3D point to subtract
     * @return the result of the translation of this point by the opposite of
     *         the given vector
     */
    public Point3D minus(Point3D p)
    {
        return new Point3D(this.x - p.x, this.y - p.y, this.z - p.z);
    }

    /**
     * Multiplies the coordinates of this point by the given factor, and returns
     * the point with new coordinates.
     * 
     * @param k
     *            the scaling factor
     * @return the point with new coordinates
     */
    public Point3D times(double k)
    {
        return new Point3D(this.x * k, this.y * k, this.z * k);
    }

    /**
     * Divides the coordinates of this point by the given factor, and returns
     * the point with new coordinates.
     * 
     * @param k
     *            the scaling factor
     * @return the point with new coordinates
     */
    public Point3D divideBy(double k)
    {
        return new Point3D(this.x / k, this.y / k, this.z / k);
    }
    

    // ===================================================================
    // Methods that may be included in Geometry3D in the future

    /**
     * Projects this point onto the XY plane and converts into a 2D point.
     * 
     * @see #projectXY()
     * 
     * @return the 2D projection of the point onto the XY plane.
     */
    public Point2D projectXY()
    {
        return new Point2D(this.x, this.y);
    }

    public Point3D transform(AffineTransform3D trans)
    {
        return trans.transform(this);
    }

    public boolean almostEquals(Point3D point, double eps)
    {
        if (Math.abs(point.x - x) > eps) return false;
        if (Math.abs(point.y - y) > eps) return false;
        if (Math.abs(point.z - z) > eps) return false;
        return true;
    }
    

    // ===================================================================
    // Implements Geometry3D methods

    @Override
    public boolean contains(Point3D point, double eps)
    {
        return distance(point) <= eps;
    }

    /**
     * Computes the distance between current point and point with coordinate
     * <code>(x,y)</code>. Uses the <code>Math.hypot()</code> function for
     * better robustness than simple square root.
     */
    public double distance(double x, double y, double z)
    {
        return Math.hypot(Math.hypot(this.x - x, this.y - y), this.z - z);
    }
    

    // ===================================================================
    // Implements Point interface

    @Override
    public double get(int dim)
    {
        return switch (dim)
        {
            case 0 -> this.x;
            case 1 -> this.y;
            case 2 -> this.z;
            default -> throw new IllegalArgumentException(
                    "Dimension should be comprised between 0 and 2");
        };
    }
    

    // ===================================================================
    // Implements Geometry interface

    /**
     * Returns true, as a point is bounded by definition.
     */
    public boolean isBounded()
    {
        return true;
    }

    @Override
    public Bounds3D bounds()
    {
        return new Bounds3D(this.x, this.x, this.y, this.y, this.z, this.z);
    }

    public Point3D duplicate()
    {
        return new Point3D(x, y, z);
    }
    

    // ===================================================================
    // Override Object's methods

    @Override
    public String toString()
    {
        return String.format(Locale.ENGLISH, "Point3D(%f, %f, %f)", x, y, z);
    }
}
