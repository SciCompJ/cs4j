/**
 * 
 */
package net.sci.array.data.scalarnd;

import net.sci.array.data.Float64Array;
import net.sci.array.type.Float64;


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
	 * @param sizes the dimensions of this array
	 */
	public BufferedFloat64ArrayND(int[] sizes)
	{
		super(sizes);
		int bufferSize = 1;
		for (int i = 0; i < sizes.length; i++)
		{
			bufferSize *= sizes[i];
		}
		this.buffer = new double[bufferSize]; 
	}

	/**
	 * Initialize a new multidimensional array, using the specified buffer.
	 * 
	 * @param sizes
	 *            the dimensions of this image
	 * @param the
	 *            array containing buffer for this image
	 */
	public BufferedFloat64ArrayND(int[] sizes, double[] buffer)
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
	public Float64 get(int[] pos)
	{
		int index = subsToInd(pos);
		return new Float64(this.buffer[index]);	
	}

	@Override
	public void set(int[] pos, Float64 value)
	{
		int index = subsToInd(pos);
		this.buffer[index] = value.getValue();
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
			buffer[index] = value.getValue();
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
