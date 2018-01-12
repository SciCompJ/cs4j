/**
 * 
 */
package net.sci.image.process.segment;

import net.sci.array.Array;
import net.sci.array.data.BinaryArray;
import net.sci.array.data.ScalarArray;
import net.sci.image.ImageArrayOperator;

/**
 * Base implementation for auto-threshold algorithms.
 * 
 * @author dlegland
 *
 */
public abstract class AutoThreshold implements ImageArrayOperator
{
	public abstract double computeThresholdValue(ScalarArray<?> array);
	
	public void process(ScalarArray<?> source, BinaryArray target)
    {
    	// compute threshold value
    	double threshold = computeThresholdValue(source);
    	
    	// create array iterators
    	ScalarArray.Iterator<?> iter1 = source.iterator(); 
    	BinaryArray.Iterator iter2 = target.iterator();
    	
    	// iterate on both arrays for computing segmented values
    	while(iter1.hasNext() && iter2.hasNext())
    	{
    		iter2.setNextBoolean(iter1.nextValue() >= threshold);
    	}
    }

	/**
	 * Computes the threshold value on a scalar array and returns the resulting
	 * binary array.
	 * 
	 * @param array
	 *            the scalar array to threshold
	 * @return the binary array resulting from thresholding
	 */
	public BinaryArray processScalar(ScalarArray<?> array)
	{
		BinaryArray result = createEmptyOutputArray(array);
		process(array, result);
		return result;
	}
	
	@Override
	public <T> BinaryArray process(Array<T> array)
	{
	    if (!(array instanceof ScalarArray))
	    {
	        throw new IllegalArgumentException("Requires a scalar array");
	    }
	    return processScalar((ScalarArray<?>) array);
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
}
