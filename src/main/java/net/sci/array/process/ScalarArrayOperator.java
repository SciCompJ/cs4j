/**
 * 
 */
package net.sci.array.process;

import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.data.ScalarArray;
import net.sci.array.type.Scalar;

/**
 * @author dlegland
 *
 */
public interface ScalarArrayOperator extends ArrayOperator
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
}
