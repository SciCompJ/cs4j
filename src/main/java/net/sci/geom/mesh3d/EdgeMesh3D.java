/**
 * 
 */
package net.sci.geom.mesh3d;

import java.util.Collection;

/**
 * Provides additional feature for management of edges.
 */
public interface EdgeMesh3D extends Mesh3D
{
    /**
     * Returns the collection of edges adjacent to a given vertex (optional
     * operation).
     * 
     * @param vertex
     *            the vertex
     * @return the edges adjacent to the specified vertex
     */
    public Collection<? extends Edge> vertexEdges(Vertex vertex);

    /**
     * Returns the collection of vertices adjacent to a given edge.
     * 
     * @param edge
     *            the edge
     * @return the vertices adjacent to the specified edge
     */
    public Collection<? extends Vertex> edgeVertices(Edge edge);
    
    /**
     * Returns the collection of faces adjacent to a given edge.
     * 
     * @param edge
     *            the edge
     * @return the faces adjacent to the specified edge
     */
    public Collection<? extends Face> edgeFaces(Edge edge);
    
    /**
     * Returns the collection of edges adjacent to a given face.
     * 
     * @param face
     *            the face
     * @return the edges adjacent to the specified face
     */
    public Collection<? extends Edge> faceEdges(Face face);
    
    
    // ===================================================================
    // Management of edges
    
    /**
     * @return the number of edges in this mesh (optional operation).
     */
    public int edgeCount();

    /**
     * Returns the collection of edges within this mesh (optional operation).
     * 
     * @return the collection of edges within this mesh.
     */
    public Iterable<? extends Edge> edges();

    /**
     * Adds an edge to this mesh structure (optional operation).
     * 
     * @param v1
     *            the source vertex
     * @param v2
     *            the target vertex
     * @return the edge instance
     */
    public Edge addEdge(Vertex v1, Vertex v2);
    
    /**
     * Removes an edge from this mesh (optional operation). The edge should not
     * belong to any face.
     * 
     * @param edge
     *            the edge to remove.
     */
    public void removeEdge(Edge edge);

}
