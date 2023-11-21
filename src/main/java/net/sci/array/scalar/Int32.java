/**
 * 
 */
package net.sci.array.scalar;


/**
 * Represents a signed 32-bits integer, simply coded with an int.
 * 
 * @author dlegland
 *
 */
public class Int32 extends Int<Int32>
{
    // =============================================================
    // Static methods
    
    /**
     * Computes the integer value between MIN_VALUE and MAX_VALUE closest to the
     * specified double value.
     * 
     * @param value
     *            a double value
     * @return the closest corresponding integer between MIN_VALUE and MAX_VALUE
     */
    public final static int convert(double value)
    {
        return (int) (value + 0.5);
    }

    // =============================================================
    // Class members
    
	int value;
	
    
	// =============================================================
    // Constructor
    
	/**
	 * Creates a new instance of Int32 using the specified value.
	 * 
	 * @param value
	 *            the value stored within this Int32
	 */
	public Int32(int value)
	{
		this.value =  value;
	}
	
	
    // =============================================================
    // Class methods
    
	@Override
	public int getInt()
	{
		return value;
	}
	
	@Override
	public double getValue()
	{
		return value;
	}

    @Override
    public Int32 fromValue(double v)
    {
        return new Int32((int) (v + 0.5));
    }


	// =============================================================
	// Override Object methods
	
	public boolean equals(Object that)
	{
		// check for self-comparison
		if (this == that)
			return true;

		// check for class
		if (!(that instanceof Int32))
			return false;

		// cast to native object is now safe
		Int32 thatInt = (Int32) that;

	    // now a proper field-by-field evaluation can be made
	    return this.value == thatInt.value;
	}
	
	public int hashCode()
	{
		return java.lang.Integer.hashCode(this.value);
	}

	@Override
    public String toString()
    {
        return String.format("Int32(%d)", this.value);
    }    
}
