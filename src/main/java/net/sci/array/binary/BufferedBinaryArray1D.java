/**
 * 
 */
package net.sci.array.binary;

/**
 * Implementation of BinaryArray1D that stores inner data in a linear array of
 * booleans.
 * 
 * @author dlegland
 *
 */
public class BufferedBinaryArray1D extends BinaryArray1D
{
    // =============================================================
    // Static methods

    /**
     * Converts the input array into an instance of the BufferedBinaryArray1D
     * class. May return the input array if it is already an instance of
     * BufferedBinaryArray1D.
     * 
     * @param array
     *            the array to convert
     * @return an instance of BufferedBinaryArray2D containing the same values
     *         as the input array.
     */
    public static final BufferedBinaryArray1D convert(BinaryArray1D array)
    {
        // if array is of correct class, simply use class cast
        if (array instanceof BufferedBinaryArray1D)
        {
            return (BufferedBinaryArray1D) array;
        }
        
        // allocate memory
        int sizeX = array.size(0);
        BufferedBinaryArray1D res = new BufferedBinaryArray1D(sizeX);
        
        // copy values
        for (int x = 0; x < sizeX; x++)
        {
            res.setBoolean(x, array.getBoolean(x));
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
     */
	public BufferedBinaryArray1D(int size0)
	{
		super(size0);
		this.buffer = new boolean[size0];
	}

    /**
     * @param size0
     *            the size of the array in the first dimension
     * @param buffer
     *            the buffer used for storing array data
     */
	public BufferedBinaryArray1D(int size0, boolean[] buffer)
	{
		super(size0);
		if (buffer.length < size0)
		{
			throw new IllegalArgumentException("Buffer size does not match array dimensions");
		}
		this.buffer = buffer;
	}
	

	// =============================================================
	// Implementation of the BinaryArray1D interface

    
    /* (non-Javadoc)
     * @see net.sci.array.binary.BinaryArray1D#getBoolean(int)
     */
    @Override
    public boolean getBoolean(int x)
    {
        return buffer[x];
    }

    /* (non-Javadoc)
     * @see net.sci.array.binary.BinaryArray1D#setBoolean(int, int, boolean)
     */
    @Override
    public void setBoolean(int x, boolean state)
    {
        buffer[x] = state;
    }
    
    
    // =============================================================
    // Implementation of the BinaryArray1D interface

    /**
     * Fills this binary array with the specified boolean value.
     * 
     * @param state
     *            the value to fill the binary array with.
     */
    public void fill(boolean state)
    {
        for (int i = 0; i < this.size0; i++)
        {
            buffer[i] = state;
        }
    }


	// =============================================================
	// Implementation of the Array interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.scalar2d.BooleanArray1D#duplicate()
	 */
	@Override
	public BinaryArray1D duplicate()
	{
		int n = this.size0;
		boolean[] buffer2 = new boolean[n];
		System.arraycopy(this.buffer, 0, buffer2, 0, n);
		return new BufferedBinaryArray1D(this.size0, buffer2);
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
			return this.index < (size0 - 1);
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
