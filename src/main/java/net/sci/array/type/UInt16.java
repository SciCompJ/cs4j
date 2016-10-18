/**
 * 
 */
package net.sci.array.type;

/**
 * Represents an unsigned 16-bits integer, coded with a short.
 * 
 * @author dlegland
 *
 */
public class UInt16 extends Int
{
	/**
	 * The maximum value that can be stored in a UInt16 instance, corresponding to 2^16-1.
	 */
	public final static int MAX_VALUE = 0x0FFFF;

	short value;
	
	/**
	 * 
	 */
	public UInt16(int value)
	{
		this.value =  (short) value;
	}
	
	public short getShort()
	{
		return value;
	}

	@Override
	public int getInt()
	{
		return value & 0x00FFFF;
	}
	
	@Override
	public double getValue()
	{
		return value & 0x00FFFF;
	}

//	public UInt16Array createArray(int[] dims)
//	{
//		return UInt16Array.create(dims);
//	}


	// =============================================================
	// Override Object methods
	
	public boolean equals(Object that)
	{
		// check for self-comparison
		if (this == that)
			return true;

		// check for class
		if (!(that instanceof UInt16))
			return false;

		// cast to native object is now safe
		UInt16 thatInt = (UInt16) that;

	    // now a proper field-by-field evaluation can be made
	    return this.value == thatInt.value;
	}
	
	public int hashCode()
	{
		return java.lang.Short.hashCode(this.value);
	}
}
