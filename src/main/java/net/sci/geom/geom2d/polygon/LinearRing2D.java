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
import net.sci.geom.geom2d.curve.Contour2D;


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
public class LinearRing2D implements Polyline2D, Contour2D
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

    public LinearRing2D() 
    {
        this.vertices = new ArrayList<Point2D>();
    }

    /**
     * Creates a new linear curve by allocating enough memory for the specified
     * number of vertices.
     * 
     * @param nVertices the number of vertices in this polyline
     */
    public LinearRing2D(int nVertices)
    {
        this.vertices = new ArrayList<Point2D>(nVertices);
    }
    
    public LinearRing2D(Point2D... vertices)
    {
        this.vertices = new ArrayList<Point2D>(vertices.length);
        for (Point2D vertex : vertices)
        {
            this.vertices.add(vertex);
        }
    }
    
    public LinearRing2D(Collection<? extends Point2D> vertices)
    {
        this.vertices = new ArrayList<Point2D>(vertices.size());
        this.vertices.addAll(vertices);
    }
    
    public LinearRing2D(double[] xcoords, double[] ycoords)
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
    
    public LinearRing2D smooth(int smoothingSize)
    {
    	// compute the number of elements before and after central vertex
    	// (ensuring M1+M2 = smoothingSize)
    	int M1 = (int) Math.floor((smoothingSize - 1) / 2);
    	int M2 = (int) Math.ceil((smoothingSize - 1) / 2);
    	
    	int nv = this.vertices.size();
    	LinearRing2D res = new LinearRing2D(nv);
    	
    	for (int i = 0; i < nv; i++)
    	{
    		double x = 0;
    		double y = 0;
    		for (int i2 = i - M1; i2 <= i + M2; i2++)
    		{
    			Point2D v = vertices.get((i2 % nv + nv) % nv);
    			x += v.getX();
    			y += v.getY();
    		}
    		x /= smoothingSize;
    		y /= smoothingSize;
    		
    		res.addVertex(new Point2D(x, y));
    	}

    	return res;
    }
    
    /**
     * Computes the index of the closest vertex to the input point.
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
     * Computes the signed area of the linear ring. Algorithm is taken from page:
     * <a href="http://local.wasp.uwa.edu.au/~pbourke/geometry/polyarea/">
     * http://local.wasp.uwa.edu.au/~pbourke/geometry/polyarea/</a>. Signed area
     * is positive if polyline is oriented counter-clockwise, and negative
     * otherwise. Result is wrong if polyline is self-intersecting.
     * 
     * @return the signed area of the polyline.
     */
    public double signedArea() 
    {
        // start from edge joining last and first vertices
        Point2D prev = this.vertices.get(this.vertices.size() - 1);

        // Iterate over all couples of adjacent vertices
        double area = 0;
        for (Point2D point : this.vertices) 
        {
            // add area of elementary parallelogram
            area += prev.getX() * point.getY() - prev.getY() * point.getX();
            prev = point;
        }
        
        // divides by 2 to consider only elementary triangles
        return area /= 2;
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
    public int vertexNumber()
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
    
    @Override
    public double signedDistance(Point2D point)
    {
        return signedDistance(point.getX(), point.getY());
    }
    
    public double signedDistance(double x, double y)
    {
        double minDist = Double.POSITIVE_INFINITY; 
                
        double area = 0;
        
        // the winding number counter
        int winding = 0;
    
        // initialize iteration with last vertex
        Point2D p0 = this.vertices.get(this.vertices.size()-1);
        Point2D previous = p0;
        double xprev = previous.getX();
        double yprev = previous.getY();

        // iterate over vertex pairs
        for (Point2D current : this.vertices)
        {
            // update distance to nearest edge 
            double dist = new LineSegment2D(previous, current).distance(x, y);
            minDist = Math.min(dist, minDist);
            
            // coordinates of current vertex
            double xcurr = current.getX();
            double ycurr = current.getY();
    
            // update area computation
            area += xprev * ycurr - yprev * xcurr;
            
            // update winding number
            if (yprev <= y)
            {
                // detect upward crossing
                if (ycurr > y) 
                    if (isLeft(xprev, yprev, xcurr, ycurr, x, y) > 0)
                        winding++;
            }
            else
            {
                // detect downward crossing
                if (ycurr <= y)
                    if (isLeft(xprev, yprev, xcurr, ycurr, x, y) < 0)
                        winding--;
            }
            
            // for next iteration
            xprev = xcurr;
            yprev = ycurr;
            previous = current;
        }
        
        boolean inside = area > 0 ^ winding == 0;
        return inside ? -minDist : minDist;
    }

	@Override
	public boolean isInside(Point2D point)
	{
		return isInside(point.getX(), point.getY());
	}

	@Override
	public boolean isInside(double x, double y)
	{
        double area = 0;
        
        // the winding number counter
        int winding = 0;
    
        // initialize with the last vertex
        Point2D previous = this.vertices.get(vertices.size() - 1);
        double xprev = previous.getX();
        double yprev = previous.getY();
    
        // iterate on vertices, keeping coordinates of previous vertex in memory
        for (Point2D current : vertices)
        {
            // coordinates of current vertex
            double xcurr = current.getX();
            double ycurr = current.getY();
    
            // update area computation
            area += xprev * ycurr - yprev * xcurr;
            
            
            if (yprev <= y)
            {
                // detect upward crossing
                if (ycurr > y) 
                    if (isLeft(xprev, yprev, xcurr, ycurr, x, y) > 0)
                        winding++;
            }
            else
            {
                // detect downward crossing
                if (ycurr <= y)
                    if (isLeft(xprev, yprev, xcurr, ycurr, x, y) < 0)
                        winding--;
            }
            
            // for next iteration
            xprev = xcurr;
            yprev = ycurr;
            previous = current;
        }
    
        if (area > 0) 
        {
            return winding == 1;
        }
        else 
        {
            return winding == 0;
        }
	}
    
    /**
     * Tests if the point p3 is Left|On|Right of the infinite line formed by p1 and p2.
     * 
     * Input:  three points P0, P1, and P2
     * Return: >0 for P2 left of the line through P0 and P1
     *         =0 for P2 on the line
     *         <0 for P2 right of the line
     *         
     * See: the January 2001 Algorithm "Area of 2D and 3D Triangles and Polygons"
     * 
     * @see SimplePolygon2D.isLeft(double, double, double, double, double, double)
     */
    private final static int isLeft(double x1, double y1, double x2, double y2, double x3, double y3)
    {
        return (int) Math.signum((x2 - x1) * (y3 - y1) - (x3 - x1) * (y2 - y1));
    }

    
    // ===================================================================
    // Methods implementing the Polyline2D interface
    
    /**
     * Returns a new linear ring with same vertices but in reverse order. The
     * first vertex remains the same.
     */
    @Override
    public Polyline2D reverse()
    {
        // create a new collection of vertices in reverse order, keeping first
        // vertex unchanged.
        int n = this.vertexNumber();
        ArrayList<Point2D> newVertices = new ArrayList<Point2D>(n);
        newVertices.add(this.vertices.get(0));
        for (int i = 1; i < n; i++)
        {
            newVertices.add(this.vertices.get(n-i));
        }
        
        LinearRing2D reverse = new LinearRing2D(0);
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
        
        // specific processing of last edge
        Point2D vertex = vertices.get(0);
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
        
        // otherwise return the first/last vertex
        return vertex;
    }

    
    // ===================================================================
    // Management of edges
    
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

	public Iterator<? extends Polyline2D.Edge> edgeIterator()
    {
    	return new EdgeIterator();
    }
    

    // ===================================================================
    // Methods implementing the Curve2D interface
    
    public LinearRing2D resampleBySpacing(double spacing)
    {
        // compute vertex number of resulting curve
        double length = this.length();
        int nv = (int) Math.round(length / spacing);
        
        // adjust step length to avoid last edge to have different size
        double spacing2 = length / (nv + 1);
        
        // create new vertices
        ArrayList<Point2D> vertices2 = new ArrayList<Point2D>(nv);
        for (int i = 0; i < nv; i++)
        {
            double pos = Math.min(i * spacing2, length);
            vertices2.add(this.getPointAtLength(pos));
        }
        
        return new LinearRing2D(vertices2);
    }
    
    public double length()
    {
        double cumSum = 0.0;
        Point2D prev = vertices.get(vertices.size() - 1);
        Iterator<Point2D> vertexIter = vertices.iterator();
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
        int nv = vertices.size();
        t = Math.min(Math.max(t, 0), nv);

        // index of vertex before point
        int ind0 = (int) Math.floor(t + Double.MIN_VALUE);
        double tl = t - ind0;

        if (ind0 == nv)
            ind0 = 0;
        Point2D p0 = vertices.get(ind0);

        // index of vertex after point
        int ind1 = ind0 + 1;
        if (ind1 == nv)
            ind1 = 0;
        Point2D p1 = vertices.get(ind1);

        // position on line;
        double x0 = p0.getX();
        double y0 = p0.getY();
        double dx = p1.getX() - x0;
        double dy = p1.getY() - y0;

        return new Point2D(x0 + tl * dx, y0 + tl *dy);
    }

    @Override
    public double getT0()
    {
        return 0;
    }

    @Override
    public double getT1()
    {
        return vertices.size();
    }

    @Override
    public boolean isClosed()
    {
        return true;
    }
    
    
    // ===================================================================
    // Geometry interface implementation
    
    /**
     * Transforms this geometry with the specified affine transform.
     * 
     * @param trans
     *            an affine transform
     * @return the transformed line string
     */
    public LinearRing2D transform(AffineTransform2D trans)
    {
        int n = this.vertexNumber();
        ArrayList<Point2D> newVertices = new ArrayList<Point2D>(n);
        for (int i = 0; i < n; i++)
        {
            newVertices.add(this.vertices.get(i).transform(trans));
        }
        
        LinearRing2D res = new LinearRing2D(0);
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
			return index < vertices.size();
		}

		@Override
		public Edge next()
		{
			return new Edge(this.index++);
		}
    }
    
}
