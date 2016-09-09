/**
 * 
 */
package net.sci.array.data.scalar3d;

import net.sci.array.data.FloatArray;
import net.sci.array.type.Float;

/**
 * @author dlegland
 *
 */
public class BufferedFloatArray3D extends FloatArray3D
{
	// =============================================================
	// Class fields

	float[] buffer;

	
	// =============================================================
	// Constructors

	/**
	 */
	public BufferedFloatArray3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
		this.buffer = new float[size0 * size1 * size2];
	}

	public BufferedFloatArray3D(int size0, int size1, int size2, float[] buffer)
	{
		super(size0, size1, size2);
		if (buffer.length < size0 * size1 * size2)
		{
			throw new IllegalArgumentException("Buffer size does not match image dimensions");
		}
		this.buffer = buffer;
	}


	// =============================================================
	// Implementation of the Array3D class

	@Override
	public double getValue(int x, int y, int z)
	{
		int index = x + this.size0 * (y + z * this.size1);
		return this.buffer[index];
	}

	@Override
	public void setValue(int x, int y, int z, double value)
	{
		int index = x + this.size0 * (y + z * this.size1);
		this.buffer[index] = (float) value;
	}

	
	// =============================================================
	// Specialization of the Array interface

	@Override
	public FloatArray3D duplicate()
	{
		float[] buffer2 = new float[size0 * size1 * size2];
		System.arraycopy(this.buffer, 0, buffer2, 0, size0 * size1 * size2);
		return new BufferedFloatArray3D(size0, size1, size2, buffer2);
	}

	
	// =============================================================
	// Implementation of the Iterator interface

	public FloatArray.Iterator iterator()
	{
		return new FloatIterator();
	}
	
	private class FloatIterator implements FloatArray.Iterator
	{
		int index = -1;
		
		public FloatIterator() 
		{
		}
		
		@Override
		public boolean hasNext()
		{
			return this.index < (size0 * size1 * size2 - 1);
		}

		@Override
		public Float next()
		{
			this.index++;
			return new Float(buffer[index]);
		}

		@Override
		public void forward()
		{
			this.index++;
		}

		@Override
		public Float get()
		{
			return new Float(buffer[index]);
		}

		@Override
		public double getValue()
		{
			return buffer[index];
		}

		@Override
		public void setValue(double value)
		{
			buffer[index] = (float) value;
		}
	}
}
