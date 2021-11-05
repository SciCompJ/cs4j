/**
 * 
 */
package net.sci.array.scalar;

/**
 * Implementation of Float64Array2D that stores inner data in a linear array of
 * doubles.
 * 
 * @see BufferedFloat32Array2D
 * 
 * @author dlegland
 */
public class BufferedFloat64Array2D extends Float64Array2D
{
    // =============================================================
    // Static methods

    /**
     * Converts the input array into an instance of the BufferedFloat64Array2D
     * class. May return the input array if it is already an instance of
     * BufferedFloat64Array2D.
     * 
     * @param array
     *            the array to convert
     * @return an instance of BufferedFloat64Array2D containing the same values as
     *         the input array.
     */
    public static final BufferedFloat64Array2D convert(Float64Array2D array)
    {
        // if array is of correct class, simply use class cast
        if (array instanceof BufferedFloat64Array2D)
        {
            return (BufferedFloat64Array2D) array;
        }
        
        // allocate memory
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        BufferedFloat64Array2D res = new BufferedFloat64Array2D(sizeX, sizeY);
        
        // copy values
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                res.setValue(x, y, array.getValue(x, y));
            }
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
	 * @param size1
	 *            the size of the array along the second dimension
	 */
	public BufferedFloat64Array2D(int size0, int size1)
	{
		super(size0, size1);
		this.buffer = new double[size0 * size1];
	}

	/**
	 * @param size0
	 *            the size of the array along the first dimension
	 * @param size1
	 *            the size of the array along the second dimension
	 * @param buffer
	 *            the buffer containing the values
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

    /* (non-Javadoc)
     * @see net.sci.array.data.Array2D#setValue(int, int, double)
     */
    @Override
    public void setValue(int x, int y, double value)
    {
        int index = x + y * this.size0;
        this.buffer[index] = value;
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
	public double getValue(int... pos)
	{
        int index = pos[0] + pos[1] * this.size0;
		return this.buffer[index];
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#setValue(int[], double)
	 */
	@Override
    public void setValue(int[] pos, double value)
	{
		int index = pos[0] + pos[1] * this.size0;
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
