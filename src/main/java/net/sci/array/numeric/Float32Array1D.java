/**
 * 
 */
package net.sci.array.numeric;

/**
 * Specialization of Array for 1D arrays of Float32 values.
 * 
 * @author dlegland
 *
 */
public abstract class Float32Array1D extends ScalarArray1D<Float32> implements Float32Array
{
	// =============================================================
	// Static methods

	public static final Float32Array1D create(int size0)
	{
	    return wrap(Float32Array.create(size0));
	}
	
    /**
     * Creates a new Float32Array1D from an array of floats.
     * 
     * @param array
     *            the array of floats containing the values.
     * @return a new instance of Float32Array1D initialized with the values of
     *         <code>array</code>
     */
    public static final Float32Array1D fromFloatArray(float[] array)
    {
        int size0 = array.length;
        Float32Array1D res = Float32Array1D.create(size0);
        for (int x = 0; x < size0; x++)
        {
            res.setFloat(x, array[x]);
        }
        return res;
    }
    
    /**
     * Encapsulates the specified instance of Float32Array into a new
     * Float32Array1D, by creating a Wrapper if necessary. If the original array
     * is already an instance of Float32Array1D, it is returned.
     * 
     * @param array
     *            the original array
     * @return a Float32Array1D view of the original array
     */
    public static Float32Array1D wrap(Float32Array array)
    {
        if (array instanceof Float32Array1D)
        { 
            return (Float32Array1D) array; 
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
	protected Float32Array1D(int size0)
	{
		super(size0);
	}

	
    // =============================================================
    // New methods
    
    public abstract float getFloat(int x);
    
    public abstract void setFloat(int x, float value);
    
    
    // =============================================================
    // Specialization of FloatArray 

    @Override
    public float getFloat(int[] pos)
    {
        return getFloat(pos[0]);
    }

    @Override
    public void setFloat(int[] pos, float floatValue)
    {
        setFloat(pos[0], floatValue);
    }

    @Override
    public Float32 get(int x)
    {
        return new Float32(getFloat(x));
    }
    
    @Override
    public double getValue(int x)
    {
        return getFloat(x);
    }

    @Override
    public void setValue(int x, double value)
    {
        setFloat(x, (float) value);
    }


	// =============================================================
	// Specialization of Array interface
	
    @Override
    public Float32Array1D duplicate()
    {
        // create output array
        Float32Array1D res = Float32Array1D.wrap(Float32Array.create(this.size0));
        for (int x = 0; x < size0; x++)
        {
            res.setFloat(x, getFloat(x));
        }
        return res;
    }

    @Override
    public void set(int x, Float32 value)
    {
        setFloat(x, value.value);
    }
    
    
    // =============================================================
    // Implementation of inner classes
    
    /**
     * Wraps a Float32 array with two dimensions into a Float32Array1D.
     */
    private static class Wrapper extends Float32Array1D
    {
        Float32Array array;

        public Wrapper(Float32Array array)
        {
            super(0);
            this.size0 = array.size(0);
            this.array = array;
        }

        @Override
        public float getFloat(int x)
        {
            return this.array.getFloat(new int[] {x});
        }

        @Override
        public void setFloat(int x, float value)
        {
            this.array.setFloat(new int[] {x}, value);
        }

        @Override
        public void setValue(int x, double value)
        {
            this.array.setValue(new int[] {x}, value);
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
