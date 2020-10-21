/**
 * 
 */
package net.sci.array.scalar;

/**
 * Implements Int16Array by storing data in a linear short buffer.
 * 
 * @author dlegland
 *
 */
public class BufferedInt16Array2D extends Int16Array2D
{
	// =============================================================
	// Class fields

	short[] buffer;

	
	// =============================================================
	// Constructors

	/**
	 * @param size0
	 *            the size of the array along the first dimension
	 * @param size1
	 *            the size of the array along the second dimension
	 */
	public BufferedInt16Array2D(int size0, int size1)
	{
		super(size0, size1);
		this.buffer = new short[size0 * size1];
	}

	/**
	 * 
	 * @param size0
	 *            the size of the array along the first dimension
	 * @param size1
	 *            the size of the array along the second dimension
	 * @param buffer
	 *            the buffer containing the values
	 */
	public BufferedInt16Array2D(int size0, int size1, short[] buffer)
	{
		super(size0, size1);
		if (buffer.length < size0 * size1)
		{
			throw new IllegalArgumentException("Buffer size does not match image dimensions");
		}
		this.buffer = buffer;
	}


	// =============================================================
	// Implementation of the Int16Array2D interface

    /* (non-Javadoc)
     * @see net.sci.array.data.scalar2d.Int16Array2D#setShort(int, int, short)
     */
    @Override
    public void setShort(int x, int y, short s)
    {
        int index = x + y * this.size0;
        this.buffer[index] = s;
    }

	/* (non-Javadoc)
	 * @see net.sci.array.data.scalar2d.Int16Array2D#getShort(int, int)
	 */
	@Override
	public short getShort(int... pos)
	{
		int index = pos[0] + pos[1] * this.size0;
		return this.buffer[index];
	}
	
	/* (non-Javadoc)
	 * @see net.sci.array.data.scalar2d.Int16Array2D#setShort(int, int, short)
	 */
	@Override
	public void setShort(int[] pos, short s)
	{
        int index = pos[0] + pos[1] * this.size0;
		this.buffer[index] = s;
	}


	// =============================================================
	// Implementation of the Array interface

	@Override
	public Int16Array2D duplicate()
	{
		short[] buffer2 = new short[size0 * size1];
		System.arraycopy(this.buffer, 0, buffer2, 0, size0 * size1);
		return new BufferedInt16Array2D(size0, size1, buffer2);
	}

	
	// =============================================================
	// Implementation of the Iterable interface

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
			return new Int16(buffer[index]);
		}

		@Override
		public void forward()
		{
			this.index++;
		}

		@Override
		public Int16 get()
		{
			return new Int16(buffer[index]);
		}

		@Override
		public short getShort()
		{
			return buffer[index];
		}

		@Override
		public void setShort(short b)
		{
			buffer[index] = b;
		}
	}
}
