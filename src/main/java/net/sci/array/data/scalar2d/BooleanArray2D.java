/**
 * 
 */
package net.sci.array.data.scalar2d;

import net.sci.array.data.BooleanArray;
import net.sci.array.type.Boolean;

/**
 * @author dlegland
 *
 */
public abstract class BooleanArray2D extends IntArray2D<Boolean> implements BooleanArray
{
	// =============================================================
	// Static methods

	/**
	 * @param size0
	 *            the size of the array along the first dimension
	 * @param size1
	 *            the size of the array along the second dimension
	 * @return a new instance of BooleanArray2D
	 */
	public static final BooleanArray2D create(int size0, int size1)
	{
		return new BufferedBooleanArray2D(size0, size1);
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
	protected BooleanArray2D(int size0, int size1)
	{
		super(size0, size1);
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
	 * @return the boolean state at the given position
	 */
	public abstract boolean getState(int x, int y);

	/**
	 * Sets the logical state at a given position
	 * 
	 * @param x
	 *            the x-coordinate of the position
	 * @param y
	 *            the y-coordinate of the position
	 * @param state
	 *            the new state at the given position
	 */
	public abstract void setState(int x, int y, boolean state);
	
	
	// =============================================================
	// Specialization of the BooleanArray interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.BooleanArray#getState(int[])
	 */
	@Override
	public boolean getState(int[] pos)
	{
		return getState(pos[0], pos[1]);
	}
	
	/* (non-Javadoc)
	 * @see net.sci.array.data.BooleanArray#setBoolean(int[], java.lang.Boolean)
	 */
	@Override
	public void setState(int[] pos, boolean value)
	{
		setState(pos[0], pos[1], value);
	}

	// =============================================================
	// Specialization of IntArray2D interface

	public int getInt(int x, int y)
	{
		return getState(x, y) ? 1 : 0; 
	}

	public void setInt(int x, int y, int value)
	{
		setState(x, y, value != 0);
	}

	// =============================================================
	// Specialization of Array2D interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#get(int, int)
	 */
	@Override
	public Boolean get(int x, int y)
	{
		return new Boolean(getState(x, y));
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#set(int, int, java.lang.Object)
	 */
	@Override
	public void set(int x, int y, Boolean value)
	{
		setState(x, y, value.getState());
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#getValue(int, int)
	 */
	@Override
	public double getValue(int x, int y)
	{
		return getState(x, y) ? 1 : 0;
	}

	/**
	 * Sets the logical state at the specified position.
	 * 
	 * @see net.sci.array.data.Array2D#setValue(int, int, double)
	 */
	@Override
	public void setValue(int x, int y, double value)
	{
		setState(x, y, value != 0);
	}

	
	// =============================================================
	// Specialization of Array interface
	
	@Override
	public BooleanArray newInstance(int... dims)
	{
		return BooleanArray.create(dims);
	}

	@Override
	public abstract BooleanArray2D duplicate();

	/* (non-Javadoc)
	 * @see net.sci.array.Array#get(int[])
	 */
	@Override
	public Boolean get(int[] pos)
	{
		return new Boolean(getState(pos[0], pos[1]));
	}

	/* (non-Javadoc)
	 * @see net.sci.array.Array#set(int[], java.lang.Object)
	 */
	@Override
	public void set(int[] pos, Boolean value)
	{
		setState(pos[0], pos[1], value.getState());
	}
}
