/**
 * 
 */
package net.sci.geom.geom2d.polygon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.sci.geom.geom2d.AffineTransform2D;
import net.sci.geom.geom2d.LineSegment2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.Vector2D;

/**
 * <p>
 * A LineString2D is an open polyline whose last point is NOT connected to the
 * first one.
 * </p>
 * 
 * @see LinearRing2D
 * 
 * @author dlegland
 */
public class LineString2D implements Polyline2D
{
    // ===================================================================
    // Static methods
    
    /**
     * Interpolates a new line string between two line strings, assuming the vertices
     * are in correspondence.
     * 
     * @param ring0
     *            the first line string, at position t=0
     * @param ring1
     *            the second line string, at position t=1
     * @param t
     *            the position of the line string to interpolate, between 0 and 1
     * @return the interpolated line string
     */
    public static final LineString2D interpolate(LineString2D ring0, LineString2D ring1, double t)
    {
        // check number of vertices
        int nv = ring0.vertexCount();
        if (ring1.vertexCount() != nv)
        {
            throw new RuntimeException("Both rings must have same number of vertices");
        }
        
        // check interpolation interval
        if (t < 0 || t > 1)
        {
            throw new RuntimeException("Interpolation value must be comprised between 0 and 1.");
        }
        double t0 = t;
        double t1 = 1 - t0;
        
        // allocate memory for result
        LineString2D res = new LineString2D(nv);
        
        // iterate over vertices
        for (int iv = 0; iv < nv; iv++)
        {
            Point2D p1 = ring0.vertexPosition(iv);
            Point2D p2 = ring1.vertexPosition(iv);
            
            double x = p1.getX() * t1 + p2.getX() * t0;
            double y = p1.getY() * t1 + p2.getY() * t0;
            res.addVertex(new Point2D(x, y));
        }

        return res;
    }
    
    
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
    // Contructors

    public LineString2D() 
    {
        this.vertices = new ArrayList<Point2D>();
    }

    /**
     * Creates a new linear curve by allocating enough memory for the specified
     * number of vertices.
     * 
     * @param nVertices the number of vertices in this polyline
     */
    public LineString2D(int nVertices)
    {
        this.vertices = new ArrayList<Point2D>(nVertices);
    }
    
    public LineString2D(Point2D... vertices)
    {
        this.vertices = new ArrayList<Point2D>(vertices.length);
        for (Point2D vertex : vertices)
        {
            this.vertices.add(vertex);
        }
    }
    
    public LineString2D(Collection<? extends Point2D> vertices)
    {
        this.vertices = new ArrayList<Point2D>(vertices.size());
        this.vertices.addAll(vertices);
    }
    
    public LineString2D(double[] xcoords, double[] ycoords)
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
     * Returns the inner collection of vertices.
     */
    public Iterable<Point2D> vertexPositions()
    {
        return vertices;
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
    

    public LineString2D smooth(int smoothingSize)
    {
        // compute the number of elements before and after central vertex
        // (ensuring M1+M2 = smoothingSize)
        int M1 = (int) Math.floor((smoothingSize - 1) / 2);
        int M2 = (int) Math.ceil((smoothingSize - 1) / 2);
        
        int nv = this.vertices.size();
        LineString2D res = new LineString2D(nv);
        
        for (int i = 0; i < nv; i++)
        {
            double x = 0;
            double y = 0;
            for (int i2 = i - M1; i2 <= i + M2; i2++)
            {
                // clamp index between 0 and vertex number
                int i2c = Math.min(Math.max(i2, 0), nv - 1);
                Point2D v = vertices.get(i2c);
                x += v.getX();
                y += v.getY();
            }
            x /= smoothingSize;
            y /= smoothingSize;
            
            // add new vertex
            res.addVertex(new Point2D(x, y));
        }

        return res;
    }
    

    public LineString2D resampleBySpacing(double spacing)
    {
        // compute vertex number of resulting curve
        double length = this.length();
        int nv = (int) Math.round(length / spacing);
        
        // adjust step length to avoid last edge to have different size
        double spacing2 = length / nv;
        
        // create new vertices
        ArrayList<Point2D> vertices2 = new ArrayList<Point2D>(nv);
        for (int i = 0; i < nv - 1; i++)
        {
            double pos = Math.min(i * spacing2, length);
            vertices2.add(getPointAtLength(pos));
        }
        vertices2.add(this.vertices.get(this.vertices.size() - 1));
        
        return new LineString2D(vertices2);
    }
    
    public LineString2D mergeMultipleVertices(double minDist)
    {
        // Allocate memory for new vertex array
        int nv = vertexCount();
        LineString2D res = new LineString2D(nv);
        
        // start with the position of the last vertex
        Point2D lastPosition = new Point2D(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        
        for (Point2D pos : vertices)
        {
            double dist = pos.distance(lastPosition);
            if (dist > minDist)
            {
                res.addVertex(pos);
                lastPosition = pos;
            }
        }
        
        return res;
    }
    
    /**
     * Returns a new linear ring with same vertices but in reverse order. The
     * first vertex of the new line string is the last vertex of this line
     * string.
     */
    @Override
    public LineString2D reverse()
    {
        int n = this.vertexCount();
        ArrayList<Point2D> newVertices = new ArrayList<Point2D>(n);
        for (int i = 0; i < n; i++)
        {
            newVertices.add(this.vertices.get(n-1-i));
        }
        
        LineString2D reverse = new LineString2D(0);
        reverse.vertices = newVertices;
        return reverse;
    }

    public Point2D getPointAtLength(double pos)
    {
        double cumSum = 0;
        Iterator<Point2D> vertexIter = vertices.iterator();
        Point2D prev = vertexIter.next();
        while(vertexIter.hasNext())
        {
            Point2D vertex = vertexIter.next();
            double dist = vertex.distance(prev);
            cumSum += dist;
            if (cumSum >= pos)
            {
                double pos0 = pos - cumSum + dist;
                double t1 = pos0 / dist;
                double t0 = 1 - t1;
                
                double x = prev.getX() * t0 + vertex.getX() * t1;
                double y = prev.getY() * t0 + vertex.getY() * t1;
                return new Point2D(x, y);
            }
            prev = vertex;
        }
        return prev;
    }


    // ===================================================================
    // Management of edges
    
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

	public Iterator<? extends Polyline2D.Edge> edgeIterator()
    {
    	return new EdgeIterator();
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
    public double getT0()
    {
        return 0;
    }

    @Override
    public double getT1()
    {
        return vertices.size() - 1;
    }
    @Override
    public boolean isClosed()
    {
        return false;
    }
    

    // ===================================================================
    // Implementation of Geometry methods
    
    /**
	 * Transforms this geometry with the specified affine transform.
	 * 
	 * @param trans
	 *            an affine transform
	 * @return the transformed line string
	 */
	public LineString2D transform(AffineTransform2D trans)
	{
	    int n = this.vertexCount();
	    ArrayList<Point2D> newVertices = new ArrayList<Point2D>(n);
	    for (int i = 0; i < n; i++)
	    {
	        newVertices.add(this.vertices.get(i).transform(trans));
	    }
	    
	    LineString2D res = new LineString2D(0);
	    res.vertices = newVertices;
	    return res;
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
    
    public class Edge implements Polyline2D.Edge
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
    // Edge iterator implementation
    
    class EdgeIterator implements Iterator<Polyline2D.Edge>
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
