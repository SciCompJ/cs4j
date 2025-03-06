/**
 * 
 */
package net.sci.array.numeric.impl;

import net.sci.array.numeric.Float64;
import net.sci.array.numeric.Float64Array;
import net.sci.array.numeric.Float64ArrayND;
import net.sci.util.MathUtils;

/**
 * @author dlegland
 *
 */
public class BufferedFloat64ArrayND extends Float64ArrayND
{
	// =============================================================
	// Class fields

	double[] buffer;

	
	// =============================================================
	// Constructors

	/**
     * Creates a new array that allocates the buffer specified by the size
     * array.
     * 
     * @param sizes
     *            the dimensions of this array
     */
	public BufferedFloat64ArrayND(int[] sizes)
	{
		super(sizes);
		
		// check validity of input size array
		long elCount = MathUtils.prod(sizes);
		if (elCount > Integer.MAX_VALUE - 8)
		{
		    throw new IllegalArgumentException("Total element count is larger than maximal size for java arays");
		}
		
		// allocate buffer
		this.buffer = new double[(int) elCount]; 
	}

	/**
	 * Initialize a new multidimensional array, using the specified buffer.
	 * 
	 * @param sizes
	 *            the dimensions of this image
	 * @param buffer
	 *            the array containing buffer for this image
	 */
	public BufferedFloat64ArrayND(int[] sizes, double[] buffer)
	{
		super(sizes);
		if (buffer.length != MathUtils.prod(sizes))
		{
			throw new IllegalArgumentException("Size of image and buffer do not match");
		}
		
		this.buffer = buffer;
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
	public Float64 get(int[] pos)
	{
		int index = subsToInd(pos);
		return new Float64(this.buffer[index]);	
	}

	@Override
	public void set(int[] pos, Float64 value)
	{
		int index = subsToInd(pos);
		this.buffer[index] = value.value();
	}

	@Override
	public double getValue(int[] pos)
	{
		int index = subsToInd(pos);
		return this.buffer[index];
	}

	@Override
	public void setValue(int[] pos, double value)
    {
		int index = subsToInd(pos);
		this.buffer[index] = value;
	}
	
	// =============================================================
    // Implementation of the Array interface
    
    @Override
    public Float64Array duplicate()
    {
    	int n = buffer.length;
    	double[] buffer2 = new double[n];
    	System.arraycopy(this.buffer, 0, buffer2, 0, n);
    	return new BufferedFloat64ArrayND(sizes, buffer2);
    }

    @Override
	public Float64Array.Iterator iterator()
	{
		return new Float64Iterator();
	}

	private class Float64Iterator implements Float64Array.Iterator
	{
		int index;
		int indexMax;
			
		public Float64Iterator()
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
		public Float64 next()
		{
			return new Float64(buffer[++index]);
		}

		@Override
		public void forward()
		{
			++index;
		}

		@Override
		public Float64 get()
		{
			return new Float64(buffer[index]);
		}

		@Override
		public void set(Float64 value)
		{
			buffer[index] = value.value();
		}

		@Override
		public double nextValue()
		{
			forward();
			return buffer[index];
		}

		@Override
		public double getValue()
		{
			return buffer[index];
		}

		@Override
		public void setValue(double value)
		{
			buffer[index] = value;
		}
	}
}
