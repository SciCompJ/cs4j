/**
 * 
 */
package net.sci.array.process;

import net.sci.array.Array;
import net.sci.array.binary.BinaryArray;
import net.sci.array.scalar.ScalarArray;

/**
 * Interface for Array operators that operate on a Binary Array instance and
 * returns a ScalarArray instance.
 * 
 * @author dlegland
 */
public interface BinaryArrayOperator extends ScalarArrayOperator
{
    /**
     * Process the input binary array and return the result in a new ScalarArray.
     * 
     * @param array
     *            the input array
     * @return the operator result as a new Scalar Array
     */
	public ScalarArray<?> processBinary(BinaryArray array);

	/**
     * Overrides the default behavior of ScalarArrayOperator to check directly
     * of the array is binary, and call the processBinary method if appropriate.
     * 
     * @param array
     *            the input array
     * @return the operator result as a new Scalar Array
     */
	@Override
	public default ScalarArray<?> processScalar(ScalarArray<?> array)
	{
        if (!(array instanceof BinaryArray))
        {
            throw new IllegalArgumentException("Requires a binary array as input");
        }
        
        return processBinary((BinaryArray) array);
	}
	
    /**
     * Process the input scalar array and return the result in a new array.
     * 
     * The input array must be an instance of BinaryArray.
     * 
     * @param array
     *            the input array
     * @return the operator result as a new instance of ScalarArray
     * @throws IllegalArgumentException
     *             if the input array is not an instance of BinaryArray
     */
    @Override
    public default <T> ScalarArray<?> process(Array<T> array)
    {
        if (!(array instanceof BinaryArray))
        {
            throw new IllegalArgumentException("Requires a binary array as input");
        }
        
        return processBinary((BinaryArray) array);
    }

	/**
	 * Override default behavior to check if input array is binary.
	 * 
	 * @return true if input array is binary
	 */
	@Override
	public default boolean canProcess(Array<?> array)
	{
		return array instanceof BinaryArray;
	}
}
