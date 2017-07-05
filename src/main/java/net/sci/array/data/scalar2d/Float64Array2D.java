/**
 * 
 */
package net.sci.array.data.scalar2d;

import net.sci.array.data.Float64Array;
import net.sci.array.type.Float64;

/**
 * @author dlegland
 *
 */
public abstract class Float64Array2D extends ScalarArray2D<Float64> implements Float64Array
{
	// =============================================================
	// Static methods

	public static final Float64Array2D create(int size0, int size1)
	{
		return new BufferedFloat64Array2D(size0, size1);
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
	protected Float64Array2D(int size0, int size1)
	{
		super(size0, size1);
	}


	// =============================================================
	// Specialization of Array2D 

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#get(int, int)
	 */
	@Override
	public net.sci.array.type.Float64 get(int x, int y)
	{
		return new Float64(getValue(x, y));
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#set(int, int, java.lang.Object)
	 */
	@Override
	public void set(int x, int y, net.sci.array.type.Float64 value)
	{
		setValue(x, y, value.getValue());
	}
	
	
	// =============================================================
	// Specialization of Array 

	@Override
	public Float64Array newInstance(int... dims)
	{
		return Float64Array.create(dims);
	}

	@Override
	public abstract Float64Array2D duplicate();

}
