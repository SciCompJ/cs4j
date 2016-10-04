/**
 * 
 */
package net.sci.array.data.scalar3d;

import net.sci.array.data.Int32Array;
import net.sci.array.type.Int32;

/**
 * @author dlegland
 *
 */
public abstract class Int32Array3D extends IntArray3D<Int32> implements Int32Array
{
	// =============================================================
	// Static methods

	public static final Int32Array3D create(int size0, int size1, int size2)
	{
		return new BufferedInt32Array3D(size0, size1, size2);
	}
	
	
	// =============================================================
	// Constructor

	protected Int32Array3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
	}


	// =============================================================
	// Specialization of the IntArray interface

	@Override
	public int getInt(int[] pos)
	{
		return getInt(pos[0], pos[1], pos[2]);
	}

	@Override
	public void setInt(int[] pos, int value)
	{
		setInt(pos[0], pos[1], pos[2], value);
	}
	

	// =============================================================
	// Specialization of the Array3D interface

	@Override
	public abstract Int32Array3D duplicate();

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#get(int, int, int)
	 */
	@Override
	public Int32 get(int x, int y, int z)
	{
		return new Int32(getInt(x, y, z));
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#set(int, int, int, java.lang.Object)
	 */
	public void set(int x, int y, int z, Int32 value)
	{
		setInt(x, y, z, value.getInt());
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#getValue(int, int, int)
	 */
	@Override
	public double getValue(int x, int y, int z)
	{
		return getInt(x, y, z);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#setValue(int, int, int, double)
	 */
	@Override
	public void setValue(int x, int y, int z, double value)
	{
		setInt(x, y, z, (int) value);
	}

	
	// =============================================================
	// Specialization of Array interface
	
	@Override
	public Int32Array newInstance(int... dims)
	{
		return Int32Array.create(dims);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.Array#get(int[])
	 */
	@Override
	public Int32 get(int[] pos)
	{
		return new Int32(getInt(pos[0], pos[1], pos[2]));
	}

	/* (non-Javadoc)
	 * @see net.sci.array.Array#set(int[], java.lang.Object)
	 */
	public void set(int[] pos, Int32 value)
	{
		setInt(pos[0], pos[1], pos[2], value.getInt());
	}
}
