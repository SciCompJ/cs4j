/**
 * 
 */
package net.sci.array.data;

import net.sci.array.Array;
import net.sci.array.type.Scalar;

/**
 * @author dlegland
 *
 */
public interface ScalarArray<T extends Scalar> extends Array<T>
{
	// =============================================================
	// Default methods for arithmetic on arrays
	

	public default ScalarArray<T> plus(double v)
	{
		ScalarArray<T> res = duplicate();
		ScalarArray.Iterator<T> iter1 = iterator();
		ScalarArray.Iterator<T> iter2 = res.iterator();
		while(iter1.hasNext())
		{
			iter2.setNextValue(iter1.nextValue() + v);
		}
		return res;
	}

	public default ScalarArray<T> minus(double v)
	{
		ScalarArray<T> res = duplicate();
		ScalarArray.Iterator<T> iter1 = iterator();
		ScalarArray.Iterator<T> iter2 = res.iterator();
		while(iter1.hasNext())
		{
			iter2.setNextValue(iter1.nextValue() - v);
		}
		return res;
	}

	public default ScalarArray<T> times(double k)
	{
		ScalarArray<T> res = duplicate();
		ScalarArray.Iterator<T> iter1 = iterator();
		ScalarArray.Iterator<T> iter2 = res.iterator();
		while(iter1.hasNext())
		{
			iter2.setNextValue(iter1.nextValue() * k);
		}
		return res;
	}

	public default ScalarArray<T> divideBy(double k)
	{
		ScalarArray<T> res = duplicate();
		ScalarArray.Iterator<T> iter1 = iterator();
		ScalarArray.Iterator<T> iter2 = res.iterator();
		while(iter1.hasNext())
		{
			iter2.setNextValue(iter1.nextValue() / k);
		}
		return res;
	}

	
	// =============================================================
	// New default methods 

	/**
     * Returns the range of values within this scalar array.
     * 
     * Does not take into account eventual NaN values, so the result never
     * contains NaN values. 
     * 
     * @return an array with two elements, containing the lowest and the largest
     *         finite values within this Array instance
     * @see #finiteValueRange()
     */
	public default double[] valueRange()
	{
		double vMin = Double.POSITIVE_INFINITY;
		double vMax = Double.NEGATIVE_INFINITY;
		for (Scalar scalar : this)
		{
			double value = scalar.getValue();
			if (!Double.isNaN(value))
			{
				vMin = Math.min(vMin, value);
				vMax = Math.max(vMax, value);
			}
		}
		return new double[]{vMin, vMax};
	}

    /**
     * Returns the range of finite values within this scalar array.
     * 
     * Does not take into account eventual NaN or infinite values, so the result
     * array always contains finite values (except if all values within array
     * are infinite).
     * 
     * @return an array with two elements, containing the lowest and the largest
     *         finite values within this Array instance
     * @see #valueRange()
     */
    public default double[] finiteValueRange()
    {
        double vMin = Double.POSITIVE_INFINITY;
        double vMax = Double.NEGATIVE_INFINITY;
        for (Scalar scalar : this)
        {
            double value = scalar.getValue();
            if (Double.isFinite(value))
            {
                vMin = Math.min(vMin, value);
                vMax = Math.max(vMax, value);
            }
        }
        return new double[]{vMin, vMax};
    }

	/**
	 * Returns the minimum value within this scalar array.
	 * 
	 * Does not take into account eventual NaN values.
	 * 
	 * @return the minimal value within this array
	 */
	public default double minValue()
	{
		double vMin = Double.POSITIVE_INFINITY;
		for (Scalar scalar : this)
		{
			double value = scalar.getValue();
			if (!Double.isNaN(value))
			{
				vMin = Math.min(vMin, value);
			}
		}
		return vMin;
	}

	/**
	 * Returns the maximum value within this scalar array.
	 * 
	 * Does not take into account eventual NaN values.
	 * 
	 * @return the maximal value within this array
	 */
	public default double maxValue()
	{
		double vMax = Double.NEGATIVE_INFINITY;
		for (Scalar scalar : this)
		{
			double value = scalar.getValue();
			if (!Double.isNaN(value))
			{
				vMax = Math.max(vMax, value);
			}
		}
		return vMax;
	}


	/**
	 * Fills the array with the specified double value.
	 * 
	 * @param value the value to fill the array with
	 */
	public default void fillValue(double value)
	{
		Iterator<? extends Scalar> iter = iterator();
		while(iter.hasNext())
		{
			iter.forward();
			iter.setValue(value);
		}
	}
	

	// =============================================================
	// Specialization of the Array interface

	@Override
	public ScalarArray<T> newInstance(int... dims);

	@Override
	public ScalarArray<T> duplicate();
	
	public ScalarArray.Iterator<T> iterator();
	

	// =============================================================
	// Inner interface

	public interface Iterator<T extends Scalar> extends Array.Iterator<T>
	{
		/**
		 * Moves this iterator to the next element and updates the value with
		 * the specified scalar value (optional operation).
		 * 
		 * @param value
		 *            the new value at the next position
		 */
		public default void setNextValue(double value)
		{
			forward();
			setValue(value);
		}
		
		public default double nextValue()
		{
			return next().getValue();
		}
	}
}
