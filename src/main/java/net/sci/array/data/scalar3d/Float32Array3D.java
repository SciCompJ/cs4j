/**
 * 
 */
package net.sci.array.data.scalar3d;

import net.sci.array.data.Float32Array;
import net.sci.array.type.Float32;

/**
 * @author dlegland
 *
 */
public abstract class Float32Array3D extends ScalarArray3D<Float32> implements Float32Array
{
	// =============================================================
	// Static methods

	/**
	 * 
	 * @param size0
	 *            the size of the array along the first dimension
	 * @param size1
	 *            the size of the array along the second dimension
	 * @param size2
	 *            the size of the array along the third dimension
	 * @return a new instance of Float32Array3D
	 */
	public static final Float32Array3D create(int size0, int size1, int size2)
	{
		return new BufferedFloat32Array3D(size0, size1, size2);
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
	 * @param size2
	 *            the size of the array along the third dimension
	 */
	protected Float32Array3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
	}


	// =============================================================
	// Specialization of Array3D 

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#get(int, int, int)
	 */
	@Override
	public Float32 get(int x, int y, int z)
	{
		return new Float32((float) getValue(x, y, z));
	}


	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#set(int, int, int, java.lang.Object)
	 */
	@Override
	public void set(int x, int y, int z, Float32 value)
	{
		setValue(x, y, z, value.getValue());
	}

	
	// =============================================================
	// Implementation of Array interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.ScalarArray#newInstance(int[])
	 */
	@Override
	public Float32Array newInstance(int... dims)
	{
		return Float32Array.create(dims);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.FloatArray#duplicate()
	 */
	@Override
	public abstract Float32Array3D duplicate();

}
