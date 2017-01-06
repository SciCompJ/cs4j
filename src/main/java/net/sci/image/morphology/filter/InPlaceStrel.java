/**
 * 
 */
package net.sci.image.morphology.filter;

import net.sci.array.data.Array2D;
import net.sci.image.morphology.Strel;

/**
 * A structuring element that can performs erosion or dilation directly in the
 * original image buffer. As InPlaceStrel do not require memory allocation, 
 * they result in faster execution.
 * 
 * @see SeparableStrel
 * @author David Legland
 *
 */
public interface InPlaceStrel extends Strel {

	/**
	 * Performs dilation of the image given as argument, and stores the result
	 * in the same image. 
	 * @param image the input image to dilate
	 */
	public void inPlaceDilation(Array2D<?> image);

	/**
	 * Performs erosion of the image given as argument, and stores the result
	 * in the same image. 
	 * @param image the input image to erode
	 */
	public void inPlaceErosion(Array2D<?> image);
	
	/**
	 * The reverse structuring element of an InPlaceStrel is also an
	 * InPlaceStrel.
	 */
	public InPlaceStrel reverse();
}
