/**
 * 
 */
package net.sci.array.scalar;

/**
 * Implementation of UInt16Array that stores inner data in a linear array of
 * shorts.
 * 
 * @author dlegland
 *
 */
public class BufferedUInt16Array2D extends UInt16Array2D
{
    // =============================================================
    // Static methods

    /**
     * Converts the input array into an instance of the BufferedUInt16Array2D
     * class. May return the input array if it is already an instance of
     * BufferedUInt16Array2D.
     * 
     * @param array
     *            the array to convert
     * @return an instance of BufferedUInt16Array2D containing the same values as
     *         the input array.
     */
    public static final BufferedUInt16Array2D convert(UInt16Array2D array)
    {
        // if array is of correct class, simply use class cast
        if (array instanceof BufferedUInt16Array2D)
        {
            return (BufferedUInt16Array2D) array;
        }
        
        // allocate memory
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        BufferedUInt16Array2D res = new BufferedUInt16Array2D(sizeX, sizeY);
        
        // copy values
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                res.setShort(x, y, array.getShort(x, y));
            }
        }
        
        // return converted array
        return res;
    }
    
    
    // =============================================================
    // Class fields

    /**
     * The array of shorts that stores array values.
     */
	short[] buffer;

	
	// =============================================================
	// Constructors

	/**
	 * @param size0
	 *            the size of the array along the first dimension
	 * @param size1
	 *            the size of the array along the second dimension
	 */
	public BufferedUInt16Array2D(int size0, int size1)
	{
		super(size0, size1);
		this.buffer = new short[size0 * size1];
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
	public BufferedUInt16Array2D(int size0, int size1, short[] buffer)
	{
		super(size0, size1);
		if (buffer.length < size0 * size1)
		{
			throw new IllegalArgumentException("Buffer size does not match image dimensions");
		}
		this.buffer = buffer;
	}


	// =============================================================
	// Implementation of the UInt16Array2D interface

    /* (non-Javadoc)
     * @see net.sci.array.scalar.UInt16Array2D#getShort(int, int)
     */
    @Override
    public short getShort(int x, int y)
    {
        int index = x + y * this.size0;
        return this.buffer[index];
    }
    
    /* (non-Javadoc)
     * @see net.sci.array.scalar.UInt16Array2D#setShort(int, int, short)
     */
    @Override
    public void setShort(int x, int y, short s)
    {
        int index = x + y * this.size0;
        this.buffer[index] = s;
    }
    
    /* (non-Javadoc)
	 * @see net.sci.array.scalar.UInt16Array2D#getShort(int[])
	 */
	@Override
	public short getShort(int[] pos)
	{
		int index = pos[0] + pos[1] * this.size0;
		return this.buffer[index];
	}
	
	/* (non-Javadoc)
	 * @see net.sci.array.scalar.UInt16Array2D#setShort(int[], short)
	 */
	@Override
	public void setShort(int[] pos, short s)
	{
		int index = pos[0] + pos[1] * this.size0;
		this.buffer[index] = s;
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
            return (double) (buffer[index] & 0x00FFFF);
        }
    }
    
    

	// =============================================================
	// Implementation of the Array interface

	@Override
	public UInt16Array2D duplicate()
	{
		short[] buffer2 = new short[size0 * size1];
		System.arraycopy(this.buffer, 0, buffer2, 0, size0 * size1);
		return new BufferedUInt16Array2D(size0, size1, buffer2);
	}

	
	// =============================================================
	// Implementation of the Iterable interface

	public UInt16Array.Iterator iterator()
	{
		return new Iterator();
	}
	
	private class Iterator implements UInt16Array.Iterator
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
		public UInt16 next()
		{
			this.index++;
			return new UInt16(buffer[index]);
		}

		@Override
		public void forward()
		{
			this.index++;
		}

		@Override
		public UInt16 get()
		{
			return new UInt16(buffer[index]);
		}

		@Override
		public short getShort()
		{
			return buffer[index];
		}

		@Override
		public void setShort(short b)
		{
			buffer[index] = b;
		}
	}

}
