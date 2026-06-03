/**
 * 
 */
package net.sci.geom.mesh3d;

import java.util.Collection;

import net.sci.geom.geom3d.Geometry3D;
import net.sci.geom.geom3d.LineSegment3D;
import net.sci.geom.geom3d.Point3D;
import net.sci.geom.geom3d.Polygon3D;
import net.sci.geom.geom3d.Vector3D;

/**
 * A polygonal mesh in 3D space. Meshes are defined by vertices, defined by a
 * position as a Point3D, and faces that connect three or more vertices.
 * 
 * Several classes implement {@code Mesh3D}, with variants in:
 * <ul>
 * <li>the number of vertices of each face (3, 4, or arbitrary)</li>
 * <li>the possibility to modify the mesh by adding and/or removing vertices or
 * faces</li>
 * <li>the ability to query topological information from the mesh (e.g. to
 * retrieve the list of faces connected to a vertex)</li>
 * <li>the possibility to manage edges between vertices</li>
 * <li>...</li>
 * </ul>
 * 
 * @author dlegland
 *
 */
public interface Mesh3D extends Geometry3D
{
    // ===================================================================
    // Geometric queries
    
    /**
     * Finds the closest vertex to the input point.
     * 
     * @param point
     *            a query point
     * @return the index of the vertex the closest to query point
     */
    public default Vertex findClosestVertex(Point3D point)
    {
        double minDist = Double.POSITIVE_INFINITY;
        Vertex closest = null;
        for (Vertex v : vertices())
        {
            double dist = v.position().distance(point);
            if (dist < minDist)
            {
                minDist = dist;
                closest = v;
            }
        }
        return closest;
    }
    

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
    public Vertex addVertex(Point3D point);

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

    public Iterable<Point3D> vertexPositions();
    
    
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
    public Mesh3D duplicate();
    
    
    // ===================================================================
    // Inner interfaces

    /**
     * Interface representing a vertex, a mesh element with dimension 0.
     */
    public interface Vertex
    {
        /**
         * @return the position of this vertex, as a 3D Point
         */
        public Point3D position();

        /**
         * Computes or retrieves the normal vector associated to a vertex
         * (optional operation).
         * 
         * @return the normal of this vertex, as a 3D Vector
         * @throws UnsupportedOperationException
         *             if removing vertices is not supported by this mesh
         */
        public Vector3D normal();
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
         * Returns the 3D polygon representing this face.
         * 
         * @return the 3D polygon representing this face.
         */
        public Polygon3D polygon();
        
        /**
         * Returns the normal of this face as a 3D vector.
         * 
         * @return the normal of this face.
         */
        public Vector3D normal();
        
        /**
         * Returns an {@code Iterable} over the vertices of this face.
         * 
         * @return an {@code Iterable} over the vertices of this face.
         */
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
            Point3D p1 = source().position();
            Point3D p2 = target().position();
            return p1.distance(p2);
        }
        
        /**
         * @return the center of this edge.
         */
        public default Point3D center()
        {
            Point3D p1 = source().position();
            Point3D p2 = target().position();
            double x = p1.x() + p2.x();
            double y = p1.y() + p2.y();
            double z = p1.z() + p2.z();
            return new Point3D(x, y, z);
        }

        /**
         * @return the line segment that represents this edge.
         */
        public LineSegment3D curve();
    }
    
}
