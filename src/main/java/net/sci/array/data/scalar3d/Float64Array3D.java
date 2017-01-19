/**
 * 
 */
package net.sci.array.data.scalar3d;

import net.sci.array.data.Float64Array;
import net.sci.array.type.Float64;

/**
 * @author dlegland
 *
 */
public abstract class Float64Array3D extends ScalarArray3D<Float64> implements Float64Array
{
	// =============================================================
	// Static methods

	public static final Float64Array3D create(int size0, int size1, int size2)
	{
		return new BufferedFloat64Array3D(size0, size1, size2);
	}
	
	
	// =============================================================
	// Constructor

	/**
	 * @param size0
	 * @param size1
	 * @param size2
	 */
	public Float64Array3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
	}


	// =============================================================
	// Specialization of Array3D 

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#get(int, int, int)
	 */
	@Override
	public Float64 get(int x, int y, int z)
	{
		return new Float64(getValue(x, y, z));
	}


	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#set(int, int, int, java.lang.Object)
	 */
	@Override
	public void set(int x, int y, int z, Float64 value)
	{
		setValue(x, y, z, value.getValue());
	}

	
	// =============================================================
	// Implementation of Array interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.ScalarArray#newInstance(int[])
	 */
	@Override
	public Float64Array newInstance(int... dims)
	{
		return Float64Array.create(dims);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.FloatArray#duplicate()
	 */
	@Override
	public abstract Float64Array3D duplicate();

}
