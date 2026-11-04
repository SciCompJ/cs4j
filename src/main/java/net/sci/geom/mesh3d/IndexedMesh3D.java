/**
 * 
 */
package net.sci.geom.mesh3d;

import net.sci.geom.geom3d.Point3D;

/**
 * A mesh that can access vertices and faces using an integer index.
 */
public interface IndexedMesh3D extends Mesh3D
{
    /**
     * Returns the instance of mesh vertex for the given index.
     * 
     * @param index
     *            the vertex index
     * @return the corresponding vertex, if it exists.
     */
    public Vertex getVertex(int index);

    /**
     * Returns the position of the vertex identified by the specified index.
     * 
     * @param index 
     *            the vertex index
     * @return the position of the corresponding index.
     */
    public Point3D vertexPosition(int index);
    
    /**
     * Returns instance of mesh face for the given index.
     * 
     * @param index
     *            the face index
     * @return the corresponding face, if it exists.
     */
    public Face getFace(int index);
    
    /**
     * Returns the vertex indices for the face corresponding to the given index.
     * The size of the vertex index array may vary depending on mesh types.
     * 
     * @param faceIndex
     *            the face index
     * @return the array of vertex indices for the face
     */
    public int[] faceVertexIndices(int index);
}
