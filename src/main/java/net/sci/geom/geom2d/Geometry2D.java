/**
 * 
 */
package net.sci.geom.geom2d;

import net.sci.geom.Geometry;

/**
 * A shape embedded into a 2-dimensional space.
 * 
 * @author dlegland
 *
 */
public interface Geometry2D extends Geometry
{
    /**
     * Returns dimensionality equals to 2.
     */
    @Override
    public default int dimensionality()
    {
        return 2;
    }

}
