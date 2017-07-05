/**
 * 
 */
package net.sci.array.data.scalarnd;

import net.sci.array.data.Int32Array;
import net.sci.array.type.Int32;

/**
 * @author dlegland
 *
 */
public abstract class Int32ArrayND extends IntArrayND<Int32> implements Int32Array
{
	// =============================================================
	// Static factory
	
	/**
	 * Creates a new array of UInt32.
	 * 
	 * @param dims
	 *            the dimensions of the array
	 * @return a new instance of Int32ArrayND
	 */
	public static Int32ArrayND create(int... dims)
	{
		return new BufferedInt32ArrayND(dims);
	}
	
	
	// =============================================================
	// Constructors
	
	/**
	 * Initialize a new array of integers.
	 * 
	 * @param sizes
	 *            the dimensions of the array
	 */
	protected Int32ArrayND(int[] sizes)
	{
		super(sizes);
	}

	
	// =============================================================
	// Specialization of Array interface
	
	@Override
	public Int32Array newInstance(int... dims)
	{
		return Int32Array.create(dims);
	}
	
}
