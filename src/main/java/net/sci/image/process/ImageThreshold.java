/**
 * 
 */
package net.sci.image.process;

import net.sci.array.Array;
import net.sci.array.data.BooleanArray;
import net.sci.array.data.ScalarArray;
import net.sci.image.Image;
import net.sci.image.ImageOperator;

/**
 * Thresholds an image, by retaining only values greater than or equal to a
 * given threshold value.
 * 
 * @author dlegland
 *
 */
public class ImageThreshold implements ImageOperator
{
	double value;
	
	/**
	 * 
	 */
	public ImageThreshold(double value)
	{
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see net.sci.image.ImageOperator#process(net.sci.image.Image, net.sci.image.Image)
	 */
	@Override
	public void process(Image inputImage, Image outputImage)
	{
		Array<?> inputData = inputImage.getData();
		if (!(inputData instanceof ScalarArray))
		{
			throw new IllegalArgumentException("Input image must be scalar");
		}
		Array<?> outputData = outputImage.getData();
		if (!(outputData instanceof BooleanArray))
		{
			throw new IllegalArgumentException("Output image must be boolean");
		}
		if (inputData.dimensionality() != outputData.dimensionality())
		{
			throw new IllegalArgumentException("Input and output images must have same dimensionality");
		}

		process((ScalarArray<?>) inputData, (BooleanArray) outputData);
	}

	public void process(ScalarArray<?> source, BooleanArray target)
	{
		ScalarArray.Iterator<?> iter1 = source.iterator(); 
		BooleanArray.Iterator iter2 = target.iterator();
		
		while(iter1.hasNext() && iter2.hasNext())
		{
			iter1.forward();
			iter2.forward();
			iter2.setState(iter1.getValue() > this.value);
		}
	}
	
	/**
	 * Creates a new binary image that can be used as output for processing the
	 * given input image.
	 * 
	 * @param inputImage
	 *            the reference image
	 * @return a new instance of Image with binary data type
	 */
	public Image createEmptyOutputImage(Image inputImage)
	{
		Array<?> array = inputImage.getData();
		array = BooleanArray.create(array.getSize());
		return new Image(array, inputImage);
	}
	
}
