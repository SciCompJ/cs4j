/**
 * 
 */
package net.sci.geom.geom3d.polyline;

import java.util.Collection;
import java.util.Iterator;

import net.sci.geom.geom3d.Box3D;
import net.sci.geom.geom3d.Curve3D;
import net.sci.geom.geom3d.Point3D;
import net.sci.geom.geom3d.line.LineSegment3D;
import net.sci.geom.geom3d.transform.AffineTransform3D;

/**
 * @author dlegland
 *
 */
public interface Polyline3D extends Curve3D
{
    // ===================================================================
    // Methods for managing vertices
    
    /**
     * Returns a pointer to the vertices.
     * 
     * @return a pointer to the collection of vertices
     */
    public Collection<Point3D> vertices();
    
    /**
     * Returns the number of vertices.
     * 
     * @return the number of vertices
     */
    public int vertexNumber();

    public Iterator<LineSegment3D> edgeIterator();

    /**
     * Returns the polyline composed with the same vertices, but in reverse order.
     * 
     * @return the polyline with same vertices but in reverse order.
     */
    public Polyline3D reverse();
    

    // ===================================================================
    // Geoemtry methods 

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
        Iterator<LineSegment3D> iter = edgeIterator();
        while(iter.hasNext())
        {
            if (iter.next().contains(point, eps))
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
        Iterator<LineSegment3D> iter = edgeIterator();
        while(iter.hasNext())
        {
            minDist = Math.min(minDist, iter.next().distance(x, y, z));
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

    public default Box3D boundingBox()
    {
        // initialize with extreme values
        double xmin = Double.POSITIVE_INFINITY;
        double xmax = Double.NEGATIVE_INFINITY;
        double ymin = Double.POSITIVE_INFINITY;
        double ymax = Double.NEGATIVE_INFINITY;
        double zmin = Double.POSITIVE_INFINITY;
        double zmax = Double.NEGATIVE_INFINITY;
        
        // compute min/max for each coordinate
        for (Point3D vertex : this.vertices())
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
        return new Box3D(xmin, xmax, ymin, ymax, zmin, zmax);
    }
}
