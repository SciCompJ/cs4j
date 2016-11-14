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
	 * Processes the given array, and returns a new Array containing the result.
	 * 
	 * @param array
	 *            the input array
	 * @return the result of operator
	 */
	public <T> Array<?> process(Array<T> array);

	/**
	 * Checks if this operator can process the specified array.
	 * 
	 * Returns true by default.
	 * 
	 * @param array
	 *            the array to check
	 * @return true if this operator can process the specified array
	 */
	public default boolean canProcess(Array<?> array)
	{
		return true;
	}
	
}
