/**
 * 
 */
package net.sci.geom.geom2d.polygon;

import java.util.Collection;
import java.util.List;

import net.sci.geom.geom2d.Bounds2D;
import net.sci.geom.geom2d.Domain2D;
import net.sci.geom.geom2d.Point2D;

/**
 * A rectangular box with four sides parallel to the main axes.
 * 
 * @see net.sci.geom.geom2d.Bounds2D
 */
public class Box2D implements Polygon2D
{
    // ===================================================================
    // class variables

    double xmin;
    double ymin;
    double xmax;
    double ymax;
    
    
    // ===================================================================
    // constructors

    /**
     * Main constructor, given bounds for x coordinate, then bounds for y
     * coordinate.
     * 
     * @param xmin
     *            the minimum value along the first dimension
     * @param xmax
     *            the maximum value along the first dimension
     * @param ymin
     *            the minimum value along the second dimension
     * @param ymax
     *            the maximum value along the second dimension
     */
    public Box2D(double xmin, double xmax, double ymin, double ymax)
    {
        this.xmin = xmin;
        this.xmax = xmax;
        this.ymin = ymin;
        this.ymax = ymax;
    }
    
    /**
     * Constructor from 2 points, giving extreme coordinates of the box.
     * 
     * @param p1
     *            a point corresponding to a corner of the box
     * @param p2
     *            a point corresponding to a corner of the box, opposite of p1
     */
    public Box2D(Point2D p1, Point2D p2)
    {
        double x1 = p1.x();
        double y1 = p1.y();
        double x2 = p2.x();
        double y2 = p2.y();
        this.xmin = Math.min(x1, x2);
        this.xmax = Math.max(x1, x2);
        this.ymin = Math.min(y1, y2);
        this.ymax = Math.max(y1, y2);
    }
    
    
    // ===================================================================
    // Accessors
    
    /**
     * Returns the center of this, as an instance of Point2D.
     * 
     * @return the center of the box.
     */
    public Point2D center()
    {
        return new Point2D((this.xmin + this.xmax) / 2, (this.ymin + this.ymax) / 2);
    }
    
    /**
     * Returns the size of the box along the x-direction.
     * 
     * @return the size of the box along the x-direction.
     */
    public double sizeX()
    {
        return this.xmax - this.xmin;
    }
    
    /**
     * Returns the size of the box along the y-direction.
     * 
     * @return the size of the box along the y-direction.
     */
    public double sizeY()
    {
        return this.ymax - this.ymin;
    }
    
    
    // ===================================================================
    // Implementation of the Polygon2D interface
    
    @Override
    public Iterable<LinearRing2D> rings()
    {
        return List.of(boundary());
    }

    @Override
    public double signedArea()
    {
        double dx = xmax - xmin;
        double dy = ymax - ymin;
        return dx * dy;
    }

    @Override
    public Collection<Point2D> vertexPositions()
    {
        return List.of(
                new Point2D(xmin, ymin),
                new Point2D(xmax, ymin),
                new Point2D(xmax, ymax),
                new Point2D(xmin, ymax)
                );
    }

    @Override
    public int vertexCount()
    {
        return 4;
    }


    @Override
    public void addVertex(Point2D vertexPosition)
    {
        throw new RuntimeException("Can not add vertices to a Box2D");
    }
    
    /**
     * Returns a new polygon whose boundary is the same as this box, but in
     * reverse direction.
     * 
     * @return the complement polygon of this box
     */
    @Override
    public Polygon2D complement()
    {
        return Polygon2D.create(
                new Point2D(xmin, ymin),
                new Point2D(xmin, ymax),
                new Point2D(xmax, ymax),
                new Point2D(xmax, ymin)
                );
    }

    @Override
    public LinearRing2D boundary()
    {
        return LinearRing2D.create(vertexPositions());
    }
    

    // ===================================================================
    // Implementation of the Geometry2D interface
    
    @Override
    public boolean contains(Point2D point)
    {
        return contains(point.x(), point.y());
    }

    @Override
    public boolean contains(double x, double y)
    {
        if (x < xmin || x > xmax) return false;
        if (y < ymin || x > ymax) return false;
        return true;
    }
    
    @Override
    public boolean contains(Point2D point, double eps)
    {
        if (point.x() < xmin - eps || point.x() > xmax + eps) return false;
        if (point.y() < ymin - eps || point.y() > ymax + eps) return false;
        return true;
    }

    @Override
    public double distance(double x, double y)
    {
        return Math.max(boundary().signedDistance(x, y), 0);
    }

    @Override
    public Bounds2D bounds()
    {
       return new Bounds2D(xmin, xmax, ymin, ymax);
    }

    @Override
    public boolean isBounded()
    {
        return true;
    }
    
    @Override
    public Domain2D duplicate()
    {
        return new Box2D(xmin, xmax, ymin, ymax);
    }
}
