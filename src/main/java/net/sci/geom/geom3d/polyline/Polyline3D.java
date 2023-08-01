/**
 * 
 */
package net.sci.geom.geom3d.polyline;

import java.util.Collection;

import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.polygon.Polyline2D;
import net.sci.geom.geom3d.AffineTransform3D;
import net.sci.geom.geom3d.Bounds3D;
import net.sci.geom.geom3d.Curve3D;
import net.sci.geom.geom3d.LineSegment3D;
import net.sci.geom.geom3d.Point3D;

/**
 * @author dlegland
 *
 */
public interface Polyline3D extends Curve3D
{
    // ===================================================================
    // Static factories
    
    /**
     * Converts a 2D polyline into a 3D polyline by translating vertices in the
     * z direction.
     * 
     * @see #projectXY()
     * 
     * @param poly
     *            the polyline to convert
     * @param z
     *            the amount of translation in the z direction
     * @return the new 3D polyline
     */
    public static Polyline3D from2d(Polyline2D poly, double z)
    {
        int n = poly.vertexCount();
        Polyline3D res = poly.isClosed() ? LinearRing3D.create(n) : LineString3D.create(n);
        for (Point2D v : poly.vertexPositions())
        {
            res.addVertex(Point3D.from2d(v, z));
        }
        return res;
    }
    
    
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
     * Returns a pointer to the vertices.
     * 
     * @return a pointer to the collection of vertices
     */
    public Collection<Point3D> vertexPositions();
    
    /**
     * Adds a vertex (optional operation).
     * 
     * @param vertexPosition the position of the vertex to add.
     */
    public void addVertex(Point3D vertexPosition);

    public Point3D vertexPosition(int index);

    
    public Iterable<? extends Edge> edges();
    
    /**
     * Returns the polyline composed with the same vertices, but in reverse order.
     * 
     * @return the polyline with same vertices but in reverse order.
     */
    public Polyline3D reverse();
    
    /**
     * Projects this polyline onto the XY plane and converts into a 2D polyline.
     * 
     * @see #from2d(Polyline2D, double)
     * 
     * @return the 2D projection of the polyline onto the XY plane.
     */
    public Polyline2D projectXY();
    
    
    // ===================================================================
    // Specialization of Geometry3D interface 

    /**
     * Transforms this geometry with the specified affine transform.
     * 
     * @param trans
     *            an affine transform
     * @return the transformed geometry
     */
    public Polyline3D transform(AffineTransform3D trans);

    
    // ===================================================================
    // Implementation of the Geometry3D interface 

    @Override
    public default boolean contains(Point3D point, double eps)
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
     * Iterate over edges to find the minimal distance between the test point
     * and this polyline.
     * 
     * @param x
     *            the x-coordinate of the point to test
     * @param y
     *            the y-coordinate of the point to test
     * @return the distance to the polyline
     */
    public default double distance(double x, double y, double z)
    {
        double minDist = Double.POSITIVE_INFINITY;
        
        // Iterate on the line segments forming the polyline
        for (Edge edge : edges())
        {
            minDist = Math.min(minDist, edge.curve().distance(x, y, z));
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

    public default Bounds3D bounds()
    {
        return Bounds3D.of(vertexPositions());
    }
    
    @Override
    public Polyline3D duplicate();
    

    // ===================================================================
    // Inner interfaces 
    
    /**
     * A vertex of a 3D polyline, used to encapsulate the position.
     * 
     * @see Edge
     */
    public interface Vertex
    {
        /**
         * @return the position of this vertex, as a Point2D.
         */
        public Point3D position();
    }

    /**
     * An edge of a 3D polyline, defined by the source and target vertices.
     * 
     * Can also returns the curve drawn by the edge, as an instance of LineSegment3D.
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
        public LineSegment3D curve(); 
    }
}
