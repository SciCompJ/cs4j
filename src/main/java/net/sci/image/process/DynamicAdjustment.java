/**
 * 
 */
package net.sci.image.process;

import net.sci.array.Array;
import net.sci.array.data.ScalarArray;
import net.sci.array.data.UInt8Array;
import net.sci.array.process.Histogram;
import net.sci.array.type.Scalar;
import net.sci.image.ImageArrayOperator;

/**
 * @author dlegland
 *
 */
public class DynamicAdjustment implements ImageArrayOperator
{
	double alpha;
	
	public DynamicAdjustment(double alpha)
	{
		this.alpha = alpha;
	}
	
	/* (non-Javadoc)
	 * @see net.sci.array.ArrayOperator#process(net.sci.array.Array)
	 */
	@Override
	public <T> Array<?> process(Array<T> array)
	{
		if (!(array instanceof ScalarArray))
		{
			throw new RuntimeException("Requires a scalar array");
		}
		
		ScalarArray<?> scalarArray = (ScalarArray<?>) array;
		double[] range = computeAdjustmentRange(scalarArray);
		
		double delta = range[1] - range[0];
		
		UInt8Array result = UInt8Array.create(array.getSize());
		
		// get iterator
		ScalarArray.Iterator<? extends Scalar> sourceIter = scalarArray.iterator();
		UInt8Array.Iterator resultIter = result.iterator();
		
		// iterate in parallel over both arrays
		while (sourceIter.hasNext() && resultIter.hasNext())
		{
			double value = sourceIter.nextValue();
			int intValue = (int) (255 * (value - range[0]) / delta);
			resultIter.forward();
			resultIter.setInt(intValue);
		}
		
		return result;
	}
	
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
//		int upperTailRefCount = (int) Math.floor(totalCount * (1 - alpha / 2));

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
}
