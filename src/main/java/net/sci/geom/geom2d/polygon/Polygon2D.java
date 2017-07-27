/**
 * 
 */
package net.sci.geom.geom2d.polygon;

import java.util.Collection;

import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.Region2D;

/**
 * A polygonal region whose boundary is composed of one or several linear
 * ring(s).
 * 
 * @author dlegland
 *
 */
public interface Polygon2D extends Region2D
{
    // ===================================================================
    // Specific methods
    
    /**
     * Returns the vertices of this polygon. 
     */
    public Collection<Point2D> vertices();
    
    /**
     * @return the number of vertices in this polygon.
     */
    public int vertexNumber();
}
