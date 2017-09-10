/**
 * 
 */
package net.sci.geom.geom2d.curve;

import java.util.ArrayList;

import net.sci.geom.geom2d.Box2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.polygon.LinearRing2D;

/**
 * An ellipse, defined by a center, two semi-axis lengths, and one orientation angle in degrees.
 * 
 * @author dlegland
 *
 */
public class Ellipse2D implements Contour2D
{
    // ===================================================================
    // Constructors
    
    /** X-coordinate of the center. */
    protected double  xc;

    /** X-coordinate of the center. */
    protected double  yc;

    /** Length of major semi-axis. Must be positive. */
    protected double  r1;
    
    /** Length of minor semi-axis. Must be positive. */
    protected double  r2;

    /** Orientation of major semi-axis, in degrees, between 0 and 180. */
    protected double  theta  = 0;

    
    // ===================================================================
    // Constructors
    
    /**
     * Define center by point, major and minor semi axis lengths, and
     * orientation angle.
     */
    public Ellipse2D(Point2D center, double r1, double r2, double theta)
    {
        this(center.getX(), center.getY(), r1, r2, theta);
    }
    
    /**
     * Define center by coordinate, major and minor semi axis lengths, and
     * orientation angle.
     */
    public Ellipse2D(double xc, double yc, double r1, double r2, double theta)
    {
        this.xc = xc;
        this.yc = yc;
        this.r1 = r1;
        this.r2 = r2;
        this.theta = theta;
    }

    // ===================================================================
    // Specific methods
    
    /**
     * Converts this ellipse into a new LinearRing2D with the specified number of vertices.
     * 
     * @param nVertices the number of vertices of the created linear ring
     * @return a new instance of LinearRing2D
     */
    public LinearRing2D asPolyline(int nVertices)
    {
        double thetaRad = Math.toRadians(this.theta);
        double cost = Math.cos(thetaRad);
        double sint = Math.sin(thetaRad);
        double dt = Math.toRadians(360.0 / nVertices);
        
        ArrayList<Point2D> vertices = new ArrayList<>(nVertices);
        for (int i = 0; i < nVertices; i++)
        {
            double x = Math.cos(i * dt) * this.r1;
            double y = Math.sin(i * dt) * this.r2;
            double x2 = x * cost - y * sint + this.xc;
            double y2 = x * sint + y * cost + this.yc;
            vertices.add(new Point2D(x2, y2));
        }
        
        return new LinearRing2D(vertices);
    }

    // ===================================================================
    // Methods implementing the Boundary2D interface
    
    public double signedDistance(Point2D point)
    {
        // TODO: use exact computation 
        return this.asPolyline(200).signedDistance(point.getX(), point.getY());
    }

    public double signedDistance(double x, double y)
    {
        // TODO: use exact computation 
        return this.asPolyline(200).signedDistance(x, y);
    }

    public boolean isInside(Point2D point)
    {
    	return isInside(point.getX(), point.getY());
    }

    public boolean isInside(double x, double y)
    {
    	return quasiDistanceToCenter(x, y) <= 1;
    }

    
    // ===================================================================
    // Methods implementing the Curve2D interface
    
    @Override
    public boolean isClosed()
    {
        return true;
    }
    

    // ===================================================================
    // Methods implementing the Geometry2D interface
    
    /* (non-Javadoc)
     * @see net.sci.geom.geom2d.Geometry2D#contains(net.sci.geom.geom2d.Point2D, double)
     */
    @Override
    public boolean contains(Point2D point, double eps)
    {
    	double rho = quasiDistanceToCenter(point.getX(), point.getY());    	
        return Math.abs(rho - 1) <= eps;
    }
    
    /**
	 * Apply to the point the transform that transforms this ellipse into unit
	 * circle, and computes distance to origin.
	 * 
	 * @param x
	 *            the x-coordinate of the point
	 * @param y
	 *            the y-coordinate of the point
	 * @return the distance of the transformed point to the origin
	 */
    private double quasiDistanceToCenter(double x, double y)
    {
    	// recenter point
    	x -= this.xc;
    	y -= this.yc;
    	
    	// pre-computes trigonometric values
    	double thetaRad = Math.toRadians(this.theta);
        double cost = Math.cos(thetaRad);
        double sint = Math.sin(thetaRad);
        
        // orient along main axes
    	double x2 = x * cost + y * sint;
        double y2 = -x * sint + y * cost;
        
        // and divides by semi axes length
        x2 /= this.r1;
        y2 /= this.r2;
    	
        return Math.hypot(x2, y2);
    }
    
    /* (non-Javadoc)
     * @see net.sci.geom.geom2d.Geometry2D#distance(double, double)
     */
    @Override
    public double distance(double x, double y)
    {
        // TODO Auto-generated method stub
        return asPolyline(200).distance(x, y);
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
        // TODO could be more precise
        return asPolyline(200).boundingBox();
    }

}
