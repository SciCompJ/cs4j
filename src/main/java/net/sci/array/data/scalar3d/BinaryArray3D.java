/**
 * 
 */
package net.sci.array.data.scalar3d;

import net.sci.array.data.BinaryArray;
import net.sci.array.type.Binary;

/**
 * A three-dimensional array containing boolean values.
 * 
 * @author dlegland
 *
 */
public abstract class BinaryArray3D extends IntArray3D<Binary> implements BinaryArray
{
	// =============================================================
	// Static methods

	public static final BinaryArray3D create(int size0, int size1, int size2)
	{
		return new BufferedBinaryArray3D(size0, size1, size2);
	}
	
	
	// =============================================================
	// Constructor

	protected BinaryArray3D(int size0, int size1, int size2)
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
	public abstract boolean getBoolean(int x, int y, int z);

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
	public abstract void setBoolean(int x, int y, int z, boolean state);
	
	
	// =============================================================
	// Specialization of the BooleanArray interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.BooleanArray#getState(int[])
	 */
	@Override
	public boolean getBoolean(int[] pos)
	{
		return getBoolean(pos[0], pos[1], pos[2]);
	}
	
	/* (non-Javadoc)
	 * @see net.sci.array.data.BooleanArray#setState(int[], java.lang.Boolean)
	 */
	@Override
	public void setBoolean(int[] pos, boolean state)
	{
		setBoolean(pos[0], pos[1], pos[2], state);
	}

    @Override
    public BinaryArray3D complement()
    {
        BinaryArray3D result = duplicate();
        BinaryArray.Iterator iter1 = iterator();
        BinaryArray.Iterator iter2 = result.iterator();
        while (iter1.hasNext() && iter2.hasNext())
        {
            iter2.setNextBoolean(!iter1.nextBoolean());
        }
        return result;
    }

    
	// =============================================================
	// Specialization of IntArrayND interface

	public int getInt(int x, int y, int z)
	{
		return getBoolean(x, y, z) ? 1 : 0; 
	}

	public void setInt(int x, int y, int z, int value)
	{
		setBoolean(x, y, z, value != 0);
	}

	// =============================================================
	// Specialization of Array3D interface

	@Override
	public abstract BinaryArray3D duplicate();

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#get(int, int, int)
	 */
	@Override
	public Binary get(int x, int y, int z)
	{
		return new Binary(getBoolean(x, y, z));
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#set(int, int, int, java.lang.Object)
	 */
	public void set(int x, int y, int z, Binary value)
	{
		setBoolean(x, y, z, value.getBoolean());
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#getValue(int, int, int)
	 */
	@Override
	public double getValue(int x, int y, int z)
	{
		return getBoolean(x, y, z) ? 1 : 0;
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#setValue(int, int, int, double)
	 */
	@Override
	public void setValue(int x, int y, int z, double value)
	{
		setBoolean(x, y, z, value != 0);
	}

	
	// =============================================================
	// Specialization of Array interface
	
	@Override
	public BinaryArray newInstance(int... dims)
	{
		return BinaryArray.create(dims);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.Array#get(int[])
	 */
	@Override
	public Binary get(int[] pos)
	{
		return new Binary(getBoolean(pos[0], pos[1], pos[2]));
	}

	/* (non-Javadoc)
	 * @see net.sci.array.Array#set(int[], java.lang.Object)
	 */
	public void set(int[] pos, Binary value)
	{
		setBoolean(pos[0], pos[1], pos[2], value.getBoolean());
	}
}
