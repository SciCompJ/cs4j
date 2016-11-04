/**
 * 
 */
package net.sci.array.data.color;

import net.sci.array.data.Int32Array;
import net.sci.array.type.RGB8;
import net.sci.array.type.UInt8;

/**
 * Implementation of multidimensional array of RGB8, by keeping value in a buffer if Int32.
 * 
 * The integer values are encoded and decoded on the fly. The number of elements
 * of the underlying integer array is the same as the number of elements of the
 * RGB8 array.
 * 
 * @author dlegland
 *
 */
public class Int32EncodedRGB8ArrayND extends RGB8ArrayND
{
	// =============================================================
	// Class variables

	Int32Array buffer;
	
	// =============================================================
	// Constructor

	public Int32EncodedRGB8ArrayND(int... sizes)
	{
		super(sizes);
		this.buffer = Int32Array.create(sizes);
	}

	public Int32EncodedRGB8ArrayND(Int32Array buffer)
	{
		super(buffer.getSize());
		this.buffer = buffer;
	}

	
	// =============================================================
	// Implementation of the Array interface

	@Override
	public RGB8 get(int[] pos)
	{
		return new RGB8(this.buffer.getInt(pos));
	}

	@Override
	public void set(int[] pos, RGB8 rgb)
	{
		this.buffer.setInt(pos, rgb.getIntCode());
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.color.RGB8Array2D#duplicate()
	 */
	@Override
	public RGB8ArrayND duplicate()
	{
		Int32Array newBuffer = this.buffer.duplicate();
		return new Int32EncodedRGB8ArrayND(newBuffer);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.color.RGB8Array#iterator()
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
