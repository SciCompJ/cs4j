/**
 * 
 */
package net.sci.array.numeric;

/**
 * @author dlegland
 *
 */
public abstract class Float32Array2D extends ScalarArray2D<Float32> implements Float32Array
{
	// =============================================================
	// Static methods

	public static final Float32Array2D create(int size0, int size1)
	{
		return wrap(Float32Array.create(size0, size1));
	}
	
    /**
     * Creates a new Float32Array2D from a two-dimensional array of floats. The
     * first index of the float array is the second dimension of the result array,
     * i.e. <code>floatArray[y][x]</code> is the same value as
     * <code>array.getFloat(x,y)</code>.
     * 
     * @param floatArray
     *            the array of floats containing the values.
     * @return a new instance of Float32Array2D initialized with the values of
     *         <code>floatArray</code>
     */
    public static final Float32Array2D fromFloatArray(float[][] floatArray)
    {
        int size1 = floatArray.length;
        int size0 = floatArray[0].length;
        Float32Array2D res = Float32Array2D.create(size0, size1);
        for (int y = 0; y < size1; y++)
        {
            for (int x = 0; x < size0; x++)
            {
                res.setFloat(x, y, floatArray[y][x]);
            }
        }
        return res;
    }
    
    /**
     * Encapsulates the specified instance of Float32Array into a new
     * Float32Array2D, by creating a Wrapper if necessary. If the original array
     * is already an instance of Float32Array2D, it is returned.
     * 
     * @param array
     *            the original array
     * @return a Float32Array2D view of the original array
     */
    public static Float32Array2D wrap(Float32Array array)
    {
        if (array instanceof Float32Array2D)
        { 
            return (Float32Array2D) array; 
        }
        return new Wrapper(array);
    }
    

	// =============================================================
	// Constructor

	/**
	 * Initialize the protected size variables. 
	 *
	 * @param size0
	 *            the size of the array along the first dimension
	 * @param size1
	 *            the size of the array along the second dimension
	 */
	protected Float32Array2D(int size0, int size1)
	{
		super(size0, size1);
	}
	

    // =============================================================
    // New methods
	
    public abstract float getFloat(int x, int y);

    public abstract void setFloat(int x, int y, float value);


    // =============================================================
    // Specialization of FloatArray 

    @Override
    public float getFloat(int[] pos)
    {
        return getFloat(pos[0], pos[1]);
    }

    @Override
    public void setFloat(int[] pos, float floatValue)
    {
        setFloat(pos[0], pos[1], floatValue);
    }

    
    // =============================================================
    // Specialization of ScalarArray2D 

    @Override
    public double getValue(int x, int y)
    {
        return getFloat(x, y);
    }

    @Override
    public void setValue(int x, int y, double value)
    {
        setFloat(x, y, (float) value);
    }
    

    // =============================================================
    // Specialization of Array2D 

    @Override
    public void set(int x, int y, Float32 value)
    {
        setFloat(x, y, value.value);
    }
    
    @Override
    public Float32 get(int x, int y)
    {
        return new Float32(getFloat(x, y));
    }
    
    /* (non-Javadoc)
     * @see net.sci.array.scalar.ScalarArray#getValue(int[])
     */
    @Override
    public double getValue(int[] pos)
    {
        return getValue(pos[0], pos[1]);
    }
    
    /* (non-Javadoc)
     * @see net.sci.array.scalar.ScalarArray#setValue(int[], double)
     */
    @Override
    public void setValue(int[] pos, double value)
    {
        setValue(pos[0], pos[1], value);
    }
    
	
	// =============================================================
	// Specialization of Array 

	@Override
	public Float32Array newInstance(int... dims)
	{
		return Float32Array.create(dims);
	}

	@Override
    public Float32Array2D duplicate()
    {
        Float32Array2D res = Float32Array2D.create(size0, size1);
        for (int[] pos : res.positions())
        {
            res.setValue(pos,  this.getValue(pos));
        }
        return res;
    }
	

    // =============================================================
    // Implementation of inner classes
    
    /**
     * Wraps a Float32 array with two dimensions into a Float32Array2D.
     */
    private static class Wrapper extends Float32Array2D
    {
        Float32Array array;

        public Wrapper(Float32Array array)
        {
            super(0, 0);
            if (array.dimensionality() != 2)
            {
                throw new IllegalArgumentException("Requires an array of dimensionality equal to 2.");
            }
            this.size0 = array.size(0);
            this.size1 = array.size(1);
            this.array = array;
        }
        
        @Override
        public float getFloat(int x, int y)
        {
            return this.array.getFloat(new int[] {x, y});
        }

        @Override
        public void setFloat(int x, int y, float floatValue)
        {
            this.array.setFloat(new int[] {x, y}, floatValue);
        }

        @Override
        public float getFloat(int[] pos)
        {
            return this.array.getFloat(pos);
        }

        @Override
        public void setFloat(int[] pos, float floatValue)
        {
            this.array.setFloat(pos, floatValue);
        }

        @Override
        public double getValue(int[] pos)
        {
            return this.array.getValue(pos);
        }

        @Override
        public void setValue(int[] pos, double value)
        {
            this.array.setValue(pos, value);
        }
        
        /**
         * Simply returns an iterator on the original array.
         */
        @Override
        public net.sci.array.numeric.Float32Array.Iterator iterator()
        {
            return this.array.iterator();
        }
    }
}
