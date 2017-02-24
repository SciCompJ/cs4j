/**
 * 
 */
package net.sci.geom.geom2d.line;

import net.sci.geom.geom2d.Box2D;
import net.sci.geom.geom2d.Curve2D;
import net.sci.geom.geom2d.Point2D;

/**
 * A line segment between two extremity points.
 * 
 * @author dlegland
 *
 */
public class LineSegment2D implements Curve2D
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
    // Implements the Geometry2D interface

    public boolean contains(Point2D point, double eps) 
    {
        if (!supportingLine().contains(point, eps))
            return false;

        // compute position on the support line
        double t = positionOnLine(point);

        if (t < -eps)
            return false;
        if (t - 1 > eps)
            return false;

        return true;
    }

    /**
     * Computes position on the line of the given point. 
     * The position is the number t such that if the point
     * belong to the line, it location is given by x=x0+t*dx and y=y0+t*dy.
     * <p>
     * If the point does not belong to the line, the method returns the position
     * of its projection on the line.
     */
    private double positionOnLine(Point2D point) 
    {
        double dx = this.x2 - this.x1;
        double dy = this.y2 - this.y1;
        double denom = dx * dx + dy * dy;
        
//        if (Math.abs(denom) < Shape2D.ACCURACY)
//            throw new DegeneratedLine2DException(this);
        double x = point.getX();
        double y = point.getY();
        return ((y - this.y1) * dy + (x - this.x1) * dx) / denom;
    }

    public StraightLine2D supportingLine()
    {
        return new StraightLine2D(this.x1, this.y1, this.x2 - this.x1,  this.y2 - this.y1);
    }

    // ===================================================================
    // Implements the Geometry interface

    @Override
    public Box2D boundingBox()
    {
        return new Box2D(getP1(), getP2());
    }
}
