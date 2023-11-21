/**
 * 
 */
package net.sci.array.process;

import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.scalar.ScalarArray;

/**
 * Interface for Array operators that operate on a ScalarArray instance and
 * returns a ScalarArray instance.
 * 
 * @author dlegland
 */
public interface ScalarArrayOperator extends ArrayOperator
{
    // =============================================================
    // New methods
    
    /**
     * Process the input scalar array and return the result in a new ScalarArray.
     * 
     * @param array
     *            the input array
     * @return the operator result as a new Scalar Array
     */
	public ScalarArray<?> processScalar(ScalarArray<?> array);

	
    // =============================================================
    // Specialize methods from ArrayOperator interface
	
    /**
     * Process the input scalar array and return the result in a new array.
     * 
     * The input array must be an instance of ScalarArray.
     * 
     * @param array
     *            the input array
     * @return the operator result 
     * @throws IllegalArgumentException
     *             if the input array is not an instance of ScalarArray
     */
    @Override
    public default <T> Array<?> process(Array<T> array)
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
