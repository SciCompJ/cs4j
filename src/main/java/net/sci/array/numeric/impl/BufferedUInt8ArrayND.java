/**
 * 
 */
package net.sci.array.numeric.impl;

import net.sci.array.numeric.UInt8;
import net.sci.array.numeric.UInt8Array;
import net.sci.array.numeric.UInt8ArrayND;
import net.sci.util.MathUtils;

/**
 * @author dlegland
 *
 */
public class BufferedUInt8ArrayND extends UInt8ArrayND
{
	// =============================================================
	// Class fields

	byte[] buffer;

	
	// =============================================================
	// Constructors

	/**
	 * @param sizes the dimensions of this array
	 */
	public BufferedUInt8ArrayND(int[] sizes)
	{
		super(sizes);
        
        // check validity of input size array
        long elCount = MathUtils.prod(sizes);
        if (elCount > Integer.MAX_VALUE - 8)
        {
            throw new IllegalArgumentException("Total element count is larger than maximal size for java arays");
        }
        
        // allocate buffer
		this.buffer = new byte[(int) elCount]; 
	}

	/**
	 * Initialize a new multidimensional array, using the specified buffer.
	 * 
	 * @param sizes
	 *            the dimensions of the array
	 * @param buffer
	 *            the array containing buffer for this image
	 */
	public BufferedUInt8ArrayND(int[] sizes, byte[] buffer)
	{
		super(sizes);
		if (buffer.length != MathUtils.prod(sizes))
		{
			throw new IllegalArgumentException("Size of image and buffer do not match");
		}
		
		this.buffer = buffer;
	}


	// =============================================================
	// New specific methods
	

	// =============================================================
	// Implementation of the UInt8Array interface
	
	@Override
	public byte getByte(int[] pos)
	{
		int index = subsToInd(pos);
		return this.buffer[index];	
	}

	@Override
	public void setByte(int[] pos, byte value)
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
		return this.buffer[index] & 0x00FF;	
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.IntArray#setInt(int[], int)
	 */
	@Override
	public void setInt(int[] pos, int intValue)
	{
		int index = subsToInd(pos);
		intValue = Math.min(Math.max(intValue, 0), 255);
		this.buffer[index] = (byte) intValue;
	}

	@Override
	public UInt8 get(int[] pos)
	{
		int index = subsToInd(pos);
		return new UInt8(this.buffer[index]);	
	}

	@Override
	public void set(int[] pos, UInt8 value)
	{
		int index = subsToInd(pos);
		this.buffer[index] = value.getByte();
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
    public UInt8Array duplicate()
    {
    	int n = buffer.length;
    	byte[] buffer2 = new byte[n];
    	System.arraycopy(this.buffer, 0, buffer2, 0, n);
    	return new BufferedUInt8ArrayND(sizes, buffer2);
    }

    @Override
	public UInt8Array.Iterator iterator()
	{
		return new UInt8Iterator();
	}

	private class UInt8Iterator implements UInt8Array.Iterator
	{
		int index;
		int indexMax;
			
		public UInt8Iterator()
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
		public UInt8 next()
		{
			return new UInt8(buffer[++index]);
		}

		@Override
		public void forward()
		{
			++index;
		}

		@Override
		public UInt8 get()
		{
			return new UInt8(buffer[index]);
		}

		@Override
		public int getInt()
		{
			return buffer[index] & 0x00FF;
		}

		@Override
		public void set(UInt8 value)
		{
			buffer[index] = value.getByte();
		}
		
		@Override
		public void setInt(int value)
		{
			buffer[index] = (byte) value; 
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

//	/**
//	 * Iterates over the positions within the array.
//	 * 
//	 * @author dlegland
//	 *
//	 */
//	public class Cursor exte.Cursor
//	{
//		int[] pos;
//		int nd;
//
//		public Cursor()
//		{
//			this.nd = sizes.length;
//			this.pos = new int[this.nd];
//			for (int d = 0; d < this.nd - 1; d++)
//			{
//				this.pos[d] = sizes[d] - 1;
//			}
//			this.pos[this.nd - 2] = -1;
//		}
//		
//		public int[] getPosition()
//		{
//			int[] res = new int[nd];
//			System.arraycopy(this.pos, 0, res, 0, nd);
//			return res;
//		}
//		
//		public boolean hasNext()
//		{
//			for (int d = 0; d < nd; d++)
//			{
//				if (this.pos[d] < sizes[d] - 1)
//					return true;
//			}
//			return false;
//		}
//		
//		public void forward()
//		{
//			incrementDim(0);
//		}
//		
//		private void incrementDim(int d)
//		{
//			this.pos[d]++;
//			if (this.pos[d] == sizes[d] && d < nd - 1)
//			{
//				this.pos[d] = 0;
//				incrementDim(d + 1);
//			}
//		}
//	}
}
