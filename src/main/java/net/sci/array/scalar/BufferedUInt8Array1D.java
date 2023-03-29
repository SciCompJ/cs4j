/**
 * 
 */
package net.sci.array.scalar;

/**
 * Implementation of UInt8Array1D that stores inner data in a linear array of
 * bytes.
 * 
 * @author dlegland
 *
 */
public class BufferedUInt8Array1D extends UInt8Array1D
{
    // =============================================================
    // Static methods

    /**
     * Converts the input array into an instance of the BufferedUInt8Array1D
     * class. May return the input array if it is already an instance of
     * BufferedUInt8Array1D.
     * 
     * @param array
     *            the array to convert
     * @return an instance of BufferedUInt8Array1D containing the same values as
     *         the input array.
     */
    public static final BufferedUInt8Array1D convert(UInt8Array1D array)
    {
        // if array is of correct class, simply use class cast
        if (array instanceof BufferedUInt8Array1D)
        {
            return (BufferedUInt8Array1D) array;
        }
        
        // allocate memory
        int sizeX = array.size(0);
        BufferedUInt8Array1D res = new BufferedUInt8Array1D(sizeX);
        
        // copy values
        for (int x = 0; x < sizeX; x++)
        {
            res.setByte(x, array.getByte(x));
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
	 */
	public BufferedUInt8Array1D(int size0)
	{
		super(size0);
		this.buffer = new byte[size0];
	}

	/**
	 * 
	 * @param size0
	 *            the size of the array along the first dimension
	 * @param buffer
	 *            the buffer containing the values
	 */
	public BufferedUInt8Array1D(int size0, byte[] buffer)
	{
		super(size0);
		this.buffer = buffer;
	}


    // =============================================================
    // Implementation of the UInt8Array1D interface

    @Override
    public void setByte(int pos, byte value)
    {
        buffer[pos] = value;
    }
    
    @Override
    public byte getByte(int pos)
    {
        return buffer[pos];
    }
    
    
    // =============================================================
    // Implementation of the IntArray1D interface

    @Override
    public void setInt(int x, int value)
    {
        buffer[x] = (byte) UInt8.clamp(value);
    }
    
    
    // =============================================================
    // Implementation of the UInt8Array interface

    @Override
    public byte getByte(int[] pos)
    {
        return buffer[pos[0]];
    }

    @Override
    public void setByte(int[] pos, byte value)
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
		buffer[pos[0]] = (byte) UInt8.clamp(value);
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
	public BufferedUInt8Array1D duplicate()
	{
		byte[] buffer2 = new byte[size0];
		System.arraycopy(this.buffer, 0, buffer2, 0, size0);
		return new BufferedUInt8Array1D(size0, buffer2);
	}

	@Override
	public UInt8Array newInstance(int... dims)
	{
		return UInt8Array.create(dims);
	}

	
	// =============================================================
	// Implementation of the Iterable interface

	public UInt8Array.Iterator iterator()
	{
		return new UInt8Iterator();
	}
	
	private class UInt8Iterator implements UInt8Array.Iterator
	{
		int index = -1;
		
		public UInt8Iterator() 
		{
		}
		
		@Override
        public byte getByte()
        {
            return buffer[index];
        }

        @Override
        public void setByte(byte s)
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
			buffer[index] = (byte) UInt8.clamp(value);;
		}

        @Override
        public boolean hasNext()
        {
        	return this.index < (size0 - 1);
        }

        @Override
        public UInt8 next()
        {
        	this.index++;
        	return new UInt8(buffer[index]);
        }

        @Override
        public UInt8 get()
        {
        	return new UInt8(buffer[index]);
        }

        @Override
        public void forward()
        {
        	this.index++;
        }
	}
}
