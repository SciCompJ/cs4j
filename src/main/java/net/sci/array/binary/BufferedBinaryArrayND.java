/**
 * 
 */
package net.sci.array.binary;

import net.sci.util.MathUtils;

/**
 * @author dlegland
 *
 */
public class BufferedBinaryArrayND extends BinaryArrayND
{
	// =============================================================
	// Class fields

	boolean[] buffer;

	
	// =============================================================
	// Constructors

	/**
	 * @param sizes the dimensions of this array
	 */
	public BufferedBinaryArrayND(int[] sizes)
	{
		super(sizes);
        
        // check validity of input size array
        long elCount = MathUtils.prod(sizes);
        if (elCount > Integer.MAX_VALUE - 8)
        {
            throw new IllegalArgumentException("Total element count is larger than maximal size for java arays");
        }
        
        // allocate buffer
		this.buffer = new boolean[(int) elCount]; 
	}

	/**
	 * Initialize a new multidimensional array, using the specified buffer.
	 * 
	 * @param sizes
	 *            the dimensions of the array
	 * @param buffer
	 *            the array containing buffer for this image
	 */
	public BufferedBinaryArrayND(int[] sizes, boolean[] buffer)
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
	// Implementation of the BinaryArray interface
	
    /**
     * Fills this binary array with the specified boolean value.
     * 
     * @param state
     *            the value to fill the binary array with.
     */
    public void fill(boolean state)
    {
        for (int i = 0; i < buffer.length; i++)
        {
            buffer[i] = state;
        }
    }
    
	@Override
	public boolean getBoolean(int[] pos)
	{
		int index = subsToInd(pos);
		return this.buffer[index];	
	}

	@Override
	public void setBoolean(int[] pos, boolean value)
	{
		int index = subsToInd(pos);
		this.buffer[index] = value;	
	}

	// =============================================================
	// Implementation of the Array interface
	
	@Override
	public BinaryArray duplicate()
	{
		int n = buffer.length;
		boolean[] buffer2 = new boolean[n];
		System.arraycopy(this.buffer, 0, buffer2, 0, n);
		return new BufferedBinaryArrayND(sizes, buffer2);
	}

	@Override
	public Binary get(int[] pos)
	{
		int index = subsToInd(pos);
		return new Binary(this.buffer[index]);	
	}

	@Override
	public void set(int[] pos, Binary value)
	{
		int index = subsToInd(pos);
		this.buffer[index] = value.getBoolean();
	}

	@Override
	public BinaryArray.Iterator iterator()
	{
		return new BinaryIterator();
	}

	private class BinaryIterator implements BinaryArray.Iterator
	{
		int index;
		int indexMax;
			
		public BinaryIterator()
		{
			int n = 1;
			int nd = sizes.length;
			for (int d = 0; d < nd; d++)
			{
				n *= sizes[d];
			}
			this.index = -1;
			this.indexMax = n - 1;
		}
		
		@Override
		public boolean hasNext()
		{
			return index < indexMax;
		}
		
		@Override
		public Binary next()
		{
			return new Binary(buffer[++index]);
		}

		@Override
		public void forward()
		{
			++index;
		}

		@Override
		public Binary get()
		{
			return new Binary(buffer[index]);
		}

		@Override
		public void set(Binary value)
		{
			buffer[index] = value.getBoolean();
		}
		
		@Override
		public boolean getBoolean()
		{
			return buffer[index];
		}

		@Override
		public void setBoolean(boolean b)
		{
			buffer[index] = b;
		}
	}
}
