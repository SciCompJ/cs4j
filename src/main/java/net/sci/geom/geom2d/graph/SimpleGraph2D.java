/**
 * 
 */
package net.sci.geom.geom2d.graph;

import java.util.ArrayList;

import net.sci.geom.geom2d.Point2D;

/**
 * @author dlegland
 *
 */
public class SimpleGraph2D
{
    ArrayList<Point2D> vertices;
    
    ArrayList<Adjacency> edges; 
    
    /**
     * 
     */
    public SimpleGraph2D()
    {
        this.vertices = new ArrayList<Point2D>();
        this.edges = new ArrayList<Adjacency>();
    }
    
    public int vertexNumber()
    {
        return this.vertices.size();
    }
    
    /**
     * Add a new vertex to the graph and returns its index.
     * 
     * @param position the position of the vertex
     * @return the index of the new vertex.
     */
    public int addVertex(Point2D position)
    {
        this.vertices.add(position);
        return this.vertices.size();
    }
    
    public Point2D getVertexPosition(int index)
    {
        return this.vertices.get(index);
    }
    
    
    public int edgeNumber()
    {
        return this.edges.size();
    }
    
    public int getSourceVertex(int edgeIndex)
    {
        return this.edges.get(edgeIndex).v1;
    }
    
    public int getTargetVertex(int edgeIndex)
    {
        return this.edges.get(edgeIndex).v2;
    }
    
    public int addEdge(int indV1, int indV2)
    {
        int nv = this.vertices.size();
        if (indV1 >= nv || indV1 >= nv)
        {
            throw new IllegalArgumentException("Vertex indices greated than the number of vertices");
        }
        
        Adjacency adj = new Adjacency(indV1, indV2);
        this.edges.add(adj);
        return edges.size();
    }
    
    /**
     * A pair of indices representing an adjacency between two vertices.
     * @author dlegland
     *
     */
    class Adjacency 
    {
        int v1;
        int v2;
        
        public Adjacency(int v1, int v2)
        {
            this.v1 = v1;
            this.v2 = v2;
        }
    }
    
    
    public static final void main(String[] args)
    {
        Point2D p1 = new Point2D(10, 10);
        Point2D p2 = new Point2D(20, 10);
        Point2D p3 = new Point2D(20, 20);
        Point2D p4 = new Point2D(10, 20);
        Point2D p5 = new Point2D(17, 15);
        
        SimpleGraph2D graph = new SimpleGraph2D();
        graph.addVertex(p1);
        graph.addVertex(p2);
        graph.addVertex(p3);
        graph.addVertex(p4);
        graph.addVertex(p5);
        
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(3, 0);
        graph.addEdge(1, 4);
        graph.addEdge(2, 4);
        
        System.out.println("nv = " + graph.vertexNumber());
        System.out.println("ne = " + graph.edgeNumber());
    }
}
