/**
 * 
 */
package net.sci.geom.geom2d.polygon;

import net.sci.geom.geom2d.Domain2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.transform.AffineTransform2D;

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
    
    /**
     * @return the set of linear rings that compose the boundary of this
     *         polygonal  domain.
     */
    public Iterable<LinearRing2D> rings();
    
    public PolygonalDomain2D transform(AffineTransform2D trans);
    
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

    
    // ===================================================================
    // Management of vertices
    
    /**
     * Returns the vertex positions of this polygon.
     * 
     * @return the vertex positions of this polygon
     */
    public Iterable<Point2D> vertexPositions();
        
    /**
     * @return the number of vertices in this polygon.
     */
    public int vertexNumber();
}
