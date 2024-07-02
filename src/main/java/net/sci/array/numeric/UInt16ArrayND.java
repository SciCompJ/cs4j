/**
 * 
 */
package net.sci.array.numeric;

import net.sci.array.numeric.impl.BufferedUInt16ArrayND;

/**
 * @author dlegland
 *
 */
public abstract class UInt16ArrayND extends IntArrayND<UInt16> implements UInt16Array
{
	// =============================================================
	// Static factory
	
	/**
	 * Creates a new array of UInt16.
	 * 
	 * @param dims
	 *            the dimensions of the array
	 * @return a new instance of UInt16ArrayND
	 */
	public static UInt16ArrayND create(int... dims)
	{
		return new BufferedUInt16ArrayND(dims);
	}
	
	
	// =============================================================
	// Constructors
	
	/**
	 * Initializes a new array of UInt16.
	 * 
	 * @param sizes
	 *            the dimensions of the array
	 */
	protected UInt16ArrayND(int[] sizes)
	{
		super(sizes);
	}

	
	// =============================================================
	// Specialization of Array interface
	
	@Override
	public UInt16Array newInstance(int... dims)
	{
		return UInt16Array.create(dims);
	}
	
}
