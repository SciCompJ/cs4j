/**
 * 
 */
package net.sci.array.scalar;

/**
 * Implementation of Int32Array1D that stores inner data in a linear array of
 * ints.
 * 
 * @author dlegland
 *
 */
public class BufferedInt32Array1D extends Int32Array1D
{
    // =============================================================
    // Static methods

    /**
     * Converts the input array into an instance of the BufferedInt32Array1D
     * class. May return the input array if it is already an instance of
     * BufferedInt32Array1D.
     * 
     * @param array
     *            the array to convert
     * @return an instance of BufferedInt32Array1D containing the same values as
     *         the input array.
     */
    public static final BufferedInt32Array1D convert(Int32Array1D array)
    {
        // if array is of correct class, simply use class cast
        if (array instanceof BufferedInt32Array1D)
        {
            return (BufferedInt32Array1D) array;
        }
        
        // allocate memory
        int sizeX = array.size(0);
        BufferedInt32Array1D res = new BufferedInt32Array1D(sizeX);
        
        // copy values
        for (int x = 0; x < sizeX; x++)
        {
            res.setInt(x, array.getInt(x));
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
	 */
	public BufferedInt32Array1D(int size0)
	{
		super(size0);
		this.buffer = new int[size0];
	}

	/**
	 * 
	 * @param size0
	 *            the size of the array along the first dimension
	 * @param buffer
	 *            the buffer containing the values
	 */
	public BufferedInt32Array1D(int size0, int[] buffer)
	{
		super(size0);
		this.buffer = buffer;
	}


	// =============================================================
	// Implementation of the IntArray2D interface

    @Override
    public void setInt(int x, int value)
    {
        buffer[x] = value;
    }

	@Override
	public int getInt(int... pos)
	{
		return buffer[pos[0]];
	}

	@Override
	public void setInt(int[] pos, int value)
	{
		buffer[pos[0]] = value;
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
	public BufferedInt32Array1D duplicate()
	{
		int[] buffer2 = new int[size0];
		System.arraycopy(this.buffer, 0, buffer2, 0, size0);
		return new BufferedInt32Array1D(size0, buffer2);
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
			return this.index < (size0 - 1);
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
