/**
 * 
 */
package net.sci.array.data.scalar3d;

import net.sci.array.data.BinaryArray;
import net.sci.array.type.Binary;

/**
 * Concrete implementation of BinaryArray3D that stores inner data in a linear
 * array of booleans.
 * 
 * @author dlegland
 *
 */
public class BufferedBinaryArray3D extends BinaryArray3D
{
	// =============================================================
	// Class fields

	boolean[] buffer;

	
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
	public BufferedBinaryArray3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
		this.buffer = new boolean[size0 * size1 * size2];
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
	 *            the buffer containing the boolean values
	 */
	public BufferedBinaryArray3D(int size0, int size1, int size2, boolean[] buffer)
	{
		super(size0, size1, size2);
		if (buffer.length < size0 * size1 * size2)
		{
			throw new IllegalArgumentException("Buffer size does not match image dimensions");
		}
		this.buffer = buffer;
	}


	// =============================================================
	// Implementation of the BooleanArray3D interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.scalar2d.BooleanArray3D#getState(int, int, int)
	 */
	@Override
	public boolean getBoolean(int x, int y, int z)
	{
		int index = x + this.size0 * (y + z * this.size1);
		return this.buffer[index];
	}
		
	/* (non-Javadoc)
	 * @see net.sci.array.data.scalar2d.BooleanArray3D#setState(int, int, int, boolean)
	 */
	@Override
	public void setBoolean(int x, int y, int z, boolean b)
	{
		int index = x + this.size0 * (y + z * this.size1);
		this.buffer[index] = b;
	}


	// =============================================================
	// Specialization of the Array interface

	@Override
	public BinaryArray3D duplicate()
	{
		boolean[] buffer2 = new boolean[size0 * size1 * size2];
		System.arraycopy(this.buffer, 0, buffer2, 0, size0 * size1 * size2);
		return new BufferedBinaryArray3D(size0, size1, size2, buffer2);
	}

	
	// =============================================================
	// Implementation of the Iterator interface

	public BinaryArray.Iterator iterator()
	{
		return new BooleanIterator();
	}
	
	private class BooleanIterator implements BinaryArray.Iterator
	{
		int index = -1;
		
		public BooleanIterator() 
		{
		}
		
		@Override
		public boolean hasNext()
		{
			return this.index < (size0 * size1 * size2 - 1);
		}

		@Override
		public Binary next()
		{
			this.index++;
			return new Binary(buffer[index]);
		}

		@Override
		public void forward()
		{
			this.index++;
		}

		@Override
		public Binary get()
		{
			return new Binary(buffer[index]);
		}

		@Override
		public void set(Binary b)
		{
			buffer[index] = b.getBoolean();
		}
		
		@Override
		public boolean getBoolean()
		{
			return buffer[index];
		}

		@Override
		public void setBoolean(boolean b)
		{
			buffer[index] = b;
		}
	}

}
