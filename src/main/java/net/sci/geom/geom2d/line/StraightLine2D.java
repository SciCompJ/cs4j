/**
 * 
 */
package net.sci.geom.geom2d.line;

import net.sci.geom.UnboundedGeometryException;
import net.sci.geom.geom2d.Box2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.Vector2D;
import net.sci.geom.geom2d.curve.Contour2D;
import net.sci.geom.geom2d.transform.AffineTransform2D;

/**
 * A straight line, with infinite bounds in each direction.
 * 
 * @author dlegland
 *
 */
public class StraightLine2D implements LinearGeometry2D, Contour2D
{
    // ===================================================================
    // class variables

    /**
     * Coordinates of starting point of the line
     */
    protected double x0, y0;

    /**
     * Direction vector of the line. dx and dy should not be both zero.
     */
    protected double dx, dy;


    // ===================================================================
    // Constructors

    public StraightLine2D(Point2D p1, Point2D p2)
    {
        this.x0 = p1.getX();
        this.y0 = p1.getY();
        this.dx = p2.getX() - this.x0;
        this.dy = p2.getY() - this.y0;
    }
    
    public StraightLine2D(Point2D origin, Vector2D direction)
    {
        this.x0 = origin.getX();
        this.y0 = origin.getY();
        this.dx = direction.getX();
        this.dy = direction.getY();
        
    }
    
    public StraightLine2D(double x0, double y0, double dx, double dy)
    {
        this.x0 = x0;
        this.y0 = y0;
        this.dx = dx;
        this.dy = dy;
    }
    

    // ===================================================================
    // Methods specific to StraightLine2D 

    /**
     * Returns the point at the specified position using the parametric
     * representation of this line.
     * 
     * @param t the position on the line
     * @return the point located at specified position
     */
    public Point2D point(double t)
    {
        return new Point2D(x0 + dx * t, y0 + dy * t);
    }
    
    /**
     * Computes the coordinates of the projection of the specified point on this
     * line.
     * 
     * @param point
     *            a point
     * @return the projection of the point on this line
     */
    public Point2D project(Point2D point)
    {
        // compute position on the line
        double t = projectedPosition(point);

        // compute position of intersection point
        return new Point2D(x0 + t * dx, y0 + t * dy);
    }

    public double projectedPosition(Point2D point)
    {
        double denom = dx * dx + dy * dy;
//        if (Math.abs(denom) < Shape2D.ACCURACY)
//            throw new DegeneratedLine2DException(this);
        return ((point.getY() - y0) * dy + (point.getX() - x0) * dx) / denom;
    }
    
    public double projectedPosition(double x, double y)
    {
        double denom = dx * dx + dy * dy;
//        if (Math.abs(denom) < Shape2D.ACCURACY)
//            throw new DegeneratedLine2DException(this);
        return ((y - y0) * dy + (x - x0) * dx) / denom;
    }
    
    
    // ===================================================================
    // Implementation of the LinearGeometry interface 

    /**
     * Transforms this straight line with the specified affine transform.
     * 
     * @param trans
     *            an affine transform
     * @return the transformed straight line
     */
    @Override
    public StraightLine2D transform(AffineTransform2D trans)
    {
        return new StraightLine2D(origin().transform(trans), direction().transform(trans));
    }

    /**
     * Returns the origin point of this line.
     */
    public Point2D origin() 
    {
        return new Point2D(x0, y0);
    }

    /**
     * Returns the direction vector of this line.
     */
    public Vector2D direction() 
    {
        return new Vector2D(dx, dy);
    }


    @Override
    public StraightLine2D supportingLine()
    {
        return this;
    }
    
    

    // ===================================================================
    // Methods implementing the Boundary2D interface
    
    @Override
    public double signedDistance(Point2D point)
    {
        return signedDistance(point.getX(), point.getY());
    }
    
    public double signedDistance(double x, double y)
    {
        // distance between point and line
        double dist = distance(x, y);
        
        // compute offset between point and line origin
        double xDiff = x - this.x0;
        double yDiff = y - this.y0;

        // determine relative position of point using dot product 
        return (xDiff * this.dy - yDiff * this.dx) > 0 ? dist : -dist;
    }

    @Override
    public boolean isInside(Point2D point)
    {
    	return isInside(point.getX(), point.getY());
    }
    
    @Override
    public boolean isInside(double x, double y)
    {
        // compute offset between point and line origin
        double xDiff = x - this.x0;
        double yDiff = y - this.y0;

        // determine relative position of point using dot product 
        return (xDiff * this.dy - yDiff * this.dx) < 0;
    }
    
    
    // ===================================================================
    // Methods implementing the Curve2D interface
    
    @Override
    public boolean isClosed()
    {
        return false;
    }
    

    // ===================================================================
    // Implementation of the Geometry2D interface 

    /* (non-Javadoc)
     * @see net.sci.geom.geom2d.Geometry2D#contains(net.sci.geom.geom2d.Point2D, double)
     */
    @Override
    public boolean contains(Point2D point, double eps)
    {
        double denom = Math.hypot(this.dx, this.dy);
        if (denom < eps)
        {
            throw new DegeneratedLine2DException(this);
        }
        double x = point.getX();
        double y = point.getY();
        return Math.sqrt(Math.abs((x - x0) * dy - (y - y0) * dx)) / denom < eps;

    }

    /**
     * Returns the distance of the given point to this line.
     * 
     * Uses formula given in Mathworld:
     * http://mathworld.wolfram.com/Point-LineDistance2-Dimensional.html
     * 
     * @param x
     *            the x-coordinate of the point
     * @param y
     *            the y-coordinate of the point
     * @return the distance between the point and the lien
     */
    public double distance(double x, double y)
    {
        // compute shift between the point and the line origin
        double sx = x - this.x0;
        double sy = y - this.y0;
        
        return Math.abs(this.dx * sy - this.dy * sx) / Math.hypot(this.dx, this.dy);
    }


    // ===================================================================
    // Implementation of the Geometry interface

    /**
     * Returns false, as a straight line is unbounded by definition.
     * 
     * @return false
     */
    @Override
    public boolean isBounded()
    {
        return false;
    }

    /* (non-Javadoc)
     * @see net.sci.geom.Geometry#boundingBox()
     */
    @Override
    public Box2D boundingBox()
    {
        throw new UnboundedGeometryException(this);
    }
}
