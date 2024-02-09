/**
 * 
 */
package net.sci.array.vector;

/**
 * Implementation of Float32VectorArray2D based on an inner buffer of float.
 * 
 * @author dlegland
 *
 */
public class BufferedFloat32VectorArray2D extends Float32VectorArray2D
{
	// =============================================================
	// Class members

	float[] buffer;
	
	int vectorLength;
	
	
	// =============================================================
	// Constructors

	/**
	 * Initialize a new array of vectors.
	 * 
	 * @param size0 array size in the first dimension
	 * @param size1 array size in the second dimension
	 * @param sizeV number of components of vectors
	 */
	public BufferedFloat32VectorArray2D(int size0, int size1, int sizeV)
	{
		super(size0, size1);
		this.vectorLength = sizeV;
		this.buffer = new float[size0 * size1 * sizeV];
	}

	/**
	 * Initialize a new array of vectors, using the specified buffer.
	 * 
	 * @param size0
	 *            array size in the first dimension
	 * @param size1
	 *            array size in the second dimension
	 * @param sizeV
	 *            number of components of vectors
	 * @param buffer
	 *            the buffer containing the float values
	 */
	public BufferedFloat32VectorArray2D(int size0, int size1, int sizeV, float[] buffer)
	{
		super(size0, size1);
		this.vectorLength = sizeV;
		if (buffer.length < size0 * size1 * sizeV)
		{
			throw new IllegalArgumentException("Buffer size does not match array dimensions");
		}
		this.buffer = buffer;
	}

	
	// =============================================================
	// Implementation of the VectorArray2D interface

	@Override
    public float getFloat(int x, int y, int c)
    {
        int offset = (y * this.size0 + x) * this.vectorLength;
        return this.buffer[offset + c];
    }

    @Override
    public void setFloat(int x, int y, int c, float value)
    {
        int offset = (y * this.size0 + x) * this.vectorLength;
        this.buffer[offset + c] = value;
    }
    
    @Override
    public double getValue(int x, int y, int c)
    {
        int offset = (y * this.size0 + x) * this.vectorLength;
        return this.buffer[offset + c];
    }


    @Override
    public void setValue(int x, int y, int c, double value)
    {
        int offset = (y * this.size0 + x) * this.vectorLength;
        this.buffer[offset + c] = (float) value;
    }


    // =============================================================
    // Implementation of the VectorArray interface

    /* (non-Javadoc)
	 * @see net.sci.array.data.VectorArray#getVectorLength()
	 */
	@Override
	public int channelCount()
	{
		return this.vectorLength;
	}

	
	// =============================================================
	// Implementation of the Array interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.VectorArray#newInstance(int[])
	 */
	@Override
	public Float32VectorArray newInstance(int... dims)
	{
		return Float32VectorArray.create(dims, this.vectorLength);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.vector.VectorArray2D#duplicate()
	 */
	@Override
	public Float32VectorArray2D duplicate()
	{
		float[] buffer2 = new float[buffer.length];
		int n = this.size0 * this.size1 * this.vectorLength;
		System.arraycopy(this.buffer, 0, buffer2, 0, n);
		return new BufferedFloat32VectorArray2D(this.size0, this.size1, this.vectorLength, buffer2);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.vector.VectorArray2D#getValues(int, int)
	 */
	@Override
	public double[] getValues(int x, int y)
	{
		double[] res = new double[this.vectorLength];
		return getValues(x, y, res);
	}

	@Override
    public double[] getValues(int x, int y, double[] values)
    {
	    int offset = (y * this.size0 + x) * this.vectorLength;
	    for (int c = 0; c < this.vectorLength; c++)
        {
            values[c] = this.buffer[offset + c];
        }
        return values;
    }

    /* (non-Javadoc)
	 * @see net.sci.array.data.vector.VectorArray2D#setValues(int, int, double[])
	 */
	@Override
	public void setValues(int x, int y, double[] values)
	{
		int offset = (y * this.size0 + x) * this.vectorLength;
		for (int c = 0; c < this.vectorLength; c++)
		{
			this.buffer[offset + c] = (float) values[c];
		}
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#get(int, int)
	 */
	@Override
	public Float32Vector get(int[] pos)
	{
		return new Float32Vector(getValues(pos));
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.VectorArray#iterator()
	 */
	@Override
	public Float32VectorArray.Iterator iterator()
	{
		return new Iterator();
	}

	private class Iterator implements Float32VectorArray.Iterator
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
		public Float32Vector next()
		{
			this.index++;
			float[] vals = new float[vectorLength];
			int offset = index * vectorLength;
			System.arraycopy(buffer, offset, vals, 0, vectorLength);
			return new Float32Vector(vals);
		}

		@Override
		public void forward()
		{
			this.index++;
		}

		@Override
		public double getValue(int c)
		{
			int ind = index * vectorLength + c;
			return buffer[ind];
		}

        @Override
        public double[] getValues(double[] values)
        {
            int ind = index * vectorLength;
            for (int c = 0; c < vectorLength; c++)
            {
                values[c] = buffer[ind + c];
            }
            return values;
        }

		@Override
		public void setValue(int c, double value)
		{
			int ind = index * vectorLength + c;
			buffer[ind] = (float) value;
		}

		@Override
		public Float32Vector get()
		{
			float[] vals = new float[vectorLength];
			int offset = index * vectorLength;
			System.arraycopy(buffer, offset, vals, 0, vectorLength);
			return new Float32Vector(vals);
		}

		@Override
		public void set(Float32Vector vect)
		{
			float[] vals = vect.getFloats();
			int offset = index * vectorLength;
			System.arraycopy(vals, 0, buffer, offset, vectorLength);
		}
	}
}
