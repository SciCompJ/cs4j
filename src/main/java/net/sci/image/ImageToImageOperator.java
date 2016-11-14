/**
 * 
 */
package net.sci.image;

import net.sci.array.Array;

/**
 * @author dlegland
 *
 */
public interface ImageToImageOperator extends ImageOperator
{
	public void process(Image inputImage, Image outputImage);
	
	/**
	 * Creates a new image that can be used as output for processing the given
	 * input image.
	 * 
	 * @param inputImage
	 *            the reference image
	 * @return a new instance of Image that can be used for processing input
	 *         image.
	 */
	public default Image createEmptyOutputImage(Image inputImage)
	{
		Array<?> array = inputImage.getData();
		array = array.newInstance(array.getSize());
		return new Image(array, inputImage);
	}
	
	public default Image process(Image inputImage)
	{
		Image outputImage = createEmptyOutputImage(inputImage);
		process(inputImage, outputImage);
		return outputImage;
	}

}
