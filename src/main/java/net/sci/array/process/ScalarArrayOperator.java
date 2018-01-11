/**
 * 
 */
package net.sci.array.process;

import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.data.ScalarArray;
import net.sci.array.type.Scalar;

/**
 * Interface for Array operators that operate on a Scalar Array instance and
 * returns a ScalarArray instance.
 * 
 * @author dlegland
 */
public interface ScalarArrayOperator extends ArrayOperator
{
	public ScalarArray<?> processScalar(ScalarArray<? extends Scalar> input);

    /**
     * Process the input scalar array and return the result in a new array.
     * 
     * @param array
     *            the input array
     * @return the operator result as a new Scalar Array
     */
    @Override
    public default <T> ScalarArray<? extends Scalar> process(Array<T> array)
    {
        if (!(array instanceof ScalarArray))
        {
            throw new IllegalArgumentException("Requires a scalar array as input");
        }
        
        return processScalar((ScalarArray<?>) array);
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
}
