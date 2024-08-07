/**
 * 
 */
package net.sci.array.numeric.impl;

import net.sci.array.numeric.Int16;
import net.sci.array.numeric.Int16Array;
import net.sci.array.numeric.Int16Array2D;

/**
 * Implementation of Int16Array that stores inner data in a linear array of
 * shorts.
 * 
 * @author dlegland
 *
 */
public class BufferedInt16Array2D extends Int16Array2D
{
    // =============================================================
    // Static methods

    /**
     * Converts the input array into an instance of the BufferedInt16Array2D
     * class. May return the input array if it already an instance of
     * BufferedInt16Array2D.
     * 
     * @param array
     *            the array to convert
     * @return an instance of BufferedInt16Array2D containing the same values as
     *         the input array.
     */
    public static final BufferedInt16Array2D convert(Int16Array2D array)
    {
        // if array is of correct class, simply use class cast
        if (array instanceof BufferedInt16Array2D)
        {
            return (BufferedInt16Array2D) array;
        }
        
        // allocate memory
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        BufferedInt16Array2D res = new BufferedInt16Array2D(sizeX, sizeY);
        
        // copy values
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                res.setShort(x, y, array.getShort(x, y));
            }
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
	 * @param size1
	 *            the size of the array along the second dimension
	 */
	public BufferedInt16Array2D(int size0, int size1)
	{
		super(size0, size1);
		this.buffer = new short[size0 * size1];
	}

	/**
	 * 
	 * @param size0
	 *            the size of the array along the first dimension
	 * @param size1
	 *            the size of the array along the second dimension
	 * @param buffer
	 *            the buffer containing the values
	 */
	public BufferedInt16Array2D(int size0, int size1, short[] buffer)
	{
		super(size0, size1);
		if (buffer.length < size0 * size1)
		{
			throw new IllegalArgumentException("Buffer size does not match image dimensions");
		}
		this.buffer = buffer;
	}


	// =============================================================
	// Implementation of the Int16Array2D interface

    /* (non-Javadoc)
     * @see net.sci.array.scalar.Int16Array2D#getShort(int, int)
     */
    @Override
    public short getShort(int x, int y)
    {
        int index = x + y * this.size0;
        return this.buffer[index];
    }
    
    /* (non-Javadoc)
     * @see net.sci.array.scalar.Int16Array2D#setShort(int, int, short)
     */
    @Override
    public void setShort(int x, int y, short s)
    {
        int index = x + y * this.size0;
        this.buffer[index] = s;
    }
    
    
    // =============================================================
    // Specialization of the Int16Array interface
    
    /* (non-Javadoc)
     * @see net.sci.array.scalar.Int16Array2D#getShort(int...)
     */
    @Override
    public short getShort(int[] pos)
    {
    	int index = pos[0] + pos[1] * this.size0;
    	return this.buffer[index];
    }

    /* (non-Javadoc)
     * @see net.sci.array.scalar.Int16Array2D#setShort(int[], short)
     */
    @Override
    public void setShort(int[] pos, short s)
    {
        int index = pos[0] + pos[1] * this.size0;
    	this.buffer[index] = s;
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
	public Int16Array2D duplicate()
	{
		short[] buffer2 = new short[size0 * size1];
		System.arraycopy(this.buffer, 0, buffer2, 0, size0 * size1);
		return new BufferedInt16Array2D(size0, size1, buffer2);
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
		public boolean hasNext()
		{
			return this.index < (size0 * size1 - 1);
		}

		@Override
		public Int16 next()
		{
			this.index++;
			return new Int16(buffer[index]);
		}

		@Override
		public void forward()
		{
			this.index++;
		}

		@Override
		public Int16 get()
		{
			return new Int16(buffer[index]);
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
