/**
 * 
 */
package net.sci.array.numeric.impl;

import net.sci.array.numeric.Float64Vector;
import net.sci.array.numeric.Float64VectorArray;
import net.sci.array.numeric.Float64VectorArray2D;

/**
 * Implementation of Float64VectorArray2D based on an inner buffer of double.
 * 
 * @author dlegland
 *
 */
public class BufferedFloat64VectorArray2D extends Float64VectorArray2D
{
	// =============================================================
	// Class members

	double[] buffer;
	
	int vectorLength;
	
	
	// =============================================================
	// Constructors

	/**
	 * Initialize a new array of vectors.
	 * 
	 * @param size0
	 *            array size in the first dimension
	 * @param size1
	 *            array size in the second dimension
	 * @param sizeV
	 *            number of components of vectors
	 */
	public BufferedFloat64VectorArray2D(int size0, int size1, int sizeV)
	{
		super(size0, size1);
		this.vectorLength = sizeV;
		this.buffer = new double[size0 * size1 * sizeV];
	}

	/**
	 * Initialize a new array of vectors, using the specified buffer.
	 * 
	 * @param size0
	 *            array size in the first dimension
	 * @param size1
	 *            array size in the second dimension
	 * @param sizeV
	 *            number of components of vectors
	 * @param buffer
	 *            the buffer containing the float values
	 */
	public BufferedFloat64VectorArray2D(int size0, int size1, int sizeV, double[] buffer)
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
	public int channelCount()
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
	public Float64VectorArray newInstance(int... dims)
	{
		return Float64VectorArray.create(dims, this.vectorLength);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.vector.VectorArray2D#duplicate()
	 */
	@Override
	public Float64VectorArray2D duplicate()
	{
		double[] buffer2 = new double[buffer.length];
		int n = this.size0 * this.size1 * this.vectorLength;
		System.arraycopy(this.buffer, 0, buffer2, 0, n);
		return new BufferedFloat64VectorArray2D(this.size0, this.size1, this.vectorLength, buffer2);
	}

    /* (non-Javadoc)
     * @see net.sci.array.data.vector.VectorArray2D#getValues(int, int)
     */
    @Override
    public double[] getValues(int x, int y)
    {
        double[] res = new double[this.vectorLength];
        return getValues(x, y, res);
    }

    @Override
    public double[] getValues(int x, int y, double[] values)
    {
        int offset = (y * this.size0 + x) * this.vectorLength;
        System.arraycopy(this.buffer, offset, values, 0, this.vectorLength);
        return values;
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
	public Float64Vector get(int[] pos)
	{
		return new Float64Vector(getValues(pos));
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.VectorArray#iterator()
	 */
	@Override
	public Float64VectorArray.Iterator iterator()
	{
		return new Iterator();
	}

	private class Iterator implements Float64VectorArray.Iterator
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
		public Float64Vector next()
		{
			this.index++;
			double[] vals = new double[vectorLength];
			int offset = index * vectorLength;
			System.arraycopy(buffer, offset, vals, 0, vectorLength);
			return new Float64Vector(vals);
		}

		@Override
		public void forward()
		{
			this.index++;
		}


		@Override
		public double getValue(int c)
		{
			int ind = index * vectorLength + c;
			return buffer[ind];
		}

        @Override
        public double[] getValues(double[] values)
        {
            int ind = index * vectorLength;
            System.arraycopy(buffer, ind, values, 0, vectorLength);
            return values;
        }

		@Override
		public void setValue(int c, double value)
		{
			int ind = index * vectorLength + c;
			buffer[ind] = value;
		}

        @Override
		public Float64Vector get()
		{
			double[] vals = new double[vectorLength];
			int offset = index * vectorLength;
			System.arraycopy(buffer, offset, vals, 0, vectorLength);
			return new Float64Vector(vals);
		}

		@Override
		public void set(Float64Vector vect)
		{
			double[] vals = vect.getValues();
			int offset = index * vectorLength;
			System.arraycopy(vals, 0, buffer, offset, vectorLength);
		}
	}

}
