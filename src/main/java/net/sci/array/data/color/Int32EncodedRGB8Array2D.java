/**
 * 
 */
package net.sci.array.data.color;

import net.sci.array.data.scalar2d.Int32Array2D;
import net.sci.array.type.RGB8;
import net.sci.array.type.UInt8;

/**
 * Implementation of 2D array of RGB8, by keeping value in a buffer if Int32.
 * 
 * The integer values are encoded and decoded on the fly. The number of elements
 * of the underlying integer array is the same as the number of elements of the
 * RGB8 array.
 * 
 * @author dlegland
 *
 */
public class Int32EncodedRGB8Array2D extends RGB8Array2D
{
	// =============================================================
	// Class variables

	Int32Array2D buffer;
	
	// =============================================================
	// Constructor

	public Int32EncodedRGB8Array2D(int size0, int size1)
	{
		super(size0, size1);
		this.buffer = Int32Array2D.create(size0, size1);
	}

	public Int32EncodedRGB8Array2D(Int32Array2D buffer)
	{
		super(buffer.getSize(0), buffer.getSize(1));
		this.buffer = buffer;
	}


	// =============================================================
	// Implementation of the VectorArray2D interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.vector.VectorArray2D#getValue(int, int, int)
	 */
	@Override
	public double getValue(int x, int y, int c)
	{
		int intCode = this.buffer.getInt(x, y);
		switch (c)
		{
		case 0: return intCode & 0x00FF;
		case 1: return (intCode >> 8) & 0x00FF;
		case 2: return (intCode >> 16) & 0x00FF;
		}
		throw new IllegalArgumentException("Channel number must be comprised between 0 and 2, not " + c);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.vector.VectorArray2D#setValue(int, int, int, double)
	 */
	@Override
	public void setValue(int x, int y, int c, double value)
	{
		int intCode = this.buffer.getInt(x, y);
		int r = intCode & 0x00FF;
		int g = intCode & 0x00FF00;
		int b = intCode & 0x00FF0000;
		int intValue = UInt8.clamp(value);
		
		switch (c)
		{
		case 0: r = intValue; break;
		case 1: g = intValue << 8; break;
		case 2: b = intValue << 16; break;
		default: throw new IllegalArgumentException("Channel number must be comprised between 0 and 2, not " + c);
		}
		
		intCode = r | g | b;
		this.buffer.setInt(x, y, intCode);
	}

	
	// =============================================================
	// Implementation of the Array2D interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#get(int, int)
	 */
	@Override
	public RGB8 get(int x, int y)
	{
		return new RGB8(this.buffer.getInt(x, y));
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#set(int, int, java.lang.Object)
	 */
	@Override
	public void set(int x, int y, RGB8 rgb)
	{
		this.buffer.setInt(x, y, rgb.getIntCode());
	}


	// =============================================================
	// Implementation of the Array interface

	@Override
	public RGB8Array newInstance(int... dims)
	{
		return RGB8Array.create(dims);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.color.RGB8Array2D#duplicate()
	 */
	@Override
	public RGB8Array2D duplicate()
	{
		Int32Array2D newBuffer = this.buffer.duplicate();
		return new Int32EncodedRGB8Array2D(newBuffer);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.color.RGB8Array2D#iterator()
	 */
	@Override
	public RGB8Array.Iterator iterator()
	{
		return new Int32ArrayRGB8Iterator(this.buffer);
	}
}
