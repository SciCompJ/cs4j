/**
 * 
 */
package net.sci.geom.geom2d.curve;

import java.util.ArrayList;
import java.util.Collection;

import net.sci.geom.geom2d.Curve2D;

/**
 * A continuous curve that can represent the contour of a region in the plane.
 * 
 * @author dlegland
 * 
 * @see Boundary2D
 * @see net.sci.geom.geom2d.Region2D
 */
public interface Contour2D extends Curve2D, Boundary2D
{
    /**
     * Returns a collection of Contour2D that contains only this contour.
     * 
     * @returns a collection of contours containing only this contour.
     */
    @Override
    public default Collection<? extends Contour2D> curves() 
    {
        ArrayList<Contour2D> res = new ArrayList<Contour2D>(1);
        res.add(this);
        return res;
    }    
}
