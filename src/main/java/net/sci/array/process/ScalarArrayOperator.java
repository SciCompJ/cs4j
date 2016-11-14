/**
 * 
 */
package net.sci.array.process;

import net.sci.array.Array;
import net.sci.array.ArrayToArrayOperator;
import net.sci.array.data.ScalarArray;
import net.sci.array.type.Scalar;

/**
 * @author dlegland
 *
 */
public interface ScalarArrayOperator extends ArrayToArrayOperator
{
	public void processScalar(ScalarArray<? extends Scalar> input, ScalarArray<? extends Scalar> output);
	
	public default void process(Array<?> input, Array<?> output)
	{
		if (input instanceof ScalarArray && output instanceof ScalarArray)
		{
			processScalar((ScalarArray<?>) input, (ScalarArray<?>) output);
		}
		else
		{
			throw new IllegalArgumentException("Requires both arrays to be scalar");
		}
	}

	/**
	 * Override default behavior to check if input array is scalar.
	 * 
	 * @return true if input array is scalar
	 */
	@Override
	public default boolean canProcess(Array<?> array)
	{
		return array instanceof ScalarArray;
	}
	
	/**
	 * Override default behavior to check if source array is scalar.
	 * 
	 * @return true if source array is scalar
	 */
	@Override
	public default boolean canProcess(Array<?> source, Array<?> target)
	{
		return canProcess(source);
	}
}
