/**
 * 
 */
package net.sci.geom.polygon2d;

import java.util.List;

import net.sci.geom.geom2d.LineSegment2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.Vector2D;

/**
 * Interface for polygonal geometries in the plane: polygons or polygonal chains
 * (polylines).
 * 
 * A polygonal geometry is defined by a set of vertices, that are associated to
 * positions. Several vertices may share the same position, but in general
 * adjacent vertices are expected to be located at different positions.
 * 
 * The {@code Edge} interface is used to describe adjacent vertices. Note that
 * depending on implementations, the index of the vertices within the list may
 * not be related to the vertex adjacency.
 */
public interface Polygonal2D
{
    // ===================================================================
    // Management of vertices
    
    /**
     * Returns the number of vertices.
     * 
     * @return the number of vertices
     */
    public int vertexCount();

    public Iterable<? extends Vertex> vertices();

    public void addVertex(Point2D vertexPosition);
    
    public void removeVertex(int vertexIndex);
    
    /**
     * Returns the vertex at a given index.
     * 
     * @param index
     *            the vertex index, between 0 and (vertexCount-1)
     * @return the vertex at the specified index.
     */
    public Vertex vertex(int index);
    
    /**
     * Returns the inner collection of points that contain the vertex
     * positions.
     * 
     * @return a list of points corresponding to the vertex positions
     */
    public List<Point2D> vertexPositions();

    /**
     * Returns the position of a vertex identified by its index.
     * 
     * @param index
     *            the index of the vertex
     * @return the position of the vertex
     */
    public Point2D vertexPosition(int index);
    
    
    // ===================================================================
    // Inner interfaces declaration
    
    
    /**
     * A vertex of a polyline or polygon, used to encapsulate the position.
     * 
     * The normal vector at the vertex may also be defined (optional operation).
     * 
     * @see Edge
     */
    public static interface Vertex
    {
        /**
         * @return the position of this vertex, as a Point2D.
         */
        public Point2D position();
        
        /**
         * Returns the normal computed at this vertex (optional operation). 
         * Default behavior is to throw an Exception.
         * 
         * @return the normal computed at this vertex, as a Vector2D.
         */
        public default Vector2D normal()
        {
            throw new RuntimeException("Unimplemented operation");
        }
    }
    
    /**
     * An edge of a polyline or polygon, defined by the source and target vertices.
     * 
     * @see Vertex
     */
    public static interface Edge
    {
        /**
         * @return the source vertex of this edge.
         */
        public Vertex source();
        
        /**
         * @return the target vertex of this edge.
         */
        public Vertex target();
        
        /**
         * @return the line segment geometry corresponding to this edge.
         */
        public LineSegment2D curve();
    }
}
