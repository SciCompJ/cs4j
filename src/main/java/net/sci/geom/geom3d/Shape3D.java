/**
 * 
 */
package net.sci.geom.geom3d;

import net.sci.geom.Shape;

/**
 * @author dlegland
 *
 */
public interface Shape3D extends Shape
{
    /**
     * Returns dimensionality equals to 3.
     */
    @Override
    public default int dimensionality()
    {
        return 3;
    }


}
