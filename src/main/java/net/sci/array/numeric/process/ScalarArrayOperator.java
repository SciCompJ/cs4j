/**
 * 
 */
package net.sci.array.numeric.process;

import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.numeric.Scalar;
import net.sci.array.numeric.ScalarArray;

/**
 * Interface for {@code ArrayOperator} that operate on an {@code Array}
 * containing {@code Scalar} elements.
 * 
 * @see net.sci.array.numeric.Scalar
 * 
 * @author dlegland
 */
public interface ScalarArrayOperator extends ArrayOperator
{
    // =============================================================
    // New methods
    
    /**
     * Processes the input scalar array and returns the result in a new
     * ScalarArray.
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
     * The input array must contain {@code Scalar} elements.
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
        if (array.elementInstanceOf(Scalar.class))
        {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            ScalarArray<?> scalarArray = ScalarArray.wrap((Array<? extends Scalar>) array);
            return processScalar(scalarArray);
        }
        
        throw new IllegalArgumentException("Requires an array of Scalar elements as input");
    }

	/**
	 * Override default behavior to check if input array contains scalar elements.
	 * 
	 * @return true if input array is scalar
	 */
	@Override
	public default boolean canProcess(Array<?> array)
	{
		return array.elementInstanceOf(Scalar.class);
	}
}
