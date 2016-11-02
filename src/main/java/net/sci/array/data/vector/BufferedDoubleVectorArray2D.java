/**
 * 
 */
package net.sci.array.data.vector;

import net.sci.array.data.DoubleVectorArray;
import net.sci.array.type.DoubleVector;

/**
 * @author dlegland
 *
 */
public class BufferedDoubleVectorArray2D extends DoubleVectorArray2D
{
	// =============================================================
	// Class members

	double[] buffer;
	
	int vectorLength;
	
	
	// =============================================================
	// Constructors

	/**
	 * @param size0 array size in the first dimension
	 * @param size1 array size in the second dimension
	 * @param sizeV number of components of vectors
	 */
	public BufferedDoubleVectorArray2D(int size0, int size1, int sizeV)
	{
		super(size0, size1);
		this.vectorLength = sizeV;
		this.buffer = new double[size0 * size1 * sizeV];
	}

	/**
	 * @param size0 array size in the first dimension
	 * @param size1 array size in the second dimension
	 * @param sizeV number of components of vectors
	 */
	public BufferedDoubleVectorArray2D(int size0, int size1, int sizeV, double[] buffer)
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
		this.buffer[offset + c] = value;
	}

	
	// =============================================================
	// Implementation of the Array interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.VectorArray#newInstance(int[])
	 */
	@Override
	public DoubleVectorArray newInstance(int... dims)
	{
		return DoubleVectorArray.create(dims, this.vectorLength);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.vector.VectorArray2D#duplicate()
	 */
	@Override
	public DoubleVectorArray2D duplicate()
	{
		double[] buffer2 = new double[buffer.length];
		int n = this.size0 * this.size1 * this.vectorLength;
		System.arraycopy(this.buffer, 0, buffer2, 0, n);
		return new BufferedDoubleVectorArray2D(this.size0, this.size1, this.vectorLength, buffer2);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.vector.VectorArray2D#getValues(int, int)
	 */
	@Override
	public double[] getValues(int x, int y)
	{
		double[] res = new double[this.vectorLength];
		int offset = (y * this.size0 + x) * this.vectorLength;
		System.arraycopy(this.buffer, offset, res, 0, this.vectorLength);
		return res;
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.vector.VectorArray2D#setValues(int, int, double[])
	 */
	@Override
	public void setValues(int x, int y, double[] values)
	{
		int offset = (y * this.size0 + x) * this.vectorLength;
		System.arraycopy(values, 0, this.buffer, offset, this.vectorLength);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#get(int, int)
	 */
	@Override
	public DoubleVector get(int x, int y)
	{
		return new DoubleVector(getValues(x, y));
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#set(int, int, java.lang.Object)
	 */
	@Override
	public void set(int x, int y, DoubleVector vect)
	{
		setValues(x, y, vect.getValues());
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.VectorArray#iterator()
	 */
	@Override
	public DoubleVectorArray.Iterator iterator()
	{
		return new Iterator();
	}

	private class Iterator implements DoubleVectorArray.Iterator
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
		public DoubleVector next()
		{
			this.index++;
			double[] vals = new double[vectorLength];
			int offset = index * vectorLength;
			System.arraycopy(buffer, offset, vals, 0, vectorLength);
			return new DoubleVector(vals);
		}

		@Override
		public void forward()
		{
			this.index++;
		}

		@Override
		public DoubleVector get()
		{
			double[] vals = new double[vectorLength];
			int offset = index * vectorLength;
			System.arraycopy(buffer, offset, vals, 0, vectorLength);
			return new DoubleVector(vals);
		}

		@Override
		public void set(DoubleVector vect)
		{
			double[] vals = vect.getValues();
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
			buffer[index] = value;
			for (int i = 1; i < vectorLength; i++)
			{
				buffer[++offset] = 0;
			}
		}
	}

}
