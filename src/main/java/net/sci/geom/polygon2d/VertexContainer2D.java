package net.sci.geom.polygon2d;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.Vector2D;

/**
 * Base implementation for classes containing vertices in a contiguous array.
 * This is the base class for {@code LinearRing2D}, {@code LineString2D}, or
 * {@code SimplePolygon2D}.
 */
public class VertexContainer2D implements Polygonal2D
{
    // ===================================================================
    // Class variables
    
    /**
     * The array of position for each vertex.
     */
    protected ArrayList<Point2D> vertices;
    
    /**
     * An optional array of vectors used to store the normal for each vertex.
     * 
     * Always initialized, but has a size of zero by default.
     */
    protected ArrayList<Vector2D> vertexNormals = new ArrayList<Vector2D>(0);
    
    
    // ===================================================================
    // Constructor
    
    protected VertexContainer2D()
    {
        this.vertices = new ArrayList<Point2D>();
    }
    
    protected VertexContainer2D(int nVertices)
    {
        this.vertices = new ArrayList<Point2D>(nVertices);
    }
    
    protected VertexContainer2D(Collection<Point2D> vertexPositions)
    {
        this.vertices = new ArrayList<Point2D>(vertexPositions.size());
        this.vertices.addAll(vertexPositions);
    }
    
    protected VertexContainer2D(double[] xcoords, double[] ycoords)
    {
        if (xcoords.length != ycoords.length)
        {
            throw new RuntimeException("Both arrays must have same length");
        }
        
        this.vertices = new ArrayList<Point2D>(xcoords.length);
        int n = xcoords.length;
        this.vertices.ensureCapacity(n);
        for (int i = 0; i < n; i++)
        {
            vertices.add(new Point2D(xcoords[i], ycoords[i]));
        }
    }

    
    // ===================================================================
    // Utility methods
    
    
    

    // ===================================================================
    // Management of vertex normals
    
    public void clearNormals()
    {
        this.vertexNormals.clear();
    }

    /**
     * Does nothing, but can be overridden for setting up the inner
     * {@code normal} variable.
     */
    public void computeNormals()
    {
    }


    // ===================================================================
    // Management of vertices
   
    
    /**
     * Computes the index of the closest vertex to the input query point.
     * 
     * @param point
     *            the query point
     * @return the index of the closest vertex to the query point
     */
    public int closestVertexIndex(Point2D point)
    {
        double minDist = Double.POSITIVE_INFINITY;
        int index = -1;
        
        for (int i = 0; i < vertices.size(); i++)
        {
            double dist = vertices.get(i).distance(point);
            if (dist < minDist)
            {
                index = i;
                minDist = dist;
            }
        }
        
        return index;
    }

    /**
     * Returns the number of vertices.
     * 
     * @return the number of vertices
     */
    public int vertexCount()
    {
        return vertices.size();
    }

    public Iterable<? extends Vertex> vertices()
    {
        return new Iterable<LocalVertex>()
        {
            @Override
            public Iterator<LocalVertex> iterator()
            {
                return new VertexIterator();
            }
        };
    }

    public void addVertex(Point2D vertexPosition)
    {
        this.vertices.add(vertexPosition);
        clearNormals();
    }
    
    public void removeVertex(int vertexIndex)
    {
        this.vertices.remove(vertexIndex);
        clearNormals();
    }
    
    /**
     * Returns the vertex at a given index.
     * 
     * @param index
     *            the vertex index, between 0 and (vertexCount-1)
     * @return the vertex at the specified index.
     */
    public Vertex vertex(int index)
    {
        return new LocalVertex(index);
    }
    
    /**
     * Returns the inner collection of points that contain the vertex
     * positions.
     * 
     * @return a list of pointss containing vertex positions
     */
    public List<Point2D> vertexPositions()
    {
        return vertices;
    }

    /**
     * Returns the position of a vertex identified by its index.
     * 
     * @param vertexIndex
     *            the index of the vertex
     * @return the position of the vertex
     */
    public Point2D vertexPosition(int vertexIndex)
    {
        return this.vertices.get(vertexIndex);
    }
    
    
    // ===================================================================
    // Inner class implementations
    
    protected class LocalVertex implements Vertex
    {
        int index;
        
        public LocalVertex(int index)
        {
            this.index = index;
        }

        @Override
        public Point2D position()
        {
            return vertices.get(this.index);
        }
        
        @Override
        public Vector2D normal()
        {
            if (vertexNormals.size() > 0)
            {
                return vertexNormals.get(this.index);
            }
            
            throw new RuntimeException("Normal vectors have not been computed");
        }
    }
    

    // ===================================================================
    // Vertex  iterator implementations
    
    protected class VertexIterator implements Iterator<LocalVertex>
    {
        /**
         * Index of current vertex in iterator. 
         */
        int index = -1;

        @Override
        public boolean hasNext()
        {
            return index < (vertices.size() - 1);
        }

        @Override
        public LocalVertex next()
        {
            return new LocalVertex(++this.index);
        }
    }
   
}
