/**
 * 
 */
package net.sci.geom.geom2d.curve;

import java.util.Collection;
import java.util.List;

import net.sci.geom.geom2d.AffineTransform2D;
import net.sci.geom.geom2d.Curve2D;

/**
 * A continuous curve that can represent the contour of a region in the plane.
 * 
 * @author dlegland
 * 
 * @see Boundary2D
 * @see net.sci.geom.geom2d.Domain2D
 */
public interface Contour2D extends Curve2D, Boundary2D
{
    /**
     * Returns a collection of Contour2D that contains only this contour.
     * 
     * @return a collection of contours containing only this contour.
     */
    @Override
    public default Collection<? extends Contour2D> curves() 
    {
        return List.of(this);
    }    
    
    @Override
    public Contour2D duplicate();
    
    @Override
    public Contour2D transform(AffineTransform2D transfo);
}
