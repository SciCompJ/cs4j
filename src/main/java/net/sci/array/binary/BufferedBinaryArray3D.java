/**
 * 
 */
package net.sci.array.binary;

import net.sci.array.Array;

/**
 * Implementation of BinaryArray3D that stores inner data in a linear array of
 * booleans.
 * 
 * This implementation is limited by the total number of elements, that must be
 * less than maximum array index in java (in the order of 2^31).
 *
 * @see SlicedBinaryArray3D
 * 
 * @author dlegland
 *
 */
public class BufferedBinaryArray3D extends BinaryArray3D
{
    // =============================================================
    // Static methods

    /**
     * Converts the input array into an instance of the BufferedBinaryArray3D
     * class. May return the input array if it is already an instance of
     * BufferedBinaryArray3D.
     * 
     * @param array
     *            the array to convert
     * @return an instance of BufferedBinaryArray3D containing the same values
     *         as the input array.
     */
    public static final BufferedBinaryArray3D convert(BinaryArray3D array)
    {
        // if array is of correct class, simply use class cast
        if (array instanceof BufferedBinaryArray3D)
        {
            return (BufferedBinaryArray3D) array;
        }
        
        // allocate memory
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
        BufferedBinaryArray3D res = new BufferedBinaryArray3D(sizeX, sizeY, sizeZ);
        
        // copy values
        for (int z = 0; z < sizeZ; z++)
        {
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    res.setBoolean(x, y, z, array.getBoolean(x, y, z));
                }
            }
        }
        // return converted array
        return res;
    }
    
    
 	// =============================================================
	// Class fields

    /**
     * The array of booleans that stores array values.
     */
    boolean[] buffer;

	
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
	public BufferedBinaryArray3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
        
        // check validity of input size array
        long elCount = Array.prod(size0, size1, size2);
        if (elCount > Integer.MAX_VALUE - 8)
        {
            throw new IllegalArgumentException("Total element count is larger than maximal size for java arays");
        }
        
        // allocate buffer
		this.buffer = new boolean[size0 * size1 * size2];
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
	 *            the buffer containing the boolean values
	 */
	public BufferedBinaryArray3D(int size0, int size1, int size2, boolean[] buffer)
	{
		super(size0, size1, size2);
		if (buffer.length < Array.prod(size0, size1, size2))
		{
			throw new IllegalArgumentException("Buffer size does not match image dimensions");
		}
		this.buffer = buffer;
	}


	// =============================================================
	// Implementation of the BinaryArray3D interface


	/* (non-Javadoc)
	 * @see net.sci.array.data.scalar2d.BooleanArray3D#setState(int, int, int, boolean)
	 */
	@Override
	public void setBoolean(int x, int y, int z, boolean b)
	{
	    int index = x + this.size0 * (y + z * this.size1);
	    this.buffer[index] = b;
	}

	
    // =============================================================
    // Implementation of the BinaryArray interface

    /**
     * Fills this binary array with the specified boolean value.
     * 
     * @param state
     *            the value to fill the binary array with.
     */
    public void fill(boolean state)
    {
        for (int i = 0; i < buffer.length; i++)
        {
            buffer[i] = state;
        }
    }

    /* (non-Javadoc)
	 * @see net.sci.array.data.scalar2d.BooleanArray3D#getState(int, int, int)
	 */
	@Override
	public boolean getBoolean(int... pos)
	{
		int index = pos[0] + this.size0 * (pos[1] + pos[2] * this.size1);
		return this.buffer[index];
	}


	// =============================================================
	// Specialization of the Array interface

	@Override
	public BinaryArray3D duplicate()
	{
		boolean[] buffer2 = new boolean[size0 * size1 * size2];
		System.arraycopy(this.buffer, 0, buffer2, 0, size0 * size1 * size2);
		return new BufferedBinaryArray3D(size0, size1, size2, buffer2);
	}

	
	// =============================================================
	// Implementation of the Iterator interface

	public BinaryArray.Iterator iterator()
	{
		return new BooleanIterator();
	}
	
	private class BooleanIterator implements BinaryArray.Iterator
	{
		int index = -1;
		
		public BooleanIterator() 
		{
		}
		
		@Override
		public boolean hasNext()
		{
			return this.index < (size0 * size1 * size2 - 1);
		}

		@Override
		public Binary next()
		{
			this.index++;
			return new Binary(buffer[index]);
		}

		@Override
		public void forward()
		{
			this.index++;
		}

		@Override
		public Binary get()
		{
			return new Binary(buffer[index]);
		}

		@Override
		public void set(Binary b)
		{
			buffer[index] = b.getBoolean();
		}
		
		@Override
		public boolean getBoolean()
		{
			return buffer[index];
		}

		@Override
		public void setBoolean(boolean b)
		{
			buffer[index] = b;
		}
	}

}
