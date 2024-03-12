/**
 * 
 */
package net.sci.array.scalar;

import java.util.function.Function;

import net.sci.array.Array;
import net.sci.array.ArrayWrapperStub;

/**
 * @author dlegland
 *
 */
public interface IntArray<I extends Int<I>> extends ScalarArray<I>
{
    // =============================================================
    // static methods

    public static <I extends Int<I>> IntArray<I> wrap(Array<I> array)
    {
        if (array instanceof IntArray) return (IntArray<I>) array;
        
        return new Wrapper<I>(array);
    }
    
    
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
	public default IntArray<I> duplicate()
	{
	    IntArray<I> res = this.newInstance(this.size());
	    res.fillInts(pos -> getInt(pos));
	    return res;
	}

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

    /**
     * Returns an iterator over the elements of the array, for implementing the
     * Iterable interface.
     * 
     * Provides a default implementation based on the
     * position iterator.
     * @param <S>
     */
    @Override
    public default IntArray.Iterator<I> iterator()
    {
        return new Iterator<I>()
        {
            PositionIterator iter = positionIterator();
            // keep an array of coordinates to avoid repetitive allocation of array
            int[] pos = new int[dimensionality()];

            @Override
            public void forward()
            {
                iter.forward();
            }

            @Override
            public boolean hasNext()
            {
                return iter.hasNext();
            }

            @Override
            public int getInt()
            {
                return IntArray.this.getInt(iter.get(pos));
            }

            @Override
            public void setInt(int value)
            {
                IntArray.this.setInt(iter.get(pos), value);
            }
            
            @Override
            public double getValue()
            {
                return IntArray.this.getValue(iter.get(pos));
            }

            @Override
            public void setValue(double value)
            {
                IntArray.this.setValue(iter.get(pos), value);
            }
            
            @Override
            public I get()
            {
                return IntArray.this.get(iter.get(pos));
            }

            @Override
            public void set(I value)
            {
                IntArray.this.set(iter.get(pos), value);
            }
        };
    }
	
    
    // =============================================================
    // Specialization of the Factory interface

    public interface Factory<I extends Int<I>> extends ScalarArray.Factory<I>
    {
        /**
         * Creates a new int array of the specified dimensions, initialized
         * with zeros.
         * 
         * @param dims
         *            the dimensions of the new array
         * @return a new scalar array initialized with zeros
         */
        public IntArray<I> create(int... dims);

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
        public default IntArray<I> create(int[] dims, I value)
        {
            IntArray<I> res = create(dims);
            res.fill(value);
            return res;
        }
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

    /**
     * Utility class the wraps an array of <code>Int</code> into an instance of
     * <code>IntArray</code>.
     * 
     * @param <I>
     *            the type of Int contained in array.
     */
    static class Wrapper<I extends Int<I>> extends ArrayWrapperStub<I> implements IntArray<I>
    {
        /**
         * the array to wrap. Already stored in super class, but store it here
         * as well to keep type of generic.
         */
        Array<I> array;
        
        /**
         * Keep a sample element to allow the creation of generic arrays, and to
         * retrieve type-related information.
         */
        I sample;
        
        protected Wrapper(Array<I> array)
        {
            super(array);
            this.array = array;
            this.sample = array.sampleElement();
        }

        @SuppressWarnings("unchecked")
        @Override
        public Class<I> dataType()
        {
            return (Class<I>) sample.getClass();
        }

        @Override
        public I createElement(double value)
        {
            return sample.fromValue(value);
        }
        
        @Override
        public I typeMin()
        {
            return sample.typeMin();
        }
        
        @Override
        public I typeMax()
        {
            return sample.typeMax();
        }
        
        @Override
        public int getInt(int[] pos)
        {
            return array.get(pos).getInt();
        }

        @Override
        public void setInt(int[] pos, int value)
        {
            array.set(pos, sample.fromInt(value));
        }

        @Override
        public void setValue(int[] pos, double value)
        {
            array.set(pos, sample.fromValue(value));
        }

        @Override
        public I get(int[] pos)
        {
            return array.get(pos);
        }

        @Override
        public void set(int[] pos, I value)
        {
            array.set(pos, value);
        }

        @Override
        public IntArray<I> newInstance(int... dims)
        {
            return IntArray.wrap(array.newInstance(array.size()));
        }

        @Override
        public net.sci.array.scalar.IntArray.Factory<I> factory()
        {
            return new net.sci.array.scalar.IntArray.Factory<I>() 
            {
                @Override
                public IntArray<I> create(int... dims)
                {
                    return IntArray.wrap(array.newInstance(array.size()));
                }
            };
        }
    }
}
