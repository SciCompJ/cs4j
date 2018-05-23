/**
 * 
 */
package net.sci.array.scalar;

/**
 * @author dlegland
 *
 */
public abstract class UInt16Array2D extends IntArray2D<UInt16> implements UInt16Array
{
	// =============================================================
	// Static methods

	/**
	 * @param size0
	 *            the size of the array along the first dimension
	 * @param size1
	 *            the size of the array along the second dimension
	 * @return a new instance of UInt16Array2D
	 */
	public static final UInt16Array2D create(int size0, int size1)
	{
		return new BufferedUInt16Array2D(size0, size1);
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
	protected UInt16Array2D(int size0, int size1)
	{
		super(size0, size1);
	}

	
	// =============================================================
	// New methods

	/**
	 * Returns the short value at a given position.
	 * 
	 * @param x
	 *            the x-coordinate of the position
	 * @param y
	 *            the y-coordinate of the position
	 * @return the short value at the given position
	 */
	public abstract short getShort(int x, int y);

	/**
	 * Sets the short value at a given position
	 * 
	 * @param x
	 *            the x-coordinate of the position
	 * @param y
	 *            the y-coordinate of the position
	 * @param value
	 *            the new short value at the given position
	 */
	public abstract void setShort(int x, int y, short value);
	
	
	// =============================================================
	// Specialization of the UInt16Array interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.UInt8Array#getByte(int[])
	 */
	@Override
	public short getShort(int[] pos)
	{
		return getShort(pos[0], pos[1]);
	}
	
	/* (non-Javadoc)
	 * @see net.sci.array.data.UInt8Array#setByte(int[], java.lang.Byte)
	 */
	@Override
	public void setShort(int[] pos, short value)
	{
		setShort(pos[0], pos[1], value);
	}


	// =============================================================
	// Specialization of IntArray2D interface

	public int getInt(int x, int y)
	{
		return getShort(x, y) & 0x00FFFF; 
	}

	public void setInt(int x, int y, int value)
	{
		setShort(x, y, (short) Math.min(Math.max(value, 0), UInt16.MAX_VALUE));
	}

	// =============================================================
	// Specialization of Array2D interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#get(int, int)
	 */
	@Override
	public UInt16 get(int x, int y)
	{
		return new UInt16(getShort(x, y));
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#set(int, int, java.lang.Object)
	 */
	@Override
	public void set(int x, int y, UInt16 value)
	{
		setShort(x, y, value.getShort());
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#getValue(int, int)
	 */
	@Override
	public double getValue(int x, int y)
	{
		return getShort(x, y) & 0x00FFFF;
	}

	/**
	 * Sets the value at the specified position, by clamping the value between 0
	 * and 2^16-1.
	 * 
	 * @see net.sci.array.Array2D#setValue(int, int, double)
	 */
	@Override
	public void setValue(int x, int y, double value)
	{
		setShort(x, y, (short) Math.min(Math.max(value, 0), UInt16.MAX_VALUE));
	}

	
	// =============================================================
	// Specialization of Array interface
	
	@Override
	public UInt16Array newInstance(int... dims)
	{
		return UInt16Array.create(dims);
	}

	@Override
	public abstract UInt16Array2D duplicate();

	/* (non-Javadoc)
	 * @see net.sci.array.Array#get(int[])
	 */
	@Override
	public UInt16 get(int[] pos)
	{
		return new UInt16(getShort(pos[0], pos[1]));
	}

	/* (non-Javadoc)
	 * @see net.sci.array.Array#set(int[], java.lang.Object)
	 */
	@Override
	public void set(int[] pos, UInt16 value)
	{
		setShort(pos[0], pos[1], value.getShort());
	}
}
