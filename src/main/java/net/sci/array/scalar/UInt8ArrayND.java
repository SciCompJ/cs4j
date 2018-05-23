/**
 * 
 */
package net.sci.array.scalar;

/**
 * @author dlegland
 *
 */
public abstract class UInt8ArrayND extends IntArrayND<UInt8> implements UInt8Array
{
	// =============================================================
	// Static factory
	
	/**
	 * Creates a new array of UInt8.
	 * 
	 * @param dims
	 *            the dimensions of the array
	 * @return a new instance of UInt8ArrayND
	 */
	public static UInt8ArrayND create(int... dims)
	{
		return new BufferedUInt8ArrayND(dims);
	}
	
	
	// =============================================================
	// Constructors
	
	/**
	 * Initialize a new array of UInt8.
	 * 
	 * @param sizes
	 *            the dimensions of the array
	 */
	protected UInt8ArrayND(int[] sizes)
	{
		super(sizes);
	}

	
	// =============================================================
	// Specialization of Array interface
	
	@Override
	public UInt8Array newInstance(int... dims)
	{
		return UInt8Array.create(dims);
	}
	
}
