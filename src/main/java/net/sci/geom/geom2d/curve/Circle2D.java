/**
 * 
 */
package net.sci.geom.geom2d.curve;

import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.hypot;
import static java.lang.Math.sin;

import java.util.ArrayList;

import net.sci.geom.geom2d.Box2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.polygon.LinearRing2D;
import net.sci.geom.geom2d.transform.AffineTransform2D;

/**
 * An circle, defined by a center and a radius.
 * 
 * @author dlegland
 * @see Ellipse2D
 */
public class Circle2D implements Contour2D
{
    // ===================================================================
    // Class variables
    
    /** X-coordinate of the center. */
    protected double  xc;

    /** Y-coordinate of the center. */
    protected double  yc;

    /** The radius of the circle */
    protected double radius;
    
    
    // ===================================================================
    // Constructors
    
    /**
     * Defines circle by coordinates of center and radius.
     * 
     * @param center
     *            the center of the circle
     * @param radius
     *            the radius of the circle
     */
    public Circle2D(Point2D center, double radius)
    {
        this(center.getX(), center.getY(), radius);
    }
    
    /**
     * Define circle by coordinates of center and radius.
     * 
     * @param xc
     *            the x-coordinate of circle center
     * @param yc
     *            the y-coordinate of circle center
     * @param radius
     *            the radius of the circle
     */
    public Circle2D(double xc, double yc, double radius)
    {
        this.xc = xc;
        this.yc = yc;
        this.radius = radius;
    }

    // ===================================================================
    // Specific methods
    
    /**
     * Converts this circle into a new LinearRing2D with the specified number of
     * vertices.
     * 
     * @param nVertices
     *            the number of vertices of the created linear ring
     * @return a new instance of LinearRing2D
     */
    public LinearRing2D asPolyline(int nVertices)
    {
        double dt = Math.toRadians(360.0 / (nVertices + 1));
        
        ArrayList<Point2D> vertices = new ArrayList<>(nVertices);
        for (int i = 0; i < nVertices; i++)
        {
            double x = cos(i * dt) * this.radius + this.xc;
            double y = sin(i * dt) * this.radius + this.yc;
            vertices.add(new Point2D(x, y));
        }
        
        return new LinearRing2D(vertices);
    }

    /**
     * @return an ellipse that can be super-imposed on this circle 
     */
    public Ellipse2D asEllipse()
    {
        return new Ellipse2D(xc, yc, radius, radius, 0);
    }
    
    /**
     * Computes the area of this circle, by multiplying the squared radius by
     * PI.
     * 
     * @see net.sci.geom.geom2d.curve.Ellipse2D#area()
     * @return the area of this circle.
     */
    public double area()
    {
        return this.radius * this.radius * Math.PI;
    }
    
    public Point2D center()
    {
        return new Point2D(xc, yc);
    }
    
    /** 
     * @return the radius of the circle
     */
    public double radius()
    {
        return radius;
    }
    
    
    // ===================================================================
    // Methods implementing the Boundary2D interface
    
    public double signedDistance(Point2D point)
    {
        return this.signedDistance(point.getX(), point.getY());
    }

    public double signedDistance(double x, double y)
    {
        // distance to center
        double d = hypot(x - this.xc, y - this.yc);
        return d - this.radius;
    }

    public boolean isInside(Point2D point)
    {
    	return isInside(point.getX(), point.getY());
    }

    public boolean isInside(double x, double y)
    {
    	return abs(hypot(x - this.xc, y - this.yc) - this.radius) <= 1;
    }

    
    // ===================================================================
    // Methods implementing the Curve2D interface
    
    @Override
    public Point2D getPoint(double t)
    {
        // position for a centered and axis-aligned ellipse
        double x = this.radius * cos(t) + this.xc;
        double y = this.radius * sin(t) + this.yc;
        return new Point2D(x, y);
    }

    @Override
    public double getT0()
    {
        return 0;
    }

    @Override
    public double getT1()
    {
        return 2 * Math.PI;
    }

    @Override
    public boolean isClosed()
    {
        return true;
    }
    
    @Override
    public Ellipse2D transform(AffineTransform2D trans)
    {
        return asEllipse().transform(trans);
    }


    // ===================================================================
    // Methods implementing the Geometry2D interface
    
    /* (non-Javadoc)
     * @see net.sci.geom.geom2d.Geometry2D#contains(net.sci.geom.geom2d.Point2D, double)
     */
    @Override
    public boolean contains(Point2D point, double eps)
    {
    	double rho = hypot(point.getX() - xc, point.getY() - yc);    	
        return Math.abs(rho - radius) <= eps;
    }
    
    /* (non-Javadoc)
     * @see net.sci.geom.geom2d.Geometry2D#distance(double, double)
     */
    @Override
    public double distance(double x, double y)
    {
        double rho = hypot(x - xc, y - yc);       
        return Math.abs(rho - radius);
    }
    
    /* (non-Javadoc)
     * @see net.sci.geom.Geometry#isBounded()
     */
    @Override
    public boolean isBounded()
    {
        return true;
    }

    /* (non-Javadoc)
     * @see net.sci.geom.geom2d.Geometry2D#boundingBox()
     */
    @Override
    public Box2D boundingBox()
    {
        return new Box2D(xc - radius, xc + radius, yc - radius, yc + radius);
    }
}
