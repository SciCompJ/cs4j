/**
 * 
 */
package net.sci.array.data.scalarnd;

import net.sci.array.data.Float64Array;
import net.sci.array.type.Float64;

/**
 * @author dlegland
 *
 */
public abstract class Float64ArrayND extends ScalarArrayND<Float64> implements Float64Array
{
	// =============================================================
	// Static factory
	
	/**
	 * Creates a new array of Float64.
	 * 
	 * @param dims
	 *            the dimensions of the array
	 * @return a new instance of Float64ArrayND
	 */
	public static Float64ArrayND create(int... dims)
	{
		return new BufferedFloat64ArrayND(dims);
	}
	
	
	// =============================================================
	// Constructors
	
	/**
	 * Initialize a new array of floats.
	 * 
	 * @param sizes
	 *            the dimensions of the array
	 */
	protected Float64ArrayND(int[] sizes)
	{
		super(sizes);
	}

	
	// =============================================================
	// Specialization of Array interface
	
	@Override
	public Float64Array newInstance(int... dims)
	{
		return Float64Array.create(dims);
	}
	
}
