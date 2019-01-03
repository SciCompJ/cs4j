/**
 * 
 */
package net.sci.geom.geom2d.polygon;

import java.util.ArrayList;
import java.util.Collection;

import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.line.LineSegment2D;
import net.sci.geom.geom2d.transform.AffineTransform2D;

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
    

    // ===================================================================
    // Inner interfaces
    
    /**
     * A vertex of a polygon, or a polygonal domain.
     */
    public interface Vertex
    {
        /**
         * @return the position of this vertex, as a Point2D
         */
        public Point2D position();

        /**
         * Changes the position of this vertex.
         * 
         * @param newPos
         *            the new position of this vertex.
         */
        public void setPosition(Point2D newPos);
        
        /**
         * @return the next vertex along the boundary of the polygon
         */
        public Vertex next();
    }
    
    /**
     * An edge of a polygon, between two vertices.
     */
    public interface Edge
    {
        /**
         * @return the source vertex of this edge
         */
        public Vertex source();
        
        /**
         * @return the target vertex of this edge
         */
        public Vertex target();
        
        /**
         * @return the line segment representing this edge
         */
        public LineSegment2D lineSegment();
    }
}
