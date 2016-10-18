/**
 * 
 */
package net.sci.array.type;


/**
 * Represents a signed 16-bits integer, coded with a short.
 * 
 * @author dlegland
 *
 */
public class Int16 extends Int
{
	short value;
	
	/**
	 * 
	 */
	public Int16(short value)
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
		if (!(that instanceof Int16))
			return false;

		// cast to native object is now safe
		Int16 thatInt = (Int16) that;

	    // now a proper field-by-field evaluation can be made
	    return this.value == thatInt.value;
	}
	
	public int hashCode()
	{
		return java.lang.Short.hashCode(this.value);
	}
}
