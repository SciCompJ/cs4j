/**
 * 
 */
package net.sci.array.type;

import net.sci.array.data.DoubleArray;

/**
 * @author dlegland
 *
 */
public class Double extends Scalar
{
	double value;
	
	/**
	 * Creates new double with default value 0.
	 */
	public Double()
	{
		value = 0;
	}
	
	/**
	 * Creates new double with specified value.
	 */
	public Double(double value)
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

	public DoubleArray createArray(int[] dims)
	{
		return DoubleArray.create(dims);
	}


	// =============================================================
	// Override Object methods
	
	public boolean equals(Object that)
	{
		// check for self-comparison
		if (this == that)
			return true;

		// check for class
		if (!(that instanceof Double))
			return false;

		// cast to native object is now safe
		Double thatDouble = (Double) that;

	    // now a proper field-by-field evaluation can be made
	    return this.value == thatDouble.value;
	}
	
	public int hashCode()
	{
		return java.lang.Double.hashCode(this.value);
	}
}
