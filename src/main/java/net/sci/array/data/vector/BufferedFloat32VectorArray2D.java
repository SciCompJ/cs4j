/**
 * 
 */
package net.sci.array.data.vector;

import net.sci.array.data.Float32VectorArray;
import net.sci.array.type.Float32Vector;

/**
 * @author dlegland
 *
 */
public class BufferedFloat32VectorArray2D extends Float32VectorArray2D
{
	// =============================================================
	// Class members

	float[] buffer;
	
	int vectorLength;
	
	
	// =============================================================
	// Constructors

	/**
	 * @param size0 array size in the first dimension
	 * @param size1 array size in the second dimension
	 * @param sizeV number of components of vectors
	 */
	public BufferedFloat32VectorArray2D(int size0, int size1, int sizeV)
	{
		super(size0, size1);
		this.vectorLength = sizeV;
		this.buffer = new float[size0 * size1 * sizeV];
	}

	/**
	 * @param size0 array size in the first dimension
	 * @param size1 array size in the second dimension
	 * @param sizeV number of components of vectors
	 */
	public BufferedFloat32VectorArray2D(int size0, int size1, int sizeV, float[] buffer)
	{
		super(size0, size1);
		this.vectorLength = sizeV;
		if (buffer.length < size0 * size1 * sizeV)
		{
			throw new IllegalArgumentException("Buffer size does not match array dimensions");
		}
		this.buffer = buffer;
	}

	
	// =============================================================
	// Implementation of the VectorArray interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.VectorArray#getVectorLength()
	 */
	@Override
	public int getVectorLength()
	{
		return this.vectorLength;
	}

	@Override
	public double getValue(int x, int y, int c)
	{
		int offset = (y * this.size0 + x) * this.vectorLength;
		return this.buffer[offset + c];
	}


	@Override
	public void setValue(int x, int y, int c, double value)
	{
		int offset = (y * this.size0 + x) * this.vectorLength;
		this.buffer[offset + c] = (float) value;
	}

	
	// =============================================================
	// Implementation of the Array interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.VectorArray#newInstance(int[])
	 */
	@Override
	public Float32VectorArray newInstance(int... dims)
	{
		return Float32VectorArray.create(dims, this.vectorLength);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.vector.VectorArray2D#duplicate()
	 */
	@Override
	public Float32VectorArray2D duplicate()
	{
		float[] buffer2 = new float[buffer.length];
		int n = this.size0 * this.size1 * this.vectorLength;
		System.arraycopy(this.buffer, 0, buffer2, 0, n);
		return new BufferedFloat32VectorArray2D(this.size0, this.size1, this.vectorLength, buffer2);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.vector.VectorArray2D#getValues(int, int)
	 */
	@Override
	public double[] getValues(int x, int y)
	{
		double[] res = new double[this.vectorLength];
		int offset = (y * this.size0 + x) * this.vectorLength;
		for (int c = 0; c < this.vectorLength; c++)
		{
			res[c] = this.buffer[offset + c];
		}
		return res;
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.vector.VectorArray2D#setValues(int, int, double[])
	 */
	@Override
	public void setValues(int x, int y, double[] values)
	{
		int offset = (y * this.size0 + x) * this.vectorLength;
		for (int c = 0; c < this.vectorLength; c++)
		{
			this.buffer[offset + c] = (float) values[c];
		}
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#get(int, int)
	 */
	@Override
	public Float32Vector get(int x, int y)
	{
		return new Float32Vector(getValues(x, y));
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#set(int, int, java.lang.Object)
	 */
	@Override
	public void set(int x, int y, Float32Vector vect)
	{
		setValues(x, y, vect.getValues());
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.VectorArray#iterator()
	 */
	@Override
	public Float32VectorArray.Iterator iterator()
	{
		return new Iterator();
	}

	private class Iterator implements Float32VectorArray.Iterator
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
		public Float32Vector next()
		{
			this.index++;
			float[] vals = new float[vectorLength];
			int offset = index * vectorLength;
			System.arraycopy(buffer, offset, vals, 0, vectorLength);
			return new Float32Vector(vals);
		}

		@Override
		public void forward()
		{
			this.index++;
		}

		@Override
		public Float32Vector get()
		{
			float[] vals = new float[vectorLength];
			int offset = index * vectorLength;
			System.arraycopy(buffer, offset, vals, 0, vectorLength);
			return new Float32Vector(vals);
		}

		@Override
		public void set(Float32Vector vect)
		{
			float[] vals = vect.getFloats();
			int offset = index * vectorLength;
			System.arraycopy(vals, 0, buffer, offset, vectorLength);
		}

		@Override
		public double getValue()
		{
			double sum = 0;
			int offset = index * vectorLength;
			for (int i = 0; i < vectorLength; i++)
			{
				double v = buffer[offset++];
				sum += v *v;
			}
			return Math.sqrt(sum);
		}

		@Override
		public void setValue(double value)
		{
			int offset = index * vectorLength;
			buffer[index] = (float) value;
			for (int i = 1; i < vectorLength; i++)
			{
				buffer[++offset] = 0;
			}
		}
	}

}
