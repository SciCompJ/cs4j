/**
 * 
 */
package net.sci.geom.graph;

import java.util.HashMap;
import java.util.Map;

import net.sci.geom.geom2d.Bounds2D;
import net.sci.geom.geom2d.Geometry2D;
import net.sci.geom.geom2d.LineSegment2D;
import net.sci.geom.geom2d.Point2D;

/**
 * @author dlegland
 *
 */
public interface Graph2D extends Geometry2D
{
    // ===================================================================
    // Vertex management
    
    public int vertexCount();
    
    public Iterable<? extends Vertex> vertices();
    
    /**
     * Add a new vertex to the graph and returns its index.
     * 
     * @param position the position of the vertex
     * @return the index of the new vertex.
     */
    public Vertex addVertex(Point2D position);
        
    
    // ===================================================================
    // Edge management
    
    public int edgeCount();
    
    public Iterable<Edge> edges();
    
    public Edge addEdge(Vertex source, Vertex target);

    public Vertex sourceVertex(Edge edge);
    
    public Vertex targetVertex(Edge edge);
    
    
    // ===================================================================
    // Implementation of the Geometry2D interface
    
    /**
     * Returns true, as a graph is bounded by definition.
     * 
     * @return true
     */
    @Override
    public default boolean isBounded()
    {
        return true;
    }

    /**
     * Returns true if the point belong to one of the edges of the graph, up to
     * the specified precision.
     */
    @Override
    public default boolean contains(Point2D point, double eps)
    {
        for (Edge edge : edges())
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
    public default double distance(double x, double y)
    {
        double minDist = Double.POSITIVE_INFINITY;
        for (Edge edge : edges())
        {
            minDist = Math.min(minDist, edge.curve().distance(x, y));
        }

        return minDist;
    }


    @Override
    public default Bounds2D bounds()
    {
        // initialize with extreme values
        double xmin = Double.POSITIVE_INFINITY;
        double xmax = Double.NEGATIVE_INFINITY;
        double ymin = Double.POSITIVE_INFINITY;
        double ymax = Double.NEGATIVE_INFINITY;
        
        // compute min/max for each coordinate
        for (Vertex v : this.vertices())
        {
            Point2D p = v.position();
            double x = p.getX();
            double y = p.getY();
            xmin = Math.min(xmin, x);
            xmax = Math.max(xmax, x);
            ymin = Math.min(ymin, y);
            ymax = Math.max(ymax, y);
        }
        
        // return new Bounding Box
        return new Bounds2D(xmin, xmax, ymin, ymax);
    }
    
    @Override
    public default Graph2D duplicate()
    {
        // create new empty graph
        SimpleGraph2D dup = new SimpleGraph2D(vertexCount(), edgeCount());
        
        // copy vertices, keeping mapping between old and new references
        Map<Vertex, Vertex> vertexMap = new HashMap<>();
        for (Vertex v : this.vertices())
        {
            Vertex v2 = dup.addVertex(v.position());
            vertexMap.put(v, v2);
        }
        
        // copy edges using vertex mapping
        for (Edge e : edges())
        {
            Vertex v1 = vertexMap.get(e.source());
            Vertex v2 = vertexMap.get(e.target());
            dup.addEdge(v1, v2);
        }
        
        // return graph
        return dup;
    }
    
    
    
    // ===================================================================
    // Inner interfaces

    /**
     * Interface representing a graph vertex.
     */
    public interface Vertex
    {
        /**
         * @return the position of this vertex, as a 3D Point
         */
        public Point2D position();
    }
    
    
    /**
     * Interface representing a graph edge
     */
    public interface Edge
    {
        /**
         * @return the source vertex of this edge
         */
        public Vertex source();
        
        /**
         * @return the target vertex of this edge
         */
        public Vertex target();

        /**
         * @return the length of this edge.
         */
        public default double length()
        {
            Point2D p1 = source().position();
            Point2D p2 = target().position();
            return p1.distance(p2);
        }
        
        /**
         * @return the center of this edge.
         */
        public default Point2D center()
        {
            Point2D p1 = source().position();
            Point2D p2 = target().position();
            double x = p1.getX() + p2.getX();
            double y = p1.getY() + p2.getY();
            return new Point2D(x, y);
        }

        /**
         * @return the line segment that represents this edge.
         */
        public LineSegment2D curve();
    }

}
