/**
 * 
 */
package net.sci.array.scalar;

import java.util.function.Function;

/**
 * @author dlegland
 *
 */
public interface IntArray<I extends Int<I>> extends ScalarArray<I>
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
		for (I i : this)
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
		for (I i : this)
		{
			vMax = Math.max(vMax, i.getInt());
		}
		return vMax;
	}
	
    
	// =============================================================
	// New methods

    /**
     * Fills the array with the specified integer value.
     * 
     * @param value the value to fill the array with
     */
    public default void fillInt(int value)
    {
        Iterator<I> iter = iterator();
        while(iter.hasNext())
        {
            iter.forward();
            iter.setInt(value);
        }
    }

    public default void fillInts(Function<int[], Integer> fun)
    {
        for (int[] pos : this.positions())
        {
            this.setInt(pos, fun.apply(pos));
        }
    }
    
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
    // Specialization of the ScalarArray interface

    /**
     * Fills the array with the specified double value.
     * 
     * @param value the value to fill the array with
     */
    public default void fillValue(double value)
    {
        int intValue = (int) value;
        this.fillInt(intValue);
    }
    
    /**
     * Returns the range of finite values within this scalar array.
     * 
     * Does not take into account eventual NaN or infinite values, so the result
     * array always contains finite values (except if all values within array
     * are infinite).
     * 
     * @return an array with two elements, containing the lowest and the largest
     *         finite values within this Array instance
     * @see #valueRange()
     */
    @Override
    public default double[] finiteValueRange()
    {
        int vMin = Integer.MAX_VALUE;
        int vMax = Integer.MIN_VALUE;
        for (int[] pos : positions())
        {
            int value = getInt(pos);
            vMin = Math.min(vMin, value);
            vMax = Math.max(vMax, value);
        }
        return new double[]{vMin, vMax};
    }

	// =============================================================
	// Specialization of the Array interface

	@Override
	public IntArray<I> newInstance(int... dims);

	@Override
	public IntArray<I> duplicate();

    @Override
    public default double getValue(int[] pos)
    {
        return getInt(pos);
    }

    @Override
    public default void setValue(int[] pos, double value)
    {
        setInt(pos, (int) value);
    }

    @Override
    public IntArray.Factory<I> factory();

	public Iterator<I> iterator();	
	
    
    // =============================================================
    // Specialization of the Factory interface

    public interface Factory<T extends Int<T>> extends ScalarArray.Factory<T>
    {
        /**
         * Creates a new int array of the specified dimensions, initialized
         * with zeros.
         * 
         * @param dims
         *            the dimensions of the new array
         * @return a new scalar array initialized with zeros
         */
        public IntArray<T> create(int... dims);

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

	public interface Iterator<T extends Int<T>> extends ScalarArray.Iterator<T>
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
