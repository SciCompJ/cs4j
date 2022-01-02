/**
 * 
 */
package net.sci.array.scalar;

/**
 * Implementation of Int32Array3D that stores inner data in a linear array of
 * int values.
 * 
 * This implementation is limited by the total number of elements, that must be
 * less than maximum array index in java (in the order of 2^31).
 *
 * @see SlicedInt32Array3D
 * 
 * @author dlegland
 *
 */
public class BufferedInt32Array3D extends Int32Array3D
{
    // =============================================================
    // Static methods

    /**
     * Converts the input array into an instance of the BufferedInt32Array3D
     * class. May return the input array if it is already an instance of
     * BufferedInt32Array3D.
     * 
     * @param array
     *            the array to convert
     * @return an instance of BufferedInt32Array3D containing the same values
     *         as the input array.
     */
    public static final BufferedInt32Array3D convert(Int32Array3D array)
    {
        // if array is of correct class, simply use class cast
        if (array instanceof BufferedInt32Array3D)
        {
            return (BufferedInt32Array3D) array;
        }
        
        // allocate memory
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
        BufferedInt32Array3D res = new BufferedInt32Array3D(sizeX, sizeY, sizeZ);
        
        // copy values
        for (int z = 0; z < sizeZ; z++)
        {
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    res.setInt(x, y, z, array.getInt(x, y, z));
                }
            }
        }
        // return converted array
        return res;
    }
    
    
    // =============================================================
    // Class fields

    /**
     * The array of ints that stores array values.
     */
	int[] buffer;

	
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
	public BufferedInt32Array3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
		this.buffer = new int[size0 * size1 * size2];
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
	 *            the buffer containing the integer values
	 */
	public BufferedInt32Array3D(int size0, int size1, int size2, int[] buffer)
	{
		super(size0, size1, size2);
		if (buffer.length < size0 * size1 * size2)
		{
			throw new IllegalArgumentException("Buffer size does not match image dimensions");
		}
		this.buffer = buffer;
	}

	// =============================================================
	// Specialization of the IntArray3D interface

    @Override
    public void setInt(int x, int y, int z, int value)
    {
        int index = x + this.size0 * (y + z * this.size1);
        this.buffer[index] = value;
    }
    
	@Override
	public int getInt(int... pos)
	{
		int index = pos[0] + this.size0 * (pos[1] + pos[2] * this.size1);
		return this.buffer[index];
	}

	@Override
	public void setInt(int[] pos, int value)
	{
		int index = pos[0] + this.size0 * (pos[1] + pos[2] * this.size1);
		this.buffer[index] = value;
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
	// Specialization of the Array interface

	@Override
	public Int32Array3D duplicate()
	{
		int[] buffer2 = new int[size0 * size1 * size2];
		System.arraycopy(this.buffer, 0, buffer2, 0, size0 * size1 * size2);
		return new BufferedInt32Array3D(size0, size1, size2, buffer2);
	}

	
	// =============================================================
	// Implementation of the Iterator interface

	public Int32Array.Iterator iterator()
	{
		return new Int32Iterator();
	}
	
	private class Int32Iterator implements Int32Array.Iterator
	{
		int index = -1;
		
		public Int32Iterator() 
		{
		}
		
		@Override
		public boolean hasNext()
		{
			return this.index < (size0 * size1 * size2 - 1);
		}

		@Override
		public Int32 next()
		{
			this.index++;
			return new Int32(buffer[index]);
		}

		@Override
		public void forward()
		{
			this.index++;
		}

		@Override
		public Int32 get()
		{
			return new Int32(buffer[index]);
		}

		@Override
		public int getInt()
		{
			return buffer[index];
		}

		@Override
		public void setInt(int value)
		{
			buffer[index] = value;
		}
	}
}
