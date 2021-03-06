/**
 * 
 */
package net.sci.array.scalar;

/**
 * Represents an unsigned 8-bits integer, coded with a byte.
 * 
 * @author dlegland
 *
 */
public class UInt8 extends Int
{
    // =============================================================
    // Constants
    
    /**
     * The minimum value that can be stored in a UInt8 instance, equal to 0.
     */
    public static final int MIN_VALUE = 0;
    
    /**
     * The maximum value that can be stored in a UInt8 instance, equal to 255.
     */
    public static final int MAX_VALUE = 255;
    
    // =============================================================
    // Static methods
    
	/**
	 * Computes the integer value between 0 and 255 closest to the specified
	 * value.
	 * 
	 * @param value
	 *            a double value
	 * @return the closest corresponding integer between 0 and 255
	 */
	public final static int clamp(double value)
	{
		return (int) Math.min(Math.max(0, value), 255);
	}
	
	
    // =============================================================
    // Class variables
    
	byte value;
	
	
    // =============================================================
    // Constructors
    
    /**
     * Creates a new instance of UInt8 using the specified value.
     * 
     * @param value
     *            the value stored within this UInt8
     */
    public UInt8(int value)
    {
        this.value = (byte) value;
    }
    
    /**
     * Creates a new instance of UInt8 using the specified byte value.
     * 
     * @param value
     *            the byte value stored within this UInt8
     */
    public UInt8(byte value)
    {
        this.value = value;
    }
    

    // =============================================================
    // Methods
    
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
	
    @Override
    public String toString()
    {
        return String.format("UInt8(%d)", this.value & 0x00FF);
    }    
}
