/**
 * 
 */
package net.sci.geom.geom2d.polygon;

import java.util.Collection;

import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.Domain2D;

/**
 * A polygonal domain whose boundary is composed of one or several linear
 * ring(s).
 * 
 * @author dlegland
 *
 */
public interface Polygon2D extends Domain2D
{
    // ===================================================================
    // Specific methods
    
    /**
     * Computed the complement of this polygon, that is the set of all the
     * points not contained by this polygon.
     * 
     * The complement polygon is expected to have a signed area opposite to the
     * signed area of this polygon.
     * 
     * @return the complement of this polygon.
     */
    public Polygon2D complement();
    
    /**
     * @return returns the signed area of this polygon.
     */
    public double signedArea();

    /**
     * Returns the vertices of this polygon. 
     */
    public Collection<Point2D> vertices();
    
    /**
     * @return the number of vertices in this polygon.
     */
    public int vertexNumber();
}
