/**
 * 
 */
package net.sci.array.data.color;

import net.sci.array.data.Int32Array;
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
	 * @see net.sci.array.data.vector.VectorArray2D#getValues(int, int)
	 */
	@Override
	public double[] getValues(int x, int y)
	{
		return new RGB8(this.buffer.getInt(x, y)).getValues();
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.vector.VectorArray2D#setValues(int, int, double[])
	 */
	@Override
	public void setValues(int x, int y, double[] values)
	{
		int r = UInt8.clamp(values[0]);
		int g = UInt8.clamp(values[1]);
		int b = UInt8.clamp(values[2]);
		int intCode = b << 16 | g << 8 | r;
		this.buffer.setInt(x, y, intCode);
	}

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
		throw new IllegalArgumentException("Channel number must be comprised between 0 and 2");
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
		default: throw new IllegalArgumentException("Channel number must be comprised between 0 and 2");
		}
		intCode = r | g | b;
		this.buffer.setInt(x, y, intCode);
//		int[] rgb = new RGB8(this.buffer.getInt(x, y)).getSamples();
//		rgb[c] = UInt8.clamp(value);
//		this.buffer.setInt(x, y, new RGB8(rgb[0], rgb[1], rgb[2]).getIntCode());
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
		return new Iterator();
	}
	
	private class Iterator implements RGB8Array.Iterator
	{
		Int32Array.Iterator intIterator;
		
		public Iterator() 
		{
			this.intIterator = buffer.iterator();
		}
		
		@Override
		public boolean hasNext()
		{
			return intIterator.hasNext();
		}

		@Override
		public RGB8 next()
		{
			forward();
			return get();
		}

		@Override
		public void forward()
		{
			intIterator.forward();
		}

		@Override
		public RGB8 get()
		{	
			return new RGB8(intIterator.getInt());
		}

		@Override
		public void set(RGB8 rgb)
		{
			intIterator.setInt(rgb.getIntCode());
		}

		@Override
		public double getValue()
		{
			return get().getValue();
		}

		@Override
		public void setValue(double value)
		{
			int val = UInt8.clamp(value);
			int intCode = val << 16 & val << 8 & val;
			intIterator.setInt(intCode);
		}
	}
}