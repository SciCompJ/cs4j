/**
 * 
 */
package net.sci.array.numeric.impl;

import java.nio.ShortBuffer;

import net.sci.array.numeric.Int16;
import net.sci.array.numeric.Int16Array;
import net.sci.array.numeric.Int16Array2D;

/**
 * Implements a 2D array of Int16 by relying on a ShortBuffer instance.
 * 
 * @see java.nio.ShortBuffer
 * @see ShortBufferUInt16Array2D
 * 
 * @author dlegland
 */
public class ShortBufferInt16Array2D extends Int16Array2D
{
	// =============================================================
	// Class fields

	ShortBuffer buffer;
	
	
	// =============================================================
	// Constructors

	/**
	 * @param size0
	 *            the size of the array along the first dimension
	 * @param size1
	 *            the size of the array along the second dimension
	 * @param buffer
	 *            the buffer containing the values
	 */
	public ShortBufferInt16Array2D(int size0, int size1, ShortBuffer buffer)
	{
		super(size0, size1);
		if (buffer.limit() < size0 * size1)
		{
			throw new IllegalArgumentException("Buffer size does not match image dimensions");
		}
		this.buffer = buffer;
	}

	
    // =============================================================
    // Specialization of ShortArray2D

    public short getShort(int x, int y)
    {
        int index = x + y * this.size0;
        return this.buffer.get(index);
    }

    public void setShort(int x, int y, short value)
    {
        int index = x + y * this.size0;
        this.buffer.put(index, value);
    }
    
    public short getShort(int[] pos)
    {
        int index = pos[0] + pos[1] * this.size0;
        return this.buffer.get(index);
    }

    public void setShort(int[] pos, short value)
    {
        int index = pos[0] + pos[1] * this.size0;
        this.buffer.put(index, value);
    }
    

    // =============================================================
    // Specialization of ScalarArray

	/* (non-Javadoc)
	 * @see net.sci.array.numeric.ScalarArray2D#getValue(int, int)
	 */
	@Override
	public double getValue(int[] pos)
	{
		int index = pos[0] + pos[1] * this.size0;
		return this.buffer.get(index);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.numeric.ScalarArray2D#setValue(int, int, double)
	 */
	@Override
	public void setValue(int[] pos, double value)
	{
        int index = pos[0] + pos[1] * this.size0;
        this.buffer.put(index, (short) value);
	}

	
    // =============================================================
    // Implementation of Array interface
	
    /* (non-Javadoc)
	 * @see net.sci.array.data.ScalarArray#iterator()
	 */
	@Override
	public Int16Array.Iterator iterator()
	{
		return new Int16Iterator();
	}

	private class Int16Iterator implements Int16Array.Iterator
	{
		int index = -1;
		
		public Int16Iterator() 
		{
		}
		
		@Override
		public boolean hasNext()
		{
			return this.index < (size0 * size1 - 1);
		}

		@Override
		public Int16 next()
		{
			this.index++;
			return new Int16(buffer.get(index));
		}

		@Override
		public void forward()
		{
			this.index++;
		}

		@Override
		public Int16 get()
		{
			return new Int16(buffer.get(index));
		}

		@Override
		public double getValue()
		{
			return buffer.get(index);
		}

		@Override
		public void setValue(double value)
		{
			buffer.put(index, (short) value);
		}

        @Override
        public short getShort()
        {
            return buffer.get(index);
        }

        @Override
        public void setShort(short s)
        {
            buffer.put(index, s);
        }
	}
}
