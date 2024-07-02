/**
 * 
 */
package net.sci.array.numeric;

import net.sci.array.numeric.impl.BufferedInt16ArrayND;

/**
 * @author dlegland
 *
 */
public abstract class Int16ArrayND extends IntArrayND<Int16> implements Int16Array
{
	// =============================================================
	// Static factory
	
	/**
	 * Creates a new array of Int16.
	 * 
	 * @param dims
	 *            the dimensions of the array
	 * @return a new instance of Int16ArrayND
	 */
	public static Int16ArrayND create(int... dims)
	{
		return new BufferedInt16ArrayND(dims);
	}
	
	
	// =============================================================
	// Constructors
	
	/**
	 * Initialize a new array of Int16.
	 * 
	 * @param sizes
	 *            the dimensions of the array
	 */
	protected Int16ArrayND(int[] sizes)
	{
		super(sizes);
	}

	
	// =============================================================
	// Specialization of Array interface
	
	@Override
	public Int16Array newInstance(int... dims)
	{
		return Int16Array.create(dims);
	}
	
}
