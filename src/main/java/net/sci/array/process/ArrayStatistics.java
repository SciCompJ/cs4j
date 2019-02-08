/**
 * 
 */
package net.sci.array.process;

import net.sci.array.scalar.ScalarArray;
import net.sci.array.scalar.Scalar;

/**
 * Collection of static methods for computing descriptive statistics on
 * multidimensional arrays.
 * 
 * @author dlegland
 *
 */
public class ArrayStatistics
{
    /**
     * Computes the maximum value within a scalar array.
     * 
     * @param array
     *            the array to analyze
     * @return the maximum value within the array
     */
    public static final double max(ScalarArray<?> array)
    {
        double max = Double.NEGATIVE_INFINITY;
        
        ScalarArray.Iterator<? extends Scalar> iter = array.iterator();
        while(iter.hasNext())
        {
            max = java.lang.Math.max(max, iter.nextValue());
        }
        
        return max;
    }

    /**
     * Computes the minimum value within a scalar array.
     * 
     * @param array
     *            the array to analyze
     * @return the minimum value within the array
     */
    public static final double min(ScalarArray<?> array)
    {
        double min = Double.POSITIVE_INFINITY;
        
        ScalarArray.Iterator<? extends Scalar> iter = array.iterator();
        while(iter.hasNext())
        {
            min = java.lang.Math.min(min, iter.nextValue());
        }
        
        return min;
    }


	/**
	 * Computes the average value within a scalar array.
	 * 
	 * @param array
	 *            the array to analyze
	 * @return the average value within the array
	 */
	public static final double mean(ScalarArray<?> array)
	{
		int count = 0;
		double sum = 0.0;
		
		ScalarArray.Iterator<? extends Scalar> iter = array.iterator();
		while(iter.hasNext())
		{
			count++;
			sum += iter.nextValue();
		}
		
		return sum / count;
	}

    /**
     * Computes the standard deviation of values within a scalar array.
     * 
     * Standard deviation is computed as the square root of the variance.
     * 
     * @see #var(ScalarArray)
     * 
     * @param array
     *            the array to analyze
     * @return the standard deviation of values within the array
     */
    public static final double std(ScalarArray<?> array)
    {
        return java.lang.Math.sqrt(std(array));
    }

	/**
	 * Computes the sum of values within a scalar array.
	 * 
	 * @param array
	 *            the array to analyze
	 * @return the sum of values within the array
	 */
	public static final double sum(ScalarArray<?> array)
	{
		double sum = 0.0;
		
		ScalarArray.Iterator<? extends Scalar> iter = array.iterator();
		while(iter.hasNext())
		{
			sum += iter.nextValue();
		}
		
		return sum;
	}


    /**
     * Computes the variance of values within a scalar array.
     * 
     * @see #mean(ScalarArray)
     * @see #std(ScalarArray)
     * 
     * @param array
     *            the array to analyze
     * @return the variance of values within the array
     */
    public static final double var(ScalarArray<?> array)
    {
        double mean = mean(array);
        double sum = 0.0;
        
        ScalarArray.Iterator<? extends Scalar> iter = array.iterator();
        while(iter.hasNext())
        {
            double v = iter.nextValue() - mean;
            sum += v * v;
        }
        
        return sum / (array.elementNumber() - 1.0);
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private ArrayStatistics()
    {
    }
}
