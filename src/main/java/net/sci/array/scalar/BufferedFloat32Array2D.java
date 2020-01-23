/**
 * 
 */
package net.sci.array.scalar;

/**
 * @author dlegland
 *
 */
public class BufferedFloat32Array2D extends Float32Array2D
{
	// =============================================================
	// Class fields

	float[] buffer;
	
	// =============================================================
	// Constructors

	/**
	 * @param size0
	 *            the size of the array along the first dimension
	 * @param size1
	 *            the size of the array along the second dimension
	 */
	public BufferedFloat32Array2D(int size0, int size1)
	{
		super(size0, size1);
		this.buffer = new float[size0 * size1];
	}

	/**
	 * @param size0
	 *            the size of the array along the first dimension
	 * @param size1
	 *            the size of the array along the second dimension
	 * @param buffer
	 *            the buffer containing the values
	 */
	public BufferedFloat32Array2D(int size0, int size1, float[] buffer)
	{
		super(size0, size1);
		if (buffer.length < size0 * size1)
		{
			throw new IllegalArgumentException("Buffer size does not match image dimensions");
		}
		this.buffer = buffer;
	}

    // =============================================================
    // Specialization of FloatArray2D

    public float getFloat(int... pos)
    {
        int index = pos[0] + pos[1] * this.size0;
        return this.buffer[index];
    }

    public void setFloat(float value, int... pos)
    {
        int index = pos[0] + pos[1] * this.size0;
        this.buffer[index] = value;
    }
    

    // =============================================================
    // Specialization of Array2D

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#getValue(int, int)
	 */
	@Override
	public double getValue(int... pos)
	{
		int index = pos[0] + pos[1] * this.size0;
		return this.buffer[index];
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#setValue(int, int, double)
	 */
	@Override
	public void setValue(double value, int... pos)
	{
        int index = pos[0] + pos[1] * this.size0;
		this.buffer[index] = (float) value;
	}

    // =============================================================
    // Implementation of Array interface

    @Override
    public Float32Array2D duplicate()
    {
        float[] buffer2 = new float[size0 * size1];
        System.arraycopy(this.buffer, 0, buffer2, 0, size0 * size1);
        return new BufferedFloat32Array2D(size0, size1, buffer2);
    }

	/* (non-Javadoc)
	 * @see net.sci.array.data.ScalarArray#iterator()
	 */
	@Override
	public Float32Array.Iterator iterator()
	{
		return new FloatIterator();
	}

	private class FloatIterator implements Float32Array.Iterator
	{
		int index = -1;
		
		public FloatIterator() 
		{
		}
		
		@Override
		public boolean hasNext()
		{
			return this.index < (size0 * size1 - 1);
		}

		@Override
		public Float32 next()
		{
			this.index++;
			return new Float32(buffer[index]);
		}

		@Override
		public void forward()
		{
			this.index++;
		}

		@Override
		public Float32 get()
		{
			return new Float32(buffer[index]);
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
