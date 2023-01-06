/**
 * 
 */
package net.sci.array.scalar;

/**
 * Implementation of Float64Array1D that stores inner data in a linear array of
 * doubles.
 * 
 * @author dlegland
 *
 */
public class BufferedFloat64Array1D extends Float64Array1D
{
    // =============================================================
    // Static methods

    /**
     * Converts the input array into an instance of the BufferedFloat64Array1D
     * class. May return the input array if it is already an instance of
     * BufferedFloat64Array1D.
     * 
     * @param array
     *            the array to convert
     * @return an instance of BufferedFloat64Array1D containing the same values as
     *         the input array.
     */
    public static final BufferedFloat64Array1D convert(Float64Array1D array)
    {
        // if array is of correct class, simply use class cast
        if (array instanceof BufferedFloat64Array1D)
        {
            return (BufferedFloat64Array1D) array;
        }
        
        // allocate memory
        int sizeX = array.size(0);
        BufferedFloat64Array1D res = new BufferedFloat64Array1D(sizeX);
        
        // copy values
        for (int x = 0; x < sizeX; x++)
        {
            res.setValue(x, array.getValue(x));
        }
        
        // return converted array
        return res;
    }
    
    
	// =============================================================
	// Class fields

    /**
     * The array of doubles that stores array values.
     */
    double[] buffer;

	
	// =============================================================
	// Constructors

	/**
	 * @param size0
	 *            the size of the array along the first dimension
	 */
	public BufferedFloat64Array1D(int size0)
	{
		super(size0);
		this.buffer = new double[size0];
	}

	/**
	 * 
	 * @param size0
	 *            the size of the array along the first dimension
	 * @param buffer
	 *            the buffer containing the values
	 */
	public BufferedFloat64Array1D(int size0, double[] buffer)
	{
		super(size0);
		this.buffer = buffer;
	}


	// =============================================================
	// Implementation of the Float64Array1D interface

    @Override
    public double getValue(int x)
    {
        return buffer[x];
    }

    @Override
    public void setValue(int x, double value)
    {
        buffer[x] = (double) value;
    }


    // =============================================================
    // Specialization of the Float64Array interface

	@Override
	public double getValue(int... pos)
	{
		return buffer[pos[0]];
	}

	@Override
	public void setValue(int[] pos, double value)
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
	public BufferedFloat64Array1D duplicate()
	{
		double[] buffer2 = new double[size0];
		System.arraycopy(this.buffer, 0, buffer2, 0, size0);
		return new BufferedFloat64Array1D(size0, buffer2);
	}

	@Override
	public Float64Array newInstance(int... dims)
	{
		return Float64Array.create(dims);
	}

	
	// =============================================================
	// Implementation of the Iterable interface

	public Float64Array.Iterator iterator()
	{
		return new Float64Iterator();
	}
	
	private class Float64Iterator implements Float64Array.Iterator
	{
		int index = -1;
		
		public Float64Iterator() 
		{
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

        @Override
        public Float64 get()
        {
        	return new Float64(buffer[index]);
        }

        @Override
		public boolean hasNext()
		{
			return this.index < (size0 - 1);
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
	}
}
