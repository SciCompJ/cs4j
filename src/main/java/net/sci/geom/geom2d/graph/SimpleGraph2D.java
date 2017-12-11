/**
 * 
 */
package net.sci.geom.geom2d.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sci.geom.geom2d.Box2D;
import net.sci.geom.geom2d.Geometry2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.line.LineSegment2D;

/**
 * @author dlegland
 *
 */
public class SimpleGraph2D implements Geometry2D
{
    // ===================================================================
    // class variables

    /**
     * The list of vertices.
     */
    ArrayList<Point2D> vertices;

    /**
     * The list of vertex-vertex adjacencies.
     */
    ArrayList<Adjacency> edges; 
    
    
    // ===================================================================
    // constructors

    /**
     * Creates a new empty graph
     */
    public SimpleGraph2D()
    {
        this.vertices = new ArrayList<Point2D>();
        this.edges = new ArrayList<Adjacency>();
    }
    
    
    // ===================================================================
    // Vertices management
    
    public int vertexNumber()
    {
        return this.vertices.size();
    }
    
    public List<Point2D> vertices()
    {
        return (List<Point2D>) Collections.unmodifiableList(this.vertices);
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
    
    public Iterator<Point2D> vertexIterator()
    {
        return this.vertices.iterator();
    }
    
    
    // ===================================================================
    // Edges management
    
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
     * Returns the line segment corresponding to the given edge.
     * 
     * @param edgeIndex
     *            the index of the edge
     * @return the line segment joining the source and target vertices of the
     *         given edge
     */
    public LineSegment2D getEdgeCurve(int edgeIndex)
    {
        Adjacency adj = edges.get(edgeIndex);
        Point2D p1 = this.vertices.get(adj.v1);
        Point2D p2 = this.vertices.get(adj.v2);
        return new LineSegment2D(p1, p2);
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
            if (getEdgeCurve(i).contains(point, eps))
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
            minDist = Math.min(minDist, getEdgeCurve(i).distance(x, y));
        }

        return minDist;
    }


    @Override
    public Box2D boundingBox()
    {
        // initialize with extreme values
        double xmin = Double.POSITIVE_INFINITY;
        double xmax = Double.NEGATIVE_INFINITY;
        double ymin = Double.POSITIVE_INFINITY;
        double ymax = Double.NEGATIVE_INFINITY;
        
        // compute min/max for each coordinate
        for (Point2D vertex : this.vertices)
        {
            double x = vertex.getX();
            double y = vertex.getY();
            xmin = Math.min(xmin, x);
            xmax = Math.max(xmax, x);
            ymin = Math.min(ymin, y);
            ymax = Math.max(ymax, y);
        }
        
        // return new Bounding Box
        return new Box2D(xmin, xmax, ymin, ymax);
    }
    
    
    // ===================================================================
    // Inner class
    
    /**
     * A pair of indices representing an adjacency between two vertices.
     * 
     * @author dlegland
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
