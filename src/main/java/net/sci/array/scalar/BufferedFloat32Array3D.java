/**
 * 
 */
package net.sci.array.scalar;

/**
 * @author dlegland
 *
 */
public class BufferedFloat32Array3D extends Float32Array3D
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
	 * @param size2
	 *            the size of the array along the third dimension
	 */
	public BufferedFloat32Array3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
		this.buffer = new float[size0 * size1 * size2];
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
	 *            the buffer containing the float values
	 */
	public BufferedFloat32Array3D(int size0, int size1, int size2, float[] buffer)
	{
		super(size0, size1, size2);
		if (buffer.length < size0 * size1 * size2)
		{
			throw new IllegalArgumentException("Buffer size does not match image dimensions");
		}
		this.buffer = buffer;
	}

    // =============================================================
    // Specialization of FloatArray3D

    @Override
    public void setFloat(int x, int y, int z, float f)
    {
        int index = x + this.size0 * (y + z * this.size1);
        this.buffer[index] = f;
    }

    public float getFloat(int... pos)
    {
        int index = pos[0] + this.size0 * (pos[1] + pos[2] * this.size1);
        return this.buffer[index];
    }

    public void setFloat(int[] pos, float value)
    {
        int index = pos[0] + this.size0 * (pos[1] + pos[2] * this.size1);
        this.buffer[index] = value;
    }
    

    // =============================================================
    // Specialization of the Array3D class

	@Override
	public double getValue(int... pos)
	{
		int index = pos[0] + this.size0 * (pos[1] + pos[2] * this.size1);
		return this.buffer[index];
	}

	@Override
	public void setValue(int[] pos, double value)
    {
	    int index = pos[0] + this.size0 * (pos[1] + pos[2] * this.size1);
        this.buffer[index] = (float) value;
	}

	
	// =============================================================
	// Specialization of the Array interface

	@Override
	public Float32Array3D duplicate()
	{
		float[] buffer2 = new float[size0 * size1 * size2];
		System.arraycopy(this.buffer, 0, buffer2, 0, size0 * size1 * size2);
		return new BufferedFloat32Array3D(size0, size1, size2, buffer2);
	}

	
	// =============================================================
	// Implementation of the Iterator interface

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
			return this.index < (size0 * size1 * size2 - 1);
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
