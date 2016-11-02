/**
 * 
 */
package net.sci.array;


/**
 * Apply a process on an array.
 * 
 * Either returns a new Array, or uses a method to put fill result array.
 * 
 * @author dlegland
 *
 */
public interface ArrayOperator
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
	 * @return a new instance of Array<?> that can be used for processing input
	 *         array.
	 */
	public default Array<?> createEmptyOutputArray(Array<?> array)
	{
		int[] dims = array.getSize();
		return array.newInstance(dims);
	}
	
	/**
	 * Processes the given array, and returns a new Array containing the result.
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
