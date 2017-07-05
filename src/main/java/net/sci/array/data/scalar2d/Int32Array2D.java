/**
 * 
 */
package net.sci.array.data.scalar2d;

import net.sci.array.data.Int32Array;
import net.sci.array.type.Int32;

/**
 * @author dlegland
 *
 */
public abstract class Int32Array2D extends IntArray2D<Int32> implements Int32Array
{
	// =============================================================
	// Static methods

	public static final Int32Array2D create(int size0, int size1)
	{
		return new BufferedInt32Array2D(size0, size1);
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
	protected Int32Array2D(int size0, int size1)
	{
		super(size0, size1);
	}

	
	// =============================================================
	// Specialization of Array2D interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#get(int, int)
	 */
	@Override
	public Int32 get(int x, int y)
	{
		return new Int32(getInt(x, y));
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#set(int, int, java.lang.Object)
	 */
	@Override
	public void set(int x, int y, Int32 value)
	{
		setInt(x, y, value.getInt());
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#getValue(int, int)
	 */
	@Override
	public double getValue(int x, int y)
	{
		return getInt(x, y);
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
		setInt(x, y, (int) value);
	}

	
	// =============================================================
	// Implementation of the IntArray interface

	@Override
	public int getInt(int[] pos)
	{
		return getInt(pos[0], pos[1]);
	}

	@Override
	public void setInt(int[] pos, int value)
	{
		setInt(pos[0], pos[1], value);		
	}


	// =============================================================
	// Specialization of Array interface
	
//	@Override
//	public Int32Array newInstance(int... dims)
//	{
//		return Int32Array.create(dims);
//	}

	@Override
	public abstract Int32Array2D duplicate();

	/* (non-Javadoc)
	 * @see net.sci.array.Array#get(int[])
	 */
	@Override
	public Int32 get(int[] pos)
	{
		return new Int32(getInt(pos[0], pos[1]));
	}

	/* (non-Javadoc)
	 * @see net.sci.array.Array#set(int[], java.lang.Object)
	 */
	@Override
	public void set(int[] pos, Int32 value)
	{
		setInt(pos[0], pos[1], value.getInt());
	}
}
