/**
 * 
 */
package net.sci.array.scalar;

/**
 * @author dlegland
 *
 */
public abstract class Float32ArrayND extends ScalarArrayND<Float32> implements Float32Array
{
	// =============================================================
	// Static factory
	
	/**
	 * Creates a new array of Float32.
	 * 
	 * @param dims
	 *            the dimensions of the array
	 * @return a new instance of FloatArrayND
	 */
	public static Float32ArrayND create(int... dims)
	{
		return new BufferedFloat32ArrayND(dims);
	}
	
	
	// =============================================================
	// Constructors
	
	/**
	 * Initialize a new array of floats.
	 * 
	 * @param sizes
	 *            the dimensions of the array
	 */
	protected Float32ArrayND(int[] sizes)
	{
		super(sizes);
	}

	
	// =============================================================
	// Specialization of Array interface
	
	@Override
	public Float32Array newInstance(int... dims)
	{
		return Float32Array.create(dims);
	}
	
}
