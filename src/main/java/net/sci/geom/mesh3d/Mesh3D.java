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
 * A polygonal mesh in 3D space
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
     * Returns the collection of faces adjacent to a given vertex.
     * 
     * @param vertex
     *            the vertex
     * @return the faces adjacent to the specified vertex
     */
    public Collection<? extends Face> vertexFaces(Vertex vertex);
    
    /**
     * Returns the neighbor vertices of a given vertex. Neighbors correspond to
     * a vertex that shares either a face or an edge.
     * 
     * @param vertex the reference vertex
     * @return the neighbors of the reference vertex
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
    
    /**
     * Returns the collection of edges adjacent to a given face (optional
     * operation).
     * 
     * @param face
     *            the face
     * @return the edges adjacent to the specified face
     */
    public Collection<? extends Edge> faceEdges(Face face);
   

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

    public Vertex addVertex(Point3D point);

    /**
     * Removes a vertex from this mesh. The vertex should not belong to any face or any edge.
     * 
     * @param vertex
     *            the vertex to remove.
     */
    public void removeVertex(Vertex vertex);
    

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
     * Removes a face from this mesh.
     * 
     * @param face
     *            the face to remove.
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
         * @return the normal of this vertex, as a 3D Vector
         */
        public Vector3D normal();
    }
    
    /**
     * Interface representing a face, a mesh element with dimension 2.
     */
    public interface Face
    {
        /**
         * @return the 3D polygon representing this face.
         */
        public Polygon3D polygon();
        
        /**
         * @return the normal of this face.
         */
        public Vector3D normal();
        
        public Iterable<Vertex> vertices();
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
