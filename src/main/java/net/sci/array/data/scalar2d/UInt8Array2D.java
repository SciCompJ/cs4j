/**
 * 
 */
package net.sci.array.data.scalar2d;

import java.io.PrintStream;

import net.sci.array.data.UInt8Array;
import net.sci.array.type.UInt8;

/**
 * @author dlegland
 *
 */
public abstract class UInt8Array2D extends IntArray2D<UInt8> implements UInt8Array
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
	public static final UInt8Array2D create(int size0, int size1)
	{
		return new BufferedUInt8Array2D(size0, size1);
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
	protected UInt8Array2D(int size0, int size1)
	{
		super(size0, size1);
	}

	
	// =============================================================
	// New methods

	/**
	 * Displays the content of this array on the stream (typically System.out).
	 * 
	 * @param ps
	 *            the stream to use for printing
	 */
	@Override
	public void print(PrintStream ps)
	{
		for (int y = 0; y < this.size1; y++)
		{
			for (int x = 0; x < this.size0; x++)
			{
				ps.printf(" %3d", this.getInt(x, y));
			}
			ps.println();
		}
	}
	
	/**
	 * Returns the byte value at a given position.
	 * 
	 * @param x
	 *            the x-coordinate of the position
	 * @param y
	 *            the y-coordinate of the position
	 * @return the byte value at the given position
	 */
	public abstract byte getByte(int x, int y);

	/**
	 * Sets the byte value at a given position
	 * 
	 * @param x
	 *            the x-coordinate of the position
	 * @param y
	 *            the y-coordinate of the position
	 * @param value
	 *            the new byte value at the given position
	 */
	public abstract void setByte(int x, int y, byte value);
	
	
	// =============================================================
	// Specialization of the UInt8Array interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.UInt8Array#getByte(int[])
	 */
	@Override
	public byte getByte(int[] pos)
	{
		return getByte(pos[0], pos[1]);
	}
	
	/* (non-Javadoc)
	 * @see net.sci.array.data.UInt8Array#setByte(int[], java.lang.Byte)
	 */
	@Override
	public void setByte(int[] pos, byte value)
	{
		setByte(pos[0], pos[1], value);
	}

	// =============================================================
	// Specialization of IntArray2D interface

	public int getInt(int x, int y)
	{
		return getByte(x, y) & 0x00FF; 
	}

	public void setInt(int x, int y, int value)
	{
		setByte(x, y, (byte) Math.min(Math.max(value, 0), 255));
	}

	// =============================================================
	// Specialization of Array2D interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#get(int, int)
	 */
	@Override
	public UInt8 get(int x, int y)
	{
		return new UInt8(getByte(x, y));
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#set(int, int, java.lang.Object)
	 */
	@Override
	public void set(int x, int y, UInt8 value)
	{
		setByte(x, y, value.getByte());
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#getValue(int, int)
	 */
	@Override
	public double getValue(int x, int y)
	{
		return getByte(x, y) & 0x00FF;
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
		value = Math.min(Math.max(value, 0), 255);
		setByte(x, y, (byte) value);
	}

	
	// =============================================================
	// Specialization of Array interface
	
	@Override
	public UInt8Array newInstance(int... dims)
	{
		return UInt8Array.create(dims);
	}

	@Override
	public abstract UInt8Array2D duplicate();

	/* (non-Javadoc)
	 * @see net.sci.array.Array#get(int[])
	 */
	@Override
	public UInt8 get(int[] pos)
	{
		return new UInt8(getByte(pos[0], pos[1]));
	}

	/* (non-Javadoc)
	 * @see net.sci.array.Array#set(int[], java.lang.Object)
	 */
	@Override
	public void set(int[] pos, UInt8 value)
	{
		setByte(pos[0], pos[1], value.getByte());
	}
}
