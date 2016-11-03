/**
 * 
 */
package net.sci.image.process.segment;

import net.sci.array.Array;
import net.sci.array.data.BooleanArray;
import net.sci.array.data.ScalarArray;
import net.sci.image.Image;
import net.sci.image.ImageOperator;

/**
 * Base implementation for auto-threshold algorithms.
 * 
 * @author dlegland
 *
 */
public abstract class AutoThreshold implements ImageOperator
{
	public abstract double computeThresholdValue(ScalarArray<?> array);
	
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
		// compute threshold value
		double value = computeThresholdValue(source);
		
		// create array iterators
		ScalarArray.Iterator<?> iter1 = source.iterator(); 
		BooleanArray.Iterator iter2 = target.iterator();
		
		// iterate on both arrays for computing segmented values
		while(iter1.hasNext() && iter2.hasNext())
		{
			iter1.forward();
			iter2.forward();
			iter2.setState(iter1.getValue() >= value);
		}
	}
	
	/**
	 * Creates a new boolean image that can be used as output for processing the
	 * given input image.
	 * 
	 * @param inputImage
	 *            the reference image
	 * @return a new instance of Image that can be used for processing input
	 *         image.
	 */
	public Image createEmptyOutputImage(Image inputImage)
	{
		Array<?> array = inputImage.getData();
		BooleanArray outputArray = BooleanArray.create(array.getSize());
		return new Image(outputArray, inputImage);
	}
}
