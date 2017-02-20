/**
 * 
 */
package net.sci.geom.geom2d;

import net.sci.geom.Shape;

/**
 * A shape embedded into a 2-dimensional space.
 * 
 * @author dlegland
 *
 */
public interface Shape2D extends Shape
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
