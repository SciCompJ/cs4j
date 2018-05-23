/**
 * 
 */
package net.sci.array.scalar;

/**
 * @author dlegland
 *
 */
public abstract class Float32Array2D extends ScalarArray2D<Float32> implements Float32Array
{
	// =============================================================
	// Static methods

	public static final Float32Array2D create(int size0, int size1)
	{
		return new BufferedFloat32Array2D(size0, size1);
	}
	
	
	// =============================================================
	// Constructor

	/**
	 * Initialize the protected size variables. 
	 *
	 * @param size0
	 *            the size of the array along the first dimension
	 * @param size1
	 *            the size of the array along the second dimension
	 */
	protected Float32Array2D(int size0, int size1)
	{
		super(size0, size1);
	}

	// =============================================================
	// Specialization of Array2D 

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#get(int, int)
	 */
	@Override
	public Float32 get(int x, int y)
	{
		return new Float32((float) getValue(x, y));
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#set(int, int, java.lang.Object)
	 */
	@Override
	public void set(int x, int y, Float32 value)
	{
		setValue(x, y, value.getValue());
	}
	
	// =============================================================
	// Specialization of Array2D 

	@Override
	public Float32Array newInstance(int... dims)
	{
		return Float32Array.create(dims);
	}

	@Override
	public abstract Float32Array2D duplicate();

}
