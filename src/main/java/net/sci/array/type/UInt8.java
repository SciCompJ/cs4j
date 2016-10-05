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


	// =============================================================
	// Override Object methods
	
	public boolean equals(Object that)
	{
		// check for self-comparison
		if (this == that)
			return true;

		// check for class
		if (!(that instanceof UInt8))
			return false;

		// cast to native object is now safe
		UInt8 thatInt = (UInt8) that;

	    // now a proper field-by-field evaluation can be made
	    return this.value == thatInt.value;
	}
	
	public int hashCode()
	{
		return java.lang.Byte.hashCode(this.value);
	}
}
