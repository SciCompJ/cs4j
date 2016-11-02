/**
 * 
 */
package net.sci.array.data.scalar2d;

import net.sci.array.data.UInt8Array;
import net.sci.array.type.UInt8;

/**
 * Implements UInt8Array by storing data in a linear byte buffer.
 * 
 * @author dlegland
 *
 */
public class BufferedUInt8Array2D extends UInt8Array2D
{
	// =============================================================
	// Class fields

	byte[] buffer;

	
	// =============================================================
	// Constructors

	/**
	 * @param size0
	 * @param size1
	 */
	public BufferedUInt8Array2D(int size0, int size1)
	{
		super(size0, size1);
		this.buffer = new byte[size0 * size1];
	}

	public BufferedUInt8Array2D(int size0, int size1, byte[] buffer)
	{
		super(size0, size1);
		if (buffer.length < size0 * size1)
		{
			throw new IllegalArgumentException("Buffer size does not match image dimensions");
		}
		this.buffer = buffer;
	}


	// =============================================================
	// Implementation of the UInt8Array2D interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.scalar2d.UInt8Array2D#getByte(int, int)
	 */
	@Override
	public byte getByte(int x, int y)
	{
		int index = x + y * this.size0;
		return this.buffer[index];
	}
	
	/* (non-Javadoc)
	 * @see net.sci.array.data.scalar2d.UInt8Array2D#setByte(int, int, byte)
	 */
	@Override
	public void setByte(int x, int y, byte b)
	{
		int index = x + y * this.size0;
		this.buffer[index] = b;
	}


	// =============================================================
	// Implementation of the Array interface

	@Override
	public UInt8Array2D duplicate()
	{
		byte[] buffer2 = new byte[size0 * size1];
		System.arraycopy(this.buffer, 0, buffer2, 0, size0 * size1);
		return new BufferedUInt8Array2D(size0, size1, buffer2);
	}

	
	// =============================================================
	// Implementation of the Iterable interface

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
			return this.index < (size0 * size1 - 1);
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
