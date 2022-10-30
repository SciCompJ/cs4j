/**
 * 
 */
package net.sci.array.scalar;

/**
 * Specialization of Array for 1D arrays of Int32 values.
 * 
 * @author dlegland
 *
 */
public abstract class Int32Array1D extends IntArray1D<Int32> implements Int32Array
{
	// =============================================================
	// Static methods

	public static final Int32Array1D create(int size0)
	{
	    return wrap(Int32Array.create(size0));
	}
	
    /**
     * Creates a new Int32Array2D from a two-dimensional array of integers. The
     * first index of the int array is the second dimension of the result array,
     * i.e. <code>intArray[y][x]</code> is the same value as
     * <code>array.getInt(x,y)</code>.
     * 
     * @param intArray
     *            the array of integers containing the values.
     * @return a new instance of Int32Array2D initialized with the values of
     *         <code>intArray</code>
     */
    public static final Int32Array1D fromIntArray(int[] intArray)
    {
        int size0 = intArray.length;
        Int32Array1D res = Int32Array1D.create(size0);
        for (int x = 0; x < size0; x++)
        {
            res.setInt(x, intArray[x]);
        }
        return res;
    }
    
    /**
     * Encapsulates the specified instance of Int32Array into a new
     * Int32Array2D, by creating a Wrapper if necessary. If the original array
     * is already an instance of Int32Array3D, it is returned.
     * 
     * @param array
     *            the original array
     * @return a Int32Array2D view of the original array
     */
    public static Int32Array1D wrap(Int32Array array)
    {
        if (array instanceof Int32Array1D)
        { 
            return (Int32Array1D) array; 
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
	protected Int32Array1D(int size0)
	{
		super(size0);
	}

	
	// =============================================================
	// Specialization of Array2D interface

	// =============================================================
	// Specialization of Array interface
	
    @Override
    public Int32Array1D duplicate()
    {
        // create output array
        Int32Array1D res = Int32Array1D.wrap(this.factory().create(this.size0));
        for (int x = 0; x < size0; x++)
        {
            res.setInt(x, getInt(x));
        }
        return res;
    }

    @Override
    public void set(int x, Int32 value)
    {
        setInt(x, value.value);
    }
    
    
    // =============================================================
    // Implementation of inner classes
    
    /**
     * Wraps a Int32 array with two dimensions into a Int32Array2D.
     */
    private static class Wrapper extends Int32Array1D
    {
        Int32Array array;

        public Wrapper(Int32Array array)
        {
            super(0);
            this.size0 = array.size(0);
            this.array = array;
        }
        
        @Override
        public void setInt(int x, int intValue)
        {
            this.array.setInt(new int[] {x}, intValue);
        }

        @Override
        public int getInt(int... pos)
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
        public net.sci.array.scalar.Int32Array.Iterator iterator()
        {
            return this.array.iterator();
        }
    }
}
