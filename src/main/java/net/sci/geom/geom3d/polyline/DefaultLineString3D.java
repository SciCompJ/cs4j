/**
 * 
 */
package net.sci.geom.geom3d.polyline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.sci.geom.geom2d.polygon.LinearRing2D;
import net.sci.geom.geom3d.LineSegment3D;
import net.sci.geom.geom3d.Point3D;

/**
 * <p>
 * A LineString3D is an open polyline whose last point is NOT connected to the
 * first one.
 * </p>
 * 
 * @author dlegland
 * @see LinearRing2D
 */
public class DefaultLineString3D implements LineString3D
{
    // ===================================================================
    // Class variables
    
    private ArrayList<Point3D> vertices;
    
    
    // ===================================================================
    // Constructors

    public DefaultLineString3D() 
    {
        this.vertices = new ArrayList<Point3D>();
    }

    /**
     * Creates a new linear curve by allocating enough memory for the specified
     * number of vertices.
     * 
     * @param nVertices the number of vertices in this polyline
     */
    public DefaultLineString3D(int nVertices)
    {
        this.vertices = new ArrayList<Point3D>(nVertices);
    }
    
    public DefaultLineString3D(Point3D... vertices)
    {
        this.vertices = new ArrayList<Point3D>(vertices.length);
        for (Point3D vertex : vertices)
        {
            this.vertices.add(vertex);
        }
    }
    
    public DefaultLineString3D(Collection<? extends Point3D> vertices)
    {
        this.vertices = new ArrayList<Point3D>(vertices.size());
        this.vertices.addAll(vertices);
    }
    
    public DefaultLineString3D(double[] xcoords, double[] ycoords, double[] zcoords)
    {
        this.vertices = new ArrayList<Point3D>(xcoords.length);
        int n = xcoords.length;
        this.vertices.ensureCapacity(n);
        for (int i = 0; i < n; i++)
        {
            vertices.add(new Point3D(xcoords[i], ycoords[i], zcoords[i]));
        }
    }
    

    // ===================================================================
    // Management of vertices
    
    /**
     * Returns the number of vertices.
     * 
     * @return the number of vertices
     */
    public int vertexCount()
    {
        return vertices.size();
    }

    @Override
    public Iterable<Polyline3D.Vertex> vertices()
    {
        return new Iterable<Polyline3D.Vertex>()
        {
            @Override
            public Iterator<Polyline3D.Vertex> iterator()
            {
                return new VertexIterator();
            }

        };
    }

    @Override
    public Point3D vertexPosition(int index)
    {
        return this.vertices.get(index);
    }

    public void addVertex(Point3D vertexPosition)
    {
        this.vertices.add(vertexPosition);
    }
    
    /**
     * Returns the inner collection of vertices.
     */
    public ArrayList<Point3D> vertexPositions()
    {
        return vertices;
    }
    
    @Override
    public Iterable<? extends Polyline3D.Edge> edges()
    {
        return new Iterable<Polyline3D.Edge>()
        {
            @Override
            public Iterator<net.sci.geom.geom3d.polyline.Polyline3D.Edge> iterator()
            {
                return new EdgeIterator();
            }
        };
    }


    // ===================================================================
    // Implementation of the Geometry interface

    @Override
    public LineString3D duplicate()
    {
        return new DefaultLineString3D(vertices);
    }

    
    // ===================================================================
    // Inner class implementations
    
    private class Vertex implements Polyline3D.Vertex
    {
        int index;
        
        public Vertex(int index)
        {
            this.index = index;
        }

        @Override
        public Point3D position()
        {
            return vertices.get(this.index);
        }
    }
    
    private class Edge implements Polyline3D.Edge
    {
        int index;

        public Edge(int index)
        {
            this.index = index;
        }
        
        @Override
        public Polyline3D.Vertex source()
        {
            return new Vertex(this.index);
        }

        @Override
        public Polyline3D.Vertex target()
        {
            return new Vertex(this.index + 1);
        }

        @Override
        public LineSegment3D curve()
        {
            Point3D v1 = vertices.get(this.index);
            Point3D v2 = vertices.get(this.index + 1);
            return new LineSegment3D(v1, v2);
        }
    }
    
    
    // ===================================================================
    // Vertex and Edge iterator implementations
    
    private class VertexIterator implements Iterator<Polyline3D.Vertex>
    {
        /**
         * Index of current vertex in iterator. 
         */
        int index = 0;

        @Override
        public boolean hasNext()
        {
            return index < vertices.size();
        }

        @Override
        public Polyline3D.Vertex next()
        {
            return new Vertex(this.index++);
        }
        
    }

    private class EdgeIterator implements Iterator<Polyline3D.Edge>
    {
        /**
         * Index of the first vertex of current edge
         */
        int index = 0;

        @Override
        public boolean hasNext()
        {
            return index < vertices.size() - 1;
        }

        @Override
        public Edge next()
        {
            return new Edge(this.index++);
        }
    }
}
