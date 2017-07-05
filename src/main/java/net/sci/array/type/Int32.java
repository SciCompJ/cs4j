/**
 * 
 */
package net.sci.array.type;


/**
 * Represents a signed 32-bits integer, simply coded with an int.
 * 
 * @author dlegland
 *
 */
public class Int32 extends Int
{
	int value;
	
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
}
