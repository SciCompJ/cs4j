/**
 * 
 */
package net.sci.array.type;

import net.sci.array.data.UInt8Array;

/**
 * @author dlegland
 *
 */
public class UInt8 extends Int
{
	byte value;
	
	/**
	 * 
	 */
	public UInt8(int value)
	{
		this.value =  (byte) value;
	}
	
	public byte getByte()
	{
		return value;
	}

	@Override
	public int getInt()
	{
		return value & 0x00FF;
	}
	
	@Override
	public double getValue()
	{
		return value & 0x00FF;
	}

	public UInt8Array createArray(int[] dims)
	{
		return UInt8Array.create(dims);
	}

}
