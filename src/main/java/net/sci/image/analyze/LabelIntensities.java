/**
 * 
 */
package net.sci.image.analyze;

import java.util.HashMap;

import net.sci.array.Arrays;
import net.sci.array.data.IntArray;
import net.sci.array.data.ScalarArray;
import net.sci.array.type.Int;
import net.sci.array.type.Scalar;
import net.sci.image.morphology.LabelImages;

/**
 * Collection of static methods for computing descriptive statistics on
 * multidimensional arrays.
 * 
 * @author dlegland
 *
 */
public class LabelIntensities
{

	/**
	 * private constructor to prevent instantiation.
	 */
	private LabelIntensities()
	{
	}

	/**
     * Computes the average value within a scalar array.
     * 
     * @param array
     *            the array to analyze
     * @param labelArray
     *            the array containing label values
     * @param labels
     *            the list of labels to process
     * @return the average value within the array
     */
	public static final double[] mean(ScalarArray<?> array, IntArray<?> labelArray, int[] labels)
	{
		// check input consistency
		if (!Arrays.isSameSize(array, labelArray))
		{
			throw new IllegalArgumentException("Both arrays should have same size");
		}
		
		// allocate memory for arrays
		int nLabels = labels.length;
		int[] count = new int[nLabels];
		double[] sum = new double[nLabels];
		
		// extract indices of labels
		HashMap<Integer, Integer> labelInds = LabelImages.mapLabelIndices(labels);
		
		// get iterators
		ScalarArray.Iterator<? extends Scalar> scalarIter = array.iterator();
		IntArray.Iterator<? extends Int> labelIter = labelArray.iterator();
		
		// iterate over elements
		while(scalarIter.hasNext())
		{
			int label = labelIter.nextInt();
			double value = scalarIter.nextValue();
			
			if (label == 0)
			{
				continue;
			}
			
			int index = labelInds.get(label);
			count[index]++;
			sum[index] += value;
		}
		
		// convert sum and count to mean
		for (int i = 0; i < nLabels; i++)
		{
			sum[i] /= count[i];
		}
		return sum;
	}

	/**
	 * Computes the sum of values within a scalar array.
	 * 
	 * @param array
	 *            the array to analyze
	 * @return the sum of values within the array
	 */
	public static final double[] sum(ScalarArray<?> array, IntArray<?> labelArray, int[] labels)
	{
		// check input consistency
		if (!Arrays.isSameSize(array, labelArray))
		{
			throw new IllegalArgumentException("Both arrays should have same size");
		}
		
		// allocate memory for arrays
		int nLabels = labels.length;
		double[] sum = new double[nLabels];
		
		// extract indices of labels
		HashMap<Integer, Integer> labelInds = LabelImages.mapLabelIndices(labels);
		
		// get iterators
		ScalarArray.Iterator<? extends Scalar> scalarIter = array.iterator();
		IntArray.Iterator<? extends Int> labelIter = labelArray.iterator();
		
		// iterate over elements
		while(scalarIter.hasNext())
		{
			int label = labelIter.nextInt();
			double value = scalarIter.nextValue();
			
			if (label == 0)
			{
				continue;
			}
			
			int index = labelInds.get(label);
			sum[index] += value;
		}
		
		// return the sum array
		return sum;
	}
}
