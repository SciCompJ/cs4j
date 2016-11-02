/**
 * 
 */
package net.sci.array.data.scalar2d;

import net.sci.array.data.BooleanArray;
import net.sci.array.type.Boolean;

/**
 * @author dlegland
 *
 */
public class BufferedBooleanArray2D extends BooleanArray2D
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
	public BufferedBooleanArray2D(int size0, int size1)
	{
		super(size0, size1);
		this.buffer = new boolean[size0 * size1];
	}

	public BufferedBooleanArray2D(int size0, int size1, boolean[] buffer)
	{
		super(size0, size1);
		if (buffer.length < size0 * size1)
		{
			throw new IllegalArgumentException("Buffer size does not match image dimensions");
		}
		this.buffer = buffer;
	}

	// =============================================================
	// Implementation of the BooleanArray2D interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.scalar2d.BooleanArray2D#getState(int, int)
	 */
	@Override
	public boolean getState(int x, int y)
	{
		int index = x + y * this.size0;
		return buffer[index];
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.scalar2d.BooleanArray2D#setState(int, int, boolean)
	 */
	@Override
	public void setState(int x, int y, boolean state)
	{
		int index = x + y * this.size0;
		buffer[index] = state;
	}

	// =============================================================
	// Implementation of the Array interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.scalar2d.BooleanArray2D#duplicate()
	 */
	@Override
	public BooleanArray2D duplicate()
	{
		int n = this.size0 * this.size1;
		boolean[] buffer2 = new boolean[n];
		System.arraycopy(this.buffer, 0, buffer2, 0, n);
		return new BufferedBooleanArray2D(this.size0, this.size1, buffer2);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.BooleanArray#iterator()
	 */
	@Override
	public net.sci.array.data.BooleanArray.Iterator iterator()
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
			return this.index < (size0 * size1 - 1);
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
		public void set(Boolean b)
		{
			buffer[index] = b.getState();
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
