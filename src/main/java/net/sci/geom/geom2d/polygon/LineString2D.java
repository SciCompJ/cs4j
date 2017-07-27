/**
 * 
 */
package net.sci.geom.geom2d.polygon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.line.LineSegment2D;

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
     * @param nVertices
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
    public ArrayList<Point2D> vertices()
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

    /**
     * Computes the index of the closest vertex to the input point.
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
    
    public Iterator<LineSegment2D> edgeIterator()
    {
    	return new EdgeIterator();
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
        newVertices.add(this.vertices.get(0));
        for (int i = 0; i < n; i++)
        {
            newVertices.set(i, this.vertices.get(n-1-i));
        }
        
        LineString2D reverse = new LineString2D(0);
        reverse.vertices = newVertices;
        return reverse;
    }


    // ===================================================================
    // Edge iterator implementation
    
    class EdgeIterator implements Iterator<LineSegment2D>
    {
    	/**
    	 * Index of the first vertex of current edge
    	 */
    	int index = -1;

    	@Override
		public boolean hasNext()
		{
			return index < vertices.size() - 2;
		}

		@Override
		public LineSegment2D next()
		{
			index++;
			int index2 = (index + 1) % vertices.size();
			return new LineSegment2D(vertices.get(index), vertices.get(index2));
		}
    }
}
