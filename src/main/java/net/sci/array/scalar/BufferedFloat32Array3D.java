/**
 * 
 */
package net.sci.array.scalar;

import net.sci.array.Array;

/**
 * Implementation of Float32Array3D that stores inner data in a linear array of
 * floats.
 * 
 * This implementation is limited by the total number of elements, that must be
 * less than maximum array index in java (in the order of 2^31).
 *
 * @see SlicedFloat32Array3D
 * 
 * @author dlegland
 *
 */
public class BufferedFloat32Array3D extends Float32Array3D
{
    // =============================================================
    // Static methods

    /**
     * Converts the input array into an instance of the BufferedFloat32Array3D
     * class. May return the input array if it is already an instance of
     * BufferedFloat32Array3D.
     * 
     * @param array
     *            the array to convert
     * @return an instance of BufferedFloat32Array3D containing the same values
     *         as the input array.
     */
    public static final BufferedFloat32Array3D convert(Float32Array3D array)
    {
        // if array is of correct class, simply use class cast
        if (array instanceof BufferedFloat32Array3D)
        {
            return (BufferedFloat32Array3D) array;
        }
        
        // allocate memory
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
        BufferedFloat32Array3D res = new BufferedFloat32Array3D(sizeX, sizeY, sizeZ);
        
        // copy values
        for (int z = 0; z < sizeZ; z++)
        {
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    res.setFloat(x, y, z, array.getFloat(x, y, z));
                }
            }
        }
        // return converted array
        return res;
    }
    
    
    // =============================================================
    // Class fields

    /**
     * The array of floats that stores array values.
     */
	float[] buffer;

	
	// =============================================================
	// Constructors

	/**
	 * @param size0
	 *            the size of the array along the first dimension
	 * @param size1
	 *            the size of the array along the second dimension
	 * @param size2
	 *            the size of the array along the third dimension
	 */
	public BufferedFloat32Array3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
        
        // check validity of input size array
        long elCount = Array.prod(size0, size1, size2);
        if (elCount > Integer.MAX_VALUE - 8)
        {
            throw new IllegalArgumentException("Total element count is larger than maximal size for java arays");
        }
        
        // allocate buffer
		this.buffer = new float[size0 * size1 * size2];
	}

	/**
	 * 
	 * @param size0
	 *            the size of the array along the first dimension
	 * @param size1
	 *            the size of the array along the second dimension
	 * @param size2
	 *            the size of the array along the third dimension
	 * @param buffer
	 *            the buffer containing the float values
	 */
	public BufferedFloat32Array3D(int size0, int size1, int size2, float[] buffer)
	{
		super(size0, size1, size2);
		if (buffer.length < Array.prod(size0, size1, size2))
		{
			throw new IllegalArgumentException("Buffer size does not match image dimensions");
		}
		this.buffer = buffer;
	}

    // =============================================================
    // Specialization of FloatArray3D

    @Override
    public void setFloat(int x, int y, int z, float f)
    {
        int index = x + this.size0 * (y + z * this.size1);
        this.buffer[index] = f;
    }

    public float getFloat(int... pos)
    {
        int index = pos[0] + this.size0 * (pos[1] + pos[2] * this.size1);
        return this.buffer[index];
    }

    public void setFloat(int[] pos, float value)
    {
        int index = pos[0] + this.size0 * (pos[1] + pos[2] * this.size1);
        this.buffer[index] = value;
    }
    

    // =============================================================
    // Specialization of the Array3D class

	@Override
	public double getValue(int... pos)
	{
		int index = pos[0] + this.size0 * (pos[1] + pos[2] * this.size1);
		return this.buffer[index];
	}

	@Override
	public void setValue(int[] pos, double value)
    {
	    int index = pos[0] + this.size0 * (pos[1] + pos[2] * this.size1);
        this.buffer[index] = (float) value;
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
            return (double) buffer[index];
        }
    }
    

    // =============================================================
	// Specialization of the Array interface

	@Override
	public Float32Array3D duplicate()
	{
		float[] buffer2 = new float[size0 * size1 * size2];
		System.arraycopy(this.buffer, 0, buffer2, 0, size0 * size1 * size2);
		return new BufferedFloat32Array3D(size0, size1, size2, buffer2);
	}

	
	// =============================================================
	// Implementation of the Iterator interface

	public Float32Array.Iterator iterator()
	{
		return new FloatIterator();
	}
	
	private class FloatIterator implements Float32Array.Iterator
	{
		int index = -1;
		
		public FloatIterator() 
		{
		}
		
		@Override
		public boolean hasNext()
		{
			return this.index < (size0 * size1 * size2 - 1);
		}

		@Override
		public Float32 next()
		{
			this.index++;
			return new Float32(buffer[index]);
		}

		@Override
		public void forward()
		{
			this.index++;
		}

		@Override
		public Float32 get()
		{
			return new Float32(buffer[index]);
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
