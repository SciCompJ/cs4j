/**
 * 
 */
package net.sci.geom.geom3d.polyline;

import java.util.Collection;

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
    
    public Iterable<? extends Edge> edges();
    
    /**
     * Returns the polyline composed with the same vertices, but in reverse order.
     * 
     * @return the polyline with same vertices but in reverse order.
     */
    public Polyline3D reverse();
    

    // ===================================================================
    // Specialization of Geometry interface 

    /**
     * Transforms this geometry with the specified affine transform.
     * 
     * @param trans
     *            an affine transform
     * @return the transformed geometry
     */
    public Polyline3D transform(AffineTransform3D trans);

    
    // ===================================================================
    // Implementation of the Geometry2D interface 

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
        // initialize with extreme values
        double xmin = Double.POSITIVE_INFINITY;
        double xmax = Double.NEGATIVE_INFINITY;
        double ymin = Double.POSITIVE_INFINITY;
        double ymax = Double.NEGATIVE_INFINITY;
        double zmin = Double.POSITIVE_INFINITY;
        double zmax = Double.NEGATIVE_INFINITY;
        
        // compute min/max for each coordinate
        for (Point3D vertex : this.vertexPositions())
        {
            double x = vertex.getX();
            double y = vertex.getY();
            double z = vertex.getZ();
            xmin = Math.min(xmin, x);
            xmax = Math.max(xmax, x);
            ymin = Math.min(ymin, y);
            ymax = Math.max(ymax, y);
            zmin = Math.min(zmin, z);
            zmax = Math.max(zmax, z);
        }
        
        // return new Bounding Box
        return new Bounds3D(xmin, xmax, ymin, ymax, zmin, zmax);
    }
    
    
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
