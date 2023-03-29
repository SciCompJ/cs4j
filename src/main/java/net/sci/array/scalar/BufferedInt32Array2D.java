/**
 * 
 */
package net.sci.array.scalar;

/**
 * Implementation of Int32Array2D that stores inner data in a linear array of
 * ints.
 * 
 * @author dlegland
 *
 */
public class BufferedInt32Array2D extends Int32Array2D
{
    // =============================================================
    // Static methods

    /**
     * Converts the input array into an instance of the BufferedInt32Array2D
     * class. May return the input array if it is already an instance of
     * BufferedInt32Array2D.
     * 
     * @param array
     *            the array to convert
     * @return an instance of BufferedInt32Array2D containing the same values as
     *         the input array.
     */
    public static final BufferedInt32Array2D convert(Int32Array2D array)
    {
        // if array is of correct class, simply use class cast
        if (array instanceof BufferedInt32Array2D)
        {
            return (BufferedInt32Array2D) array;
        }
        
        // allocate memory
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        BufferedInt32Array2D res = new BufferedInt32Array2D(sizeX, sizeY);
        
        // copy values
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                res.setInt(x, y, array.getInt(x, y));
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
	 */
	public BufferedInt32Array2D(int size0, int size1)
	{
		super(size0, size1);
		this.buffer = new int[size0 * size1];
	}

	/**
	 * 
	 * @param size0
	 *            the size of the array along the first dimension
	 * @param size1
	 *            the size of the array along the second dimension
	 * @param buffer
	 *            the buffer containing the values
	 */
	public BufferedInt32Array2D(int size0, int size1, int[] buffer)
	{
		super(size0, size1);
		if (buffer.length < size0 * size1)
		{
			throw new IllegalArgumentException("Buffer size does not match image dimensions");
		}
		this.buffer = buffer;
	}


	// =============================================================
	// Implementation of the IntArray2D interface

    @Override
    public int getInt(int x, int y)
    {
        int index = x + this.size0 * y;
        return this.buffer[index];
    }

    @Override
    public void setInt(int x, int y, int value)
    {
        int index = y * size0 + x;
        buffer[index] = value;
    }

	@Override
	public int getInt(int[] pos)
	{
		int index = pos[1] * size0 + pos[0];
		return buffer[index];
	}

	@Override
	public void setInt(int[] pos, int value)
	{
		int index = pos[1] * size0 + pos[0];
		buffer[index] = value;
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
	public BufferedInt32Array2D duplicate()
	{
		int[] buffer2 = new int[size0 * size1];
		System.arraycopy(this.buffer, 0, buffer2, 0, size0 * size1);
		return new BufferedInt32Array2D(size0, size1, buffer2);
	}

	@Override
	public Int32Array newInstance(int... dims)
	{
		return Int32Array.create(dims);
	}

	
	// =============================================================
	// Implementation of the Iterable interface

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
			return this.index < (size0 * size1 - 1);
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
