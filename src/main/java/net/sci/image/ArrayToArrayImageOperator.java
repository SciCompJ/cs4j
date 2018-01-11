/**
 * 
 */
package net.sci.image;

import net.sci.array.Array;
import net.sci.array.ArrayToArrayOperator;

/**
 * An image operator that operates on image data array.
 * 
 * As methods provide default implementation, it suffices to add the
 * 'implements' tag to an array operator to make it applicable to image as well.
 * 
 * @author dlegland
 *
 */
@Deprecated
//TODO: remove
public interface ArrayToArrayImageOperator extends ArrayToArrayOperator, ImageArrayOperator
{
	public default void process(Image inputImage, Image outputImage)
	{
		Array<?> inputArray = inputImage.getData();
		Array<?> outputArray = outputImage.getData();
		process(inputArray, outputArray);
	}

	/**
	 * Calls the "createEmptyOutputArray" methods from ArrayOperator interface
	 * for creating the result array that will be used for storing data.
	 * 
	 * @param image
	 *            the reference image
	 * @return a new instance of Image that can be used for processing input
	 *         image.
	 */
	public default Image createEmptyOutputImage(Image image)
	{
		Array<?> array = image.getData();
		Array<?> newArray = createEmptyOutputArray(array);
		return new Image(newArray, image);
	}
}
