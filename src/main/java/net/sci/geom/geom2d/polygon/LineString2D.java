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

/**
 * <p>
 * A LineString2D is an open polyline whose last point is NOT connected to the
 * first one.
 * </p>
 * 
 * @author dlegland
 * @see LinearRing2D
 */
public class LineString2D implements Polyline2D
{
    // ===================================================================
    // Class variables
    
    private ArrayList<Point2D> vertices;
    
    
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
    // Methods implementing the Polyline2D interface
    
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
            double pos = Math.min(i * spacing2, nv - 1);
            vertices2.add(getPointAtLength(pos));
        }
        vertices2.add(this.vertices.get(this.vertices.size() - 1));
        
        return new LineString2D(vertices2);
    }
    
    /**
     * Transforms this geometry with the specified affine transform.
     * 
     * @param trans
     *            an affine transform
     * @return the transformed line string
     */
    public LineString2D transform(AffineTransform2D trans)
    {
        int n = this.vertexNumber();
        ArrayList<Point2D> newVertices = new ArrayList<Point2D>(n);
        for (int i = 0; i < n; i++)
        {
            newVertices.add(this.vertices.get(i).transform(trans));
        }
        
        LineString2D res = new LineString2D(0);
        res.vertices = newVertices;
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
        int n = this.vertexNumber();
        ArrayList<Point2D> newVertices = new ArrayList<Point2D>(n);
        for (int i = 0; i < n; i++)
        {
            newVertices.add(this.vertices.get(n-1-i));
        }
        
        LineString2D reverse = new LineString2D(0);
        reverse.vertices = newVertices;
        return reverse;
    }

    public Iterator<LineSegment2D> edgeIterator()
    {
    	return new EdgeIterator();
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
    // Edge iterator implementation
    
    class EdgeIterator implements Iterator<LineSegment2D>
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
		public LineSegment2D next()
		{
			int index2 = (index + 1) % vertices.size();
			return new LineSegment2D(vertices.get(index++), vertices.get(index2));
		}
    }
}
