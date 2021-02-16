/**
 * 
 */
package net.sci.geom.geom3d.surface;

import java.util.ArrayList;
import java.util.Collection;

import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.curve.Circle2D;
import net.sci.geom.geom3d.Bounds3D;
import net.sci.geom.geom3d.Geometry3D;
import net.sci.geom.geom3d.Plane3D;
import net.sci.geom.geom3d.Point3D;
import net.sci.geom.geom3d.StraightLine3D;
import net.sci.geom.geom3d.Vector3D;

/**
 * A 3D sphere, defined by a center and a radius.
 * 
 * @author dlegland
 *
 */
public class Sphere3D implements Geometry3D
{
    // ===================================================================
    // Class variables

    /**
     * The center of the sphere.
     */
    Point3D center;
    
    /**
     * The radius of the sphere.
     */
    double radius;
    

    // ===================================================================
    // Constructors

    /**
     * Creates a new sphere from center and radius.
     * 
     * @param center the center of the sphere
     * @param radius the radius of the sphere
     */
    public Sphere3D(Point3D center, double radius)
    {
        this.center = center;
        this.radius = radius;
    }
    
    /**
     * Creates a unit sphere, centered at the origin and with a radius of 1.
     */
    public Sphere3D()
    {
        this(ORIGIN, 1);
    }
    
    
    // ===================================================================
    // Functions specific to Sphere3D

    /**
     * Computes the volume enclosed by this sphere, by multiplying the cube of the
     * radius by 4*PI/3.
     * 
     * @return the volume of this sphere
     */
    public double volume()
    {
        double r = this.radius;
        return r * r * r * (4.0 * Math.PI / 3.0); 
    }

    /**
     * Computes the surface area of this sphere, by multiplying the square of the
     * radius by 4*PI.
     * 
     * @return the surface area of this sphere
     */
    public double surfaceArea()
    {
        double r = this.radius;
        return r * r * (4.0 * Math.PI); 
    }

    
    
    
    // ===================================================================
    // Getters

    /**
     * @return the center
     */
    public Point3D center()
    {
        return center;
    }

    /**
     * @return the radius
     */
    public double radius()
    {
        return radius;
    }


    /**
     * Computes the set of intersections between this sphere and a straight line.
     * 
     * Returns either a list of two points if the distance between line and
     * sphere center is less than or equal to the sphere radius, or an empty
     * list otherwise.
     * 
     * @param line
     *            the query straight line
     * @return the list of intersections with the straight line
     */
    public Collection<Point3D> intersections(StraightLine3D line)
    {
        // Compute difference between line origin and sphere center
        Vector3D dc = new Vector3D(this.center, line.origin());
        
        // Compute coefficients of equation
        Vector3D dir = line.direction();
        double a = dir.dotProduct(dir);
        double b = 2 * dc.dotProduct(dir);
        double c = dc.dotProduct(dc) - this.radius * this.radius;
        
        // compute discriminant
        double delta = b * b - 4 * a * c;
        
        // Special case of no intersection between line and sphere
        if (delta < 0)
        {
            return new ArrayList<Point3D>(0);
        }
        
        // compute curvilinear abscissa of points on the line
        double sqrtDelta = Math.sqrt(delta);
        double t1 = (-b - sqrtDelta) / (2 * a);
        double t2 = (-b + sqrtDelta) / (2 * a);
        
        // add the two points on the line
        ArrayList<Point3D> result = new ArrayList<Point3D>(2);
        result.add(line.getPoint(t1));
        result.add(line.getPoint(t2));
        return result;
    }

    /**
     * Computes the intersection between this sphere and a 3D plane.
     * 
     * Returns either an instance of Circle2D defined in the space of the plane,
     * or null if the plane does not intersect the sphere.
     * 
     * @param plane
     *            the query plane
     * @return intersection of the plane with the sphere, or null if the plane
     *         does not intersect the sphere.
     */
    public Circle2D intersection2d(Plane3D plane)
    {
        // distance between plane and sphere center
        double dist = plane.distance(center);
        
        // check non intersection case
        if (dist > radius)
        {
            return null;
        }
        
        // projection of sphere center on plane -> gives circle center
        Point2D center2d = plane.projection2d(center);

        // compute radius on circle
        double radius2d = Math.sqrt(radius * radius - dist * dist);

        // create the new circle
        return new Circle2D(center2d, radius2d);
    }
    

    // ===================================================================
    // Implementation of the Geometry3D interface

    /* (non-Javadoc)
     * @see net.sci.geom.geom3d.Geometry3D#contains(net.sci.geom.geom3d.Point3D, double)
     */
    @Override
    public boolean contains(Point3D point, double eps)
    {
        return center.distance(point) < radius + eps;
    }
    
    /* (non-Javadoc)
     * @see net.sci.geom.geom3d.Geometry3D#distance(double, double, double)
     */
    @Override
    public double distance(double x, double y, double z)
    {
        return Math.abs(center.distance(x, y, z) - radius);
    }
    
    /* (non-Javadoc)
     * @see net.sci.geom.geom3d.Geometry3D#boundingBox()
     */
    @Override
    public Bounds3D bounds()
    {
        double x = center.getX();
        double y = center.getY();
        double z = center.getZ();
        return new Bounds3D(x - radius, x + radius, y - radius, y + radius, z - radius, z + radius);
    }
    
    
    // ===================================================================
    // Implementation of the Geometry interface

    /** 
     * Returns true, as a sphere is always bounded.
     * 
     * @return true.
     * 
     * @see net.sci.geom.Geometry#isBounded()
     */
    @Override
    public boolean isBounded()
    {
        return true;
    }    
}
