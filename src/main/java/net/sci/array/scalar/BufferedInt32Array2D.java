/**
 * 
 */
package net.sci.array.scalar;

/**
 * @author dlegland
 *
 */
public class BufferedInt32Array2D extends Int32Array2D
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
	 */
	public BufferedInt32Array2D(int size0, int size1)
	{
		super(size0, size1);
		this.buffer = new int[size0 * size1];
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
	public BufferedInt32Array2D(int size0, int size1, int[] buffer)
	{
		super(size0, size1);
		if (buffer.length < size0 * size1)
		{
			throw new IllegalArgumentException("Buffer size does not match image dimensions");
		}
		this.buffer = buffer;
	}


	// =============================================================
	// Implementation of the IntArray2D interface

	@Override
	public int getInt(int x, int y)
	{
		int index = y * size0 + x;
		return buffer[index];
	}

	@Override
	public void setInt(int x, int y, int value)
	{
		int index = y * size0 + x;
		buffer[index] = value;
	}


	// =============================================================
	// Implementation of the Array interface

	@Override
	public BufferedInt32Array2D duplicate()
	{
		int[] buffer2 = new int[size0 * size1];
		System.arraycopy(this.buffer, 0, buffer2, 0, size0 * size1);
		return new BufferedInt32Array2D(size0, size1, buffer2);
	}

	@Override
	public Int32Array newInstance(int... dims)
	{
		return Int32Array.create(dims);
	}

	
	// =============================================================
	// Implementation of the Iterable interface

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
			return this.index < (size0 * size1 - 1);
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
