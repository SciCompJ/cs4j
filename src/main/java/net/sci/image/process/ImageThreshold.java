/**
 * 
 */
package net.sci.image.process;

import net.sci.array.Array;
import net.sci.array.data.BinaryArray;
import net.sci.array.data.ScalarArray;
import net.sci.image.ArrayToArrayImageOperator;

/**
 * Thresholds an image, by retaining only values greater than or equal to a
 * given threshold value.
 * 
 * @author dlegland
 *
 */
public class ImageThreshold implements ArrayToArrayImageOperator
{
	double value;
	
	/**
	 * Creates a new instance of ImageThreshold.
	 * 
	 * @param value
	 *     the value for threshold.
	 */
	public ImageThreshold(double value)
	{
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see net.sci.image.ImageOperator#process(net.sci.image.Image, net.sci.image.Image)
	 */
	@Override
	public void process(Array<?> source, Array<?> target)
	{
		if (!(source instanceof ScalarArray))
		{
			throw new IllegalArgumentException("Input image must be scalar");
		}
		if (!(target instanceof BinaryArray))
		{
			throw new IllegalArgumentException("Output image must be boolean");
		}
		if (source.dimensionality() != target.dimensionality())
		{
			throw new IllegalArgumentException("Input and output images must have same dimensionality");
		}

		processScalar((ScalarArray<?>) source, (BinaryArray) target);
	}

//	/* (non-Javadoc)
//	 * @see net.sci.image.ImageOperator#process(net.sci.image.Image, net.sci.image.Image)
//	 */
//	@Override
//	public void process(Image inputImage, Image outputImage)
//	{
//		Array<?> inputData = inputImage.getData();
//		if (!(inputData instanceof ScalarArray))
//		{
//			throw new IllegalArgumentException("Input image must be scalar");
//		}
//		Array<?> outputData = outputImage.getData();
//		if (!(outputData instanceof BooleanArray))
//		{
//			throw new IllegalArgumentException("Output image must be boolean");
//		}
//		if (inputData.dimensionality() != outputData.dimensionality())
//		{
//			throw new IllegalArgumentException("Input and output images must have same dimensionality");
//		}
//
//		processScalar((ScalarArray<?>) inputData, (BooleanArray) outputData);
//	}

	public void processScalar(ScalarArray<?> source, BinaryArray target)
	{
		ScalarArray.Iterator<?> iter1 = source.iterator(); 
		BinaryArray.Iterator iter2 = target.iterator();
		
		while(iter1.hasNext() && iter2.hasNext())
		{
			iter1.forward();
			iter2.forward();
			iter2.setState(iter1.getValue() > this.value);
		}
	}
	
	/**
	 * Creates a new binary array that can be used as output for processing the
	 * given input array.
	 * 
	 * @param array
	 *            the reference array
	 * @return a new instance of BooleanArray
	 */
	public BinaryArray createEmptyOutputArray(Array<?> array)
	{
		return BinaryArray.create(array.getSize());
	}
	
//	/**
//	 * Creates a new binary image that can be used as output for processing the
//	 * given input image.
//	 * 
//	 * @param inputImage
//	 *            the reference image
//	 * @return a new instance of Image with binary data type
//	 */
//	public Image createEmptyOutputImage(Image inputImage)
//	{
//		Array<?> array = inputImage.getData();
//		array = BooleanArray.create(array.getSize());
//		return new Image(array, inputImage);
//	}
	
	public boolean canProcess(Array<?> source, Array<?> target)
	{
		if (!(source instanceof ScalarArray))
		{
			return false;
		}
		if (!(target instanceof BinaryArray))
		{
			return false;
		}
		return source.dimensionality() == target.dimensionality();
	}
}
