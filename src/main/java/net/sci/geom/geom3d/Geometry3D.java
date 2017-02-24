/**
 * 
 */
package net.sci.geom.geom3d;

import net.sci.geom.Geometry;

/**
 * @author dlegland
 *
 */
public interface Geometry3D extends Geometry
{
    /**
     * Returns dimensionality equals to 3.
     */
    @Override
    public default int dimensionality()
    {
        return 3;
    }
    
    public Box3D boundingBox();
}
