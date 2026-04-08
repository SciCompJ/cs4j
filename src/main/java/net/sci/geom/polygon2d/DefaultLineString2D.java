/**
 * 
 */
package net.sci.geom.polygon2d;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.sci.geom.geom2d.LineSegment2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.Vector2D;

/**
 * Default implementation for linear rings, based on an inner array of vertex
 * positions.
 * 
 * @see LineString2D
 * @see LinearRing2D
 * 
 * @author dlegland
 */
public class DefaultLineString2D extends VertexContainer2D implements LineString2D
{
    // ===================================================================
    // Constructors

    public DefaultLineString2D() 
    {
        super();
    }

    /**
     * Creates a new linear curve by allocating enough memory for the specified
     * number of vertices.
     * 
     * @param nVertices the number of vertices in this polyline
     */
    public DefaultLineString2D(int nVertices)
    {
        super(nVertices);
    }
    
    public DefaultLineString2D(Point2D... vertices)
    {
        this.vertices = new ArrayList<Point2D>(vertices.length);
        for (Point2D vertex : vertices)
        {
            this.vertices.add(vertex);
        }
    }
    
    public DefaultLineString2D(Collection<Point2D> vertices)
    {
        super(vertices);
    }
    
    public DefaultLineString2D(double[] xcoords, double[] ycoords)
    {
        super(xcoords, ycoords);
    }
 
    
    // ===================================================================
    // Management of vertex normals
    
    /**
     * Recomputes normal associated to each vertex, by computing tangents of
     * adjacent vertices.
     */
    public void computeNormals()
    {
        // allocate memory for storing normals
        this.clearNormals();
        int nVertices = this.vertices.size();
        this.vertexNormals.ensureCapacity(nVertices);
        
        // compute tangent of first edge
        Point2D V0 = this.vertices.get(0);
        Point2D V1 = this.vertices.get(1);
        Vector2D T0 = new Vector2D(V0, V1).normalize();
        
        // compute normal at first vertex
        this.vertexNormals.add(T0.rotate90(-1));
        
        // process regular vertices
        final double k = Math.sqrt(2) / 2.0;
        for (int i = 1; i < nVertices - 1; i++)
        {
            V0 = V1;
            V1 = this.vertices.get(i+1);
            Vector2D T1 = new Vector2D(V0, V1).normalize();
            
            // compute average of the two normalized tangent vectors, and rotate
            this.vertexNormals.add(T0.plus(T1).times(k).rotate90(-1));
            
            T0 = T1;
        }
        
        // compute normal at last vertex
        this.vertexNormals.add(T0.rotate90(-1));
    }
    

    // ===================================================================
    // Management of edges
    
    public int edgeCount()
    {
        return vertices.size() - 1;
    }

    public Edge edge(int edgeIndex)
    {
    	if (edgeIndex < 0 || edgeIndex >= vertices.size()-1)
    	{
    		throw new RuntimeException("Edge index out of bounds: " + edgeIndex);
    	}
    	return new LocalEdge(edgeIndex);
    }
    
    @Override
	public Iterable<? extends Edge> edges()
	{
		return new Iterable<Edge>() 
		{
			@Override
			public Iterator<Edge> iterator()
			{
				return new EdgeIterator();
			}
		};
	}


    // ===================================================================
    // Methods implementing the Curve2D interface
    
    public double length()
    {
        double cumSum = 0.0;
        Iterator<Point2D> vertexIter = vertices.iterator();
        Point2D prev = vertexIter.next();
        while(vertexIter.hasNext())
        {
            Point2D vertex = vertexIter.next();
            double dist = vertex.distance(prev);
            cumSum += dist;
            prev = vertex;
        }
        
        return cumSum;
    }
    	
    @Override
    public LineString2D duplicate()
    {
        DefaultLineString2D dup = new DefaultLineString2D(this.vertices.size());
        dup.vertices.addAll(this.vertices);
        dup.vertexNormals.addAll(this.vertexNormals);
        return dup;
    }
    

    // ===================================================================
    // Inner class implementations
    
	private class LocalEdge implements Edge
    {
    	int index;

    	public LocalEdge(int index)
    	{
    		this.index = index;
    	}
    	
		@Override
		public Vertex source()
		{
			return new LocalVertex(this.index);
		}

		@Override
		public Vertex target()
		{
			return new LocalVertex(this.index + 1);
		}

		@Override
		public LineSegment2D curve()
		{
			Point2D v1 = vertices.get(this.index);
			Point2D v2 = vertices.get(this.index + 1);
			return new LineSegment2D(v1, v2);
		}
    }
    
    private class EdgeIterator implements Iterator<Edge>
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
		public LocalEdge next()
		{
			return new LocalEdge(this.index++);
		}
    }
}
