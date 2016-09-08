/**
 * 
 */
package net.sci.array.data.scalarnd;

import net.sci.array.data.ArrayND;
import net.sci.array.data.UInt8Array;
import net.sci.array.type.UInt8;

/**
 * @author dlegland
 *
 */
public abstract class UInt8ArrayND extends ArrayND<UInt8> implements UInt8Array
{
	public static UInt8ArrayND create(int[] dims)
	{
		return new BufferedUInt8ArrayND(dims);
	}
	
	/**
	 * @param sizes
	 */
	protected UInt8ArrayND(int[] sizes)
	{
		super(sizes);
	}
	
}
