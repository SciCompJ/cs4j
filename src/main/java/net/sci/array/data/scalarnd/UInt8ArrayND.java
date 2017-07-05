/**
 * 
 */
package net.sci.array.data.scalarnd;

import net.sci.array.data.UInt8Array;
import net.sci.array.type.UInt8;

/**
 * @author dlegland
 *
 */
public abstract class UInt8ArrayND extends IntArrayND<UInt8> implements UInt8Array
{
	// =============================================================
	// Static factory
	
	public static UInt8ArrayND create(int... dims)
	{
		return new BufferedUInt8ArrayND(dims);
	}
	
	
	// =============================================================
	// Constructors
	
	/**
	 * @param sizes
	 */
	protected UInt8ArrayND(int[] sizes)
	{
		super(sizes);
	}

	
	// =============================================================
	// Specialization of Array interface
	
	@Override
	public UInt8Array newInstance(int... dims)
	{
		return UInt8Array.create(dims);
	}
	
}
