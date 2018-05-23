/**
 * 
 */
package net.sci.array.scalar;

/**
 * @author dlegland
 *
 */
public class BufferedInt32Array3D extends Int32Array3D
{
	// =============================================================
	// Class fields

	int[] buffer;

	
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
	public BufferedInt32Array3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
		this.buffer = new int[size0 * size1 * size2];
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
	 *            the buffer containing the integer values
	 */
	public BufferedInt32Array3D(int size0, int size1, int size2, int[] buffer)
	{
		super(size0, size1, size2);
		if (buffer.length < size0 * size1 * size2)
		{
			throw new IllegalArgumentException("Buffer size does not match image dimensions");
		}
		this.buffer = buffer;
	}

	// =============================================================
	// Specialization of the IntArray3D interface

	@Override
	public int getInt(int x, int y, int z)
	{
		int index = x + this.size0 * (y + z * this.size1);
		return this.buffer[index];
	}

	@Override
	public void setInt(int x, int y, int z, int value)
	{
		int index = x + this.size0 * (y + z * this.size1);
		this.buffer[index] = value;
	}
	
	// =============================================================
	// Specialization of the Array interface

	@Override
	public Int32Array3D duplicate()
	{
		int[] buffer2 = new int[size0 * size1 * size2];
		System.arraycopy(this.buffer, 0, buffer2, 0, size0 * size1 * size2);
		return new BufferedInt32Array3D(size0, size1, size2, buffer2);
	}

	
	// =============================================================
	// Implementation of the Iterator interface

	public Int32Array.Iterator iterator()
	{
		return new Int32Iterator();
	}
	
	private class Int32Iterator implements Int32Array.Iterator
	{
		int index = -1;
		
		public Int32Iterator() 
		{
		}
		
		@Override
		public boolean hasNext()
		{
			return this.index < (size0 * size1 * size2 - 1);
		}

		@Override
		public Int32 next()
		{
			this.index++;
			return new Int32(buffer[index]);
		}

		@Override
		public void forward()
		{
			this.index++;
		}

		@Override
		public Int32 get()
		{
			return new Int32(buffer[index]);
		}

		@Override
		public int getInt()
		{
			return buffer[index];
		}

		@Override
		public void setInt(int value)
		{
			buffer[index] = value;
		}
	}
}
