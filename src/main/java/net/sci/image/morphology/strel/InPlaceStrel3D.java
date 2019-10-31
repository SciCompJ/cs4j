/**
 * 
 */
package net.sci.image.morphology.strel;

import net.sci.array.scalar.ScalarArray3D;

/**
 * A 3D structuring element that can performs erosion or dilation directly on
 * the original array. As InPlaceStrel do not require memory allocation, they
 * usually result in faster execution.
 * 
 * @see InPlaceStrel2D
 * 
 * @author dlegland
 *
 */
public interface InPlaceStrel3D extends Strel3D
{
    // ===================================================================
    // Static methods
    
    public static InPlaceStrel3D wrap(InPlaceStrel2D strel2d)
    {
        return new InPlaceStrel2DWrapper(strel2d);
    }
    
    // ===================================================================
    // New methods
    
    /**
     * Performs a morphological dilation on the array given as argument, and
     * stores the result in the same array.
     * 
     * @param array
     *            the input array to dilate
     */
    public void inPlaceDilation(ScalarArray3D<?> array);
    
    /**
     * Performs a morphological erosion on the array given as argument, and
     * stores the result in the same array.
     * 
     * @param array
     *            the input array to erode
     */
    public void inPlaceErosion(ScalarArray3D<?> array);
    

    // ===================================================================
    // Default implementations of Strel3D methods
    
    public default ScalarArray3D<?> dilation(ScalarArray3D<?> array)
    {
        ScalarArray3D<?> result = array.duplicate();
        this.inPlaceDilation(result);
        return result;
    }

    public default ScalarArray3D<?> erosion(ScalarArray3D<?> array)
    {
        ScalarArray3D<?> result = array.duplicate();
        this.inPlaceErosion(result);
        return result;
    }

    public default ScalarArray3D<?> closing(ScalarArray3D<?> array)
    {
        ScalarArray3D<?> result = array.duplicate();
        this.inPlaceDilation(result);
        this.reverse().inPlaceErosion(result);
        return result;
    }

    public default ScalarArray3D<?> opening(ScalarArray3D<?> array)
    {
        ScalarArray3D<?> result = array.duplicate();
        this.inPlaceErosion(result);
        this.reverse().inPlaceDilation(result);
        return result;
    }


    // ===================================================================
    // Specialize Strel3D methods
    
    /**
     * The reverse structuring element of an InPlaceStrel is also an
     * InPlaceStrel.
     */
    public InPlaceStrel3D reverse();
    
}
