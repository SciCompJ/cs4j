/**
 * 
 */
package net.sci.geom.geom2d.polygon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.sci.geom.geom2d.AffineTransform2D;
import net.sci.geom.geom2d.Point2D;

/**
 * A polygonal region whose boundary is a single linear ring.
 * 
 * @author dlegland
 * 
 * @see LinearRing2D
 */
public interface Polygon2D extends PolygonalDomain2D
{
    // ===================================================================
    // Static factories    
    
    /**
     * Creates a new instance of Polygon2D from a collection of vertices.
     * 
     * @param vertices
     *            the vertices stored in an array of Point2D
     * @return a new polygon formed by the vertices
     */
    public static Polygon2D create(Collection<? extends Point2D> vertices)
    {
        return new DefaultPolygon2D(vertices);
    }

    /**
     * Creates a new instance of Polygon2D from a collection of vertices.
     * 
     * @param vertices
     *            the vertices stored in an array of Point2D
     * @return a new polygon formed by the vertices
     */
    public static Polygon2D create(Point2D... vertices)
    {
        return new DefaultPolygon2D(vertices);
    }
    
    /**
     * Creates a new instance of Polygon2D from the x and y coordinates of each vertex.
     * 
     * @param xcoords
     *            the x coordinate of each vertex
     * @param ycoords
     *            the y coordinate of each vertex
     * @return a new polygon formed by the vertices
     */
    public static Polygon2D create(double[] xcoords, double[] ycoords)
    {
        return new DefaultPolygon2D(xcoords, ycoords);
    }
    
    
    // ===================================================================
    // Default implementations 
    
    public default Point2D centroid()
    {
        // accumulators
        double sx = 0.0;
        double sy = 0.0;
        double area = 0.0;
        
        // identify coordinates of the last vertex
        Iterator<Point2D> iter = vertexPositions().iterator();
        Point2D p0 = iter.next();
        while(iter.hasNext())
        {
            p0 = iter.next();
        }
        double x0 = p0.getX();
        double y0 = p0.getY();
        
        // iterate over edges
        iter = vertexPositions().iterator();
        while(iter.hasNext())
        {
            // coordinates of current vertex
            Point2D p1 = iter.next();
            double x1 = p1.getX();
            double y1 = p1.getY();
            
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
    
    
    // ===================================================================
    // Specialization of the PolygonalDomain2D interface    
    
    @Override
    public default Polygon2D transform(AffineTransform2D trans)
    {
        ArrayList<Point2D> newVertices = new ArrayList<>(this.vertexNumber());
        for (Point2D point : this.vertexPositions())
        {
            newVertices.add(point.transform(trans));
        }
        return Polygon2D.create(newVertices);
    }

    @Override
    public Polygon2D complement();
    
    @Override
    public LinearRing2D boundary();
    
}
