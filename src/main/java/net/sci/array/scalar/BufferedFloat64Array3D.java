/**
 * 
 */
package net.sci.array.scalar;

/**
 * Implementation of Float64Array3D that stores inner data in a linear array of
 * int values.
 * 
 * This implementation is limited by the total number of elements, that must be
 * less than maximum array index in java (in the order of 2^31).
 *
 * @see SlicedFloat64Array3D
 * 
 * @author dlegland
 *
 */
public class BufferedFloat64Array3D extends Float64Array3D
{
    // =============================================================
    // Static methods

    /**
     * Converts the input array into an instance of the BufferedFloat64Array3D
     * class. May return the input array if it is already an instance of
     * BufferedFloat64Array3D.
     * 
     * @param array
     *            the array to convert
     * @return an instance of BufferedFloat64Array3D containing the same values
     *         as the input array.
     */
    public static final BufferedFloat64Array3D convert(Float64Array3D array)
    {
        // if array is of correct class, simply use class cast
        if (array instanceof BufferedFloat64Array3D)
        {
            return (BufferedFloat64Array3D) array;
        }
        
        // allocate memory
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
        BufferedFloat64Array3D res = new BufferedFloat64Array3D(sizeX, sizeY, sizeZ);
        
        // copy values
        for (int z = 0; z < sizeZ; z++)
        {
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    res.setValue(x, y, z, array.getValue(x, y, z));
                }
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
    public void setValue(int x, int y, int z, double value)
    {
        int index = (z * this.size1 + y) * this.size0 + x;
        this.buffer[index] = value;
    }

	@Override
	public double getValue(int... pos)
	{
	    int index = (pos[2] * this.size1 + pos[1]) * this.size0 + pos[0];
        return this.buffer[index];
	}

	@Override
	public void setValue(int[] pos, double value)
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
