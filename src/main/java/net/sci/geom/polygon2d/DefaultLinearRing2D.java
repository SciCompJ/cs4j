/**
 * 
 */
package net.sci.geom.polygon2d;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.sci.geom.geom2d.LineSegment2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.Vector2D;


/**
 * <p>
 * A LinearRing2D is a polyline whose last point is connected to the first one.
 * This is typically the boundary of a (Simple)Polygon2D. For open polylines,
 * the class LineString2D may be used.
 * </p>
 * 
 * <p>
 * The name 'LinearRing2D' was used for 2 reasons:
 * <ul>
 * <li>it is short</li>
 * <li>it is consistent with the JTS name</li>
 * </ul>
 * </p>
 * 
 * @see Polyline2D
 * @see LineString2D
 * 
 * @author dlegland
 */
public class DefaultLinearRing2D implements LinearRing2D
{
    // ===================================================================
    // Class variables
    
    /**
     * The array of coordinates for each vertex.
     */
    private ArrayList<Point2D> vertices;
    
    /**
     * An optional array of vectors used to store the normal for each vertex.
     * 
     * Always initialized, but has a size of zero by default.
     */
    private ArrayList<Vector2D> vertexNormals = new ArrayList<Vector2D>(0);
    
    
    // ===================================================================
    // Constructors

    public DefaultLinearRing2D() 
    {
        this.vertices = new ArrayList<Point2D>();
    }

    /**
     * Creates a new linear curve by allocating enough memory for the specified
     * number of vertices.
     * 
     * @param nVertices the number of vertices in this polyline
     */
    public DefaultLinearRing2D(int nVertices)
    {
        this.vertices = new ArrayList<Point2D>(nVertices);
    }
    
    public DefaultLinearRing2D(Point2D... vertices)
    {
        this.vertices = new ArrayList<Point2D>(vertices.length);
        for (Point2D vertex : vertices)
        {
            this.vertices.add(vertex);
        }
    }
    
    public DefaultLinearRing2D(Collection<? extends Point2D> vertices)
    {
        this.vertices = new ArrayList<Point2D>(vertices.size());
        this.vertices.addAll(vertices);
    }
    
    public DefaultLinearRing2D(double[] xcoords, double[] ycoords)
    {
        this.vertices = new ArrayList<Point2D>(xcoords.length);
        int n = xcoords.length;
        this.vertices.ensureCapacity(n);
        for (int i = 0; i < n; i++)
        {
            vertices.add(new Point2D(xcoords[i], ycoords[i]));
        }
    }
    
    // ===================================================================
    // Methods specific to LinearRing2D
    

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
    public Iterable<Polyline2D.Vertex> vertices()
    {
        return new Iterable<Polyline2D.Vertex>()
        {
            @Override
            public Iterator<Polyline2D.Vertex> iterator()
            {
                return new VertexIterator();
            }

        };
    }

    public void addVertex(Point2D vertexPosition)
    {
        this.vertices.add(vertexPosition);
    }
    
    public void removeVertex(int vertexIndex)
    {
        this.vertices.remove(vertexIndex);
    }
    
    /**
     * Returns the vertex at a given index.
     * 
     * @param index
     *            the vertex index, between 0 and (vertexCount-1)
     * @return the vertex at the specified index.
     */
    public Polyline2D.Vertex vertex(int index)
    {
        return new Vertex(index);
    }
    
    // ===================================================================
    // Methods specific to LinearRing2D
    
    
    // ===================================================================
    // Management of vertices
    
    /**
     * Returns the inner collection of vertices.
     */
    public List<Point2D> vertexPositions()
    {
        return vertices;
    }

    public Point2D vertexPosition(int vertexIndex)
    {
        return this.vertices.get(vertexIndex);
    }
    
    
    // ===================================================================
    // Management of vertex normals
    
    public void clearNormals()
    {
        this.vertexNormals.clear();
    }
    
    public void computeNormals()
    {
        // allocate memory for storing normals
        this.clearNormals();
        int nVertices = this.vertices.size();
        this.vertexNormals.ensureCapacity(nVertices);
        
        // compute tangent of last edge
        Point2D V0 = this.vertices.get(nVertices - 1);
        Point2D V1 = this.vertices.get(0);
        Vector2D T0 = new Vector2D(V0, V1).normalize();
        
        // process regular vertices
        final double k = Math.sqrt(2) / 2.0;
        for (int i = 0; i < nVertices; i++)
        {
            V0 = V1;
            V1 = this.vertices.get((i + 1) % nVertices);
            Vector2D T1 = new Vector2D(V0, V1).normalize();
            
            // compute average of the two normalized tangent vectors, and rotate
            this.vertexNormals.add(T0.plus(T1).times(k).rotate90(-1));
            
            T0 = T1;
        }
    }
    

    // ===================================================================
    // Methods implementing the Polyline2D interface
    
    // ===================================================================
    // Methods implementing the Boundary2D interface
    
    // ===================================================================
    // Methods implementing the Polyline2D interface
    

    
    // ===================================================================
    // Management of edges
    
    public int edgeCount()
    {
        return vertices.size();
    }

    public Polyline2D.Edge edge(int edgeIndex)
    {
    	if (edgeIndex < 0 || edgeIndex >= vertices.size())
    	{
    		throw new RuntimeException("Edge index out of bounds: " + edgeIndex);
    	}
    	return new Edge(edgeIndex);
    }
    
    @Override
	public Iterable<? extends Polyline2D.Edge> edges()
	{
		return new Iterable<Polyline2D.Edge>() 
		{
			@Override
			public Iterator<Polyline2D.Edge> iterator()
			{
				return new EdgeIterator();
			}
		};
	}

    @Override
    public LinearRing2D duplicate()
    {
        DefaultLinearRing2D dup = new DefaultLinearRing2D(this.vertices.size());
        dup.vertices.addAll(this.vertices);
        dup.vertexNormals.addAll(this.vertexNormals);
        return dup;
    }

	
    // ===================================================================
    // Inner class implementations
    
	private class Vertex implements Polyline2D.Vertex
    {
    	int index;
    	
    	public Vertex(int index)
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
    
	
	private class Edge implements Polyline2D.Edge
    {
    	int index;

    	public Edge(int index)
    	{
    		this.index = index;
    	}
    	
		@Override
		public Polyline2D.Vertex source()
		{
			return new Vertex(this.index);
		}

		@Override
		public Polyline2D.Vertex target()
		{
			return new Vertex((this.index + 1) % vertices.size());
		}

		@Override
		public LineSegment2D curve()
		{
			Point2D v1 = vertices.get(this.index);
			Point2D v2 = vertices.get((this.index + 1) % vertices.size());
			return new LineSegment2D(v1, v2);
		}
    }
    
    
    // ===================================================================
    // Vertex and Edge iterator implementations
    
    private class VertexIterator implements Iterator<Polyline2D.Vertex>
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
        public Polyline2D.Vertex next()
        {
            return new Vertex(this.index++);
        }
        
    }

    private class EdgeIterator implements Iterator<Polyline2D.Edge>
    {
    	/**
    	 * Index of the first vertex of current edge
    	 */
    	int index = 0;

    	@Override
		public boolean hasNext()
		{
			return index < vertices.size();
		}

		@Override
		public Edge next()
		{
			return new Edge(this.index++);
		}
    }
    
}
