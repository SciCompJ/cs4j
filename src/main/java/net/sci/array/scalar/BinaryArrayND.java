/**
 * 
 */
package net.sci.array.scalar;

/**
 * @author dlegland
 *
 */
public abstract class BinaryArrayND extends IntArrayND<Binary> implements BinaryArray
{
	// =============================================================
	// Static factory
	
	/**
	 * Creates a new array of Binary.
	 * 
	 * @param dims
	 *            the dimensions of the array
	 * @return a new instance of BinaryArrayND
	 */
	public static BinaryArrayND create(int... dims)
	{
		return new BufferedBinaryArrayND(dims);
	}
	
	
	// =============================================================
	// Constructors
	
	/**
	 * Initialize a new array of Binary.
	 * 
	 * @param sizes
	 *            the dimensions of the array
	 */
	protected BinaryArrayND(int[] sizes)
	{
		super(sizes);
	}



	// =============================================================
	// Specialization of Array interface
	
	@Override
	public BinaryArray newInstance(int... dims)
	{
		return BinaryArray.create(dims);
	}
	
}
