/**
 * 
 */
package net.sci.array.numeric;

/**
 * Specialization of Array for 1D arrays of Int16 values.
 * 
 * @author dlegland
 *
 */
public abstract class Int16Array1D extends IntArray1D<Int16> implements Int16Array
{
	// =============================================================
	// Static methods

	public static final Int16Array1D create(int size0)
	{
	    return wrap(Int16Array.create(size0));
	}
	
    /**
     * Creates a new Int16Array1D from an array of integers. 
     * 
     * @param intArray
     *            the array of integers containing the values.
     * @return a new instance of Int16Array1D initialized with the values of
     *         <code>intArray</code>
     */
    public static final Int16Array1D fromIntArray(int[] intArray)
    {
        int size0 = intArray.length;
        Int16Array1D res = Int16Array1D.create(size0);
        for (int x = 0; x < size0; x++)
        {
            res.setInt(x, intArray[x]);
        }
        return res;
    }
    
    /**
     * Encapsulates the specified instance of Int16Array into a new
     * Int16Array1D, by creating a Wrapper if necessary. If the original array
     * is already an instance of Int16Array1D, it is returned.
     * 
     * @param array
     *            the original array
     * @return a Int16Array1D view of the original array
     */
    public static Int16Array1D wrap(Int16Array array)
    {
        if (array instanceof Int16Array1D)
        { 
            return (Int16Array1D) array; 
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
	protected Int16Array1D(int size0)
	{
		super(size0);
	}
	
	
    // =============================================================
    // New method(s)
    
    public abstract short getShort(int pos);
    
    public abstract void setShort(int pos, short value);
    
	
    // =============================================================
    // Specialization of the IntArray1D interface

    @Override
    public int getInt(int x)
    {
        return getShort(x);
    }

    @Override
    public void setInt(int x, int value)
    {
        setShort(x, (short) Int16.clamp(value));
    }

	
	// =============================================================
	// Specialization of Array[1D] interfaces
	
    @Override
    public Int16Array1D duplicate()
    {
        // create output array
        Int16Array1D res = Int16Array1D.wrap(this.factory().create(this.size0));
        for (int x = 0; x < size0; x++)
        {
            res.setInt(x, getInt(x));
        }
        return res;
    }

    @Override
    public Int16 get(int x)
    {
        return new Int16(getShort(x));
    }
    
    @Override
    public void set(int x, Int16 value)
    {
        setInt(x, value.value);
    }
    
    
    // =============================================================
    // Implementation of inner classes
    
    /**
     * Wraps a Int16 array with two dimensions into a Int16Array1D.
     */
    private static class Wrapper extends Int16Array1D
    {
        Int16Array array;

        public Wrapper(Int16Array array)
        {
            super(0);
            this.size0 = array.size(0);
            this.array = array;
        }
        
        @Override
        public short getShort(int pos)
        {
            return this.array.getShort(new int[pos]);
        }

        @Override
        public void setShort(int pos, short value)
        {
            this.array.setShort(new int[pos], value);
        }

        @Override
        public short getShort(int[] pos)
        {
            return this.array.getShort(pos);
        }

        @Override
        public void setShort(int[] pos, short value)
        {
            this.array.setShort(pos, value);
        }

        @Override
        public void setInt(int x, int intValue)
        {
            this.array.setInt(new int[] {x}, intValue);
        }

        @Override
        public int getInt(int[] pos)
        {
            return this.array.getInt(pos);
        }

        @Override
        public void setInt(int[] pos, int value)
        {
            this.array.setInt(pos, value);
        }

        /**
         * Simply returns an iterator on the original array.
         */
        @Override
        public net.sci.array.numeric.Int16Array.Iterator iterator()
        {
            return this.array.iterator();
        }
    }
}
