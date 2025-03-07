/**
 * 
 */
package net.sci.array.numeric.impl;

import net.sci.array.numeric.Int32;
import net.sci.array.numeric.Int32Array;
import net.sci.array.numeric.Int32ArrayND;
import net.sci.util.MathUtils;

/**
 * @author dlegland
 *
 */
public class BufferedInt32ArrayND extends Int32ArrayND
{
	// =============================================================
	// Class fields

	int[] buffer;

	
	// =============================================================
	// Constructors

	/**
	 * @param sizes the dimensions of this array
	 */
	public BufferedInt32ArrayND(int[] sizes)
	{
		super(sizes);
        
        // check validity of input size array
        long elCount = MathUtils.prod(sizes);
        if (elCount > Integer.MAX_VALUE - 8)
        {
            throw new IllegalArgumentException("Total element count is larger than maximal size for java arays");
        }
        
        // allocate buffer
        this.buffer = new int[(int) elCount]; 
	}

	/**
	 * Initialize a new multidimensional array, using the specified buffer.
	 * 
	 * @param sizes
	 *            the dimensions of this image
	 * @param buffer
	 *            the array containing buffer for this image
	 */
	public BufferedInt32ArrayND(int[] sizes, int[] buffer)
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
	
	
	// =============================================================
	// Implementation of the IntArray interface
	
	/* (non-Javadoc)
	 * @see net.sci.array.data.IntArray#getInt(int[])
	 */
	@Override
	public int getInt(int[] pos)
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
		this.buffer[index] = intValue;
	}

	// =============================================================
	// Implementation of the Array interface
	
	@Override
	public Int32 get(int[] pos)
	{
		int index = subsToInd(pos);
		return new Int32(this.buffer[index]);	
	}

	@Override
	public void set(int[] pos, Int32 value)
	{
		int index = subsToInd(pos);
		this.buffer[index] = value.intValue();
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
    public Int32Array duplicate()
    {
    	int n = buffer.length;
    	int[] buffer2 = new int[n];
    	System.arraycopy(this.buffer, 0, buffer2, 0, n);
    	return new BufferedInt32ArrayND(sizes, buffer2);
    }

    @Override
	public Int32Array.Iterator iterator()
	{
		return new Int32Iterator();
	}

	private class Int32Iterator implements Int32Array.Iterator
	{
		int index;
		int indexMax;
			
		public Int32Iterator()
		{
            this.index = -1;
            this.indexMax = (int) MathUtils.prod(sizes) - 1;
		}
		
		@Override
		public boolean hasNext()
		{
			return index < indexMax;
		}
		
		@Override
		public Int32 next()
		{
			return new Int32(buffer[++index]);
		}

		@Override
		public void forward()
		{
			++index;
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
		public void set(Int32 value)
		{
			buffer[index] = value.intValue();
		}
		
		@Override
		public void setInt(int value)
		{
			buffer[index] = value; 
		}

	}
}
