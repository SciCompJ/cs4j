/**
 * 
 */
package net.sci.geom.geom3d.curve;

import java.util.ArrayList;

import net.sci.geom.geom3d.Box3D;
import net.sci.geom.geom3d.Curve3D;
import net.sci.geom.geom3d.Point3D;
import net.sci.geom.geom3d.Vector3D;
import net.sci.geom.geom3d.line.Plane3D;
import net.sci.geom.geom3d.polyline.LinearRing3D;
import net.sci.geom.geom3d.transform.AffineTransform3D;

/**
 * A 3D circle, defined by a center, a radius, and the orientation of the normal.
 * 
 * @author dlegland
 *
 */
public class Circle3D implements Curve3D
{
    // parametrization: [0 2*PI].
    
    // ===================================================================
    // Class variables
    
    private Point3D center;

    private double radius;
    
    //TODO: use vector or spherical angle?
    private Vector3D normal;
    
    
    // ===================================================================
    // Constructors

    /**
     * empty constructor, corresponding to 
     */
    public Circle3D()
    {
        this.center = ORIGIN;
        this.radius = 0.0;
        this.normal = new Vector3D(0, 0, 1);
    }


    // ===================================================================
    // Methods specific to Circle 3D

    public Point3D center()
    {
        return center;
    }

    public double radius()
    {
        return radius;
    }

    public Plane3D supportingPlane()
    {
        return new Plane3D(center, normal);
    }
    
    /**
     * Converts this circle into a new LinearRing3D with the specified number of
     * vertices.
     * 
     * @param nVertices
     *            the number of vertices of the created linear ring
     * @return a new instance of LinearRing3D
     */
    public LinearRing3D asPolyline(int nVertices)
    {
        double dt = Math.toRadians(360.0 / (nVertices + 1));
        
        ArrayList<Point3D> vertices = new ArrayList<>(nVertices);
        for (int i = 0; i < nVertices; i++)
        {
            vertices.add(getPoint(i * dt));
        }
        
        return new LinearRing3D(vertices);
    }


    // ===================================================================
    // Methods implementing the Curve3D interface

    /* (non-Javadoc)
     * @see net.sci.geom.geom3d.Curve3D#getPoint(double)
     */
    @Override
    public Point3D getPoint(double t)
    {
        // TODO Auto-generated method stub
        double x0 = Math.cos(t) * this.radius;
        double y0 = Math.sin(t) * this.radius;
        double z0 = 0;
        // TODO: add rotation and translation
        return new Point3D();
    }

    /* (non-Javadoc)
     * @see net.sci.geom.geom3d.Curve3D#getT0()
     */
    @Override
    public double getT0()
    {
        return 0;
    }

    /* (non-Javadoc)
     * @see net.sci.geom.geom3d.Curve3D#getT1()
     */
    @Override
    public double getT1()
    {
        return 2 * Math.PI;
    }

    /**
     * Returns true, as a circle is always closed.
     * 
     * @return true
     * 
     * @see net.sci.geom.geom3d.Curve3D#isClosed()
     */
    @Override
    public boolean isClosed()
    {
        return true;
    }

    /* (non-Javadoc)
     * @see net.sci.geom.geom3d.Curve3D#transform(net.sci.geom.geom3d.transform.AffineTransform3D)
     */
    @Override
    public Curve3D transform(AffineTransform3D trans)
    {
        // TODO Auto-generated method stub
        return null;
    }


    // ===================================================================
    // Methods implementing the Geometry3D interface

    /* (non-Javadoc)
     * @see net.sci.geom.geom3d.Geometry3D#contains(net.sci.geom.geom3d.Point3D, double)
     */
    @Override
    public boolean contains(Point3D point, double eps)
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see net.sci.geom.geom3d.Geometry3D#distance(double, double, double)
     */
    @Override
    public double distance(double x, double y, double z)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see net.sci.geom.geom3d.Geometry3D#boundingBox()
     */
    @Override
    public Box3D boundingBox()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Returns true, as a circle is always bounded.
     * 
     * @return true
     * 
     * @see net.sci.geom.geom3d.Curve3D#isClosed()
     */
    @Override
    public boolean isBounded()
    {
        return true;
    }
}
