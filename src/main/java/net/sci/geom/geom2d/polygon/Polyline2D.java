/**
 * 
 */
package net.sci.geom.geom2d.polygon;

import java.util.Iterator;

import net.sci.geom.geom2d.Box2D;
import net.sci.geom.geom2d.Curve2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.line.LineSegment2D;
import net.sci.geom.geom2d.transform.AffineTransform2D;

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
     * Returns an iterable over the positions of the vertices.
     * 
     * @return the positions of the vertices
     */
    public Iterable<Point2D> vertexPositions();
    
    /**
     * Returns the number of vertices.
     * 
     * @return the number of vertices
     */
    public int vertexNumber();


    public Iterator<LineSegment2D> edgeIterator();


    // ===================================================================
    // method related to Curve2D 

    /**
     * Returns the polyline composed with the same vertices, but in reverse order.
     * 
     * @return the polyline with same vertices but in reverse order.
     */
    public Polyline2D reverse();
    

    // ===================================================================
    // Geometry methods 

    /**
     * Transforms this geometry with the specified affine transform.
     * 
     * @param trans
     *            an affine transform
     * @return the transformed geometry
     */
    public Polyline2D transform(AffineTransform2D trans);

    
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
     * @param x
     *            the x-coordinate of the point to test
     * @param y
     *            the y-coordinate of the point to test
     * @return the distance to the polyline
     */
    public default double distance(double x, double y)
    {
        double minDist = Double.POSITIVE_INFINITY;
        
        // Iterate on the line segments forming the polyline
        Iterator<LineSegment2D> iter = edgeIterator();
        while(iter.hasNext())
        {
            minDist = Math.min(minDist, iter.next().distance(x, y));
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
        for (Point2D vertex : this.vertexPositions())
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
