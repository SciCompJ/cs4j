/**
 * 
 */
package net.sci.array.type;

import net.sci.array.data.FloatArray;

/**
 * @author dlegland
 *
 */
public class Float extends Scalar
{
	float value;
	
	/**
	 * Creates new Float with default value 0.
	 */
	public Float()
	{
		value = 0;
	}
	
	/**
	 * Creates new Float with specified value.
	 */
	public Float(float value)
	{
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see net.sci.array.type.Scalar#getValue()
	 */
	@Override
	public double getValue()
	{
		return value;
	}

	public FloatArray createArray(int[] dims)
	{
		return FloatArray.create(dims);
	}


	// =============================================================
	// Override Object methods
	
	public boolean equals(Object that)
	{
		// check for self-comparison
		if (this == that)
			return true;

		// check for class
		if (!(that instanceof Float))
			return false;

		// cast to native object is now safe
		Float thatFloat = (Float) that;

	    // now a proper field-by-field evaluation can be made
	    return this.value == thatFloat.value;
	}
	
	public int hashCode()
	{
		return java.lang.Float.hashCode(this.value);
	}
}
