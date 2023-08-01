/**
 * 
 */
package net.sci.geom.graph;

import java.util.ArrayList;
import java.util.Iterator;

import net.sci.geom.geom2d.Bounds2D;
import net.sci.geom.geom2d.LineSegment2D;
import net.sci.geom.geom2d.Point2D;

/**
 * @author dlegland
 *
 */
public class SimpleGraph2D implements Graph2D
{
    // ===================================================================
    // class variables

    /**
     * The list of vertices.
     */
    ArrayList<Point2D> vertices;

    /**
     * The array of edges. Each edge contains indices of source and target vertices.
     */
    ArrayList<Edge> edges = null;

    
    // ===================================================================
    // Constructors

    /**
     * Creates a new empty graph
     */
    public SimpleGraph2D()
    {
        this.vertices = new ArrayList<Point2D>();
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
    public SimpleGraph2D(int nVertices, int nEdges)
    {
        this.vertices = new ArrayList<Point2D>(nVertices);
        this.edges = new ArrayList<Edge>(nEdges);
    }
    
    
    // ===================================================================
    // Vertices management
    
    public int vertexCount()
    {
        return this.vertices.size();
    }
    
    public Iterable<Graph2D.Vertex> vertices()
    {
        return new Iterable<Graph2D.Vertex>() {
            @Override
            public Iterator<Graph2D.Vertex> iterator()
            {
                return new VertexIterator();
            }
        };
    }
    
    /**
     * Add a new vertex to the graph and returns its index.
     * 
     * @param position the position of the vertex
     * @return the index of the new vertex.
     */
    public Graph2D.Vertex addVertex(Point2D position)
    {
        this.vertices.add(position);
        return new Vertex(this.vertices.size());
    }
    
    public Point2D vertexPosition(int index)
    {
        return this.vertices.get(index);
    }
    
    public Iterator<Point2D> vertexIterator()
    {
        return this.vertices.iterator();
    }
    
    /**
     * Cast to local Vertex class
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
    public Iterable<Graph2D.Edge> edges()
    {
        return new Iterable<Graph2D.Edge>() {
            @Override
            public Iterator<Graph2D.Edge> iterator()
            {
                return new EdgeIterator();
            }
        };
    }
    
    public Vertex sourceVertex(Graph2D.Edge edge)
    {
        return new Vertex(getEdge(edge).iv1);
    }
    
    public int sourceVertex(int edgeIndex)
    {
        return this.edges.get(edgeIndex).iv1;
    }
    
    public Vertex targetVertex(Graph2D.Edge edge)
    {
        return new Vertex(getEdge(edge).iv2);
    }

    public int targetVertex(int edgeIndex)
    {
        return this.edges.get(edgeIndex).iv2;
    }
    
    public Graph2D.Edge addEdge(Graph2D.Vertex v1, Graph2D.Vertex v2)
    {
        // create new edge
        Edge edge = new Edge(getVertex(v1), getVertex(v2)); 
        
        // add new edge to mesh
//        int index = edges.size();
        edges.add(edge);
//        edgeIndices.put(edge, index);
        
        // return edge instance
        return edge;
    }

    public Graph2D.Edge addEdge(int indV1, int indV2)
    {
        int nv = this.vertices.size();
        if (indV1 >= nv || indV2 >= nv)
        {
            throw new IllegalArgumentException("Vertex indices greated than the number of vertices");
        }
        
        Edge edge = new Edge(indV1, indV2);
        this.edges.add(edge);
        return edge;
    }

    /**
     * Returns the line segment corresponding to the given edge.
     * 
     * @param edgeIndex
     *            the index of the edge
     * @return the line segment joining the source and target vertices of the
     *         given edge
     */
    public LineSegment2D edgeCurve(int edgeIndex)
    {
        Edge adj = edges.get(edgeIndex);
        Point2D p1 = this.vertices.get(adj.iv1);
        Point2D p2 = this.vertices.get(adj.iv2);
        return new LineSegment2D(p1, p2);
    }
    
    /**
     * Cast to local Edge class
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
        for (int i = 0; i < this.edges.size(); i++)
        {
            if (edgeCurve(i).contains(point, eps))
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
        for (int i = 0; i < this.edges.size(); i++)
        {
            minDist = Math.min(minDist, edgeCurve(i).distance(x, y));
        }

        return minDist;
    }


    @Override
    public Bounds2D bounds()
    {
        return Bounds2D.of(vertices);
    }
    
    
    // ===================================================================
    // Inner class
    
    public class Vertex implements Graph2D.Vertex
    {
        // the index of the vertex
        int index;

        public Vertex(int index)
        {
            this.index = index;
        }
        
        @Override
        public Point2D position()
        {
            return vertices.get(index);
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

    
    private class VertexIterator implements Iterator<Graph2D.Vertex>
    {
        int index = 0;
        @Override
        public boolean hasNext()
        {
            return index < vertices.size();
        }
        
        @Override
        public Vertex next()
        {
            return new Vertex(index++);
        }
    }

    public class Edge implements Graph2D.Edge, Comparable<Edge>
    {
        /** index of first vertex  (iv1 < iv2) */
        int iv1;
        
        /** index of second vertex (iv1 < iv2) */
        int iv2;

        public Edge(Vertex v1, Vertex v2)
        {
            this(v1.index, v2.index);
        }
        
        public Edge(int iv1, int iv2)
        {
            if (iv1 < iv2)
            {
                this.iv1 = iv1;
                this.iv2 = iv2;
            }
            else
            {
                this.iv1 = iv2;
                this.iv2 = iv1;
            }
        }
        
        @Override
        public Graph2D.Vertex source()
        {
            return new Vertex(iv1);
        }

        @Override
        public Graph2D.Vertex target()
        {
            return new Vertex(iv2);
        }

        public LineSegment2D curve()
        {
            Point2D p1 = vertexPosition(iv1);
            Point2D p2 = vertexPosition(iv2);
            return new LineSegment2D(p1, p2);
        }
        
       
        /**
         * Implements compareTo to allows for fast indexing.
         */
        @Override
        public int compareTo(Edge that)
        {
            int diff = this.iv1 - that.iv1;
            if (diff != 0)
                return diff;
            return this.iv2 - that.iv2;
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
            if (this.iv1 != that.iv1) return false;
            if (this.iv2 != that.iv2) return false;
            return true;
        }
        
        @Override
        public int hashCode()
        {
            int hash = 1;
            hash = hash * 17 + iv1;
            hash = hash * 17 + iv2;
            return hash;
        }
    }
    
    private class EdgeIterator implements Iterator<Graph2D.Edge>
    {
        int index = 0;
        @Override
        public boolean hasNext()
        {
            return index < edges.size();
        }
        
        @Override
        public Graph2D.Edge next()
        {
            return edges.get(index++);
        }
    }

    public static final void main(String[] args)
    {
        Point2D p1 = new Point2D(10, 10);
        Point2D p2 = new Point2D(20, 10);
        Point2D p3 = new Point2D(20, 20);
        Point2D p4 = new Point2D(10, 20);
        Point2D p5 = new Point2D(27, 15);
        
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
        
        System.out.println("nv = " + graph.vertexCount());
        System.out.println("ne = " + graph.edgeCount());
    }

}
