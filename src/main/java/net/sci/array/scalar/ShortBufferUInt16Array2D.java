/**
 * 
 */
package net.sci.array.scalar;

import java.nio.ShortBuffer;

/**
 * Implements a 2D array of UInt16 by relying on a ShortBuffer instance.
 * 
 * @see java.nio.ShortBuffer
 * 
 * @author dlegland
 */
public class ShortBufferUInt16Array2D extends UInt16Array2D
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
	public ShortBufferUInt16Array2D(int size0, int size1, ShortBuffer buffer)
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

    public short getShort(int... pos)
    {
        int index = pos[0] + pos[1] * this.size0;
        return this.buffer.get(index);
    }

    public void setShort(int x, int y, short value)
    {
        int index = x + y * this.size0;
        this.buffer.put(index, value);
    }
    
    public void setShort(int[] pos, short value)
    {
        int index = pos[0] + pos[1] * this.size0;
        this.buffer.put(index, value);
    }
    

    // =============================================================
    // Specialization of ScalarArray

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#getValue(int, int)
	 */
	@Override
	public double getValue(int... pos)
	{
		int index = pos[0] + pos[1] * this.size0;
		return this.buffer.get(index);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#setValue(int, int, double)
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
	public UInt16Array.Iterator iterator()
	{
		return new UInt16Iterator();
	}

	private class UInt16Iterator implements UInt16Array.Iterator
	{
		int index = -1;
		
		public UInt16Iterator() 
		{
		}
		
		@Override
		public boolean hasNext()
		{
			return this.index < (size0 * size1 - 1);
		}

		@Override
		public UInt16 next()
		{
			this.index++;
			return new UInt16(buffer.get(index));
		}

		@Override
		public void forward()
		{
			this.index++;
		}

		@Override
		public UInt16 get()
		{
			return new UInt16(buffer.get(index));
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
