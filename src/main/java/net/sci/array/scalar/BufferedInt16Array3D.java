/**
 * 
 */
package net.sci.array.scalar;

/**
 * @author dlegland
 *
 */
public class BufferedInt16Array3D extends Int16Array3D
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
	 * @param size2
	 *            the size of the array along the third dimension
	 */
	public BufferedInt16Array3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
		this.buffer = new short[size0 * size1 * size2];
	}

	/**
	 * 
	 * @param size0
	 *            the size of the array along the first dimension
	 * @param size1
	 *            the size of the array along the second dimension
	 * @param size2
	 *            the size of the array along the third dimension
	 * @param buffer
	 *            the buffer containing the short values
	 */
	public BufferedInt16Array3D(int size0, int size1, int size2, short[] buffer)
	{
		super(size0, size1, size2);
		if (buffer.length < size0 * size1 * size2)
		{
			throw new IllegalArgumentException("Buffer size does not match image dimensions");
		}
		this.buffer = buffer;
	}


	// =============================================================
	// Specialization of the UInt16Array3D interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.scalar2d.UInt16Array3D#getShort(int, int, int)
	 */
	@Override
	public short getShort(int... pos)
	{
		int index = pos[0] + this.size0 * (pos[1] + pos[2] * this.size1);
		return this.buffer[index];
	}
		
	/* (non-Javadoc)
	 * @see net.sci.array.data.scalar2d.UInt16Array3D#setShort(int, int, int, short)
	 */
	@Override
	public void setShort(short value, int... pos)
	{
	    int index = pos[0] + this.size0 * (pos[1] + pos[2] * this.size1);
        this.buffer[index] = value;
	}

	
	// =============================================================
	// Specialization of the Array interface

	@Override
	public Int16Array3D duplicate()
	{
		short[] buffer2 = new short[size0 * size1 * size2];
		System.arraycopy(this.buffer, 0, buffer2, 0, size0 * size1 * size2);
		return new BufferedInt16Array3D(size0, size1, size2, buffer2);
	}

	
	// =============================================================
	// Implementation of the Iterator interface

	public Int16Array.Iterator iterator()
	{
		return new Iterator();
	}
	
	private class Iterator implements Int16Array.Iterator
	{
		int index = -1;
		
		public Iterator() 
		{
		}
		
		@Override
		public boolean hasNext()
		{
			return this.index < (size0 * size1 * size2 - 1);
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
		public void setShort(short s)
		{
			buffer[index] = s;
		}
	}

}
