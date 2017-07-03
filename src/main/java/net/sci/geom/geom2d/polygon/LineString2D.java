/**
 * 
 */
package net.sci.geom.geom2d.polygon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.sci.geom.geom2d.Box2D;
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
    
    
    // ===================================================================
    // Implementation of the Geometry2D interface 

    @Override
    public boolean contains(Point2D point, double eps)
    {
        // Extract the last point of the collection
        Point2D previous = vertices.get(0);
        
        // Iterate on couple of vertices, starting from couple (firt,first)
        for (Point2D current : vertices)
        {
            LineSegment2D edge = new LineSegment2D(previous, current);
            
            if (edge.contains(point, eps))
            {
                return true;
            }
            
            previous = current;
        }
        
        return false;
    }

    // Iterate over edges to find the minimal distance
    @Override
    public double distance(Point2D point)
    {
        // Extract the last point of the collection
        Point2D previous = vertices.get(vertices.size() - 1);
        double minDist = Double.POSITIVE_INFINITY;
        
        // Iterate on couple of vertices, starting from couple (last,first)
        for (Point2D current : vertices)
        {
            LineSegment2D edge = new LineSegment2D(previous, current);
            
            double dist = edge.distance(point);
            if (dist < minDist)
            {
                minDist = dist;
            }
            
            previous = current;
        }
        
        return minDist;
    }
    
    // ===================================================================
    // Implementation of the Geometry interface 

    /**
     * Returns true, as a linear ring is bounded by definition.
     */
    public boolean isBounded()
    {
        return true;
    }

    @Override
    public Box2D boundingBox()
    {
        // initialize with extreme values
        double xmin = Double.POSITIVE_INFINITY;
        double xmax = Double.NEGATIVE_INFINITY;
        double ymin = Double.POSITIVE_INFINITY;
        double ymax = Double.NEGATIVE_INFINITY;
        
        // compute min/max for each coordinate
        for (Point2D vertex : this.vertices)
        {
            double x = vertex.getX();
            double y = vertex.getY();
            xmin = Math.min(xmin, x);
            xmax = Math.max(xmax, x);
            ymin = Math.min(ymin, y);
            ymax = Math.max(ymax, y);
        }
        
        // return new Bounding Box
        return new Box2D(xmin, xmax, ymin, ymax);
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
