/**
 * 
 */
package net.sci.array.binary;

/**
 * Implementation of BinaryArray2D that stores inner data in a linear array of
 * booleans.
 * 
 * @author dlegland
 *
 */
public class BufferedBinaryArray2D extends BinaryArray2D
{
    // =============================================================
    // Static methods

    /**
     * Converts the input array into an instance of the BufferedBinaryArray2D
     * class. May return the input array if it is already an instance of
     * BufferedBinaryArray2D.
     * 
     * @param array
     *            the array to convert
     * @return an instance of BufferedBinaryArray2D containing the same values
     *         as the input array.
     */
    public static final BufferedBinaryArray2D convert(BinaryArray2D array)
    {
        // if array is of correct class, simply use class cast
        if (array instanceof BufferedBinaryArray2D)
        {
            return (BufferedBinaryArray2D) array;
        }
        
        // allocate memory
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        BufferedBinaryArray2D res = new BufferedBinaryArray2D(sizeX, sizeY);
        
        // copy values
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                res.setBoolean(x, y, array.getBoolean(x, y));
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
     *            the size of the array in the first dimension
     * @param size1
     *            the size of the array in the second dimension
     */
	public BufferedBinaryArray2D(int size0, int size1)
	{
		super(size0, size1);
		this.buffer = new boolean[size0 * size1];
	}

    /**
     * @param size0
     *            the size of the array in the first dimension
     * @param size1
     *            the size of the array in the second dimension
     * @param buffer
     *            the buffer used for storing array data
     */
	public BufferedBinaryArray2D(int size0, int size1, boolean[] buffer)
	{
		super(size0, size1);
		if (buffer.length < size0 * size1)
		{
			throw new IllegalArgumentException("Buffer size does not match array dimensions");
		}
		this.buffer = buffer;
	}
	

	// =============================================================
	// Implementation of the BooleanArray2D interface

    /* (non-Javadoc)
     * @see net.sci.array.scalar.BinaryArray2D#setBoolean(int, int, boolean)
     */
    @Override
    public void setBoolean(int x, int y, boolean state)
    {
        int index = x + y * this.size0;
        buffer[index] = state;
    }

	/* (non-Javadoc)
	 * @see net.sci.array.scalar.BinaryArray#getBoolean(int[])
	 */
	@Override
	public boolean getBoolean(int... pos)
	{
		int index = pos[0] + pos[1] * this.size0;
		return buffer[index];
	}


	// =============================================================
	// Implementation of the Array interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.scalar2d.BooleanArray2D#duplicate()
	 */
	@Override
	public BinaryArray2D duplicate()
	{
		int n = this.size0 * this.size1;
		boolean[] buffer2 = new boolean[n];
		System.arraycopy(this.buffer, 0, buffer2, 0, n);
		return new BufferedBinaryArray2D(this.size0, this.size1, buffer2);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.BooleanArray#iterator()
	 */
	@Override
	public net.sci.array.binary.BinaryArray.Iterator iterator()
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
			return this.index < (size0 * size1 - 1);
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
