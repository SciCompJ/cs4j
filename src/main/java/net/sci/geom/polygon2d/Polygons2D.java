/**
 * 
 */
package net.sci.geom.polygon2d;

import java.util.Collection;
import java.util.Iterator;

import net.sci.geom.geom2d.Point2D;
import net.sci.geom.polygon2d.process.GiftWrappingConvexHull2D;

/**
 * A collection of static methods operating on polygons.
 * 
 * @author dlegland
 *
 */
public class Polygons2D
{
    /**
     * Computes the centroid of the specified polygon. 
     * 
     * @param polygon
     *            the polygon
     * @return the centroid of the polygon, as a Point2D.
     */
    public static final Point2D centroid(Polygon2D polygon)
    {
        // accumulators
        double sx = 0.0;
        double sy = 0.0;
        double area = 0.0;
        
        // identify coordinates of the last vertex
        Iterator<Point2D> iter = polygon.vertexPositions().iterator();
        Point2D p0 = iter.next();
        while(iter.hasNext())
        {
            p0 = iter.next();
        }
        double x0 = p0.x();
        double y0 = p0.y();
        
        // iterate over edges
        iter = polygon.vertexPositions().iterator();
        while(iter.hasNext())
        {
            // coordinates of current vertex
            Point2D p1 = iter.next();
            double x1 = p1.x();
            double y1 = p1.y();
            
            // update accumulators
            double common = x0 * y1 - x1 * y0;
            sx += (x0 + x1) * common;
            sy += (y0 + y1) * common;
            area += common / 2;
            
            // prepare for next edge
            x0 = x1;
            y0 = y1;
        }
        
        // compute centroid coordinates
        return new Point2D(sx / 6 / area, sy / 6 / area);
    }
    
    /**
     * Computes the signed area of the specified polygon. Signed area is
     * positive if polygon is oriented counter-clockwise, and negative is
     * polygon is oriented clockwise.
     * 
     * @param polygon
     *            the polygon
     * @return the signed area of the polygon.
     */
    public static final double signedArea(Polygon2D polygon)
    {
        // accumulator
        double area = 0.0;
        
        // identify coordinates of the last vertex
        Iterator<Point2D> iter = polygon.vertexPositions().iterator();
        Point2D p0, p1 = iter.next();
        while(iter.hasNext())
        {
            p1 = iter.next();
        }
        
        // iterate over edges
        iter = polygon.vertexPositions().iterator();
        while(iter.hasNext())
        {
            // vertex coordinates of current edge
            p0 = p1;
            p1 = iter.next();
            
            // update accumulators
            area += (p0.x() * p1.y() - p1.x() * p0.y()) / 2;
        }
        
        return area;
    }
    
    /**
     * Computes the convex hull of a set of points and return the result as a
     * single Polygon2D.
     * 
     * Uses Jarvis algorithm, also known as "Gift wrap" algorithm.
     * 
     * 
     * @param points
     *            a set of points in the 2D space
     * @return the convex hull of the points, as a Polygon2D
     * 
     * @see net.sci.geom.polygon2d.process.GiftWrappingConvexHull2D
     */
    public static final Polygon2D convexHull(Collection<? extends Point2D> points)
    {
        if (points.size() < 3) 
        {
            throw new RuntimeException("Requires at least three points to compute a convex hull");
        }

        GiftWrappingConvexHull2D algo = new GiftWrappingConvexHull2D();
        return algo.process(points);
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private Polygons2D()
    {
    }
}
