/**
 * 
 */
package net.sci.array;


/**
 * Specialization of the ArrayOperator interface that adds the possibility to
 * specify the array receiving the result of the process.
 * 
 * @author dlegland
 *
 */
@Deprecated
// TODO: remove
public interface ArrayToArrayOperator extends ArrayOperator
{
	/**
	 * Processes the given source array, and put the result in the given target
	 * array.
	 * 
	 * @param source
	 *            the input array
	 * @param target
	 *            the output array
	 */
	public void process(Array<?> source, Array<?> target);
	
	/**
	 * Creates a new array that can be used as output for processing the given
	 * input array.
	 * 
	 * @param array
	 *            the reference array
	 * @return a new instance of Array that can be used for processing input array.
	 */
	public default Array<?> createEmptyOutputArray(Array<?> array)
	{
		int[] dims = array.getSize();
		return array.newInstance(dims);
	}
	
	/**
	 * Checks if this operator can process the specified source array and put
	 * the result in the specified target array.
	 * 
	 * Returns true by default.
	 * 
	 * @param source
	 *            the source array
	 * @param target
	 *            the target array
	 * @return true if this operator can process the source and store result in the target
	 */
	public default boolean canProcess(Array<?> source, Array<?> target)
	{
		return true;
	}
	
	/**
	 * Provides a default implementation for processing the input array, consisting in 
	 * <ol>
	 * <li>creating a new array for storing the result</li>
	 * <li>call the process(Array,Array) method</li>
	 * <li>restun the instance of the output array</li>
	 * </ol>
	 * 
	 * @param array
	 *            the input array
	 * @return the result of operator
	 */
	public default <T> Array<?> process(Array<T> array)
	{
		Array<?> result = createEmptyOutputArray(array);
		process(array, result);
		return result;
	}

}
