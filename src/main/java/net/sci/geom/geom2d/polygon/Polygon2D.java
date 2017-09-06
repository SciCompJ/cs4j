/**
 * 
 */
package net.sci.geom.geom2d.polygon;

import java.util.Collection;

import net.sci.geom.geom2d.Point2D;

/**
 * A polygonal region whose boundary is a single linear ring.
 * 
 * @author dlegland
 * 
 * @see LinearRing2D
 */
public interface Polygon2D extends PolygonalDomain2D
{
    // ===================================================================
    // Static factories    
    
    /**
     * Creates a new instance of Polygon2D from a collection of vertices.
     * 
     * @param vertices
     *            the vertices stored in an array of Point2D
     */
    public static Polygon2D create(Collection<? extends Point2D> vertices)
    {
        return new SimplePolygon2D(vertices);
    }

    /**
     * Creates a new instance of Polygon2D from a collection of vertices.
     * 
     * @param vertices
     *            the vertices stored in an array of Point2D
     */
    public static Polygon2D create(Point2D... vertices)
    {
        return new SimplePolygon2D(vertices);
    }
    
    /**
     * Creates a new instance of Polygon2D from the x and y coordinates of each vertex.
     * 
     * @param xcoords
     *            the x coordinate of each vertex
     * @param ycoords
     *            the y coordinate of each vertex
     */
    public static Polygon2D create(double[] xcoords, double[] ycoords)
    {
        return new SimplePolygon2D(xcoords, ycoords);
    }
    
    // ===================================================================
    // Specialization of the PolygonalDomain2D interface    
    
    @Override
    public Polygon2D complement();
    
    @Override
    public LinearRing2D boundary();
    
}
