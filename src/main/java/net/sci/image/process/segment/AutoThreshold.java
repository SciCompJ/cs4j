/**
 * 
 */
package net.sci.image.process.segment;

import net.sci.array.Array;
import net.sci.array.data.BinaryArray;
import net.sci.array.data.ScalarArray;
import net.sci.image.Image;
import net.sci.image.ImageToImageOperator;

/**
 * Base implementation for auto-threshold algorithms.
 * 
 * @author dlegland
 *
 */
public abstract class AutoThreshold implements ImageToImageOperator
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
		if (!(outputData instanceof BinaryArray))
		{
			throw new IllegalArgumentException("Output image must be boolean");
		}
		if (inputData.dimensionality() != outputData.dimensionality())
		{
			throw new IllegalArgumentException("Input and output images must have same dimensionality");
		}

		process((ScalarArray<?>) inputData, (BinaryArray) outputData);
	}

	/**
	 * Computes the threshold value on a scalar array and returns the resulting
	 * binary array.
	 * 
	 * @param source
	 *            the scalar array to threshold
	 * @return the binary array resulting from thresholding
	 */
	public BinaryArray processScalar(ScalarArray<?> source)
	{
		BinaryArray target = createEmptyOutputArray(source);
		process(source, target);
		return target;
	}
	
	public void process(ScalarArray<?> source, BinaryArray target)
	{
		// compute threshold value
		double value = computeThresholdValue(source);
		
		// create array iterators
		ScalarArray.Iterator<?> iter1 = source.iterator(); 
		BinaryArray.Iterator iter2 = target.iterator();
		
		// iterate on both arrays for computing segmented values
		while(iter1.hasNext() && iter2.hasNext())
		{
			iter1.forward();
			iter2.forward();
			iter2.setState(iter1.getValue() >= value);
		}
	}
	
	/**
	 * Creates a new boolean array that can be used as output for processing the
	 * given input array.
	 * 
	 * @param inputArray
	 *            the reference array
	 * @return a new instance of Array that can be used for processing input
	 *         array.
	 */
	public BinaryArray createEmptyOutputArray(Array<?> inputArray)
	{
		return BinaryArray.create(inputArray.getSize());
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
		BinaryArray outputArray = BinaryArray.create(array.getSize());
		return new Image(outputArray, inputImage);
	}
}
