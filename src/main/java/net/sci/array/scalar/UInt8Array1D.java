/**
 * 
 */
package net.sci.array.scalar;

/**
 * Specialization of Array for 1D arrays of UInt8 values.
 * 
 * @author dlegland
 *
 */
public abstract class UInt8Array1D extends IntArray1D<UInt8> implements UInt8Array
{
	// =============================================================
	// Static methods

	public static final UInt8Array1D create(int size0)
	{
	    return wrap(UInt8Array.create(size0));
	}
	
    /**
     * Creates a new UInt8Array1D from an array of integers. 
     * 
     * @param intArray
     *            the array of integers containing the values.
     * @return a new instance of UInt8Array1D initialized with the values of
     *         <code>intArray</code>
     */
    public static final UInt8Array1D fromIntArray(int[] intArray)
    {
        int size0 = intArray.length;
        UInt8Array1D res = UInt8Array1D.create(size0);
        for (int x = 0; x < size0; x++)
        {
            res.setInt(x, intArray[x]);
        }
        return res;
    }
    
    /**
     * Encapsulates the specified instance of UInt8Array into a new
     * UInt8Array1D, by creating a Wrapper if necessary. If the original array
     * is already an instance of UInt8Array1D, it is returned.
     * 
     * @param array
     *            the original array
     * @return a UInt8Array1D view of the original array
     */
    public static UInt8Array1D wrap(UInt8Array array)
    {
        if (array instanceof UInt8Array1D)
        { 
            return (UInt8Array1D) array; 
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
	protected UInt8Array1D(int size0)
	{
		super(size0);
	}
	
	
    // =============================================================
    // New method(s)
    
    public abstract void setByte(int pos, byte value);
    
	
	// =============================================================
	// Specialization of Array interface
	
    @Override
    public UInt8Array1D duplicate()
    {
        // create output array
        UInt8Array1D res = UInt8Array1D.wrap(UInt8Array.create(this.size0));
        for (int x = 0; x < size0; x++)
        {
            res.setInt(x, getInt(x));
        }
        return res;
    }

    @Override
    public void set(int x, UInt8 value)
    {
        setInt(x, value.value);
    }
    
    
    // =============================================================
    // Implementation of inner classes
    
    /**
     * Wraps a UInt8 array with two dimensions into a UInt8Array1D.
     */
    private static class Wrapper extends UInt8Array1D
    {
        UInt8Array array;

        public Wrapper(UInt8Array array)
        {
            super(0);
            this.size0 = array.size(0);
            this.array = array;
        }

        @Override
        public void setByte(int pos, byte value)
        {
            this.array.setByte(new int[pos], value);
        }

        @Override
        public byte getByte(int... pos)
        {
            return this.array.getByte(pos);
        }

        @Override
        public void setByte(int[] pos, byte value)
        {
            this.array.setByte(pos, value);
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
        public net.sci.array.scalar.UInt8Array.Iterator iterator()
        {
            return this.array.iterator();
        }
    }
}
