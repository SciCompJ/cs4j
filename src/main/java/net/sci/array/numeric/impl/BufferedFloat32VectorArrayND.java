/**
 * 
 */
package net.sci.array.numeric.impl;

import net.sci.array.numeric.Float32Vector;
import net.sci.array.numeric.Float32VectorArray;
import net.sci.array.numeric.Float32VectorArrayND;
import net.sci.util.MathUtils;

/**
 * Implementation of Float32VectorArrayND based on an inner buffer of float.
 * 
 * @author dlegland
 *
 */
public class BufferedFloat32VectorArrayND extends Float32VectorArrayND
{
	// =============================================================
	// Class members

	float[] buffer;
	
	int vectorLength;
	
	
	// =============================================================
	// Constructors

	/**
	 * Initialize a new array of vectors.
	 * 
	 * @param sizes
	 *            the array of sizes of the array
	 * @param sizeV
	 *            the number of components of vectors
	 */
	public BufferedFloat32VectorArrayND(int[] sizes, int sizeV)
	{
		super(sizes);
		this.vectorLength = sizeV;
        
        // check validity of input size array
        long elCount = MathUtils.prod(sizes) * sizeV;
        if (elCount > Integer.MAX_VALUE - 8)
        {
            throw new IllegalArgumentException("Total element count is larger than maximal size for java arays");
        }
        
        // allocate buffer
		this.buffer = new float[(int) elCount];
	}

	/**
	 * Initialize a new array of vectors, using the specified buffer.
	 * 
     * 
     * @param sizes
     *            the array of sizes of the array
     * @param sizeV
     *            the number of components of vectors
	 * @param buffer
	 *            the buffer containing the double values
	 */
	public BufferedFloat32VectorArrayND(int[] sizes, int sizeV, float[] buffer)
	{
		super(sizes);
		this.vectorLength = sizeV;
		if (buffer.length < MathUtils.prod(sizes) * sizeV)
		{
			throw new IllegalArgumentException("Buffer size does not match array dimensions");
		}
		this.buffer = buffer;
	}
	

    // =============================================================
    // Implementation of Float32VectorArray interface

    @Override
    public float getFloat(int[] pos, int channel)
    {
        int index = subsToInd(pos) * vectorLength + channel;
        return this.buffer[index];
    }

    @Override
    public void setFloat(int[] pos, int channel, float value)
    {
        int index = subsToInd(pos) * vectorLength + channel;
        this.buffer[index] = value;
    }
    
	
	// =============================================================
	// Implementation of the VectorArray interface

	@Override
    public int channelCount()
    {
        return this.vectorLength;
    }

    @Override
    public double[] getValues(int[] pos)
    {
        return getValues(pos, new double[vectorLength]);
    }

    @Override
    public double[] getValues(int[] pos, double[] values)
    {
        int index = subsToInd(pos) * vectorLength;
        System.arraycopy(this.buffer, index, values, 0, vectorLength);
        return values;
    }

    @Override
    public void setValues(int[] pos, double[] values)
    {
        int index = subsToInd(pos) * vectorLength;
        System.arraycopy(values, 0, this.buffer, index, vectorLength);
    }

    @Override
    public double getValue(int[] pos, int channel)
    {
        int index = subsToInd(pos) * vectorLength + channel;
        return this.buffer[index];
    }

    @Override
    public void setValue(int[] pos, int channel, double value)
    {
        int index = subsToInd(pos) * vectorLength + channel;
        this.buffer[index] = (float) value;
    }

    
    // =============================================================
    // Implementation of the Array interface
    
    @Override
    public Float32Vector get(int[] pos)
    {
        return new Float32Vector(getValues(pos, new double[vectorLength]));
    }

    @Override
    public void set(int[] pos, Float32Vector vect)
    {
        setValues(pos, vect.getValues());
    }

    /* (non-Javadoc)
	 * @see net.sci.array.data.vector.VectorArray2D#duplicate()
	 */
	@Override
	public Float32VectorArray duplicate()
	{
		float[] buffer2 = new float[buffer.length];
		int n = buffer.length;
		System.arraycopy(this.buffer, 0, buffer2, 0, n);
		return new BufferedFloat32VectorArrayND(this.sizes, this.vectorLength, buffer2);
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
		int maxIndex;
		
		public Iterator() 
		{
	        maxIndex = (int) MathUtils.prod(sizes) - 1;
		}
		
		@Override
		public boolean hasNext()
		{
			return this.index < maxIndex;
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
		public double getValue(int c)
		{
			int ind = index * vectorLength + c;
			return buffer[ind];
		}

        @Override
        public double[] getValues(double[] values)
        {
            int ind = index * vectorLength;
            for (int c = 0; c < vectorLength; c++)
            {
                values[c] = buffer[ind + c];
            }
            return values;
        }

		@Override
		public void setValue(int c, double value)
		{
			int ind = index * vectorLength + c;
			buffer[ind] = (float) value;
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
	}

}
