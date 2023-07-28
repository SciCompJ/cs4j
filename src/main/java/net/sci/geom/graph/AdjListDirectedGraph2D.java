/**
 * 
 */
package net.sci.geom.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import net.sci.geom.geom2d.LineSegment2D;
import net.sci.geom.geom2d.Point2D;

/**
 * Implements a directed Graph2D by keeping for each vertex the list of adjacent
 * in and out edges.
 * 
 * @see SimpleGraph2D
 * 
 * @author dlegland
 *
 */
public class AdjListDirectedGraph2D implements DirectedGraph2D
{
    // ===================================================================
    // class variables
    
    /**
     * The vertices of the graph.
     */
    ArrayList<Vertex> vertices;

    /**
     * The array of edges. Each edge contains indices of source and target vertices.
     */
    ArrayList<Edge> edges = null;

    
    // ===================================================================
    // Constructors

    /**
     * Creates a new empty graph.
     */
    public AdjListDirectedGraph2D()
    {
        this.vertices = new ArrayList<Vertex>();
        this.edges = new ArrayList<Edge>();
    }
    
    /**
     * Creates a new empty graph, allocating enough memory for the specified
     * number of vertices and edges.
     * 
     * @param nVertices
     *            the allocated number of vertices
     * @param nEdges
     *            the allocated number of edges
     */
    public AdjListDirectedGraph2D(int nVertices, int nEdges)
    {
        this.vertices = new ArrayList<Vertex>(nVertices);
        this.edges = new ArrayList<Edge>(nEdges);
    }
    
    
    // ===================================================================
    // New methods for vertices management
    
    /**
     * @param vertex
     *            a vertex belonging to the graph
     * @return the (unmodifiable) list of edges with the specified vertex as
     *         target
     */
    public Collection<Graph2D.Edge> inEdges(Graph2D.Vertex vertex)
    {
        return Collections.unmodifiableCollection(getVertex(vertex).inEdges);
    }
    
    /**
     * @param vertex
     *            a vertex belonging to the graph
     * @return the (unmodifiable) list of edges with the specified vertex as
     *         source
     */
    public Collection<Graph2D.Edge> outEdges(Graph2D.Vertex vertex)
    {
        return Collections.unmodifiableCollection(getVertex(vertex).outEdges);
    }
    
//    public Collection<Graph2D.Vertex> adjacentVertices(Graph2D.Vertex vertex)
//    {
//        ArrayList<Graph2D.Vertex> adjVertices = new ArrayList<>();
//        for (Edge edge : getVertex(vertex).adjacentEdges)
//        {
//            if (edge.v1 != vertex) adjVertices.add(edge.v1);
//            if (edge.v2 != vertex) adjVertices.add(edge.v2);
//        }
//        return adjVertices;
//    }
    
    
    // ===================================================================
    // Vertices management
    
    public int vertexCount()
    {
        return this.vertices.size();
    }
    
    public Iterable<? extends DirectedGraph2D.Vertex> vertices()
    {
        return this.vertices;
    }
    
    /**
     * Add a new vertex to the graph and returns its index.
     * 
     * @param position the position of the vertex
     * @return the index of the new vertex.
     */
    public DirectedGraph2D.Vertex addVertex(Point2D position)
    {
        Vertex v = new Vertex(position);
        v.index = this.vertices.size();
        this.vertices.add(v);
        return v;
    }
        
    /**
     * Cast to local Vertex class.
     * 
     * @param vertex
     *            the Vertex instance
     * @return the same instance casted to local Vertex implementation
     */
    private Vertex getVertex(Graph2D.Vertex vertex)
    {
        if (!(vertex instanceof Vertex))
        {
            throw new IllegalArgumentException("Vertex should be an instance of inner Vertex implementation");
        }
        return (Vertex) vertex;
    }

    
    // ===================================================================
    // Edges management
    
    public int edgeCount()
    {
        return this.edges.size();
    }
    
    @Override
    public Iterable<? extends DirectedGraph2D.Edge> edges()
    {
        return this.edges;
    }
    
    public Vertex sourceVertex(Graph2D.Edge edge)
    {
        return getEdge(edge).v1;
    }
        
    public Vertex targetVertex(Graph2D.Edge edge)
    {
        return getEdge(edge).v2;
    }

    public Edge addEdge(Graph2D.Vertex v1, Graph2D.Vertex v2)
    {
        // create new edge
        Edge edge = new Edge(getVertex(v1), getVertex(v2)); 
        
        // add new edge to graph
        edges.add(edge);
        
        // return edge instance
        return edge;
    }
    
    /**
     * Cast to local Edge class.
     * 
     * @param edge
     *            the Edge instance
     * @return the same instance casted to local Edge implementation
     */
    private Edge getEdge(Graph2D.Edge edge)
    {
        if (!(edge instanceof Edge))
        {
            throw new IllegalArgumentException("Edge should be an instance of inner Edge implementation");
        }
        return (Edge) edge;
    }
    
    
    // ===================================================================
    // Implementation of the Geometry2D interface
    
    /**
     * Returns true, as a graph is bounded by definition.
     * 
     * @return true
     */
    @Override
    public boolean isBounded()
    {
        return true;
    }

    /**
     * Returns true if the point belong to one of the edges of the graph, up to
     * the specified precision.
     */
    @Override
    public boolean contains(Point2D point, double eps)
    {
        for (Edge edge : edges)
        {
            if (edge.curve().contains(point, eps))
                return true;
        }

        return false;
    }

    /**
     * Returns the distance to the nearest edge.
     */
    @Override
    public double distance(double x, double y)
    {
        double minDist = Double.POSITIVE_INFINITY;
        for (Edge edge : edges)
        {
            minDist = Math.min(minDist, edge.curve().distance(x, y));
        }

        return minDist;
    }
    
    
    // ===================================================================
    // Inner classes

    /**
     * Inner class for representing vertices.
     */
    public class Vertex implements DirectedGraph2D.Vertex
    {
        /** the index of the vertex */
        int index;
        
        /** The position of this vertex */
        Point2D position;
        
        /** The list of edges with this vertex as target */
        ArrayList<Edge> inEdges;
        
        /** The list of edges with this vertex as source */
        ArrayList<Edge> outEdges;
        
        public Vertex(Point2D position)
        {
            this.position = position;
            this.inEdges = new ArrayList<Edge>(2);
            this.outEdges = new ArrayList<Edge>(2);
        }
        

        @Override
        public int inDegree()
        {
            return inEdges.size();
        }

        @Override
        public Iterable<? extends DirectedGraph2D.Edge> inEdges()
        {
            return Collections.unmodifiableCollection(inEdges);
        }

        @Override
        public int outDegree()
        {
            return outEdges.size();
        }

        @Override
        public Iterable<? extends DirectedGraph2D.Edge> outEdges()
        {
            return Collections.unmodifiableCollection(outEdges);
        }
        
        @Override
        public Point2D position()
        {
            return this.position;
        }
        
        // ===================================================================
        // Update display
        
        @Override
        public String toString()
        {
            return "Vertex(" + this.position.x() + ", " + this.position.y() + ")";
        }
        
        // ===================================================================
        // Override equals and hashcode to allow indexing
        
        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof Vertex))
            {
                return false;
            }
            
            Vertex that = (Vertex) obj;
            return this.index == that.index;
        }
        
        @Override
        public int hashCode()
        {
            return this.index + 17;
        }
    }

    /**
     * Inner class for representing edges.
     */
    public class Edge implements DirectedGraph2D.Edge, Comparable<Edge>
    {
        /** The first (source) vertex of this edge.*/
        Vertex v1;
        
        /** The second (target) vertex of this edge.*/
        Vertex v2;
        
        public Edge(Vertex v1, Vertex v2)
        {
            this.v1 = v1;
            this.v2 = v2;
            
            // update in and out edges
            v1.outEdges.add(this);
            v2.inEdges.add(this);
        }

        @Override
        public DirectedGraph2D.Vertex source()
        {
            return v1;
        }

        @Override
        public DirectedGraph2D.Vertex target()
        {
            return v2;
        }

        public LineSegment2D curve()
        {
            return new LineSegment2D(v1.position, v2.position);
        }
       
        /**
         * Implements compareTo to allows for fast indexing.
         */
        @Override
        public int compareTo(Edge that)
        {
            int diff = this.v1.index - that.v1.index;
            if (diff != 0)
                return diff;
            return this.v2.index - that.v2.index;
        }
        

        // ===================================================================
        // Update display
        
        @Override
        public String toString()
        {
            return "Edge(V" + v1.index + ",V" + v2.index + ")";
        }
        
        // ===================================================================
        // Override equals and hashcode to allow indexing
        
        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof Edge))
            {
                return false;
            }
            
            Edge that = (Edge) obj;
            if (this.v1 != that.v1) return false;
            if (this.v2 != that.v2) return false;
            return true;
        }
        
        @Override
        public int hashCode()
        {
            int hash = 1;
            hash = hash * 17 + v1.index;
            hash = hash * 17 + v2.index;
            return hash;
        }
    }
    
    
    public static final void main(String[] args)
    {
        Point2D p1 = new Point2D(10, 10);
        Point2D p2 = new Point2D(20, 10);
        Point2D p3 = new Point2D(20, 20);
        Point2D p4 = new Point2D(10, 20);
        Point2D p5 = new Point2D(27, 15);
        
        AdjListDirectedGraph2D graph = new AdjListDirectedGraph2D();
        Graph2D.Vertex v1 = graph.addVertex(p1);
        Graph2D.Vertex v2 = graph.addVertex(p2);
        Graph2D.Vertex v3 = graph.addVertex(p3);
        Graph2D.Vertex v4 = graph.addVertex(p4);
        Graph2D.Vertex v5 = graph.addVertex(p5);
        
        graph.addEdge(v1, v2);
        graph.addEdge(v2, v3);
        graph.addEdge(v3, v4);
        graph.addEdge(v4, v1);
        graph.addEdge(v2, v5);
        graph.addEdge(v3, v5);
        
        System.out.println("nv = " + graph.vertexCount());
        System.out.println("ne = " + graph.edgeCount());
    }

}
