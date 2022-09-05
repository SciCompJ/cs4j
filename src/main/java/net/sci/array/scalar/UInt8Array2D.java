/**
 * 
 */
package net.sci.array.scalar;

/**
 * @author dlegland
 *
 */
public abstract class UInt8Array2D extends IntArray2D<UInt8> implements UInt8Array
{
	// =============================================================
	// Static methods

	/**
     * Creates a new 2D array containing UInt8 values. Uses the default factory,
     * and a wrapper to UInt8Array2D if necessary.
     * 
     * @param size0
     *            the size of the array along the first dimension
     * @param size1
     *            the size of the array along the second dimension
     * @return a new instance of UInt8Array2D
     */
	public static final UInt8Array2D create(int size0, int size1)
	{
		return wrap(UInt8Array.create(size0, size1));
	}
	
	/**
     * Creates a new UInt8Array2D from a two-dimensional array of integers. The
     * first index of the int array is the second dimension of the result array,
     * i.e. <code>intArray[y][x]</code> is the same value as
     * <code>array.getInt(x,y)</code>.
     * 
     * @param intArray
     *            the array of integers containing the values.
     * @return a new instance of UInt8Array2D initialized with the values of
     *         <code>intArray</code>
     */
    public static final UInt8Array2D fromIntArray(int[][] intArray)
    {
        int size1 = intArray.length;
        int size0 = intArray[0].length;
        UInt8Array2D res = UInt8Array2D.create(size0, size1);
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
     * Encapsulates the instance of Scalar array into a new UInt8Array, by
     * creating a Wrapper if necessary. 
     * If the original array is already an instance of UInt8Array, it is returned.  
     * 
     * @param array
     *            the original array
     * @return a UInt8 view of the original array
     */
    public static UInt8Array2D wrap(UInt8Array array)
    {
        if (array instanceof UInt8Array2D)
        {
            return (UInt8Array2D) array;
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
	protected UInt8Array2D(int size0, int size1)
	{
		super(size0, size1);
	}

	
	// =============================================================
	// New methods
	
	public abstract void setByte(int x, int y, byte b);
	
	
	// =============================================================
	// Specialization of the UInt8Array interface
    
    /* (non-Javadoc)
     * @see net.sci.array.scalar.UInt8Array#setByte(int[], byte)
     */
    @Override
    public void setByte(int[] pos, byte b)
    {
        setByte(pos[0], pos[1], b);
    }

    
    // =============================================================
    // Specialization of the IntArray2D interface

    @Override
    public void setInt(int x, int y, int value)
    {
        setByte(x, y, (byte) UInt8.clamp(value));
    }


	// =============================================================
	// Specialization of ScalarArray2D interface


    @Override
    public void setValue(int x, int y, double value)
    {
        setByte(x, y, (byte) UInt8.clamp(value));
    }


	// =============================================================
	// Specialization of Array2D interface

    @Override
    public void set(int x, int y, UInt8 value)
    {
        setByte(x, y, value.value);
    }
    
	
	// =============================================================
	// Specialization of Array interface

	@Override
	public UInt8Array2D duplicate()
    {
        UInt8Array2D res = UInt8Array2D.create(size0, size1);
        for (int y = 0; y < size1; y++)
        {
            for (int x = 0; x < size0; x++)
            {
                res.setByte(x, y, getByte(x, y));
            }
        }
        return res;
    }
	
	/**
     * Wraps a UInt8 array into a UInt8Array2D, with two dimensions.
     */
    private static class Wrapper extends UInt8Array2D
    {
        UInt8Array array;

        public Wrapper(UInt8Array array)
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
        public void setByte(int x, int y, byte b)
        {
            this.array.setByte(new int[] {x, y}, b);
        }

        @Override
        public byte getByte(int... pos)
        {
            return this.array.getByte(pos);
        }

        @Override
        public void setByte(int[] pos, byte b)
        {
            this.array.setByte(pos, b);
        }

        /**
         * Simply returns an iterator on the original array.
         */
        @Override
        public net.sci.array.scalar.UInt8Array.Iterator iterator()
        {
            return this.array.iterator();
        }
    }
}
