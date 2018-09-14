/**
 * 
 */
package net.sci.array.scalar;

/**
 * @author dlegland
 *
 */
public interface IntArray<T extends Int> extends ScalarArray<T>
{
	// =============================================================
	// New default methods

	/**
	 * Returns the minimum integer value within this array.
	 * 
	 * @return the minimal int value within this array
	 */
	public default int minInt()
	{
		int vMin = Integer.MIN_VALUE;
		for (Int i : this)
		{
			vMin = Math.min(vMin, i.getInt());
		}
		return vMin;
	}

	/**
	 * Returns the maximum integer value within this array.
	 * 
	 * @return the maximal int value within this array
	 */
	public default int maxInt()
	{
		int vMax = Integer.MIN_VALUE;
		for (Int i : this)
		{
			vMax = Math.max(vMax, i.getInt());
		}
		return vMax;
	}

	
	// =============================================================
	// New methods

	/**
	 * Returns the value at the specified position as an integer.
	 * 
	 * @param pos
	 *            the position
	 * @return the integer value
	 */
	public int getInt(int[] pos);
	
	/**
	 * Sets the value at the specified position as an integer.
	 * 
	 * @param pos
	 *            the position
	 * @param value
	 *            the new integer value
	 */
	public void setInt(int[] pos, int value);
	
	
	// =============================================================
	// Specialization of the Array interface

	@Override
	public IntArray<T> newInstance(int... dims);

	@Override
	public IntArray<T> duplicate();

    public default double getValue(int[] pos)
    {
        return getInt(pos);
    }

    public default void setValue(int[] pos, double value)
    {
        setInt(pos, (int) value);
    }

    @Override
    public IntArray.Factory<T> getFactory();

	public Iterator<T> iterator();	
	
    
    // =============================================================
    // Specialization of the Factory interface

    public interface Factory<T extends Int> extends ScalarArray.Factory<T>
    {
        /**
         * Creates a new int array of the specified dimensions, initialized
         * with zeros.
         * 
         * @param dims
         *            the dimensions of the new array
         * @return a new scalar array initialized with zeros
         */
        public IntArray<T> create(int[] dims);

        /**
         * Creates a new Int array with the specified dimensions, filled with
         * the specified initial value.
         * 
         * @param dims
         *            the dimensions of the array to be created
         * @param value
         *            an instance of the initial integer value
         * @return a new instance of IntArray
         */
        public IntArray<T> create(int[] dims, T value);
    }
    

	// =============================================================
	// Inner interface

	public interface Iterator<T extends Int> extends ScalarArray.Iterator<T>
	{
		public int getInt();
		public void setInt(int value);
		
		/**
		 * Moves this iterator to the next element and updates the value with
		 * the specified integer value (optional operation).
		 * 
		 * @param intValue
		 *            the new value at the next position
		 */
		public default void setNextInt(int intValue)
		{
			forward();
			setInt(intValue);
		}
		
		/**
		 * Iterates and returns the next int value.
		 * 
		 * @return the next int value.
		 */
		public default int nextInt()
		{
			forward();
			return getInt();
		}
		
		@Override
		public default double getValue()
		{
			return get().getValue();
		}
		
		@Override
		public default void setValue(double value)
		{
			setInt((int) value);
		}
	}

}
