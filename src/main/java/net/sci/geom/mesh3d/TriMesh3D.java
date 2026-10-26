/**
 * 
 */
package net.sci.geom.mesh3d;

import java.util.Collection;

/**
 * A 3D polygon mesh that contains only triangle faces.
 * 
 * Mostly works as a "tagging" interface to facilitate identification of
 * triangle meshes, even if more specific methods could be added in the future.
 * 
 * @author dlegland
 */
public interface TriMesh3D extends Mesh3D
{
    // =============================================================
    // New methods

    /**
     * Adds a triangular face defined by references to its three vertices.
     * 
     * @param v1
     *            reference to the first face vertex
     * @param v2
     *            reference to the second face vertex
     * @param v3
     *            reference to the third face vertex
     * @return the index of the newly created face
     */
    public Mesh3D.Face addFace(Mesh3D.Vertex v1, Mesh3D.Vertex v2, Mesh3D.Vertex v3);
    
    
    // =============================================================
    // Specialization of the Mesh3D interface

    @Override
    public Collection<? extends Face> vertexFaces(Vertex vertex);

    @Override
    public Iterable<? extends Face> faces();
    
    
    // =============================================================
    // New inner interfaces

    /**
     * Specialization of the {@code Mesh3D.Face} interface, that assumes the
     * face to contain only three vertices.
     */
    public interface Face extends Mesh3D.Face
    {
        @Override
        public default int vertexCount()
        {
            return 3;
        }
    }
}
