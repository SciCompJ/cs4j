package net.sci.geom.mesh;

import static net.sci.geom.geom3d.Vector3D.dotProduct;

import java.util.Arrays;
import java.util.Collection;

import net.sci.geom.geom3d.Bounds3D;
import net.sci.geom.geom3d.Plane3D;
import net.sci.geom.geom3d.Point3D;
import net.sci.geom.geom3d.Polygon3D;
import net.sci.geom.geom3d.Vector3D;


/**
 * A basic implementation of a 3D triangle, mainly used for debugging purpose.
 * 
 * @author dlegland
 *
 */
public class Triangle3D implements Polygon3D
{
    // ===================================================================
    // Class variables

    Point3D p1;
    Point3D p2;
    Point3D p3;
    
    
    // ===================================================================
    // Constructor

    public Triangle3D(Point3D p1, Point3D p2, Point3D p3)
    {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
    }

    
    // ===================================================================
    // Methods specific to Triangle3D
    
    /**
     * Computes the position of the projected point onto the plane containing
     * the triangle.
     * 
     * @param point
     *            the 3D point to project on the triangle
     * @return an array with two values
     */
    public double[] projectedPosition(Point3D point)
    {
        // triangle origin and direction vectors
        Vector3D v12 = new Vector3D(p1, p2);
        Vector3D v13 = new Vector3D(p1, p3);

        // identify coefficients of second order equation
        double a = dotProduct(v12, v12);
        double b = dotProduct(v12, v13);
        double c = dotProduct(v13, v13);
        Vector3D diffP = new Vector3D(point, p1);
        double d = dotProduct(v12, diffP);
        double e = dotProduct(v13, diffP);
        // f = dot(diffP, diffP, 2);

        // compute position of projected point in the plane of the triangle
        double det = a * c - b * b ;
        double s = b * e - c * d ;
        double t = b * d - a * e ;

        return new double[] {s / det, t / det};
    }
    
    
    // ===================================================================
    // Methods implementing the Polygon3D interface
    
    public Plane3D supportingPlane()
    {
        Vector3D v12 = new Vector3D(p1, p2);
        Vector3D v13 = new Vector3D(p1, p3);
        return new Plane3D(p1, v12, v13);
    }


    @Override
    public Collection<Point3D> vertices()
    {
        return Arrays.asList(p1, p2, p3);
    }


    // ===================================================================
    // Methods implementing the Geometry3D interface

    @Override
    public boolean contains(Point3D point, double eps)
    {
        if (!supportingPlane().contains(point, eps))
        {
            return false;
        }
        
        double[] pos = projectedPosition(point);
        if (pos[0] < -eps) return false;
        if (pos[1] < -eps) return false;
        if (pos[0] + pos[1] > 1+eps) return false;
        return true;
    }

    @Override
    public double distance(double x, double y, double z)
    {
        // triangle origin and direction vectors
        double x1 = p1.getX();
        double y1 = p1.getY();
        double z1 = p1.getZ();
        Vector3D v12 = new Vector3D(p1, p2);
        Vector3D v13 = new Vector3D(p1, p3);

        // identify coefficients of second order equation
        double a = dotProduct(v12, v12);
        double b = dotProduct(v12, v13);
        double c = dotProduct(v13, v13);
        Vector3D diffP = new Vector3D(x1 - x, y1 - y, z1 - z);
        double d = dotProduct(v12, diffP);
        double e = dotProduct(v13, diffP);
        // f = dot(diffP, diffP, 2);

        // compute position of projected point in the plane of the triangle
        double det = a * c - b * b ;
        double s = b * e - c * d ;
        double t = b * d - a * e ;

        // switch depending on the region where the projection occur
        if (s + t < det)
        {
            if (s < 0)
            {
                if (t < 0)
                {
                    // region 4
                    // The minimum distance must occur 
                    // * on the line t = 0
                    // * on the line s = 0 with t >= 0
                    // * at the intersection of the two lines
                    
                    if (d < 0)
                    {
                        // minimum on edge t = 0 with s > 0.
                        t = 0;
                        if (a <= -d)
                            s = 1;
                        else
                            s = -d / a;
                    }
                    else
                    {
                        // minimum on edge s = 0
                        s = 0;
                        if (e >= 0)
                            t = 0;
                        else if (c <= -e)
                            t = 1;
                        else
                            t = -e / c;
                    }
                }
                else
                {
                    // region 3
                    // The minimum distance must occur on the line s = 0
                    s = 0;
                    if (e >= 0)
                        t = 0;
                    else
                    {
                        if (c <= -e)
                            t = 1;
                        else
                            t = -e / c;
                    }
                }
            }                
            else
            {
                if (t < 0)
                {
                    // region 5
                    // The minimum distance must occur on the line t = 0
                    t = 0;
                    if (d >= 0)
                        s = 0;
                    else
                    {
                        if (a <= -d)
                            s = 1;
                        else
                            s = -d / a;
                    }
                }
                else
                {                    
                    // region 0
                    // the minimum distance occurs inside the triangle
                    s = s / det;
                    t = t / det;
                }
            }
        } 
        else
        {
            if (s < 0)
            {
                // region 2
                // The minimum distance must occur:
                // * on the line s + t = 1
                // * on the line s = 0 with t <= 1
                // * or at the intersection of the two (s=0; t=1)
                
                double tmp0 = b + d;
                double tmp1 = c + e;
                
                if (tmp1 > tmp0)
                {
                    // minimum on edge s+t = 1, with s > 1
                    double numer = tmp1 - tmp0;
                    double denom = a - 2 * b + c;
                    if (numer >= denom)
                        s = 1;
                    else
                        s = numer / denom;
                    t = 1 - s;
                }
                else
                {
                    // minimum on edge s = 0, with t <= 1
                    s = 0;
                    if (tmp1 <= 0)
                        t = 1;
                    else if (e >= 0)
                        t = 0;
                    else
                        t = -e / c;
                }
            }   
            else if (t < 0)
            {
                // region 6
                // The minimum distance must occur
                // * on the line s + t = 1
                // * on the line t = 0, with s <= 1
                // * at the intersection of the two lines
                double tmp0 = b + e;
                double tmp1 = a + d;
                
                if (tmp1 > tmp0)
                {
                    // minimum on edge s+t=1, with t > 0
                    double numer = tmp1 - tmp0;
                    double denom = a - 2 * b + c;
                    if (numer > denom)
                        t = 1;
                    else
                        t = numer / denom;
                    s = 1 - t;
                }                    
                else
                {
                    // minimum on edge t = 0 with s <= 1
                    t = 0;
                    if (tmp1 <= 0)
                        s = 1;
                    else if (d >= 0)
                        s = 0;
                    else
                        s = -d / a;
                }
            }                
            else
            {
                // region 1
                // The minimum distance must occur on the line s + t = 1
                double numer = (c + e) - (b + d);
                if (numer <= 0)
                    s = 0;
                else
                {
                    double denom = a - 2 * b + c;
                    if (numer >= denom)
                        s = 1;
                    else
                        s = numer / denom;
                }
                
                t = 1 - s;
            }
        }

        // compute coordinates of closest point on plane
        Point3D proj = p1.plus(v12.times(s)).plus(v13.times(t));

        // distance between point and closest point on plane
        double dist = proj.distance(x, y, z);
        
        return dist;
    }

    @Override
    public Bounds3D bounds()
    {
        double xmin = Math.min(Math.min(p1.getX(), p2.getX()), p3.getX());
        double xmax = Math.max(Math.max(p1.getX(), p2.getX()), p3.getX());
        double ymin = Math.min(Math.min(p1.getY(), p2.getY()), p3.getY());
        double ymax = Math.max(Math.max(p1.getY(), p2.getY()), p3.getY());
        double zmin = Math.min(Math.min(p1.getZ(), p2.getZ()), p3.getZ());
        double zmax = Math.max(Math.max(p1.getZ(), p2.getZ()), p3.getZ());

        return new Bounds3D(xmin, xmax, ymin, ymax, zmin, zmax);
    }

    @Override
    public boolean isBounded()
    {
        return true;
    }
    
}
