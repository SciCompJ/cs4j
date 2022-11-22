/**
 * 
 */
package net.sci.geom.graph;

import net.sci.geom.geom2d.Point2D;

/**
 * Specialization of the Graph2D interface for directed graphs.
 * 
 * Vertices of a directed graph provide methods for accessing in and out edges.
 * The order of vertices during edge creation specifies the direction of the
 * edge.
 * 
 * @author dlegland
 */
public interface DirectedGraph2D extends Graph2D
{
    // ===================================================================
    // Vertex management
    
    public Iterable<? extends Vertex> vertices();
    
    /**
     * Add a new vertex to the graph and returns its index.
     * 
     * @param position the position of the vertex
     * @return the index of the new vertex.
     */
    public Vertex addVertex(Point2D position);
        
    public Iterable<? extends Edge> edges();
    

    // ===================================================================
    // Inner interfaces

    /**
     * A vertex of a directed graph.
     */
    public interface Vertex extends Graph2D.Vertex
    {
        /**
         * @return the number of edges with this vertex as target.
         */
        public int inDegree();
        
        /**
         * @return all the edges of the graph with this vertex as target.
         */
        public Iterable<? extends Edge> inEdges();
        
        /**
         * @return the number of edges with this vertex as source.
         */
        public int outDegree();
        
        /**
         * @return all the edges of the graph with this vertex as source.
         */
        public Iterable<? extends Edge> outEdges();
    }
    
    /**
     * An edge of a directed graph.
     */
    public interface Edge extends Graph2D.Edge
    {
        /**
         * @return the source vertex of this edge
         */
        public Vertex source();
        
        /**
         * @return the target vertex of this edge
         */
        public Vertex target();
    }

}
