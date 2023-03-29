/**
 * 
 */
package net.sci.array.scalar;

/**
 * Implementation of Float32Array2D that stores inner data in a linear array of
 * floats.
 * 
 * @see BufferedFloat64Array2D
 * 
 * @author dlegland
 */
public class BufferedFloat32Array2D extends Float32Array2D
{
    // =============================================================
    // Static methods

    /**
     * Converts the input array into an instance of the BufferedFloat32Array2D
     * class. May return the input array if it is already an instance of
     * BufferedFloat32Array2D.
     * 
     * @param array
     *            the array to convert
     * @return an instance of BufferedFloat32Array2D containing the same values as
     *         the input array.
     */
    public static final BufferedFloat32Array2D convert(Float32Array2D array)
    {
        // if array is of correct class, simply use class cast
        if (array instanceof BufferedFloat32Array2D)
        {
            return (BufferedFloat32Array2D) array;
        }
        
        // allocate memory
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        BufferedFloat32Array2D res = new BufferedFloat32Array2D(sizeX, sizeY);
        
        // copy values
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                res.setFloat(x, y, array.getFloat(x, y));
            }
        }
        
        // return converted array
        return res;
    }
    
    
	// =============================================================
	// Class fields

    /**
     * The array of floats that stores array values.
     */
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

    public float getFloat(int x, int y)
    {
        int index = x + y * this.size0;
        return this.buffer[index];
    }
    
    public void setFloat(int x, int y, float value)
    {
        int index = x + y * this.size0;
        this.buffer[index] = value;
    }
    
    public float getFloat(int[] pos)
    {
        int index = pos[0] + pos[1] * this.size0;
        return this.buffer[index];
    }

    public void setFloat(int[] pos, float value)
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
	public double getValue(int[] pos)
	{
		int index = pos[0] + pos[1] * this.size0;
		return this.buffer[index];
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#setValue(int, int, double)
	 */
	@Override
	public void setValue(int[] pos, double value)
	{
        int index = pos[0] + pos[1] * this.size0;
		this.buffer[index] = (float) value;
	}

    
    // =============================================================
    // Specialization of the ScalarArray interface
    
    @Override
    public Iterable<Double> values()
    {
        return new Iterable<Double>()
        {
            @Override
            public java.util.Iterator<Double> iterator()
            {
                return new ValueIterator();
            }
        };
    }
    
    /**
     * Inner implementation of iterator on double values.
     */
    private class ValueIterator implements java.util.Iterator<Double>
    {
        int index = -1;
        
        @Override
        public boolean hasNext()
        {
            return this.index < (buffer.length - 1);
        }

        @Override
        public Double next()
        {
            this.index++;
            return (double) buffer[index];
        }
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
		
        @Override
        public float getFloat()
        {
            return buffer[index];
        }

        @Override
        public void setFloat(float value)
        {
            buffer[index] = value;
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
		
		@Override
        public Float32 get()
        {
        	return new Float32(buffer[index]);
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
	}
}
