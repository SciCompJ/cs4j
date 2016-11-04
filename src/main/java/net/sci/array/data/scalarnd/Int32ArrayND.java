/**
 * 
 */
package net.sci.array.data.scalarnd;

import net.sci.array.data.ArrayND;
import net.sci.array.data.Int32Array;
import net.sci.array.type.Int32;

/**
 * @author dlegland
 *
 */
public abstract class Int32ArrayND extends ArrayND<Int32> implements Int32Array
{
	// =============================================================
	// Static factory
	
	public static Int32ArrayND create(int... dims)
	{
		return new BufferedInt32ArrayND(dims);
	}
	
	
	// =============================================================
	// Constructors
	
	/**
	 * @param sizes
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
