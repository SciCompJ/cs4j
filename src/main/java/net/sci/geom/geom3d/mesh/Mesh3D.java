/**
 * 
 */
package net.sci.geom.geom3d.mesh;

import java.util.Collection;

import net.sci.geom.geom3d.Point3D;
import net.sci.geom.geom3d.Vector3D;
import net.sci.geom.geom3d.Geometry3D;

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

    public Collection<? extends Vertex> vertices();

    public Vertex addVertex(Point3D point);
    
    public Collection<Point3D> vertexPositions();
    
//    public Iterator<Point3D> vertexIterator();
    
    /**
     * @return the number of vertices in this mesh.
     */
    public int vertexNumber();
    
    
    // ===================================================================
    // Management of edges ? 
    
    // ===================================================================
    // Management of faces

    public Collection<? extends Face> faces();

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
        public Collection<? extends Face> faces();
        public Collection<? extends Edge> edges();
        
        public Point3D position();
        public Vector3D normal();
    }

    /**
     * Interface representing a face, a mesh element with dimension 2.
     */
    public interface Face
    {
        public Collection<? extends Vertex> vertices();
        public Collection<? extends Edge> edges();
        
        public Vector3D normal();
    }
    
    /**
     * Interface representing an edge, a mesh element with dimension 1.
     */
    public interface Edge
    {
        public Collection<? extends Vertex> vertices();
        public Collection<? extends Face> faces();
    }
}
