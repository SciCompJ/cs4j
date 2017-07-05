/**
 * 
 */
package net.sci.array.data.scalarnd;

import net.sci.array.data.Float32Array;
import net.sci.array.type.Float32;


/**
 * @author dlegland
 *
 */
public class BufferedFloat32ArrayND extends Float32ArrayND
{
	// =============================================================
	// Class fields

	float[] buffer;

	
	// =============================================================
	// Constructors

	/**
	 * Initialize a new array of floats.
	 * 
	 * @param sizes the dimensions of this array
	 */
	public BufferedFloat32ArrayND(int[] sizes)
	{
		super(sizes);
		int bufferSize = 1;
		for (int i = 0; i < sizes.length; i++)
		{
			bufferSize *= sizes[i];
		}
		this.buffer = new float[bufferSize]; 
	}

	/**
	 * Initialize a new multidimensional array, using the specified buffer.
	 * 
	 * @param sizes
	 *            the dimensions of this image
	 * @param buffer
	 *            the array containing buffer for this image
	 */
	public BufferedFloat32ArrayND(int[] sizes, float[] buffer)
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
	public Float32Array duplicate()
	{
		int n = buffer.length;
		float[] buffer2 = new float[n];
		System.arraycopy(this.buffer, 0, buffer2, 0, n);
		return new BufferedFloat32ArrayND(sizes, buffer2);
	}

	@Override
	public Float32 get(int[] pos)
	{
		int index = subsToInd(pos);
		return new Float32(this.buffer[index]);	
	}

	@Override
	public void set(int[] pos, Float32 value)
	{
		int index = subsToInd(pos);
		this.buffer[index] = (float) value.getValue();
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
		this.buffer[index] = (float) value;
	}
	
	@Override
	public Float32Array.Iterator iterator()
	{
		return new Float32Iterator();
	}

	private class Float32Iterator implements Float32Array.Iterator
	{
		int index;
		int indexMax;
			
		public Float32Iterator()
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
		public Float32 next()
		{
			return new Float32(buffer[++index]);
		}

		@Override
		public void forward()
		{
			++index;
		}

		@Override
		public Float32 get()
		{
			return new Float32(buffer[index]);
		}

		@Override
		public void set(Float32 value)
		{
			buffer[index] = (float) value.getValue();
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
			buffer[index] = (float) value;
		}
	}
}
