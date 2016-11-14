/**
 * 
 */
package net.sci.array.type;

import static java.lang.Double.doubleToLongBits;

/**
 * @author dlegland
 *
 */
public class DoubleVector extends Vector<Double>
{
	double[] data;
	
	public DoubleVector(double[] array)
	{
		this.data = new double[array.length];
		System.arraycopy(array, 0, this.data, 0, array.length);
	}
	
	/**
	 * Returns a defensive copy of the inner array.
	 */
	@Override
	public double[] getValues()
	{
		double[] res = new double[this.data.length];
		System.arraycopy(this.data, 0, res, 0, this.data.length);
		return res;
	}

	/**
	 * Returns the value at the specified position.
	 */
	@Override
	public double getValue(int i)
	{
		return this.data[i];
	}

	@Override
	public int size()
	{
		return this.data.length;
	}
	

	// =============================================================
	// Override Object methods
	
	public boolean equals(Object that)
	{
		// check for self-comparison
		if (this == that)
			return true;

		// check for class
		if (!(that instanceof DoubleVector))
			return false;

		// cast to native object is now safe
		DoubleVector thatVector = (DoubleVector) that;

	    // now a proper field-by-field evaluation can be made
		if (this.data.length != thatVector.data.length)
			return false;
		for (int i = 0; i < this.data.length; i++)
		{
			if (doubleToLongBits(this.data[i]) != doubleToLongBits(thatVector.data[i]))
				return false;
		}
	    return true;
	}
	
	public int hashCode()
	{
		int code = 23;
		for (double d : this.data)
		{
			code = hash(code, doubleToLongBits(d));
		}
		return code;
	}
	
	/** longs. */
	private static int hash(int aSeed, long aLong)
	{
		return firstTerm(aSeed) + (int) (aLong ^ (aLong >>> 32));
	}

	// PRIVATE
	private static final int fODD_PRIME_NUMBER = 37;

	private static int firstTerm(int aSeed)
	{
		return fODD_PRIME_NUMBER * aSeed;
	}
}
