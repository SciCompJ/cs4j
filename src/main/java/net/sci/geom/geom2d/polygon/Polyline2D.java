/**
 * 
 */
package net.sci.geom.geom2d.polygon;

import java.util.Collection;
import java.util.Iterator;

import net.sci.geom.geom2d.Box2D;
import net.sci.geom.geom2d.Curve2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.line.LineSegment2D;

/**
 * <p>
 * A Polyline2D is a curve composed of several line segments. It can be open or
 * closed. Open polylines are called LineString2D. Closed polylines are called
 * LineString2D.
 * </p>
 * 
 * @author dlegland
 * @see LineString2D
 */
public interface Polyline2D extends Curve2D
{
    // ===================================================================
    // Methods for managing vertices
    
    /**
     * Returns a pointer to the vertices.
     */
    public Collection<Point2D> vertices();
    
    /**
     * Returns the number of vertices.
     * 
     * @return the number of vertices
     */
    public int vertexNumber();


    public Iterator<LineSegment2D> edgeIterator();

    /**
     * Returns the polyline composed with the same vertices, but in reverse order.
     * 
     * @return the polyline with same vertices but in reverse order.
     */
    public Polyline2D reverse();
    
    // ===================================================================
    // Implementation of the Geometry2D interface 

    @Override
    public default boolean contains(Point2D point, double eps)
    {
        // Iterate on the line segments forming the polyline
        Iterator<LineSegment2D> iter = edgeIterator();
        while(iter.hasNext())
        {
            if (iter.next().contains(point, eps))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Iterate over edges to find the minimal distance between the test point
     * and this polyline.
     * 
     * @param point
     *            the point to test
     * @return the distance to the polyline
     */
    public default double distance(Point2D point)
    {
        double minDist = Double.POSITIVE_INFINITY;
        
        // Iterate on the line segments forming the polyline
        Iterator<LineSegment2D> iter = edgeIterator();
        while(iter.hasNext())
        {
            double dist = iter.next().distance(point);
            if (dist < minDist)
            {
                minDist = dist;
            }
        }
        
        return minDist;
    }
    
    // ===================================================================
    // Implementation of the Geometry interface 

    /**
     * Returns true, as a linear ring is bounded by definition.
     */
    public default boolean isBounded()
    {
        return true;
    }

    public default Box2D boundingBox()
    {
        // initialize with extreme values
        double xmin = Double.POSITIVE_INFINITY;
        double xmax = Double.NEGATIVE_INFINITY;
        double ymin = Double.POSITIVE_INFINITY;
        double ymax = Double.NEGATIVE_INFINITY;
        
        // compute min/max for each coordinate
        for (Point2D vertex : this.vertices())
        {
            double x = vertex.getX();
            double y = vertex.getY();
            xmin = Math.min(xmin, x);
            xmax = Math.max(xmax, x);
            ymin = Math.min(ymin, y);
            ymax = Math.max(ymax, y);
        }
        
        // return new Bounding Box
        return new Box2D(xmin, xmax, ymin, ymax);
    }
}
