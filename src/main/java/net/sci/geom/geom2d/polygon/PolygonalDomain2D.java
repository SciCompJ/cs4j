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
public interface PolygonalDomain2D extends Domain2D
{
    // ===================================================================
    // Specific methods
    
    /**
     * Computed the complement of this polygonal domain, that is the set of all
     * the points not contained by this polygonal domain.
     * 
     * The complement polygonal domain is expected to have a signed area
     * opposite to the signed area of this polygonal domain.
     * 
     * @return the complement of this polygon.
     */
    public PolygonalDomain2D complement();
    
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
