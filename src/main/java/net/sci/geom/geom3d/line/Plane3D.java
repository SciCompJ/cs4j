/**
 * 
 */
package net.sci.geom.geom3d.line;

import net.sci.geom.UnboundedGeometryException;
import net.sci.geom.geom3d.Box3D;
import net.sci.geom.geom3d.Geometry3D;
import net.sci.geom.geom3d.Point3D;
import net.sci.geom.geom3d.Vector3D;

/**
 * @author dlegland
 *
 */
public class Plane3D implements Geometry3D
{
    // ===================================================================
    // class variables

    /**
     * Coordinates of the origin ofthe plane
     */
    protected double x0, y0, z0;

    /**
     * First direction vector of the plane. dx1, dy1 and dz1 should not be all zero.
     */
    protected double dx1, dy1, dz1;

    /**
     * Second direction vector of the plane. dx2, dy2 and dz2 should not be all zero.
     */
    protected double dx2, dy2, dz2;


    // ===================================================================
    // Constructors

    /**
     * Default constructor
     */
    public Plane3D()
    {
        x0 = 0; y0 = 0; z0 = 0;
        dx1 = 1; dy1 = 0; dz1 = 0;
        dx2 = 0; dy2 = 1; dz2 = 0;
    }

    /**
     * Default constructor
     */
    public Plane3D(Point3D origin, Vector3D v1, Vector3D v2)
    {
        x0 = origin.getX();
        y0 = origin.getY();
        z0 = origin.getZ();
        dx1 = v1.getX();
        dy1 = v1.getY();
        dz1 = v1.getZ();
        dx2 = v2.getX();
        dy2 = v2.getY();
        dz2 = v2.getZ();
    }


    // ===================================================================
    // Methods specific to Plane3D
    
    public Point3D intersection(StraightLine3D line)
    {
        // plane normal
        Vector3D n = normal();

        // difference between origins of line and plane
        Vector3D dp = new Vector3D(line.origin(), this.origin()); 

        // dot product of line direction with plane normal
        double denom = Vector3D.dotProduct(n, line.direction());

        // relative position of intersection point on line (can be inf in case of a
        // line parallel to the plane)
        double t = Vector3D.dotProduct(n, dp) / denom;

        // compute coord of intersection point
        Point3D point = line.origin().plus(line.direction().times(t));

        return point;
    }

    public StraightLine3D intersection(Plane3D plane)
    {
        // TODO: implement
        return null;
    }
    
    public Point3D origin()
    {
        return new Point3D(this.x0, this.y0, this.z0);
    }
    
    /**
     * @return the normal to the plane, computed as the cross product of the two direction vectors
     */
    public Vector3D normal()
    {
        return new Vector3D(
                dy1 * dz2 - dz1 * dy2, 
                dz1 * dx2 - dx1 * dz2, 
                dx1 * dy2 - dy1 * dx2);
    }


    // ===================================================================
    // Methods implementing the Geometry3D interface

    /* (non-Javadoc)
     * @see net.sci.geom.geom3d.Geometry3D#contains(net.sci.geom.geom3d.Point3D, double)
     */
    @Override
    public boolean contains(Point3D point, double eps)
    {
        return distance(point) < eps;
    }

    /* (non-Javadoc)
     * @see net.sci.geom.geom3d.Geometry3D#distance(double, double, double)
     */
    @Override
    public double distance(double x, double y, double z)
    {
        // normalized plane normal
        Vector3D normal = this.normal().normalize();
       
        // compute difference of coordinates between plane origin and point
        Vector3D dp = new Vector3D(this.x0 - x, this.y0 - y, this.z0 - z);
        
        double d = Math.abs(Vector3D.dotProduct(normal, dp));
        return d;
    }

    /* (non-Javadoc)
     * @see net.sci.geom.geom3d.Geometry3D#boundingBox()
     */
    @Override
    public Box3D boundingBox()
    {
        throw new UnboundedGeometryException(this);
    }

    /* (non-Javadoc)
     * @see net.sci.geom.Geometry#isBounded()
     */
    @Override
    public boolean isBounded()
    {
        return false;
    }
}
