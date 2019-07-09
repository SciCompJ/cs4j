/**
 * 
 */
package net.sci.geom.geom3d.mesh;

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
    // Management of vertices

    /**
     * @return the collection of vertices within this mesh.
     */
    public Vertices vertices();

    /**
     * @return the number of vertices in this mesh.
     */
    public int vertexNumber();

    public Vertex addVertex(Point3D point);
    
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
    // Management of edges ? 
    
    /**
     * Returns the collection of edges within this mesh (optional operation).
     * 
     * @return the collection of edges within this mesh.
     */
    public Edges edges();

    /**
     * @return the number of edges in this mesh.
     */
    public int edgeNumber();

    // ===================================================================
    // Management of faces

    /**
     * @return the collection of faces within this mesh.
     */
    public Faces faces();

    /**
     * @return the number of faces in this mesh.
     */
    public int faceNumber();

    
    // ===================================================================
    // Inner interfaces

    // Idea: for each type of element, provide methods for investigating the
    // topology (i.e. adjacent elements of other dimensions), and methods for
    // investigating the geometry (area, length, position...)
    
    /**
     * Interface representing a vertex, a mesh element with dimension 0.
     */
    public interface Vertex
    {
        /**
         * @return the collection of faces adjacent to this vertex.
         */
        public Collection<? extends Face> faces();

        /**
         * @return the collection of edges adjacent to this vertex.
         */
        public Collection<? extends Edge> edges();
        
        /**
         * @return the position of this vertex, as a 3D Point
         */
        public Point3D position();

        /**
         * @return the normal of this vertex, as a 3D Vector
         */
        public default Vector3D normal()
        {
            Vector3D normal = new Vector3D();
            for (Face face : this.faces())
            {
                normal.plus(face.normal());
            }
            return normal.normalize();
        }
    }

    /**
     * The collection of vertices stored in a mesh.
     */
    public interface Vertices extends Iterable<Vertex>
    {
        public int size();
    }
    
    /**
     * Interface representing a face, a mesh element with dimension 2.
     */
    public interface Face
    {
        /**
         * @return the collection of vertices adjacent to this face.
         */
        public Collection<? extends Vertex> vertices();

        /**
         * @return the collection of edges adjacent to this face.
         */
        public Collection<? extends Edge> edges();
        
        /**
         * @return the 3D polygon representing this face.
         */
        public Polygon3D polygon();
        
        /**
         * @return the normal of this face.
         */
        public Vector3D normal();
    }
    
    /**
     * The collection of faces stored in a mesh.
     */
    public interface Faces extends Iterable<Face>
    {
        public int size();
    }
    
    /**
     * Interface representing an edge, a mesh element with dimension 1.
     */
    public interface Edge
    {
        /**
         * @return the collection of vertices adjacent to this edge.
         */
        public Collection<? extends Vertex> vertices();
        
        /**
         * @return the source vertex of this edge
         */
        public Vertex source();
        
        /**
         * @return the target vertex of this edge
         */
        public Vertex target();

        /**
         * @return the collection of faces adjacent to this edge.
         */
        public Collection<? extends Face> faces();
        
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
            double x = p1.getX() + p2.getX();
            double y = p1.getY() + p2.getY();
            double z = p1.getZ() + p2.getZ();
            return new Point3D(x, y, z);
        }

        /**
         * @return the line segment that represents this edge.
         */
        public LineSegment3D curve();
    }
    
    /**
     * The collection of edges stored in a mesh.
     */
    public interface Edges extends Iterable<Edge>
    {
        public int size();
    }
    
}
