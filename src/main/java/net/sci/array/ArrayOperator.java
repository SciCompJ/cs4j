/**
 * 
 */
package net.sci.array;

import net.sci.algo.Algo;

/**
 * General interface for algorithms that take an array as input and return an
 * array.
 * 
 * The main method to implement is the <code>process(Array)</code> method. The
 * <code>canProcess(Array)</code> can also be re-implemented to check at runtime
 * if the algorithm is suited for a given array (correct dimensions, type...).
 * 
 * @author dlegland
 *
 */
public interface ArrayOperator extends Algo
{
	/**
     * Processes the given array, and returns a new Array containing the result.
     * 
     * @param <T>
     *            the type of the input array
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
