/**
 * 
 */
package net.sci.geom.geom2d;

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
    // Implements the Geometry interface

    @Override
    public Box2D boundingBox()
    {
        return new Box2D(getP1(), getP2());
    }
}
