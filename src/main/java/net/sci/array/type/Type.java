/**
 * 
 */
package net.sci.array.type;

import net.sci.array.Array;

/**
 * The superclass of all array types.
 * 
 * Necessary for checking type consistency of two arrays, or dynamically
 * creating arrays of a given type.
 * 
 * @author dlegland
 *
 */
public interface Type<T>
{
	public abstract Array<? extends T> createArray(int[] dims);
}
