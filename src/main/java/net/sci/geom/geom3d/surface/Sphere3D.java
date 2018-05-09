/**
 * 
 */
package net.sci.geom.geom3d.surface;

import net.sci.geom.geom3d.Box3D;
import net.sci.geom.geom3d.Geometry3D;
import net.sci.geom.geom3d.Point3D;

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
    public Point3D getCenter()
    {
        return center;
    }

    /**
     * @return the radius
     */
    public double getRadius()
    {
        return radius;
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
    public Box3D boundingBox()
    {
        double x = center.getX();
        double y = center.getY();
        double z = center.getZ();
        return new Box3D(x - radius, x + radius, y - radius, y + radius, z - radius, z + radius);
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
