/**
 * 
 */
package net.sci.array.scalar;

/**
 * Implementation of Float32Array1D that stores inner data in a linear array of
 * floats.
 * 
 * @author dlegland
 *
 */
public class BufferedFloat32Array1D extends Float32Array1D
{
    // =============================================================
    // Static methods

    /**
     * Converts the input array into an instance of the BufferedFloat32Array1D
     * class. May return the input array if it is already an instance of
     * BufferedFloat32Array1D.
     * 
     * @param array
     *            the array to convert
     * @return an instance of BufferedFloat32Array1D containing the same values as
     *         the input array.
     */
    public static final BufferedFloat32Array1D convert(Float32Array1D array)
    {
        // if array is of correct class, simply use class cast
        if (array instanceof BufferedFloat32Array1D)
        {
            return (BufferedFloat32Array1D) array;
        }
        
        // allocate memory
        int sizeX = array.size(0);
        BufferedFloat32Array1D res = new BufferedFloat32Array1D(sizeX);
        
        // copy values
        for (int x = 0; x < sizeX; x++)
        {
            res.setFloat(x, array.getFloat(x));
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
	 */
	public BufferedFloat32Array1D(int size0)
	{
		super(size0);
		this.buffer = new float[size0];
	}

	/**
	 * 
	 * @param size0
	 *            the size of the array along the first dimension
	 * @param buffer
	 *            the buffer containing the values
	 */
	public BufferedFloat32Array1D(int size0, float[] buffer)
	{
		super(size0);
		this.buffer = buffer;
	}


	// =============================================================
	// Implementation of the Float32Array1D interface

    @Override
    public float getFloat(int x)
    {
        return buffer[x];
    }

    @Override
    public void setFloat(int x, float value)
    {
        buffer[x] = value;
    }
    
    @Override
    public void setValue(int x, double value)
    {
        buffer[x] = (float) value;
    }


    // =============================================================
    // Specialization of the Float32Array interface

	@Override
	public float getFloat(int[] pos)
	{
		return buffer[pos[0]];
	}

	@Override
	public void setFloat(int[] pos, float value)
	{
		buffer[pos[0]] = value;
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
	// Implementation of the Array interface

	@Override
	public BufferedFloat32Array1D duplicate()
	{
		float[] buffer2 = new float[size0];
		System.arraycopy(this.buffer, 0, buffer2, 0, size0);
		return new BufferedFloat32Array1D(size0, buffer2);
	}

	@Override
	public Float32Array newInstance(int... dims)
	{
		return Float32Array.create(dims);
	}

	
	// =============================================================
	// Implementation of the Iterable interface

	public Float32Array.Iterator iterator()
	{
		return new Float32Iterator();
	}
	
	private class Float32Iterator implements Float32Array.Iterator
	{
		int index = -1;
		
		public Float32Iterator() 
		{
		}

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
        public Float32 get()
        {
        	return new Float32(buffer[index]);
        }

        @Override
		public boolean hasNext()
		{
			return this.index < (size0 - 1);
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
