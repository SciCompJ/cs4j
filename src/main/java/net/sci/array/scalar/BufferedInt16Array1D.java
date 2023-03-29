/**
 * 
 */
package net.sci.array.scalar;

/**
 * Implementation of Int16Array1D that stores inner data in a linear array of
 * shorts.
 * 
 * @author dlegland
 *
 */
public class BufferedInt16Array1D extends Int16Array1D
{
    // =============================================================
    // Static methods

    /**
     * Converts the input array into an instance of the BufferedInt16Array1D
     * class. May return the input array if it is already an instance of
     * BufferedInt16Array1D.
     * 
     * @param array
     *            the array to convert
     * @return an instance of BufferedInt16Array1D containing the same values as
     *         the input array.
     */
    public static final BufferedInt16Array1D convert(Int16Array1D array)
    {
        // if array is of correct class, simply use class cast
        if (array instanceof BufferedInt16Array1D)
        {
            return (BufferedInt16Array1D) array;
        }
        
        // allocate memory
        int sizeX = array.size(0);
        BufferedInt16Array1D res = new BufferedInt16Array1D(sizeX);
        
        // copy values
        for (int x = 0; x < sizeX; x++)
        {
            res.setShort(x, array.getShort(x));
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
	 */
	public BufferedInt16Array1D(int size0)
	{
		super(size0);
		this.buffer = new short[size0];
	}

	/**
	 * 
	 * @param size0
	 *            the size of the array along the first dimension
	 * @param buffer
	 *            the buffer containing the values
	 */
	public BufferedInt16Array1D(int size0, short[] buffer)
	{
		super(size0);
		this.buffer = buffer;
	}


    // =============================================================
    // Implementation of the Int16Array1D interface

    @Override
    public short getShort(int pos)
    {
        return buffer[pos];
    }
    
    @Override
    public void setShort(int pos, short value)
    {
        buffer[pos] = value;
    }
    
        
    // =============================================================
    // Implementation of the Int16Array interface

    @Override
    public short getShort(int[] pos)
    {
        return buffer[pos[0]];
    }

    @Override
    public void setShort(int[] pos, short value)
    {
        buffer[pos[0]] = value;
    }
    

    // =============================================================
    // Implementation of the IntArray interface

	@Override
	public int getInt(int[] pos)
	{
		return buffer[pos[0]];
	}

	@Override
	public void setInt(int[] pos, int value)
	{
		buffer[pos[0]] = (short) Int16.clamp(value);
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
	public BufferedInt16Array1D duplicate()
	{
		short[] buffer2 = new short[size0];
		System.arraycopy(this.buffer, 0, buffer2, 0, size0);
		return new BufferedInt16Array1D(size0, buffer2);
	}

	@Override
	public Int16Array newInstance(int... dims)
	{
		return Int16Array.create(dims);
	}

	
	// =============================================================
	// Implementation of the Iterable interface

	public Int16Array.Iterator iterator()
	{
		return new Int16Iterator();
	}
	
	private class Int16Iterator implements Int16Array.Iterator
	{
		int index = -1;
		
		public Int16Iterator() 
		{
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

        @Override
		public int getInt()
		{
			return buffer[index];
		}

		@Override
		public void setInt(int value)
		{
			buffer[index] = (short) Int16.clamp(value);;
		}

        @Override
        public boolean hasNext()
        {
        	return this.index < (size0 - 1);
        }

        @Override
        public Int16 next()
        {
        	this.index++;
        	return new Int16(buffer[index]);
        }

        @Override
        public Int16 get()
        {
        	return new Int16(buffer[index]);
        }

        @Override
        public void forward()
        {
        	this.index++;
        }
	}
}
