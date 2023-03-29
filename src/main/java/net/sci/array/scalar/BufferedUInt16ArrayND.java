/**
 * 
 */
package net.sci.array.scalar;

import net.sci.array.Array;

/**
 * @author dlegland
 *
 */
public class BufferedUInt16ArrayND extends UInt16ArrayND
{
	// =============================================================
	// Class fields

	short[] buffer;

	
	// =============================================================
	// Constructors

	/**
	 * @param sizes the dimensions of this array
	 */
	public BufferedUInt16ArrayND(int[] sizes)
	{
		super(sizes);
        
        // check validity of input size array
        long elCount = Array.prod(sizes);
        if (elCount > Integer.MAX_VALUE - 8)
        {
            throw new IllegalArgumentException("Total element count is larger than maximal size for java arays");
        }
        
        // allocate buffer
        this.buffer = new short[(int) elCount]; 
	}

	/**
	 * Initialize a new multidimensional array, using the specified buffer.
	 * 
	 * @param sizes
	 *            the dimensions of the array
	 * @param buffer
	 *            the array containing buffer for this image
	 */
	public BufferedUInt16ArrayND(int[] sizes, short[] buffer)
	{
		super(sizes);
		if (buffer.length != Array.prod(sizes))
		{
			throw new IllegalArgumentException("Size of image and buffer do not match");
		}
		
		this.buffer = buffer;
	}


	// =============================================================
	// New specific methods
	
	

	// =============================================================
	// Implementation of the UInt16Array interface
	
	@Override
	public short getShort(int[] pos)
	{
		int index = subsToInd(pos);
		return this.buffer[index];	
	}

	@Override
	public void setShort(int[] pos, short value)
	{
		int index = subsToInd(pos);
		this.buffer[index] = value;	
	}

	
	// =============================================================
	// Implementation of the IntArray interface
	
	/* (non-Javadoc)
	 * @see net.sci.array.data.IntArray#getInt(int[])
	 */
	@Override
	public int getInt(int[] pos)
	{
		int index = subsToInd(pos);
		return this.buffer[index] & 0x00FFFF;	
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.IntArray#setInt(int[], int)
	 */
	@Override
	public void setInt(int[] pos, int intValue)
	{
		int index = subsToInd(pos);
		intValue = Math.min(Math.max(intValue, 0), 0x00FFFF);
		this.buffer[index] = (short) intValue;
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
    
	@Override
	public UInt16 get(int[] pos)
	{
		int index = subsToInd(pos);
		return new UInt16(this.buffer[index]);	
	}

	@Override
	public void set(int[] pos, UInt16 value)
	{
		int index = subsToInd(pos);
		this.buffer[index] = value.getShort();
	}
	

	// =============================================================
    // Implementation of the Array interface
    
    @Override
    public UInt16Array duplicate()
    {
    	int n = buffer.length;
    	short[] buffer2 = new short[n];
    	System.arraycopy(this.buffer, 0, buffer2, 0, n);
    	return new BufferedUInt16ArrayND(sizes, buffer2);
    }

    @Override
	public UInt16Array.Iterator iterator()
	{
		return new UInt16Iterator();
	}

	private class UInt16Iterator implements UInt16Array.Iterator
	{
		int index;
		int indexMax;
			
		public UInt16Iterator()
		{
            this.index = -1;
            this.indexMax = (int) Array.prod(sizes) - 1;
		}
		
		@Override
		public boolean hasNext()
		{
			return index < indexMax;
		}
		
		@Override
		public UInt16 next()
		{
			return new UInt16(buffer[++index]);
		}

		@Override
		public void forward()
		{
			++index;
		}

		@Override
		public UInt16 get()
		{
			return new UInt16(buffer[index]);
		}

		@Override
		public int getInt()
		{
			return buffer[index] & 0x00FFFF;
		}

		@Override
		public void set(UInt16 value)
		{
			buffer[index] = value.getShort();
		}
		
		@Override
		public void setInt(int value)
		{
			buffer[index] = (short) value; 
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
