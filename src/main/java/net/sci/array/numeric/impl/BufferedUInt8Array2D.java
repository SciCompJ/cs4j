/**
 * 
 */
package net.sci.array.numeric.impl;

import net.sci.array.numeric.UInt8;
import net.sci.array.numeric.UInt8Array;
import net.sci.array.numeric.UInt8Array2D;

/**
 * Implementation of UInt8Array2D that stores inner data in a linear array of
 * bytes.
 * 
 * @author dlegland
 *
 */
public class BufferedUInt8Array2D extends UInt8Array2D
{
    // =============================================================
    // Static methods

    /**
     * Converts the input array into an instance of the BufferedUInt8Array2D
     * class. May return the input array if it is already an instance of
     * BufferedUInt8Array2D.
     * 
     * @param array
     *            the array to convert
     * @return an instance of BufferedUInt8Array2D containing the same values as
     *         the input array.
     */
    public static final BufferedUInt8Array2D convert(UInt8Array2D array)
    {
        // if array is of correct class, simply use class cast
        if (array instanceof BufferedUInt8Array2D)
        {
            return (BufferedUInt8Array2D) array;
        }
        
        // allocate memory
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        BufferedUInt8Array2D res = new BufferedUInt8Array2D(sizeX, sizeY);
        
        // copy values
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                res.setByte(x, y, array.getByte(x, y));
            }
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
     * @param size1
     *            the size of the array along the second dimension
     */
    public BufferedUInt8Array2D(int size0, int size1)
    {
        super(size0, size1);
        this.buffer = new byte[size0 * size1];
    }

    /**
     * 
     * @param size0
     *            the size of the array along the first dimension
     * @param size1
     *            the size of the array along the second dimension
     * @param buffer
     *            the buffer containing the byte values
     */
    public BufferedUInt8Array2D(int size0, int size1, byte[] buffer)
    {
        super(size0, size1);
        if (buffer.length < size0 * size1)
        {
            throw new IllegalArgumentException("Buffer size does not match image dimensions");
        }
        this.buffer = buffer;
    }
    

    // =============================================================
    // Implementation of the UInt8Array2D interface

    /* (non-Javadoc)
     * @see net.sci.array.scalar.UInt8Array2D#getByte(int, int)
     */
    @Override
    public byte getByte(int x, int y)
    {
        int index = x + y * this.size0;
        return this.buffer[index];
    }
    
    /* (non-Javadoc)
     * @see net.sci.array.scalar.UInt8Array2D#setByte(int, int, byte)
     */
    @Override
    public void setByte(int x, int y, byte b)
    {
        int index = x + y * this.size0;
        this.buffer[index] = b;
    }
    
    
    // =============================================================
    // Implementation of the UInt8Array interface

    /* (non-Javadoc)
     * @see net.sci.array.scalar.UInt8Array2D#getByte(int[])
     */
    @Override
    public byte getByte(int[] pos)
    {
        int index = pos[0] + pos[1] * this.size0;
        return this.buffer[index];
    }
    
    /* (non-Javadoc)
     * @see net.sci.array.data.scalar2d.UInt8Array2D#setByte(int[], byte)
     */
    @Override
    public void setByte(int[] pos, byte b)
    {
        int index = pos[0] + pos[1] * this.size0;
        this.buffer[index] = b;
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
            return (double) (buffer[index] & 0x00FF);
        }
    }
    
    
	// =============================================================
	// Implementation of the Array interface

	@Override
	public UInt8Array2D duplicate()
	{
		byte[] buffer2 = new byte[size0 * size1];
		System.arraycopy(this.buffer, 0, buffer2, 0, size0 * size1);
		return new BufferedUInt8Array2D(size0, size1, buffer2);
	}
    
    
	// =============================================================
	// Implementation of the Iterable interface

	public UInt8Array.Iterator iterator()
	{
		return new Iterator();
	}
	
	private class Iterator implements UInt8Array.Iterator
	{
		int index = -1;
		
		public Iterator() 
		{
		}
		
		@Override
		public boolean hasNext()
		{
			return this.index < (size0 * size1 - 1);
		}

		@Override
		public UInt8 next()
		{
			this.index++;
			return new UInt8(buffer[index]);
		}

		@Override
		public void forward()
		{
			this.index++;
		}

		@Override
		public UInt8 get()
		{
			return new UInt8(buffer[index]);
		}

		@Override
		public void set(UInt8 value)
		{
			buffer[index] = value.getByte();
		}
		
		@Override
		public byte getByte()
		{
			return buffer[index];
		}

		@Override
		public void setByte(byte b)
		{
			buffer[index] = b;
		}
	}

}
