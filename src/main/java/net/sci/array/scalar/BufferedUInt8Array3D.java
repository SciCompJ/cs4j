/**
 * 
 */
package net.sci.array.scalar;

/**
 * @author dlegland
 *
 */
public class BufferedUInt8Array3D extends UInt8Array3D
{
	// =============================================================
	// Class fields

	byte[] buffer;

	
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
	public BufferedUInt8Array3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
		this.buffer = new byte[size0 * size1 * size2];
	}

	/**
	 * @param size0
	 *            the size of the array along the first dimension
	 * @param size1
	 *            the size of the array along the second dimension
	 * @param size2
	 *            the size of the array along the third dimension
	 * @param buffer
	 *            the buffer containing the byte values
	 */
	public BufferedUInt8Array3D(int size0, int size1, int size2, byte[] buffer)
	{
		super(size0, size1, size2);
		if (buffer.length < size0 * size1 * size2)
		{
			throw new IllegalArgumentException("Buffer size does not match image dimensions");
		}
		this.buffer = buffer;
	}


	// =============================================================
	// Specialization of the UInt8Array3D interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.scalar2d.UInt8Array3D#getByte(int, int, int)
	 */
	@Override
	public byte getByte(int x, int y, int z)
	{
		int index = x + this.size0 * (y + z * this.size1);
		return this.buffer[index];
	}
		
	/* (non-Javadoc)
	 * @see net.sci.array.data.scalar2d.UInt8Array3D#setByte(int, int, int, byte)
	 */
	@Override
	public void setByte(int x, int y, int z, byte b)
	{
		int index = x + this.size0 * (y + z * this.size1);
		this.buffer[index] = b;
	}

	
	// =============================================================
	// Specialization of the Array interface

	@Override
	public UInt8Array3D duplicate()
	{
		byte[] buffer2 = new byte[size0 * size1 * size2];
		System.arraycopy(this.buffer, 0, buffer2, 0, size0 * size1 * size2);
		return new BufferedUInt8Array3D(size0, size1, size2, buffer2);
	}

	
	// =============================================================
	// Implementation of the Iterator interface

	public UInt8Array.Iterator iterator()
	{
		return new Iterator();
	}
	
	private class Iterator implements UInt8Array.Iterator
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
		public UInt8 next()
		{
			this.index++;
			return new UInt8(buffer[index]);
		}

		@Override
		public void forward()
		{
			this.index++;
		}

		@Override
		public UInt8 get()
		{
			return new UInt8(buffer[index]);
		}

		@Override
		public void set(UInt8 value)
		{
			buffer[index] = value.getByte();
		}
		
		@Override
		public byte getByte()
		{
			return buffer[index];
		}

		@Override
		public void setByte(byte b)
		{
			buffer[index] = b;
		}
	}

}