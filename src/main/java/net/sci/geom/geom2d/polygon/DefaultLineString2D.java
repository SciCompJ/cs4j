/**
 * 
 */
package net.sci.geom.geom2d.polygon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.sci.geom.geom2d.LineSegment2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.Vector2D;

/**
 * <p>
 * Default implementation for line strings. A line string is an open polyline
 * whose last point is NOT connected to the first one.
 * </p>
 * 
 * @see LineString2D
 * @see LinearRing2D
 * 
 * @author dlegland
 */
public class DefaultLineString2D implements LineString2D
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

    public DefaultLineString2D() 
    {
        this.vertices = new ArrayList<Point2D>();
    }

    /**
     * Creates a new linear curve by allocating enough memory for the specified
     * number of vertices.
     * 
     * @param nVertices the number of vertices in this polyline
     */
    public DefaultLineString2D(int nVertices)
    {
        this.vertices = new ArrayList<Point2D>(nVertices);
    }
    
    public DefaultLineString2D(Point2D... vertices)
    {
        this.vertices = new ArrayList<Point2D>(vertices.length);
        for (Point2D vertex : vertices)
        {
            this.vertices.add(vertex);
        }
    }
    
    public DefaultLineString2D(Collection<? extends Point2D> vertices)
    {
        this.vertices = new ArrayList<Point2D>(vertices.size());
        this.vertices.addAll(vertices);
    }
    
    public DefaultLineString2D(double[] xcoords, double[] ycoords)
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

    /**
     * Returns the inner collection of vertices.
     */
    public Collection<Point2D> vertexPositions()
    {
        return vertices;
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
    
    public Point2D vertexPosition(int vertexIndex)
    {
        return this.vertices.get(vertexIndex);
    }
    
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
    // Methods implementing the Polyline2D interface


    // ===================================================================
    // Management of edges
    
    public int edgeCount()
    {
        return vertices.size() - 1;
    }

    public Polyline2D.Edge edge(int edgeIndex)
    {
    	if (edgeIndex < 0 || edgeIndex >= vertices.size()-1)
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
    public Point2D getPoint(double t)
    {
        // format position to stay between limits
        double t0 = this.getT0();
        double t1 = this.getT1();
        t = Math.max(Math.min(t, t1), t0);

        // index of vertex before point
        int ind0 = (int) Math.floor(t + Double.MIN_VALUE);
        double tl = t - ind0;
        Point2D p0 = vertices.get(ind0);

        // check if equal to last vertex
        if (t == t1)
            return p0;

        // index of vertex after point
        int ind1 = ind0+1;
        Point2D p1 = vertices.get(ind1);

        // position on line;
        double x0 = p0.getX();
        double y0 = p0.getY();
        double dx = p1.getX() - x0;
        double dy = p1.getY() - y0;
        return new Point2D(x0 + tl * dx, y0 + tl * dy);
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
			return new Vertex(this.index + 1);
		}

		@Override
		public LineSegment2D curve()
		{
			Point2D v1 = vertices.get(this.index);
			Point2D v2 = vertices.get(this.index + 1);
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
			return index < vertices.size() - 1;
		}

		@Override
		public Edge next()
		{
			return new Edge(this.index++);
		}
    }

}
