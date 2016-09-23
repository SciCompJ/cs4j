/**
 * 
 */
package net.sci.array.data.scalar2d;

import net.sci.array.data.FloatArray;
import net.sci.array.type.Float;

/**
 * @author dlegland
 *
 */
public abstract class FloatArray2D extends ScalarArray2D<Float> implements FloatArray
{
	// =============================================================
	// Static methods

	public static final FloatArray2D create(int size0, int size1)
	{
		return new BufferedFloatArray2D(size0, size1);
	}
	
	
	// =============================================================
	// Constructor

	/**
	 * @param size0
	 * @param size1
	 */
	public FloatArray2D(int size0, int size1)
	{
		super(size0, size1);
	}

	// =============================================================
	// Specialization of Array2D 

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#get(int, int)
	 */
	@Override
	public Float get(int x, int y)
	{
		return new Float((float) getValue(x, y));
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#set(int, int, java.lang.Object)
	 */
	@Override
	public void set(int x, int y, Float value)
	{
		setValue(x, y, value.getValue());
	}
	
	// =============================================================
	// Specialization of Array2D 

	@Override
	public FloatArray newInstance(int... dims)
	{
		return FloatArray.create(dims);
	}

	@Override
	public abstract FloatArray2D duplicate();

}
