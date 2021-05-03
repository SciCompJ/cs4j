/**
 * 
 */
package net.sci.array.scalar;

import java.nio.FloatBuffer;

/**
 * Implements a 2D array of Float32 by relying on a FloatBuffer instance.
 * 
 * @see java.nio.FloatBuffer
 * 
 * @author dlegland
 */
public class FloatBufferFloat32Array2D extends Float32Array2D
{
	// =============================================================
	// Class fields

	FloatBuffer buffer;
	
	
	// =============================================================
	// Constructors

	/**
	 * @param size0
	 *            the size of the array along the first dimension
	 * @param size1
	 *            the size of the array along the second dimension
	 * @param buffer
	 *            the buffer containing the values
	 */
	public FloatBufferFloat32Array2D(int size0, int size1, FloatBuffer buffer)
	{
		super(size0, size1);
		if (buffer.limit() < size0 * size1)
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
        return this.buffer.get(index);
    }

    public void setFloat(int x, int y, float value)
    {
        int index = x + y * this.size0;
        this.buffer.put(index, value);
    }
    
    public void setFloat(int[] pos, float value)
    {
        int index = pos[0] + pos[1] * this.size0;
        this.buffer.put(index, value);
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
		return this.buffer.get(index);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#setValue(int, int, double)
	 */
	@Override
	public void setValue(int[] pos, double value)
	{
        int index = pos[0] + pos[1] * this.size0;
        this.buffer.put(index, (float) value);
	}

	
    // =============================================================
    // Implementation of Array interface
	
    @Override
    public Float32Array2D duplicate()
    {
        float[] buffer2 = new float[size0 * size1];
        this.buffer.get(buffer2);
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
			return new Float32(buffer.get(index));
		}

		@Override
		public void forward()
		{
			this.index++;
		}

		@Override
		public Float32 get()
		{
			return new Float32(buffer.get(index));
		}

		@Override
		public double getValue()
		{
			return buffer.get(index);
		}

		@Override
		public void setValue(double value)
		{
			buffer.put(index, (float) value);
		}
	}
}
