/**
 * 
 */
package net.sci.geom.geom2d.line;

import net.sci.geom.UnboundedGeometryException;
import net.sci.geom.geom2d.Box2D;
import net.sci.geom.geom2d.Curve2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.Vector2D;

/**
 * @author dlegland
 *
 */
public class StraightLine2D implements Curve2D
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


    // ===================================================================
    // Implementation of the Geometry2D interface 

    /* (non-Javadoc)
     * @see net.sci.geom.geom2d.Geometry2D#contains(net.sci.geom.geom2d.Point2D, double)
     */
    @Override
    public boolean contains(Point2D point, double eps)
    {
        double denom = Math.hypot(this.dx, this.dy);
//        if (denom < eps)
//        {
//            throw new DegeneratedLine2DException(this);
//        }
        double x = point.getX();
        double y = point.getY();
        return Math.sqrt(Math.abs((x - x0) * dy - (y - y0) * dx)) / denom < eps;

    }
    

    // ===================================================================
    // Implementation of the Geometry interface

    /* (non-Javadoc)
     * @see net.sci.geom.Geometry#boundingBox()
     */
    @Override
    public Box2D boundingBox()
    {
        throw new UnboundedGeometryException(this);
    }
    
}
