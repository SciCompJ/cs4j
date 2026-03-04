/**
 * 
 */
package net.sci.geom.geom2d.curve;

import static java.lang.Math.cos;
import static java.lang.Math.hypot;
import static java.lang.Math.sin;

import net.sci.geom.geom2d.AffineTransform2D;
import net.sci.geom.geom2d.Bounds2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.Vector2D;
import net.sci.geom.polygon2d.LinearRing2D;

/**
 * An circle, defined by a center and a radius.
 * 
 * @author dlegland
 * @see Ellipse2D
 */
public class Circle2D implements Contour2D
{
    /**
     * Computes the circumscribed circle, commonly referred to as circumcircle,
     * of three points. Computation is based on equations from Wikipedia.
     * 
     * See <a href=
     * "https://en.wikipedia.org/wiki/Circumcircle#Cartesian_coordinates_2">https://en.wikipedia.org/wiki/Circumcircle</a>
     * 
     * @param p1
     *            the first point
     * @param p2
     *            the second point
     * @param p3
     *            the third point
     * @return the unique circle that contains the three points, if it exists
     */
    public static final Circle2D circumCircle(Point2D p1, Point2D p2, Point2D p3)
    {
        // find the point with lowest distance from origin, and apply a shift to
        // translate this point on the origin
        double d1 = sqNorm(p1);  
        double d2 = sqNorm(p2);  
        double d3 = sqNorm(p3);
        
        Point2D refPoint;
        Vector2D pB, pC;
        if (d1 <= d2 && d1 <= d3)
        {
            refPoint = p1;
            pB = new Vector2D(p1, p2); 
            pC = new Vector2D(p1, p3);
        }
        else if (d2 <= d1 && d2 <= d3)
        {
            refPoint = p2;
            pB = new Vector2D(p2, p1); 
            pC = new Vector2D(p2, p3);
        }
        else // if (d3 <= d1 && d3 <= d2)
        {
            refPoint = p3;
            pB = new Vector2D(p3, p1); 
            pC = new Vector2D(p3, p2);
        }
        
        double denom = 2 * (pB.x() * pC.y() - pB.y() * pC.x());
        double sqNormB = sqNorm(pB);
        double sqNormC = sqNorm(pC);
        double ux =  (pC.y() * sqNormB - pB.y() * sqNormC) / denom;
        double uy =  (pB.x() * sqNormC - pC.x() * sqNormB) / denom;
        double r = Math.hypot(ux, uy);
                
        return new Circle2D(refPoint.x() + ux, refPoint.y() + uy, r);
    }
    
    private final static double sqNorm(Point2D p)
    {
        return p.x() * p.x() + p.y() * p.y();
    }
    
    private final static double sqNorm(Vector2D v)
    {
        return v.x() * v.x() + v.y() * v.y();
    }
    
    
    // ===================================================================
    // Class variables
    
    /** X-coordinate of the center. */
    protected double xc;

    /** Y-coordinate of the center. */
    protected double yc;

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
        this(center.x(), center.y(), radius);
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
     * Converts this circle into an instance of Ellipse2D.
     * 
     * @return an ellipse that can be super-imposed on this circle
     */
    public Ellipse2D asEllipse()
    {
        return new Ellipse2D(xc, yc, radius, radius, 0);
    }
    
    /**
     * Returns the center of this circle.
     * 
     * @return the center of the circle.
     */
    public Point2D center()
    {
        return new Point2D(xc, yc);
    }
    
    /**
     * Returns the radius of the circle.
     * 
     * @return the radius of the circle
     */
    public double radius()
    {
        return radius;
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
    
    /**
     * Computes the perimeter of this circle, by multiplying the radius by 2*PI.
     * 
     * @see #area()
     * @return the perimeter of this circle.
     */
    public double perimeter()
    {
        return 2 * Math.PI * this.radius;
    }

    
    /**
     * Checks if this circle is equal to the specified circle up to the absolute
     * tolerance value given as parameter. The tolerance is used to compare
     * position of centers and circle radius.
     * 
     * @param circle
     *            the circle to compare with
     * @param eps
     *            the (absolute) tolerance on center coordinates and radius
     * @return true if the circles are equal up to the <code>eps</code>
     *         tolerance value.
     */
    public boolean almostEquals(Circle2D circle, double eps)
    {
        if (!this.center().almostEquals(circle.center(), eps)) return false;
        if (Math.abs(circle.radius - this.radius) > eps) return false;
        return true;
    }
    
    
    // ===================================================================
    // Methods implementing the Boundary2D interface
    
    public double signedDistance(Point2D point)
    {
        return this.signedDistance(point.x(), point.y());
    }

    public double signedDistance(double x, double y)
    {
        // distance to center
        double d = hypot(x - this.xc, y - this.yc);
        return d - this.radius;
    }

    public boolean isInside(Point2D point)
    {
    	return isInside(point.x(), point.y());
    }

    public boolean isInside(double x, double y)
    {
    	return hypot(x - this.xc, y - this.yc) <= this.radius;
    }

    
    // ===================================================================
    // Methods implementing the Curve2D interface
    
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
        
        LinearRing2D res = LinearRing2D.create(nVertices);
        for (int i = 0; i < nVertices; i++)
        {
            double x = cos(i * dt) * this.radius + this.xc;
            double y = sin(i * dt) * this.radius + this.yc;
            res.addVertex(new Point2D(x, y));
        }
        
        return res;
    }

    @Override
    public Point2D point(double t)
    {
        // position for a centered and axis-aligned ellipse
        double x = this.radius * cos(t) + this.xc;
        double y = this.radius * sin(t) + this.yc;
        return new Point2D(x, y);
    }

    @Override
    public double t0()
    {
        return 0;
    }

    @Override
    public double t1()
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
    	double rho = hypot(point.x() - xc, point.y() - yc);    	
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
    public Bounds2D bounds()
    {
        return new Bounds2D(xc - radius, xc + radius, yc - radius, yc + radius);
    }
    
    @Override
    public Circle2D duplicate()
    {
        return new Circle2D(xc, yc, radius);
    }
}
