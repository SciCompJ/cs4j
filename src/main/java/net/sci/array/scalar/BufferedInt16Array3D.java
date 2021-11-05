/**
 * 
 */
package net.sci.array.scalar;

/**
 * Implementation of Int16Array3D that stores inner data in a linear array of
 * shorts.
 * 
 * This implementation is limited by the total number of elements, that must be
 * less than maximum array index in java (in the order of 2^31).
 *
 * @see SlicedInt16Array3D
 * 
 * @author dlegland
 *
 */
public class BufferedInt16Array3D extends Int16Array3D
{
    // =============================================================
    // Static methods

    /**
     * Converts the input array into an instance of the BufferedInt16Array3D
     * class. May return the input array if it is already an instance of
     * BufferedInt16Array3D.
     * 
     * @param array
     *            the array to convert
     * @return an instance of BufferedInt16Array3D containing the same values
     *         as the input array.
     */
    public static final BufferedInt16Array3D convert(Int16Array3D array)
    {
        // if array is of correct class, simply use class cast
        if (array instanceof BufferedInt16Array3D)
        {
            return (BufferedInt16Array3D) array;
        }
        
        // allocate memory
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
        BufferedInt16Array3D res = new BufferedInt16Array3D(sizeX, sizeY, sizeZ);
        
        // copy values
        for (int z = 0; z < sizeZ; z++)
        {
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    res.setShort(x, y, z, array.getShort(x, y, z));
                }
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
	 * @param size2
	 *            the size of the array along the third dimension
	 */
	public BufferedInt16Array3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
		this.buffer = new short[size0 * size1 * size2];
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
	 *            the buffer containing the short values
	 */
	public BufferedInt16Array3D(int size0, int size1, int size2, short[] buffer)
	{
		super(size0, size1, size2);
		if (buffer.length < size0 * size1 * size2)
		{
			throw new IllegalArgumentException("Buffer size does not match image dimensions");
		}
		this.buffer = buffer;
	}


	// =============================================================
	// Specialization of the Int16Array3D interface

    /* (non-Javadoc)
     * @see net.sci.array.scalar.Int16Array3D#setShort(int, int, int, short)
     */
    @Override
    public void setShort(int x, int y, int z, short value)
    {
        int index = x + this.size0 * (y + z * this.size1);
        this.buffer[index] = value;
    }

	/* (non-Javadoc)
	 * @see net.sci.array.scalar.Int16Array3D#getShort(int, int, int)
	 */
	@Override
	public short getShort(int... pos)
	{
		int index = pos[0] + this.size0 * (pos[1] + pos[2] * this.size1);
		return this.buffer[index];
	}
		
	/* (non-Javadoc)
	 * @see net.sci.array.scalar.Int16Array3D#setShort(int[], short)
	 */
	@Override
	public void setShort(int[] pos, short value)
	{
	    int index = pos[0] + this.size0 * (pos[1] + pos[2] * this.size1);
        this.buffer[index] = value;
	}

	
	// =============================================================
	// Specialization of the Array interface

	@Override
	public Int16Array3D duplicate()
	{
		short[] buffer2 = new short[size0 * size1 * size2];
		System.arraycopy(this.buffer, 0, buffer2, 0, size0 * size1 * size2);
		return new BufferedInt16Array3D(size0, size1, size2, buffer2);
	}

	
	// =============================================================
	// Implementation of the Iterator interface

	public Int16Array.Iterator iterator()
	{
		return new Iterator();
	}
	
	private class Iterator implements Int16Array.Iterator
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
		public Int16 next()
		{
			this.index++;
			return new Int16(buffer[index]);
		}

		@Override
		public void forward()
		{
			this.index++;
		}

		@Override
		public Int16 get()
		{
			return new Int16(buffer[index]);
		}

		@Override
		public short getShort()
		{
			return buffer[index];
		}

		@Override
		public void setShort(short s)
		{
			buffer[index] = s;
		}
	}

}
