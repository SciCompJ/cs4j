/**
 * 
 */
package net.sci.array.scalar;

/**
 * @author dlegland
 *
 */
public class BufferedFloat64Array3D extends Float64Array3D
{
	// =============================================================
	// Class fields

	double[] buffer;

	
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
	public BufferedFloat64Array3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
		this.buffer = new double[size0 * size1 * size2];
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
	 *            the buffer containing the double values
	 */
	public BufferedFloat64Array3D(int size0, int size1, int size2, double[] buffer)
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
	public double getValue(int... pos)
	{
	    int index = (pos[2] * this.size1 + pos[1]) * this.size0 + pos[0];
        return this.buffer[index];
	}

	@Override
	public void setValue(double value, int... pos)
    {
	    int index = (pos[2] * this.size1 + pos[1]) * this.size0 + pos[0];
        this.buffer[index] = value;
	}

	
	// =============================================================
	// Specialization of the Array interface

	@Override
	public Float64Array3D duplicate()
	{
		double[] buffer2 = new double[size0 * size1 * size2];
		System.arraycopy(this.buffer, 0, buffer2, 0, size0 * size1 * size2);
		return new BufferedFloat64Array3D(size0, size1, size2, buffer2);
	}

	
	// =============================================================
	// Implementation of the Iterator interface

	public Float64Array.Iterator iterator()
	{
		return new FloatIterator();
	}
	
	private class FloatIterator implements Float64Array.Iterator
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
