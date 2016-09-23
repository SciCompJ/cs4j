/**
 * 
 */
package net.sci.array.data.scalar3d;

import net.sci.array.data.FloatArray;
import net.sci.array.type.Float;

/**
 * @author dlegland
 *
 */
public abstract class FloatArray3D extends ScalarArray3D<Float> implements FloatArray
{
	// =============================================================
	// Static methods

	public static final FloatArray3D create(int size0, int size1, int size2)
	{
		return new BufferedFloatArray3D(size0, size1, size2);
	}
	
	
	// =============================================================
	// Constructor

	/**
	 * @param size0
	 * @param size1
	 * @param size2
	 */
	public FloatArray3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
	}


	// =============================================================
	// Specialization of Array3D 

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#get(int, int, int)
	 */
	@Override
	public Float get(int x, int y, int z)
	{
		return new Float((float) getValue(x, y, z));
	}


	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#set(int, int, int, java.lang.Object)
	 */
	@Override
	public void set(int x, int y, int z, Float value)
	{
		setValue(x, y, z, value.getValue());
	}

	
	// =============================================================
	// Implementation of Array interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.ScalarArray#newInstance(int[])
	 */
	@Override
	public FloatArray newInstance(int... dims)
	{
		return FloatArray.create(dims);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.FloatArray#duplicate()
	 */
	@Override
	public abstract FloatArray3D duplicate();

}
