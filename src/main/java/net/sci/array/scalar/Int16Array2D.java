/**
 * 
 */
package net.sci.array.scalar;

/**
 * @author dlegland
 *
 */
public abstract class Int16Array2D extends IntArray2D<Int16> implements Int16Array
{
	// =============================================================
	// Static methods

	/**
	 * @param size0
	 *            the size of the array along the first dimension
	 * @param size1
	 *            the size of the array along the second dimension
	 * @return a new instance of Int16Array2D
	 */
	public static final Int16Array2D create(int size0, int size1)
	{
	    return wrap(Int16Array.create(size0, size1));
	}
	
    /**
     * Creates a new Int16Array2D from a two-dimensional array of integers. The
     * first index of the int array is the second dimension of the result array,
     * i.e. <code>intArray[y][x]</code> is the same value as
     * <code>array.getInt(x,y)</code>.
     * 
     * @param intArray
     *            the array of integers containing the values.
     * @return a new instance of Int16Array2D initialized with the values of
     *         <code>intArray</code>
     */
    public static final Int16Array2D fromIntArray(int[][] intArray)
    {
        int size1 = intArray.length;
        int size0 = intArray[0].length;
        Int16Array2D res = Int16Array2D.create(size0, size1);
        for (int y = 0; y < size1; y++)
        {
            for (int x = 0; x < size0; x++)
            {
                res.setInt(x, y, intArray[y][x]);
            }
        }
        return res;
    }
    
    /**
     * Encapsulates the specified instance of Int16Array into a new
     * Int16Array2D, by creating a Wrapper if necessary. If the original array
     * is already an instance of Int16Array3D, it is returned.
     * 
     * @param array
     *            the original array
     * @return a Int16Array2D view of the original array
     */
    public static Int16Array2D wrap(Int16Array array)
    {
        if (array instanceof Int16Array2D)
        { 
            return (Int16Array2D) array; 
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
	protected Int16Array2D(int size0, int size1)
	{
		super(size0, size1);
	}

	// =============================================================
	// New methods
	
    public abstract void setShort(int x, int y, short s);

	
	// =============================================================
	// Specialization of the Int16Array interface

    public void setShort(int [] pos, short s)
    {
        setShort(pos[0], pos[1], s);
    }
    
    
    // =============================================================
    // Specialization of the IntArray2D interface

    @Override
    public void setInt(int x, int y, int value)
    {
        setShort(x, y, (short) Int16.clamp(value));
    }

    
	// =============================================================
	// Specialization of the ScalarArray2D interface

    @Override
    public void setValue(int x, int y, double value)
    {
        setShort(x, y, (short) Int16.clamp(value));
    }

    
	// =============================================================
	// Specialization of Array2D interface

    @Override
    public void set(int x, int y, Int16 value)
    {
        setShort(x, y, value.value);
    }


	// =============================================================
	// Specialization of Array interface
	
    @Override
    public Int16Array2D duplicate()
    {
        // create output array
        Int16Array2D res = Int16Array2D.create(this.size0, this.size1);

        for (int y = 0; y < size1; y++)
        {
            for (int x = 0; x < size0; x++)
            {
                res.setShort(x, y, getShort(x, y));
            }
        }
        
        return res;
    }
    
    
    // =============================================================
    // Implementation of inner classes
    
    /**
     * Wraps a Int16 array with two dimensions into a Int16Array2D.
     */
    private static class Wrapper extends Int16Array2D
    {
        Int16Array array;

        public Wrapper(Int16Array array)
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
        public void setShort(int x, int y, short s)
        {
            this.array.setShort(new int[] {x, y}, s);
        }

        @Override
        public short getShort(int... pos)
        {
            return this.array.getShort(pos);
        }

        @Override
        public void setShort(int[] pos, short value)
        {
            this.array.setShort(pos, value);
        }

        @Override
        public Int16Array2D duplicate()
        {
            return new Wrapper(this.array.duplicate());
        }

        /**
         * Simply returns an iterator on the original array.
         */
        @Override
        public net.sci.array.scalar.Int16Array.Iterator iterator()
        {
            return this.array.iterator();
        }
    }
}
