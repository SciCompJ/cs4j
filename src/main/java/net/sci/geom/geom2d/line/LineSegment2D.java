/**
 * 
 */
package net.sci.geom.geom2d.line;

import net.sci.geom.geom2d.Box2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.Vector2D;
import net.sci.geom.geom2d.transform.AffineTransform2D;

/**
 * A line segment between two extremity points.
 * 
 * @see StraightLine2D
 * 
 * @author dlegland
 */
public class LineSegment2D implements LinearGeometry2D
{
    // =============================================================
    // class variables

    private double x1;
    private double y1;
    private double x2;
    private double y2;
 
    
    // =============================================================
    // Constructor
    
    public LineSegment2D(Point2D p1, Point2D p2)
    {
        this.x1 = p1.getX();
        this.y1 = p1.getY();
        this.x2 = p2.getX();
        this.y2 = p2.getY();
    }
    
    
    // =============================================================
    // General methods
    
    public double length()
    {
        return Math.hypot(x2 - x1, y2 - y1);
    }
    
    
    // =============================================================
    // Accessors
    
    public Point2D getP1()
    {
        return new Point2D(x1, y1);
    }
    
    public Point2D getP2()
    {
        return new Point2D(x2, y2);
    }

    
    // ===================================================================
    // Implementation of the LinearGeometry interface 

    /**
     * Transforms this line segment with the specified affine transform.
     * 
     * @param trans
     *            an affine transform
     * @return the transformed line segment
     */
    @Override
    public LineSegment2D transform(AffineTransform2D trans)
    {
        return new LineSegment2D(getP1().transform(trans), getP2().transform(trans));
    }

    /**
     * Returns the origin point of this line.
     */
    public Point2D origin() 
    {
        return new Point2D(this.x1, this.y1);
    }

    /**
     * Returns the direction vector of this line.
     */
    public Vector2D direction() 
    {
        return new Vector2D(this.x2 - this.x1, this.y2 - this.y1);
    }


    @Override
    public StraightLine2D supportingLine()
    {
        return new StraightLine2D(this.x1, this.y1, this.x2 - this.x1,  this.y2 - this.y1);
    }

    
    // ===================================================================
    // Methods implementing the Curve2D interface
    
    @Override
    public boolean isClosed()
    {
        return false;
    }
    
    
    // ===================================================================
    // Implements the Geometry2D interface

    public boolean contains(Point2D point, double eps) 
    {
        if (!supportContains(point, eps))
            return false;

        // compute position on the support line
        double t = positionOnLine(point.getX(), point.getY());

        if (t < -eps)
            return false;
        if (t - 1 > eps)
            return false;

        return true;
    }

    /**
     * Returns true if the specified point lies on the line covering the object,
     * with the given precision.
     * 
     * @see StraightLine2D.contains(Point2D, double)
     */
    private boolean supportContains(Point2D point, double eps) 
    {
        double dx = this.x2 - this.x1;
        double dy = this.y2 - this.y1;
        
        double denom = Math.hypot(dx, dy);
        if (denom < eps)
        {
            throw new DegeneratedLine2DException(this);
        }
        
        double x = point.getX();
        double y = point.getY();
        return Math.sqrt(Math.abs((x - this.x1) * dy - (y - this.y1) * dx)) / denom < eps;
    }

    /**
     * Computes position on the line of the given point. 
     * The position is the number t such that if the point
     * belong to the line, it location is given by x=x0+t*dx and y=y0+t*dy.
     * <p>
     * If the point does not belong to the line, the method returns the position
     * of its projection on the line.
     * 
     * Assumes a non-degenerated line.
     */
    private double positionOnLine(double x, double y) 
    {
        double dx = this.x2 - this.x1;
        double dy = this.y2 - this.y1;
        double denom = dx * dx + dy * dy;
        return ((y - this.y1) * dy + (x - this.x1) * dx) / denom;
    }

    @Override
    public double distance(double x, double y)
    {
        // In case of line segment with same extremities, computes distance to initial point 
        if (length() < 100 * Double.MIN_VALUE)
        {
            return Math.hypot(this.x1 - x, this.y1 - y);
        }
        
        // compute position on the supporting line
        StraightLine2D line = this.supportingLine();
        double t = line.projectedPosition(x, y);

        // clamp with parameterization bounds of edge
        t = Math.max(Math.min(t, 1), 0);
        
        // compute position of projected point on the edge
        Point2D proj = line.point(t);
        
        // return distance to projected point
        return proj.distance(x, y);
    }

    
    // ===================================================================
    // Implements the Geometry interface

    /**
     * Returns true, as a line segment is bounded by definition.
     */
    public boolean isBounded()
    {
        return true;
    }

    @Override
    public Box2D boundingBox()
    {
        return new Box2D(getP1(), getP2());
    }
}
