/**
 * 
 */
package net.sci.array.data.scalar2d;

import net.sci.array.data.Float64Array;
import net.sci.array.type.Float64;

/**
 * @author dlegland
 *
 */
public class BufferedFloat64Array2D extends Float64Array2D
{
	// =============================================================
	// Class fields

	double[] buffer;
	
	// =============================================================
	// Constructors

	/**
	 * @param size0
	 * @param size1
	 */
	public BufferedFloat64Array2D(int size0, int size1)
	{
		super(size0, size1);
		this.buffer = new double[size0 * size1];
	}

	/**
	 * @param size0
	 * @param size1
	 */
	public BufferedFloat64Array2D(int size0, int size1, double[] buffer)
	{
		super(size0, size1);
		if (buffer.length < size0 * size1)
		{
			throw new IllegalArgumentException("Buffer size does not match image dimensions");
		}
		this.buffer = buffer;
	}

	@Override
	public Float64Array2D duplicate()
	{
		double[] buffer2 = new double[size0 * size1];
		System.arraycopy(this.buffer, 0, buffer2, 0, size0 * size1);
		return new BufferedFloat64Array2D(size0, size1, buffer2);
	}
	
	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#getValue(int, int)
	 */
	@Override
	public double getValue(int x, int y)
	{
		int index = x + y * this.size0;
		return this.buffer[index];
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#setValue(int, int, double)
	 */
	@Override
	public void setValue(int x, int y, double value)
	{
		int index = x + y * this.size0;
		this.buffer[index] = value;
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.ScalarArray#iterator()
	 */
	@Override
	public Float64Array.Iterator iterator()
	{
		return new DoubleIterator();
	}

	private class DoubleIterator implements Float64Array.Iterator
	{
		int index = -1;
		
		public DoubleIterator() 
		{
		}
		
		@Override
		public boolean hasNext()
		{
			return this.index < (size0 * size1 - 1);
		}

		@Override
		public Float64 next()
		{
			this.index++;
			return new Float64(buffer[index]);
		}

		@Override
		public void forward()
		{
			this.index++;
		}

		@Override
		public Float64 get()
		{
			return new Float64(buffer[index]);
		}

		@Override
		public double getValue()
		{
			return buffer[index];
		}

		@Override
		public void setValue(double value)
		{
			buffer[index] = value;
		}
	}
}
