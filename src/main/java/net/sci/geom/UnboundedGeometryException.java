/**
 * 
 */

package net.sci.geom;

/**
 * Exception thrown when an unbounded geometry is involved in an operation that
 * assumes a bounded geometry.
 * 
 * @author dlegland
 */
public class UnboundedGeometryException extends RuntimeException
{
    /**
     * The (unbounded) geometry that caused the exception.
     */
    private Geometry geometry;
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public UnboundedGeometryException(Geometry geometry)
    {
        this.geometry = geometry;
    }
    
    public Geometry getGeometry()
    {
        return geometry;
    }
}
