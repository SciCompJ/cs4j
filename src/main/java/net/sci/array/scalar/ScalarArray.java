/**
 * 
 */
package net.sci.array.scalar;

import java.util.function.UnaryOperator;

import net.sci.array.Array;

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

	/**
     * Applies the given function to each element of the array, and return a new
     * Array with the same class.
     * 
     * @param fun
     *            the function to apply
     * @return the result array
     */
    public default ScalarArray<T> apply(UnaryOperator<Double> fun)
    {
        ScalarArray<T> res = duplicate();
        ScalarArray.Iterator<T> iter1 = iterator();
        ScalarArray.Iterator<T> iter2 = res.iterator();
        while(iter1.hasNext())
        {
            iter2.setNextValue(fun.apply(iter1.nextValue()));
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
	
    @Override
    public ScalarArray.Factory<T> getFactory();

	public ScalarArray.Iterator<T> iterator();
	
	
    // =============================================================
    // Specialization of the Factory interface

	public interface Factory<T extends Scalar> extends Array.Factory<T>
	{
        /**
         * Creates a new scalar array of the specified dimensions, initialized
         * with zeros.
         * 
         * @param dims
         *            the dimensions of the new array
         * @return a new scalar array initialized with zeros
         */
	    public ScalarArray<T> create(int[] dims);

        /**
         * Creates a new scalar array with the specified dimensions, filled with
         * the specified initial value.
         * 
         * @param dims
         *            the dimensions of the array to be created
         * @param value
         *            an instance of the initial value
         * @return a new instance of ScalarArray
         */
        public ScalarArray<T> create(int[] dims, T value);
	}
	
	
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