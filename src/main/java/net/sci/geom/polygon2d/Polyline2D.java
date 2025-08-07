/**
 * 
 */
package net.sci.geom.polygon2d;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sci.geom.geom2d.AffineTransform2D;
import net.sci.geom.geom2d.Bounds2D;
import net.sci.geom.geom2d.Curve2D;
import net.sci.geom.geom2d.LineSegment2D;
import net.sci.geom.geom2d.LinearGeometry2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.Vector2D;

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
            return LinearRing2D.create(vertices);
        }
        else
        {
            return LineString2D.create(vertices);
        }
    }
    
    
    
    // ===================================================================
    // New methods for polylines
    
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
     * Smoothes a polyline by computing for each vertex the average position of
     * neighbor vertices.
     * 
     * @param smoothingSize
     *            the number of neighbors to consider for smoothing.
     * @return the smoothed polyline.
     */
    public Polyline2D smooth(int smoothingSize);
    
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
    	double x = point.x();
    	double y = point.y();
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
    // Global management of vertices
    
    /**
     * Merge adjacent vertices that are two close away. Returns a new polyline.
     * 
     * @param minDist
     *            the minimum distance to ensure between vertices.
     * @return a polyline where consecutive vertices are at least
     *         <code>minDist</code> apart.
     */
    public Polyline2D mergeMultipleVertices(double minDist);
    
    
    // ===================================================================
    // Methods for managing vertices
    
    /**
     * Returns the number of vertices.
     * 
     * @return the number of vertices
     */
    public int vertexCount();

    /**
     * Returns an iterable over  the vertices of the polyline.
     * 
     * @return the positions of the vertices
     */
    public Iterable<? extends Vertex> vertices();

    /**
     * Adds a new vertex at the specified location.
     * 
     * @param pos the location of the new vertex.
     */
    public void addVertex(Point2D pos);
    
    public Point2D vertexPosition(int vertexIndex);
    
    /**
     * Returns an iterable over the positions of the vertices.
     * 
     * @return the positions of the vertices
     */
    public List<Point2D> vertexPositions();
    

    // ===================================================================
    // Management of edges 

    /**
     * Returns the number of edges within this polyline. In case of a closed
     * polyline, this equals the number of vertices. In case of an open
     * polyline, this equals the number of vertices minus one.
     * 
     * @return the number of edges within this polyline.
     */
    public int edgeCount();
    
    public Iterable<? extends Edge> edges();
    
    public Edge edge(int edgeIndex);


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

    public default Bounds2D bounds()
    {
        return Bounds2D.of(this.vertexPositions());
    }
    
    @Override
    public Polyline2D duplicate();
    

    // ===================================================================
    // Inner interfaces 
    
    /**
     * A vertex of the polyline, used to encapsulate the position.
     * 
     * The normal vector at the vertex may also be defined (optional operation).
     * 
     * @see Edge
     */
    public interface Vertex
    {
        /**
         * @return the position of this vertex, as a Point2D.
         */
        public Point2D position();
        
        /**
         * Returns the normal computed at this vertex (optional operation). 
         * Default is to throw an Exception.
         * 
         * @return the normal computed at this vertex, as a Vector2D.
         */
        public default Vector2D normal()
        {
            throw new RuntimeException("Unimplemented operation");
        }
    }

    /**
     * An edge of the polyline, defined by the source and target vertices.
     * 
     * @see Vertex
     */
    public interface Edge
    {
        /**
         * @return the source vertex of this edge.
         */
        public Vertex source();
        
        /**
         * @return the target vertex of this edge.
         */
        public Vertex target();
        
        /**
         * @return the line segment geometry corresponding to this edge.
         */
        public LineSegment2D curve(); 
    }

}
