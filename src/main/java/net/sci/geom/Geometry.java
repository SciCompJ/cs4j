/**
 * 
 */
package net.sci.geom;

import net.sci.array.Dimensional;

/**
 * A shape embedded into a N-dimensional space.
 * 
 * @author dlegland
 *
 */
public interface Geometry extends Dimensional
{
    /**
     * @return true if this geometry can be bounded by a bounding box with
     *         finite value, or false otherwise (like for straight lines, rays,
     *         parabola...)
     */
    public boolean isBounded();
    
    /**
     * @return the bounding box of this geometry
     */
    public Box boundingBox();
}
