/**
 * 
 */
package net.sci.image.morphology.strel;

import net.sci.array.numeric.ScalarArray2D;

/**
 * A structuring element that can performs erosion or dilation directly in the
 * original image buffer. As InPlaceStrel do not require memory allocation, 
 * they result in faster execution.
 * 
 * @see SeparableStrel2D
 * @author David Legland
 *
 */
public interface InPlaceStrel2D extends Strel2D
{
    // ===================================================================
    // New methods
    
	/**
	 * Performs dilation of the image given as argument, and stores the result
	 * in the same image. 
	 * @param image the input image to dilate
	 */
	public void inPlaceDilation(ScalarArray2D<?> image);

	/**
	 * Performs erosion of the image given as argument, and stores the result
	 * in the same image. 
	 * @param image the input image to erode
	 */
	public void inPlaceErosion(ScalarArray2D<?> image);
	
	
    // ===================================================================
    // Default implementations of Strel2D methods
    
    public default ScalarArray2D<?> dilation(ScalarArray2D<?> array)
    {
        ScalarArray2D<?> result = array.duplicate();
        this.inPlaceDilation(result);
        return result;
    }

    public default ScalarArray2D<?> erosion(ScalarArray2D<?> array)
    {
        ScalarArray2D<?> result = array.duplicate();
        this.inPlaceErosion(result);
        return result;
    }

    public default ScalarArray2D<?> closing(ScalarArray2D<?> array)
    {
        ScalarArray2D<?> result = array.duplicate();
        this.inPlaceDilation(result);
        this.reverse().inPlaceErosion(result);
        return result;
    }

    public default ScalarArray2D<?> opening(ScalarArray2D<?> array)
    {
        ScalarArray2D<?> result = array.duplicate();
        this.inPlaceErosion(result);
        this.reverse().inPlaceDilation(result);
        return result;
    }

    
    // ===================================================================
    // Specialize Strel2D methods
    
	/**
	 * The reverse structuring element of an InPlaceStrel is also an
	 * InPlaceStrel.
	 */
	public InPlaceStrel2D reverse();
}
