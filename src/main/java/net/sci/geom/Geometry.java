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
     * Checks whether this geometry is bounded.
     * 
     * @return true if this geometry can be bounded by a bounding box with
     *         finite value, or false otherwise (like for straight lines, rays,
     *         parabola...)
     */
    public boolean isBounded();
    
    /**
     * Returns the bounds of this geometry
     * 
     * @return the bounds of this geometry
     */
    public Bounds bounds();
    
    /**
     * Returns a deep-copy of this geometry.
     * 
     * @return a deep-copy of this geometry.
     */
    public Geometry duplicate();
}
