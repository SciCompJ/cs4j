package net.sci.geom.geom2d.graph;

import java.util.ArrayList;
import java.util.Iterator;

import net.sci.geom.geom2d.Box2D;
import net.sci.geom.geom2d.Curve2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.line.LineSegment2D;

public class DefaultGraph2D implements Graph2D<DefaultGraph2D.Vertex, DefaultGraph2D.Edge>
{
    ArrayList<Point2D> vertexCoordinates = new ArrayList<Point2D>();

    ArrayList<Edge> edges = new ArrayList<Edge>();

    public DefaultGraph2D()
    {
    }

    
    @Override
    public Iterable<Edge> adjacentEdges(Vertex vertex)
    {
        ArrayList<Edge> adjEdges = new ArrayList<Edge>();
        for (Edge edge : edges)
        {
            if (edge.sourceIndex == vertex.index || edge.targetIndex == vertex.index)
            {
                adjEdges.add(edge);
            }
        }
        return adjEdges;
    }

    @Override
    public Iterable<Vertex> neighborVertices(Vertex vertex)
    {
        ArrayList<Vertex> neighbors = new ArrayList<Vertex>();
        for (Edge edge : edges)
        {
            if (edge.sourceIndex == vertex.index) 
            {
                neighbors.add(new Vertex(edge.targetIndex));
            }
            else if (edge.targetIndex == vertex.index)
            {
                neighbors.add(new Vertex(edge.sourceIndex));
            }
        }
        return neighbors;
    }

    
    public Vertex addVertex(Point2D position)
    {
        this.vertexCoordinates.add(position);
        return new Vertex(this.vertexCoordinates.size() - 1);
    }
    
    public int vertexNumber()
    {
        return vertexCoordinates.size();
    }
    
    @Override
    public Iterator<Vertex> vertexIterator()
    {
        return new Iterator<Vertex>()
        {
            int index = 0;
            
            @Override
            public boolean hasNext()
            {
                return index < vertexNumber();
            }

            @Override
            public Vertex next()
            {
                return new Vertex(index++);
            }
        };
    }

    
    public Edge addEdge(Vertex source, Vertex target)
    {
        return addEdge(source.index, target.index);
    }
    
    public Edge addEdge(int sourceIndex, int targetIndex)
    {
        Edge edge = new Edge(sourceIndex, targetIndex);
        this.edges.add(edge);
        return edge;
    }
    
    public int edgeNumber()
    {
        return edges.size();
    }
    
    @Override
    public Iterable<Edge> edges()
    {
        return edges;
    }

    
    @Override
    public boolean isBounded()
    {
        return true;
    }

    @Override
    public boolean contains(Point2D point, double eps)
    {
        for (Edge edge : edges)
        {
            if (edge.curve().contains(point, eps))
            {
                return true;
            }
        }
        return false;
    }

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

    @Override
    public Box2D boundingBox()
    {
        // initialize with extreme values
        double xmin = Double.POSITIVE_INFINITY;
        double xmax = Double.NEGATIVE_INFINITY;
        double ymin = Double.POSITIVE_INFINITY;
        double ymax = Double.NEGATIVE_INFINITY;
        
        // compute min/max for each coordinate
        for (Point2D vertex : this.vertexCoordinates)
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

    public class Vertex implements Graph2D.Vertex
    {
        int index;
        
        public Vertex(int index)
        {
            this.index = index;
        }

        public int index()
        {
            return index;
        }
        
        @Override
        public Point2D position()
        {
            return vertexCoordinates.get(index);
        }

        @Override
        public void setPosition(Point2D pos)
        {
            vertexCoordinates.set(index, pos);
        }
        
        public boolean equals(Object obj)
        {
            if (!(obj instanceof Vertex)) return false;
            Vertex that = (Vertex) obj;
            return that.index == this.index;
        }
    }

    public class Edge implements Graph2D.Edge
    {
        int sourceIndex;
        int targetIndex;

        public Edge(int sourceIndex, int targetIndex)
        {
            this.sourceIndex = sourceIndex;
            this.targetIndex = targetIndex;
        }
        
        public int sourceIndex()
        {
            return sourceIndex;
        }
        
        public int targetIndex()
        {
            return targetIndex;
        }
        
        @Override
        public Curve2D curve()
        {
            Point2D p1 = vertexCoordinates.get(sourceIndex);
            Point2D p2 = vertexCoordinates.get(targetIndex);
            return new LineSegment2D(p1, p2);
        }

        @Override
        public net.sci.geom.geom2d.graph.Graph2D.Vertex source()
        {
            return new Vertex(sourceIndex);
        }

        @Override
        public net.sci.geom.geom2d.graph.Graph2D.Vertex target()
        {
            return new Vertex(targetIndex);
        }
        
        public boolean equals(Object obj)
        {
            if (!(obj instanceof Edge)) return false;
            Edge that = (Edge) obj;
            if (this.sourceIndex != that.sourceIndex) return false;
            if (this.targetIndex != that.targetIndex) return false;
            return true;
        }
    }
    
    public static final void main(String[] args)
    {
        Point2D p1 = new Point2D(10, 10);
        Point2D p2 = new Point2D(20, 10);
        Point2D p3 = new Point2D(20, 20);
        Point2D p4 = new Point2D(10, 20);
        Point2D p5 = new Point2D(17, 15);
        
        DefaultGraph2D graph = new DefaultGraph2D();
        Vertex v1 = graph.addVertex(p1);
        Vertex v2 = graph.addVertex(p2);
        Vertex v3 = graph.addVertex(p3);
        Vertex v4 = graph.addVertex(p4);
        Vertex v5 = graph.addVertex(p5);
        
        graph.addEdge(v1, v2);
        graph.addEdge(v2, v3);
        graph.addEdge(v3, v4);
        graph.addEdge(v4, v1);
        graph.addEdge(v2, v5);
        graph.addEdge(v3, v5);
        
        System.out.println("nv = " + graph.vertexNumber());
        System.out.println("ne = " + graph.edgeNumber());
        
        Iterable<Vertex> neighs = graph.neighborVertices(v2);
        System.out.print("Neighbors of v2:");
        for (Vertex v : neighs)
        {
            System.out.print(" " + v.index);
        }
        System.out.println();
    }

}
