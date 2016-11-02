/**
 * 
 */
package net.sci.array.type;

import static java.lang.Float.floatToRawIntBits;

/**
 * @author dlegland
 *
 */
public class FloatVector extends Vector<Float>
{
	// =============================================================
	// Class variables
	
	float[] data;
	
	
	// =============================================================
	// Constructor
	
	public FloatVector(float[] array)
	{
		this.data = new float[array.length];
		System.arraycopy(array, 0, this.data, 0, array.length);
	}
	
	public FloatVector(double[] array)
	{
		this.data = new float[array.length];
		for (int c = 0; c < array.length; c++)
		{
			this.data[c] = (float) array[c];
		}
	}
	

	// =============================================================
	// New methods
	
	/**
	 * @return a defensive copy of the inner float data.
	 */
	public float[] getFloats()
	{
		float[] res = new float[this.data.length];
		System.arraycopy(this.data, 0, res, 0, this.data.length);
		return res;
	}
	
	/**
	 * Returns the specified component of the float vector. No bound checking is
	 * performed.
	 * 
	 * @param c
	 *            the index of the component
	 * @return the specified component of the vector.
	 */
	public float getFloat(int c)
	{
		return this.data[c];
	}

	// =============================================================
	// Implementation of Vector interface
	
	/**
	 * Returns a defensive copy of the inner array.
	 */
	@Override
	public double[] getValues()
	{
		double[] res = new double[this.data.length];
		for(int c = 0; c < this.data.length; c++)
		{
			res[c] = this.data[c];
		}
		return res;
	}

	/**
	 * Returns the value at the specified position.
	 */
	@Override
	public double getValue(int c)
	{
		return this.data[c];
	}


	// =============================================================
	// Override Object methods
	
	public boolean equals(Object that)
	{
		// check for self-comparison
		if (this == that)
			return true;

		// check for class
		if (!(that instanceof FloatVector))
			return false;

		// cast to native object is now safe
		FloatVector thatVector = (FloatVector) that;

	    // now a proper field-by-field evaluation can be made
		if (this.data.length != thatVector.data.length)
			return false;
		for (int i = 0; i < this.data.length; i++)
		{
			if (floatToRawIntBits(this.data[i]) != floatToRawIntBits(thatVector.data[i]))
				return false;
		}
	    return true;
	}
	
	public int hashCode()
	{
		int code = 23;
		for (float f : this.data)
		{
			code = hash(code, floatToRawIntBits(f));
		}
		return code;
	}
	
	/** longs. */
	private static int hash(int aSeed, int anInt)
	{
		return firstTerm(aSeed) + (int) (anInt ^ (anInt >>> 16));
	}

	// PRIVATE
	private static final int fODD_PRIME_NUMBER = 37;

	private static int firstTerm(int aSeed)
	{
		return fODD_PRIME_NUMBER * aSeed;
	}
}
