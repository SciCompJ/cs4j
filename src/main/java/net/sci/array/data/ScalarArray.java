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
	// New methods

	/**
	 * Returns the range of values within this scalar array.
	 * 
	 * Does not take into account eventual NaN values.
	 * 
	 * @return an array with two elements, containing the lowest and the largest
	 *         finite values within this Array instance
	 */
	public default double[] getValueRange()
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
	 * Fills the array with the specified double value.
	 * 
	 * @param value the value to fill the array with
	 */
	public default void fill(double value)
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
		public default double nextValue()
		{
			return next().getValue();
		}
	}

}
