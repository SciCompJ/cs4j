/**
 * 
 */
package net.sci.geom.mesh2d;

import java.util.Collection;

import net.sci.geom.geom2d.Geometry2D;
import net.sci.geom.geom2d.LineSegment2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.polygon2d.Polygon2D;

/**
 * A polygonal mesh in 2D plane. Meshes are defined by vertices, defined by a
 * position as a Point2D, and faces that connect three or more vertices.
 */
public interface Mesh2D extends Geometry2D
{
    // ===================================================================
    // Topological queries
    
    /**
     * Returns the collection of faces adjacent to a given vertex (optional
     * operation).
     * 
     * @param vertex
     *            the vertex
     * @return the faces adjacent to the specified vertex
     * @throws UnsupportedOperationException
     *             if topological queries are not supported by this mesh
     */
    public Collection<? extends Face> vertexFaces(Vertex vertex);
    
    /**
     * Returns the neighbor vertices of a given vertex (optional operation).
     * Neighbors correspond to a vertex that shares either a face or an edge.
     * 
     * @param vertex
     *            the reference vertex
     * @return the neighbors of the reference vertex
     * @throws UnsupportedOperationException
     *             if topological queries are not supported by this mesh
     */
    public Collection<? extends Vertex> vertexNeighbors(Vertex vertex);
    
    /**
     * Returns the collection of vertices adjacent to a given face.
     * 
     * @param face
     *            the face
     * @return the vertices adjacent to the specified face
     */
    public Collection<? extends Vertex> faceVertices(Face face);
    

    // ===================================================================
    // Management of vertices

    /**
     * @return the number of vertices in this mesh.
     */
    public int vertexCount();

    /**
     * @return the collection of vertices within this mesh.
     */
    public Iterable<? extends Vertex> vertices();

    /**
     * Adds a new vertex to this mesh (optional operation).
     * 
     * @param point
     *            the position of the vertex to add
     * @return the newly created vertex
     * @throws UnsupportedOperationException
     *             if adding vertices is not supported by this mesh
     */
    public Vertex addVertex(Point2D point);

    /**
     * Removes a vertex from this mesh (optional operation). The vertex should
     * not belong to any face or any edge.
     * 
     * @param vertex
     *            the vertex to remove.
     * @throws UnsupportedOperationException
     *             if removing vertices is not supported by this mesh
     */
    public void removeVertex(Vertex vertex);
    
    /**
     * Returns the positions of the vertices defining this mesh.
     * 
     * @return the vertex positions of this mesh
     */

    public Iterable<Point2D> vertexPositions();
    
    
    // ===================================================================
    // Management of faces

    /**
     * @return the number of faces in this mesh.
     */
    public int faceCount();

    /**
     * @return the collection of faces within this mesh.
     */
    public Iterable<? extends Face> faces();

    /**
     * Removes a face from this mesh (optional operation).
     * 
     * @param face
     *            the face to remove.
     * @throws UnsupportedOperationException
     *             if removing vertices is not supported by this mesh
     */
    public void removeFace(Face face);

    @Override
    public Mesh2D duplicate();
    
    
    // ===================================================================
    // Inner interfaces

    /**
     * Interface representing a vertex, a mesh element with dimension 0.
     */
    public interface Vertex
    {
        /**
         * @return the position of this vertex, as a 2D Point
         */
        public Point2D position();
    }
    
    /**
     * Interface representing a face, a mesh element with dimension 2.
     */
    public interface Face
    {
        /**
         * Counts the number of vertices of this face.
         * 
         * @return the number of vertices of this face.
         */
        public int vertexCount();
        
        /**
         * @return the 2D polygon representing this face.
         */
        public Polygon2D polygon();
        
        public Iterable<? extends Vertex> vertices();
    }
    
    
    /**
     * Interface representing an edge, a mesh element with dimension 1.
     * The management of edges by Mesh implementations is optional.
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
         * @return the length of this edge.
         */
        public default double length()
        {
            Point2D p1 = source().position();
            Point2D p2 = target().position();
            return p1.distance(p2);
        }
        
        /**
         * @return the center of this edge.
         */
        public default Point2D center()
        {
            return Point2D.interpolate(source().position(), target().position(), 0.5);
        }

        /**
         * @return the line segment that represents this edge.
         */
        public default LineSegment2D curve()
        {
            return LineSegment2D.of(source().position(), target().position());
        }
    }
    

}
