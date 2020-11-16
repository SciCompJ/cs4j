/**
 * 
 */
package net.sci.geom.geom2d.polygon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.sci.geom.geom2d.AffineTransform2D;
import net.sci.geom.geom2d.Box2D;
import net.sci.geom.geom2d.Curve2D;
import net.sci.geom.geom2d.LineSegment2D;
import net.sci.geom.geom2d.LinearGeometry2D;
import net.sci.geom.geom2d.Point2D;

/**
 * <p>
 * A Polyline2D is a curve composed of several line segments. It can be open or
 * closed. Open polylines are called LineString2D. Closed polylines are called
 * LineString2D.
 * </p>
 * 
 * @author dlegland
 * @see LineString2D
 */
public interface Polyline2D extends Curve2D
{
    // ===================================================================
    // Static factories
    
    public static Polyline2D create(List<Point2D> vertices, boolean closed)
    {
        if (closed)
        {
            return new LinearRing2D(vertices);
        }
        else
        {
            return new LineString2D(vertices);
        }
    }
    
    
    // ===================================================================
    // Inner interfaces 
    
    /**
     * A vertex of the polyline, used to encapsulate the position.
     */
    public interface Vertex
    {
    	public Point2D position();
    }

    /**
     * An edge of the polyline, defined by the source and target vertices.
     */
    public interface Edge
    {
    	public Vertex source();
    	
    	public Vertex target();
    	
    	public LineSegment2D curve(); 
    }

    
    // ===================================================================
    // New methods
    
    /**
     * Re-samples the polyline using the specified sampling step. The sampling
     * step is adapted such that all edges of new polyline have approximately
     * the same length.
     * 
     * @param spacing
     *            the spacing between two vertices on the original curve
     * @return a re-sampled polyline.
     */
    public Polyline2D resampleBySpacing(double spacing);

    /**
     * Returns a point from its curvilinear abscissa, between 0 and
     * polyline.length().
     * 
     * @param pos
     *            the curvilinear abcissa of the point, between 0 and
     *            polyline.length().
     * @return the coordinates of the point
     */
    public Point2D getPointAtLength(double pos);
    
    /**
     * @return return the curvilinear length of this polyline, as the sum of edge lengths.
     */
    public double length();
    
    
    // ===================================================================
    // Methods for managing vertices
    
    /**
     * Returns the number of vertices.
     * 
     * @return the number of vertices
     */
    public int vertexNumber();

    public Point2D vertexPosition(int vertexIndex);
    
    /**
     * Returns an iterable over the positions of the vertices.
     * 
     * @return the positions of the vertices
     */
    public Iterable<Point2D> vertexPositions();
    

    // ===================================================================
    // Management of edges 

    public Edge edge(int edgeIndex);
    
    public Iterable<? extends Edge> edges();
    
    public Iterator<? extends Edge> edgeIterator();


    // ===================================================================
    // Methods related to Curve2D 

	public default Collection<Point2D> intersections(LinearGeometry2D line)
	{
		ArrayList<Point2D> inters = new ArrayList<Point2D>();
		for (Edge edge : edges())
		{
			Point2D point = edge.curve().intersection(line);
			if (point != null)
			{
				inters.add(point);
			}
		}
		return inters;
	}
	
	
    /**
     * Returns the polyline composed with the same vertices, but in reverse order.
     * 
     * @return the polyline with same vertices but in reverse order.
     */
    public Polyline2D reverse();
    
    /**
	 * Compute the orthogonal projection of the input point onto this polyline.
	 * 
	 * @param point
	 *            the point to project.
	 * @return the position of the projected point.
	 */
	public default Point2D projection(Point2D point)
	{
		double dist, minDist = Double.POSITIVE_INFINITY;
		double x = point.getX();
		double y = point.getY();
		Point2D proj = vertexPosition(0);

		for (Edge edge : edges())
		{
			LineSegment2D seg = edge.curve();
			dist = seg.distance(x, y);
			if (dist < minDist)
			{
				minDist = dist;
				proj = seg.projection(point);
			}
		}

		return proj;
	}


    // ===================================================================
    // Geometry methods 

    /**
     * Transforms this geometry with the specified affine transform.
     * 
     * @param trans
     *            an affine transform
     * @return the transformed geometry
     */
    public Polyline2D transform(AffineTransform2D trans);

    
    // ===================================================================
    // Implementation of the Geometry2D interface 

    @Override
    public default boolean contains(Point2D point, double eps)
    {
        // Iterate on the line segments forming the polyline
    	for (Edge edge : edges())
    	{
    		if (edge.curve().contains(point, eps)) 
    		{
    			return true;
    		}
    	}
        return false;
    }

    /**
     * Iterates over edges to find the minimal distance between the test point
     * and this polyline.
     * 
     * @param x
     *            the x-coordinate of the point to test.
     * @param y
     *            the y-coordinate of the point to test.
     * @return the distance to the polyline.
     */
    public default double distance(double x, double y)
    {
        double minDist = Double.POSITIVE_INFINITY;
        
        // Iterate on the line segments forming the polyline
        for (Edge edge : edges())
        {
            minDist = Math.min(minDist, edge.curve().distance(x, y));
        }
        return minDist;
    }
    
    // ===================================================================
    // Implementation of the Geometry interface 

    /**
     * Returns true, as a linear ring is bounded by definition.
     */
    public default boolean isBounded()
    {
        return true;
    }

    public default Box2D boundingBox()
    {
        // initialize with extreme values
        double xmin = Double.POSITIVE_INFINITY;
        double xmax = Double.NEGATIVE_INFINITY;
        double ymin = Double.POSITIVE_INFINITY;
        double ymax = Double.NEGATIVE_INFINITY;
        
        // compute min/max for each coordinate
        for (Point2D vertex : this.vertexPositions())
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
}
