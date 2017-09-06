/**
 * 
 */
package net.sci.geom.geom2d.polygon;

import java.util.Collection;
import java.util.Iterator;

import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.transform.AffineTransform2D;
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
    // New methods
    
    public PolygonalDomain2D transform(AffineTransform2D trans);
    
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
     * Returns an iterator to the set of vertices contained within this polygon.
     * 
     * @return an iterator to the vertices in the polygon.
     */
    public default Iterator<Point2D> vertexIterator()
    {
        return this.vertices().iterator();
    }
    
    /**
     * @return the number of vertices in this polygon.
     */
    public int vertexNumber();
}
