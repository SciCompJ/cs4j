/**
 * 
 */
package net.sci.array;

/**
 * Generates array of any dimensions.
 * 
 * @author dlegland
 *
 */
public interface ArrayFactory<T>
{
	/**
	 * Creates a new array with the specified dimensions, filled with the
	 * specified initial value.
	 * 
	 * @param dims
	 *            the dimensions of the array to be created
	 * @param value
	 *            an instance of the initial value
	 * @return a new instance of Array
	 */
	public Array<T> create(int[] dims, T value);
}
