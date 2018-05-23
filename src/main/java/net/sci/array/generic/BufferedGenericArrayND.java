/**
 * 
 */
package net.sci.array.generic;

import net.sci.array.Array;
import net.sci.array.ArrayND;
import net.sci.array.scalar.GenericArray;


/**
 * Concrete array containing generic data stored in a linear Object buffer.
 * 
 * @author dlegland
 *
 */
public class BufferedGenericArrayND<T> extends ArrayND<T> implements GenericArray<T>
{
	// =============================================================
	// Class fields

    /** The buffer containing Array elements */
    Object[] buffer;
    
    /** initialization value, required for creating new arrays */
    T initValue;
	
	// =============================================================
	// Constructors

	/**
     * Initialize a new array containing generic data.
     * 
     * @param sizes
     *            the dimensions of this array
     * @param initValue
     *            the initial value, used to known the data type of elements
     */
	public BufferedGenericArrayND(int[] sizes, T initValue)
	{
		super(sizes);
		int bufferSize = 1;
		for (int i = 0; i < sizes.length; i++)
		{
			bufferSize *= sizes[i];
		}
		this.buffer = new Object[bufferSize]; 
        this.initValue = initValue;
        fill(initValue);
	}

	/**
	 * Initialize a new multidimensional array, using the specified buffer.
	 * 
	 * @param sizes
	 *            the dimensions of this image
	 * @param buffer
	 *            the array containing buffer for this image
	 */
	public BufferedGenericArrayND(int[] sizes, T[] buffer)
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
        this.initValue = buffer[0];
	}


	// =============================================================
	// Implementation of the Array interface
	
	@Override
	@SuppressWarnings("unchecked")
    public Array<T> duplicate()
	{
        int n = this.buffer.length;
        Object[] newBuffer = new Object[n];
        System.arraycopy(this.buffer, 0, newBuffer, 0, n);
        return new BufferedGenericArrayND<T>(this.sizes, (T[]) newBuffer);
	}

	@Override
	@SuppressWarnings("unchecked")
    public T get(int[] pos)
	{
		int index = subsToInd(pos);
		return (T) this.buffer[index];	
	}

	@Override
	public void set(int[] pos, T value)
	{
		int index = subsToInd(pos);
		this.buffer[index] = (T) value;
	}

	@Override
	public double getValue(int[] pos)
	{
	    throw new RuntimeException("Unimplemented operation");
	}

	@Override
	public void setValue(int[] pos, double value)
	{
	    throw new RuntimeException("Unimplemented operation");
	}
	
    @SuppressWarnings("unchecked")
    @Override
    public Class<T> getDataType()
    {
        return (Class<T>) initValue.getClass();
    }

    @Override
	public Array.Iterator<T> iterator()
	{
		return new Iterator();
	}

	private class Iterator implements Array.Iterator<T>
	{
		int index;
		int indexMax;
			
		public Iterator()
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
        @SuppressWarnings("unchecked")
		public T next()
		{
			return (T) buffer[++index];
		}

		@Override
		public void forward()
		{
			++index;
		}

		@Override
		@SuppressWarnings("unchecked")
        public T get()
		{
			return (T) buffer[index];
		}

		@Override
		public void set(T value)
		{
			buffer[index] = (T) value;
		}

		@Override
		public double nextValue()
		{
		    throw new RuntimeException("Unimplemented operation");
		}

		@Override
		public double getValue()
		{
		    throw new RuntimeException("Unimplemented operation");
		}

		@Override
		public void setValue(double value)
		{
			buffer[index] = (float) value;
		}
	}
}
