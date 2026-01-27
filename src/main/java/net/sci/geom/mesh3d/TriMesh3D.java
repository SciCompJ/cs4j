/**
 * 
 */
package net.sci.geom.mesh3d;

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
    
    public interface Face extends Mesh3D.Face
    {
        @Override
        public default int vertexCount()
        {
            return 3;
        }
    }
}
