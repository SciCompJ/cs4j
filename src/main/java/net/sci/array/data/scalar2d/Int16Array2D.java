/**
 * 
 */
package net.sci.array.data.scalar2d;

import net.sci.array.data.Int16Array;
import net.sci.array.type.Int16;

/**
 * @author dlegland
 *
 */
public abstract class Int16Array2D extends IntArray2D<Int16> implements Int16Array
{
	// =============================================================
	// Static methods

	public static final Int16Array2D create(int size0, int size1)
	{
		return new BufferedInt16Array2D(size0, size1);
	}
	
	
	// =============================================================
	// Constructor

	protected Int16Array2D(int size0, int size1)
	{
		super(size0, size1);
	}

	// =============================================================
	// New methods

	/**
	 * Returns the short value at a given position.
	 */
	public abstract short getShort(int x, int y);

	/**
	 * Sets the short value at a given position
	 */
	public abstract void setShort(int x, int y, short value);
	
	
	// =============================================================
	// Specialization of the Int16Array interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.UInt16Array#getShort(int[])
	 */
	@Override
	public short getShort(int[] pos)
	{
		return getShort(pos[0], pos[1]);
	}
	
	/* (non-Javadoc)
	 * @see net.sci.array.data.UInt16Array#setShort(int[], java.lang.Short)
	 */
	@Override
	public void setShort(int[] pos, short value)
	{
		setShort(pos[0], pos[1], value);
	}
	

	// =============================================================
	// Specialization of the IntArray2D interface

	@Override
	public int getInt(int x, int y)
	{
		return getShort(x, y);
	}

	@Override
	public void setInt(int x, int y, int value)
	{
		setShort(x, y, (short) Math.min(Math.max(value, Int16.MIN_VALUE), Int16.MAX_VALUE));
	}


	// =============================================================
	// Specialization of Array2D interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#get(int, int)
	 */
	@Override
	public Int16 get(int x, int y)
	{
		return new Int16(getShort(x, y));
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#set(int, int, java.lang.Object)
	 */
	@Override
	public void set(int x, int y, Int16 value)
	{
		setShort(x, y, value.getShort());
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#getValue(int, int)
	 */
	@Override
	public double getValue(int x, int y)
	{
		return getShort(x, y);
	}

	/**
	 * Sets the value at the specified position, by clamping the value between 0
	 * and 255.
	 * 
	 * @see net.sci.array.data.Array2D#setValue(int, int, double)
	 */
	@Override
	public void setValue(int x, int y, double value)
	{
		setShort(x, y, (short) Math.min(Math.max(value, Int16.MIN_VALUE), Int16.MAX_VALUE));
	}


	// =============================================================
	// Specialization of Array interface
	
//	@Override
//	public Int32Array newInstance(int... dims)
//	{
//		return Int32Array.create(dims);
//	}

	@Override
	public abstract Int16Array2D duplicate();

	/* (non-Javadoc)
	 * @see net.sci.array.Array#get(int[])
	 */
	@Override
	public Int16 get(int[] pos)
	{
		return new Int16(getShort(pos[0], pos[1]));
	}

	/* (non-Javadoc)
	 * @see net.sci.array.Array#set(int[], java.lang.Object)
	 */
	@Override
	public void set(int[] pos, Int16 value)
	{
		setInt(pos[0], pos[1], value.getInt());
	}
}