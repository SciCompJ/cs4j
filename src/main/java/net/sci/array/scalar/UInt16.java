/**
 * 
 */
package net.sci.array.scalar;

/**
 * Represents an unsigned 16-bits integer, coded with a short.
 * 
 * @author dlegland
 *
 */
public class UInt16 extends Int
{
    // =============================================================
    // Constants
    
    /**
     * The minimum value that can be stored in a UInt16 instance, corresponding to 0.
     */
    public final static int MIN_VALUE = 0;

	/**
	 * The maximum value that can be stored in a UInt16 instance, corresponding to 2^16-1.
	 */
	public final static int MAX_VALUE = 0x0FFFF;

	
    // =============================================================
    // Static methods
    
	/**
	 * Computes the integer value between 0 and MAX_VALUE closest to the specified
	 * double value.
	 * 
	 * @param value
	 *            a double value
	 * @return the closest corresponding integer between 0 and MAX_VALUE
	 */
	public final static int clamp(double value)
	{
		return (int) Math.min(Math.max(0, value), MAX_VALUE);
	}


	// =============================================================
    // Class members
    
	short value;
	

	// =============================================================
    // Constructor
    
	/**
	 * Creates a new instance of UInt16 using the specified value.
	 * 
	 * @param value
	 *            the value stored within this UInt16
	 */
	public UInt16(int value)
	{
		this.value =  (short) value;
	}
	

	// =============================================================
    // Class methods
    
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
    
    @Override
    public String toString()
    {
        return String.format("UInt16(%d)", this.value & 0x00FFFF);
    }    
}
