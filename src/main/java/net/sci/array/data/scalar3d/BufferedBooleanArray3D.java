/**
 * 
 */
package net.sci.array.data.scalar3d;

import net.sci.array.data.BooleanArray;
import net.sci.array.type.Boolean;

/**
 * @author dlegland
 *
 */
public class BufferedBooleanArray3D extends BooleanArray3D
{
	// =============================================================
	// Class fields

	boolean[] buffer;

	
	// =============================================================
	// Constructors

	/**
	 * @param size0
	 * @param size1
	 */
	public BufferedBooleanArray3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
		this.buffer = new boolean[size0 * size1 * size2];
	}

	public BufferedBooleanArray3D(int size0, int size1, int size2, boolean[] buffer)
	{
		super(size0, size1, size2);
		if (buffer.length < size0 * size1 * size2)
		{
			throw new IllegalArgumentException("Buffer size does not match image dimensions");
		}
		this.buffer = buffer;
	}


	// =============================================================
	// Specialization of the BooleanArray3D interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.scalar2d.BooleanArray3D#getState(int, int, int)
	 */
	@Override
	public boolean getState(int x, int y, int z)
	{
		int index = x + this.size0 * (y + z * this.size1);
		return this.buffer[index];
	}
		
	/* (non-Javadoc)
	 * @see net.sci.array.data.scalar2d.BooleanArray3D#setState(int, int, int, boolean)
	 */
	@Override
	public void setState(int x, int y, int z, boolean b)
	{
		int index = x + this.size0 * (y + z * this.size1);
		this.buffer[index] = b;
	}

	
	// =============================================================
	// Specialization of the Array interface

	@Override
	public BooleanArray3D duplicate()
	{
		boolean[] buffer2 = new boolean[size0 * size1 * size2];
		System.arraycopy(this.buffer, 0, buffer2, 0, size0 * size1 * size2);
		return new BufferedBooleanArray3D(size0, size1, size2, buffer2);
	}

	
	// =============================================================
	// Implementation of the Iterator interface

	public BooleanArray.Iterator iterator()
	{
		return new BooleanIterator();
	}
	
	private class BooleanIterator implements BooleanArray.Iterator
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
		public Boolean next()
		{
			this.index++;
			return new Boolean(buffer[index]);
		}

		@Override
		public void forward()
		{
			this.index++;
		}

		@Override
		public Boolean get()
		{
			return new Boolean(buffer[index]);
		}

		@Override
		public boolean getState()
		{
			return buffer[index];
		}

		@Override
		public void setState(boolean b)
		{
			buffer[index] = b;
		}
	}

}
