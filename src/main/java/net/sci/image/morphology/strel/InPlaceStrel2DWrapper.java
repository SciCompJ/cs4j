/**
 * 
 */
package net.sci.image.morphology.strel;

import net.sci.array.numeric.ScalarArray3D;

/**
 * Utility class that encapsulates a 2D in-place strel to see it as a 3D
 * in-place strel. The neighborhood of the resulting structuring element is
 * entirely located in the current slice.
 * 
 * @author dlegland
 */
public class InPlaceStrel2DWrapper extends Strel2DWrapper implements InPlaceStrel3D
{
    public InPlaceStrel2DWrapper(InPlaceStrel2D strel2d)
    {
        super(strel2d);
    }
    
    
    @Override
    public void inPlaceDilation3d(ScalarArray3D<?> array)
    {
        int sizeZ = array.size(2);
        for (int z = 0; z < sizeZ; z++)
        {
            // perform operation on current slice
            this.fireProgressChanged(this, z, sizeZ);
            ((InPlaceStrel2D) this.strel2d).inPlaceDilation2d(array.slice(z));
        }
        this.fireProgressChanged(this, 1, 1);
    }


    @Override
    public void inPlaceErosion3d(ScalarArray3D<?> array)
    {
        int sizeZ = array.size(2);
        for (int z = 0; z < sizeZ; z++)
        {
            // perform operation on current slice
            this.fireProgressChanged(this, z, sizeZ);
            ((InPlaceStrel2D) this.strel2d).inPlaceErosion2d(array.slice(z));
        }
        this.fireProgressChanged(this, 1, 1);
    }


    @Override
    public InPlaceStrel3D reverse()
    {
        return new InPlaceStrel2DWrapper(((InPlaceStrel2D) strel2d).reverse());
    }
}
