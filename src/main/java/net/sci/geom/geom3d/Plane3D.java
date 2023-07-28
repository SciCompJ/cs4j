/**
 * 
 */
package net.sci.geom.geom3d;

import net.sci.geom.UnboundedGeometryException;
import net.sci.geom.geom2d.Point2D;

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
     * Default constructor using an origin point and two direction vectors.
     * 
     * @param origin
     *            the origin point of the new plane
     * @param v1
     *            the first direction vector of the new plane
     * @param v2
     *            the second direction vector of the new plane
     */
    public Plane3D(Point3D origin, Vector3D v1, Vector3D v2)
    {
        x0 = origin.x();
        y0 = origin.y();
        z0 = origin.z();
        dx1 = v1.x();
        dy1 = v1.y();
        dz1 = v1.z();
        dx2 = v2.x();
        dy2 = v2.y();
        dz2 = v2.z();
    }

    /**
     * Constructor using an origin point and a normal vector.
     * 
     * @param origin
     *            the plane origin
     * @param normal
     *            a vector normal to the plane
     */
    public Plane3D(Point3D origin, Vector3D normal)
    {
        // setup origin
        x0 = origin.x();
        y0 = origin.y();
        z0 = origin.z();
        
        // find a vector not colinear to the normal
        Vector3D v0 = new Vector3D(1, 0, 0);
        if (Vector3D.crossProduct(normal, v0).norm() < 1e-14)
        {
            v0 = new Vector3D(0, 1, 0);
        }
        
        // create direction vectors
        Vector3D v1 = Vector3D.crossProduct(normal, v0).normalize();
        Vector3D v2 = Vector3D.crossProduct(normal, v1).normalize();

        // setup direction
        dx1 = v1.x();
        dy1 = v1.y();
        dz1 = v1.z();
        dx2 = v2.x();
        dy2 = v2.y();
        dz2 = v2.z();
    }
    
    private Plane3D(double x0, double y0, double z0, double dx1, double dy1, double dz1, double dx2, double dy2, double dz2)
    {
        this.x0 = x0;
        this.y0 = y0;
        this.z0 = z0;
        this.dx1 = dx1;
        this.dy1 = dy1;
        this.dz1 = dz1;
        this.dx2 = dx2;
        this.dy2 = dy2;
        this.dz2 = dz2;
    }


    // ===================================================================
    // Accessors
    
    public Point3D origin()
    {
        return new Point3D(this.x0, this.y0, this.z0);
    }
    
    public Vector3D directionVector1()
    {
        return new Vector3D(dx1, dy1, dz1);
    }
    
    public Vector3D directionVector2()
    {
        return new Vector3D(dx2, dy2, dz2);
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
        // compute plane normals
        Vector3D n1 = this.normal().normalize();
        Vector3D n2 = plane.normal().normalize();
        
//        // test if planes are parallel
//        if abs(cross(n1, n2, 2)) < tol
//        line = [NaN NaN NaN NaN NaN NaN];
//        return;
//        end

        // Uses Hessian form, ie : N.p = d
        // nI this case, d can be found as : -N.p0, when N is normalized
        double d1 = Vector3D.dotProduct(n1, new Vector3D(this.origin()));
        double d2 = Vector3D.dotProduct(n2, new Vector3D(plane.origin()));
        
        //% compute dot products
        double dot1 = Vector3D.dotProduct(n1, n1);
        double dot2 = Vector3D.dotProduct(n2, n2);
        double dot12 = Vector3D.dotProduct(n1, n2);

        // intermediate computations
        double det = dot1 * dot2 - dot12 * dot12;
        double c1 = (d1 * dot2 - d2 * dot12) / det;
        double c2 = (d2 * dot1 - d1 * dot12) / det;

        // compute line origin and direction
        Point3D p0 = new Point3D(n1.times(c1).plus(n2.times(c2)));
        Vector3D dp = Vector3D.crossProduct(n1, n2);

        return new StraightLine3D(p0, dp);
    }

    /**
     * Computes the projection of the point on this plane.
     * 
     * @param point
     *            the point to project
     * @return the 3D position of the projection
     */
    public Point3D projection(Point3D point)
    {
        Point3D origin = origin();
        Vector3D normal = normal();

        // difference between origin of plane and point
        Vector3D diffPoint = new Vector3D(point, origin);
        
        // relative position of point on normal's line
        double norm = normal.norm();
        double t = Vector3D.dotProduct(normal, diffPoint) / (norm * norm);

        // add relative difference to project point back to plane
        return point.plus(normal.times(t));
    }
    
    /**
     * Computes the projection of the point on this plane and returns result in coordinate of plane.
     * 
     * @param point
     *            the point to project
     * @return the 2D position of the projection
     */
    public Point2D projection2d(Point3D point)
    {
        Point3D origin = origin();

        // origin and direction vectors of the plane
        Vector3D d1 = new Vector3D(dx1, dy1, dz1);
        Vector3D d2 = new Vector3D(dx2, dy2, dz2);
        
        Vector3D diffPoint = new Vector3D(origin, point);
        double s = Vector3D.dotProduct(diffPoint, d1) / d1.norm();
        double t = Vector3D.dotProduct(diffPoint, d2) / d2.norm();

        return new Point2D(s, t);
    }
    
    public Plane3D transform(AffineTransform3D trans)
    {
        Point3D ot = origin().transform(trans);
        Vector3D v1t = directionVector1().transform(trans);
        Vector3D v2t = directionVector2().transform(trans);
        return new Plane3D(ot, v1t, v2t);
    }
    
    /**
     * Normalizes this plane by keeping same normal vector and same distance to origin, but
     * normalizing each direction vector and ensuring their orthogonality.
     * 
     * The second direction vector of normalized plane may not be aligned with
     * second direction vector of original plane.
     * 
     * @return the normalized plane.
     */
    public Plane3D normalize()
    {
        // compute normalized first direction vector
        Vector3D v1 = directionVector1().normalize();

        // compute normalized second direction vector
        Vector3D n = normal().normalize();
        Vector3D v2 = n.crossProduct(v1).normalize();

        // compute origin point of the new plane
        Plane3D p = new Plane3D(this.origin(), v1, v2);
        Point3D newOrigin = p.projection(ORIGIN);

        // create the resulting plane
        return new Plane3D(newOrigin, v1, v2);
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
    public Bounds3D bounds()
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
    
    @Override
    public Plane3D duplicate()
    {
        return new Plane3D(x0, y0, z0, dx1, dy1, dz1, dx2, dy2, dz2);
    }
}
