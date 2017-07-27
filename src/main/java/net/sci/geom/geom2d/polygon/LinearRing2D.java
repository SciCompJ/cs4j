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
 * A LineString2D is a line string whose last point is connected to the first one.
 * This is typically the boundary of a (Simple)Polygon2D.
 * </p>
 * <p>
 * The name 'LineString2D' was used for 2 reasons:
 * <ul><li>it is short</li> <li>it is consistent with the JTS name</li></ul>
 * </p>
 * @author dlegland
 */
public class LinearRing2D implements Polyline2D
{
    // ===================================================================
    // Class variables
    
    private ArrayList<Point2D> vertices;
    
    // ===================================================================
    // Contructors

    public LinearRing2D() 
    {
        this.vertices = new ArrayList<Point2D>();
    }

    /**
     * Creates a new linear curve by allocating enough memory for the specified
     * number of vertices.
     * 
     * @param nVertices
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
    // Methods specific to ClosedPolyline2D

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

    public Iterator<LineSegment2D> edgeIterator()
    {
    	return new EdgeIterator();
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
			return index < vertices.size() - 1;
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
