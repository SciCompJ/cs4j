/**
 * 
 */
package net.sci.array.scalar;

/**
 * @author dlegland
 *
 */
public class Float64 extends Scalar
{
	double value;
	
	/**
	 * Creates new double with default value 0.
	 */
	public Float64()
	{
		value = 0;
	}
	
	/**
	 * Creates new double with specified value.
	 * 
	 * @param value
	 *            the value stored within this Float64
	 */
	public Float64(double value)
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

	public Float64Array createArray(int[] dims)
	{
		return Float64Array.create(dims);
	}


	// =============================================================
	// Override Object methods
	
	public boolean equals(Object that)
	{
		// check for self-comparison
		if (this == that)
			return true;

		// check for class
		if (!(that instanceof Float64))
			return false;

		// cast to native object is now safe
		Float64 thatDouble = (Float64) that;

	    // now a proper field-by-field evaluation can be made
	    return this.value == thatDouble.value;
	}
	
	public int hashCode()
	{
		return java.lang.Double.hashCode(this.value);
	}

	@Override
    public String toString()
    {
        return String.format("Float64(%f)", this.value);
    }    
}
