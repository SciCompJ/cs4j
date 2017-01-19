/**
 * 
 */
package net.sci.array.data.scalar3d;

import net.sci.array.data.Int16Array;
import net.sci.array.type.Int16;

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
	 * @param size1
	 */
	public BufferedInt16Array3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
		this.buffer = new short[size0 * size1 * size2];
	}

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
	public short getShort(int x, int y, int z)
	{
		int index = x + this.size0 * (y + z * this.size1);
		return this.buffer[index];
	}
		
	/* (non-Javadoc)
	 * @see net.sci.array.data.scalar2d.UInt16Array3D#setShort(int, int, int, short)
	 */
	@Override
	public void setShort(int x, int y, int z, short value)
	{
		int index = x + this.size0 * (y + z * this.size1);
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
