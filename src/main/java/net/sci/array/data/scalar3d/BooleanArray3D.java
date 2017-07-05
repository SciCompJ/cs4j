/**
 * 
 */
package net.sci.array.data.scalar3d;

import net.sci.array.data.BooleanArray;
import net.sci.array.type.Boolean;

/**
 * @author dlegland
 *
 */
public abstract class BooleanArray3D extends IntArray3D<Boolean> implements BooleanArray
{
	// =============================================================
	// Static methods

	public static final BooleanArray3D create(int size0, int size1, int size2)
	{
		return new BufferedBooleanArray3D(size0, size1, size2);
	}
	
	
	// =============================================================
	// Constructor

	protected BooleanArray3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
	}

	
	// =============================================================
	// New methods

	/**
	 * Returns the logical state at a given position.
	 * 
	 * @param x
	 *            the x-coordinate of the position
	 * @param y
	 *            the y-coordinate of the position
	 * @param z
	 *            the z-coordinate of the position
	 * @return the boolean value at the given position
	 */
	public abstract boolean getState(int x, int y, int z);

	/**
	 * Sets the logical state at a given position.
	 * 
	 * @param x
	 *            the x-coordinate of the position
	 * @param y
	 *            the y-coordinate of the position
	 * @param z
	 *            the z-coordinate of the position
	 * @param state
	 *            the new state at the given position
	 */
	public abstract void setState(int x, int y, int z, boolean state);
	
	
	// =============================================================
	// Specialization of the BooleanArray interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.BooleanArray#getState(int[])
	 */
	@Override
	public boolean getState(int[] pos)
	{
		return getState(pos[0], pos[1], pos[2]);
	}
	
	/* (non-Javadoc)
	 * @see net.sci.array.data.BooleanArray#setState(int[], java.lang.Boolean)
	 */
	@Override
	public void setState(int[] pos, boolean state)
	{
		setState(pos[0], pos[1], pos[2], state);
	}

	// =============================================================
	// Specialization of IntArrayND interface

	public int getInt(int x, int y, int z)
	{
		return getState(x, y, z) ? 1 : 0; 
	}

	public void setInt(int x, int y, int z, int value)
	{
		setState(x, y, z, value != 0);
	}

	// =============================================================
	// Specialization of Array3D interface

	@Override
	public abstract BooleanArray3D duplicate();

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#get(int, int, int)
	 */
	@Override
	public Boolean get(int x, int y, int z)
	{
		return new Boolean(getState(x, y, z));
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#set(int, int, int, java.lang.Object)
	 */
	public void set(int x, int y, int z, Boolean value)
	{
		setState(x, y, z, value.getState());
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#getValue(int, int, int)
	 */
	@Override
	public double getValue(int x, int y, int z)
	{
		return getState(x, y, z) ? 1 : 0;
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#setValue(int, int, int, double)
	 */
	@Override
	public void setValue(int x, int y, int z, double value)
	{
		setState(x, y, z, value != 0);
	}

	
	// =============================================================
	// Specialization of Array interface
	
	@Override
	public BooleanArray newInstance(int... dims)
	{
		return BooleanArray.create(dims);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.Array#get(int[])
	 */
	@Override
	public Boolean get(int[] pos)
	{
		return new Boolean(getState(pos[0], pos[1], pos[2]));
	}

	/* (non-Javadoc)
	 * @see net.sci.array.Array#set(int[], java.lang.Object)
	 */
	public void set(int[] pos, Boolean value)
	{
		setState(pos[0], pos[1], pos[2], value.getState());
	}
}
