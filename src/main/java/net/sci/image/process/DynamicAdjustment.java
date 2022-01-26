/**
 * 
 */
package net.sci.image.process;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.process.Histogram;
import net.sci.array.scalar.ScalarArray;
import net.sci.array.scalar.UInt8Array;
import net.sci.image.ImageArrayOperator;

/**
 * Converts a scalar array into a UInt8 array by computing the best dynamic
 * adjustment from the input array.
 * 
 * @author dlegland
 *
 */
public class DynamicAdjustment extends AlgoStub implements ImageArrayOperator
{
    /**
     * The proportion of extreme values to drop out.
     */
	double alpha;
	
	/**
     * Creates a new DynamicAdjustment operator, by specifying the proportion of
     * array elements with extreme values to drop out.
     * 
     * @param alpha
     *            the proportion of input array elements that will be saturated,
     *            as a fraction between 0 and 1. Typical values are 0.01 or
     *            0.05).
     */
	public DynamicAdjustment(double alpha)
	{
		this.alpha = alpha;
	}
	
	/**
     * Apply dynamic adjustment to a scalar array.
     * 
     * @param array
     *            the input array to process.
     * @return a new UInt8 array with the same size as the input array.
     */
    public UInt8Array processScalar(ScalarArray<?> array)
    {
        double[] range = computeAdjustmentRange(array);
        
        double delta = range[1] - range[0];
        
        UInt8Array result = UInt8Array.create(array.size());
        
        result.fillInts(pos -> (int) (255 * (array.getValue(pos) - range[0]) / delta));
        
        return result;
    }
    
    /**
     * Computes the minimum and maximum values in the input array that will be
     * converted to 0 and 255, respectively.
     * 
     * @param array
     *            the array to convert into a UInt8 array
     * @return an array containing the values that will be mapped to 0 and 255
     *         in the result array.
     */
    public double[] computeAdjustmentRange(ScalarArray<?> array)
    {
        double[] valueRange = array.valueRange();
        
        int nBins = 200;
        int[] histo = Histogram.histogram(array, valueRange, nBins);
        double[] bins = Histogram.computeBinPositions(valueRange, nBins);
        
        int totalCount = 0;
        for (int count : histo)
        {
            totalCount += count;
        }
        
        // number of elements in lower and upper tails
        int tailRefCount = (int) Math.floor(totalCount * alpha / 2);

        int lowerTailCount = 0;
        int lowerTailIndex = 0;
        while (lowerTailCount < tailRefCount)
        {
            lowerTailCount += histo[lowerTailIndex];
            lowerTailIndex++;
        }
        
        int upperTailCount = 0;
        int upperTailIndex = nBins-1;
        while (upperTailCount < tailRefCount)
        {
            upperTailCount += histo[upperTailIndex];
            upperTailIndex--;
        }
        
        double lowerTailBound = bins[lowerTailIndex - 1];
        double upperTailBound = bins[upperTailIndex + 1];
        return new double[]{lowerTailBound, upperTailBound};
    }

    /**
     * Implements the process method from ArrayOperator interface. In practice,
     * the "processScalar" can be used directly.
     * 
     * @see #processScalar(ScalarArray)
     * @see net.sci.array.ArrayOperator#process(net.sci.array.Array)
     */
	@Override
	public <T> UInt8Array process(Array<T> array)
	{
		if (!(array instanceof ScalarArray))
		{
			throw new RuntimeException("Requires a scalar array");
		}
		
		return processScalar((ScalarArray<?>) array);
	}
}
