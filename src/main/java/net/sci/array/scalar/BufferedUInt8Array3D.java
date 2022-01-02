/**
 * 
 */
package net.sci.array.scalar;


/**
 * Implementation of UInt8Array3D that stores inner data in a linear array of
 * bytes.
 * 
 * This implementation is limited by the total number of elements, that must be
 * less than maximum array index in java (in the order of 2^31).
 *
 * @see SlicedUInt8Array3D
 * 
 * @author dlegland
 *
 */
public class BufferedUInt8Array3D extends UInt8Array3D
{
    // =============================================================
    // Static methods

    /**
     * Converts the input array into an instance of the BufferedUInt8Array3D
     * class. May return the input array if it is already an instance of
     * BufferedUInt8Array3D.
     * 
     * @param array
     *            the array to convert
     * @return an instance of BufferedUInt8Array3D containing the same values
     *         as the input array.
     */
    public static final BufferedUInt8Array3D convert(UInt8Array3D array)
    {
        // if array is of correct class, simply use class cast
        if (array instanceof BufferedUInt8Array3D)
        {
            return (BufferedUInt8Array3D) array;
        }
        
        // allocate memory
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
        BufferedUInt8Array3D res = new BufferedUInt8Array3D(sizeX, sizeY, sizeZ);
        
        // copy values
        for (int z = 0; z < sizeZ; z++)
        {
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    res.setByte(x, y, z, array.getByte(x, y, z));
                }
            }
        }
        // return converted array
        return res;
    }
    
    
	// =============================================================
	// Class fields

    /**
     * The array of bytes that stores array values.
     */
    byte[] buffer;

	
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
	public BufferedUInt8Array3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
		this.buffer = new byte[size0 * size1 * size2];
	}

	/**
	 * @param size0
	 *            the size of the array along the first dimension
	 * @param size1
	 *            the size of the array along the second dimension
	 * @param size2
	 *            the size of the array along the third dimension
	 * @param buffer
	 *            the buffer containing the byte values
	 */
	public BufferedUInt8Array3D(int size0, int size1, int size2, byte[] buffer)
	{
		super(size0, size1, size2);
		if (buffer.length < size0 * size1 * size2)
		{
			throw new IllegalArgumentException("Buffer size does not match image dimensions");
		}
		this.buffer = buffer;
	}


	// =============================================================
	// Implementation of the UInt8Array3D interface

    /* (non-Javadoc)
     * @see net.sci.array.data.scalar2d.UInt8Array3D#setByte(int, int, int, byte)
     */
    @Override
    public void setByte(int x, int y, int z, byte b)
    {
        int index = x + this.size0 * (y + z * this.size1);
        this.buffer[index] = b;
    }

    
    // =============================================================
    // Implementation of the UInt8Array interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.scalar2d.UInt8Array3D#getByte(int, int, int)
	 */
	@Override
	public byte getByte(int... pos)
	{
		int index = pos[0] + this.size0 * (pos[1] + pos[2] * this.size1);
		return this.buffer[index];
	}
		
	/* (non-Javadoc)
	 * @see net.sci.array.data.scalar2d.UInt8Array3D#setByte(int, int, int, byte)
	 */
	@Override
	public void setByte(int[] pos, byte b)
	{
		int index = pos[0] + this.size0 * (pos[1] + pos[2] * this.size1);
		this.buffer[index] = b;
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
            return (double) (buffer[index] & 0x00FF);
        }
    }
    
    

	// =============================================================
	// Implementation of the Array interface

	@Override
	public UInt8Array3D duplicate()
	{
		byte[] buffer2 = new byte[size0 * size1 * size2];
		System.arraycopy(this.buffer, 0, buffer2, 0, size0 * size1 * size2);
		return new BufferedUInt8Array3D(size0, size1, size2, buffer2);
	}

	
	// =============================================================
	// Implementation of the Iterator interface

	public UInt8Array.Iterator iterator()
	{
		return new Iterator();
	}
	
	private class Iterator implements UInt8Array.Iterator
	{
		int index = -1;
		
		public Iterator() 
		{
		}
		
		@Override
		public boolean hasNext()
		{
			return this.index < (size0 * size1 * size2 - 1);
		}

		@Override
		public UInt8 next()
		{
			this.index++;
			return new UInt8(buffer[index]);
		}

		@Override
		public void forward()
		{
			this.index++;
		}

		@Override
		public UInt8 get()
		{
			return new UInt8(buffer[index]);
		}

		@Override
		public void set(UInt8 value)
		{
			buffer[index] = value.getByte();
		}
		
		@Override
		public byte getByte()
		{
			return buffer[index];
		}

		@Override
		public void setByte(byte b)
		{
			buffer[index] = b;
		}
	}
}
