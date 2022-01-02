/**
 * 
 */
package net.sci.array.scalar;

/**
 * @author dlegland
 *
 */
public class BufferedInt16ArrayND extends Int16ArrayND
{
	// =============================================================
	// Class fields

	short[] buffer;

	
	// =============================================================
	// Constructors

	/**
	 * @param sizes the dimensions of this array
	 */
	public BufferedInt16ArrayND(int[] sizes)
	{
		super(sizes);
        this.buffer = new short[cumProd(sizes)]; 
	}

	/**
	 * Initialize a new multidimensional array, using the specified buffer.
	 * 
	 * @param sizes
	 *            the dimensions of the array
	 * @param buffer
	 *            the array containing buffer for this image
	 */
	public BufferedInt16ArrayND(int[] sizes, short[] buffer)
	{
		super(sizes);
		int bufferSize = 1;
		for (int i = 0; i < sizes.length; i++)
		{
			bufferSize *= sizes[i];
		}
		if (buffer.length != bufferSize)
		{
			throw new IllegalArgumentException("Size of image and buffer do not match");
		}
		
		this.buffer = buffer;
	}


	// =============================================================
	// New specific methods
	
	public int getInt(int i)
	{
		return buffer[i];
	}
	
	public void setInt(int i, int value)
	{
		buffer[i] = (short) Int16.clamp(value);
	}
	

	// =============================================================
	// Implementation of the Int16Array interface
	
	@Override
	public short getShort(int... pos)
	{
		int index = subsToInd(pos);
		return this.buffer[index];	
	}

	@Override
	public void setShort(int[] pos, short s)
	{
		int index = subsToInd(pos);
		this.buffer[index] = s;	
	}

	// =============================================================
	// Implementation of the IntArray interface
	
	/* (non-Javadoc)
	 * @see net.sci.array.data.IntArray#getInt(int[])
	 */
	@Override
	public int getInt(int... pos)
	{
		int index = subsToInd(pos);
		return this.buffer[index];	
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.IntArray#setInt(int[], int)
	 */
	@Override
	public void setInt(int[] pos, int intValue)
	{
		int index = subsToInd(pos);
		this.buffer[index] = (short) Int16.clamp(intValue);
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
	public Int16Array duplicate()
	{
		int n = buffer.length;
		short[] buffer2 = new short[n];
		System.arraycopy(this.buffer, 0, buffer2, 0, n);
		return new BufferedInt16ArrayND(sizes, buffer2);
	}

	@Override
	public Int16 get(int... pos)
	{
		int index = subsToInd(pos);
		return new Int16(this.buffer[index]);	
	}

	@Override
	public void set(int[] pos, Int16 value)
	{
		int index = subsToInd(pos);
		this.buffer[index] = value.getShort();
	}

	@Override
	public Int16Array.Iterator iterator()
	{
		return new Int16Iterator();
	}

	private class Int16Iterator implements Int16Array.Iterator
	{
		int index;
		int indexMax;
			
		public Int16Iterator()
		{
            this.index = -1;
            this.indexMax = cumProd(sizes) - 1;
		}
		
		@Override
		public boolean hasNext()
		{
			return index < indexMax;
		}
		
		@Override
		public Int16 next()
		{
			return new Int16(buffer[++index]);
		}

		@Override
		public void forward()
		{
			++index;
		}

		@Override
		public Int16 get()
		{
			return new Int16(buffer[index]);
		}

		@Override
		public int getInt()
		{
			return buffer[index];
		}

		@Override
		public void set(Int16 value)
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
