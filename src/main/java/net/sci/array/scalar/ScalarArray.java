/**
 * 
 */
package net.sci.array.scalar;

import java.util.function.Function;
import java.util.function.UnaryOperator;

import net.sci.array.Array;
import net.sci.array.Arrays;
import net.sci.array.DefaultPositionIterator;
import net.sci.array.NumericArray;

/**
 * Specialization of the Array interface that contains Scalar values. 
 * 
 * Provides methods for accessing and modifying values as double.
 * 
 * @author dlegland
 *
 */
public interface ScalarArray<T extends Scalar> extends NumericArray<T>
{
	// =============================================================
	// New default methods 

	/**
     * Returns the range of values within this scalar array.
     * 
     * Does not take into account eventual NaN values, so the result never
     * contains NaN values. 
     * 
     * @return an array with two elements, containing the lowest and the largest
     *         finite values within this Array instance
     * @see #finiteValueRange()
     */
	public default double[] valueRange()
	{
		double vMin = Double.POSITIVE_INFINITY;
		double vMax = Double.NEGATIVE_INFINITY;
		for (Scalar scalar : this)
		{
			double value = scalar.getValue();
			if (!Double.isNaN(value))
			{
				vMin = Math.min(vMin, value);
				vMax = Math.max(vMax, value);
			}
		}
		return new double[]{vMin, vMax};
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
    public default double[] finiteValueRange()
    {
        double vMin = Double.POSITIVE_INFINITY;
        double vMax = Double.NEGATIVE_INFINITY;
        for (Scalar scalar : this)
        {
            double value = scalar.getValue();
            if (Double.isFinite(value))
            {
                vMin = Math.min(vMin, value);
                vMax = Math.max(vMax, value);
            }
        }
        return new double[]{vMin, vMax};
    }

	/**
	 * Returns the minimum value within this scalar array.
	 * 
	 * Does not take into account eventual NaN values.
	 * 
	 * @return the minimal value within this array
	 */
	public default double minValue()
	{
		double vMin = Double.POSITIVE_INFINITY;
		for (Scalar scalar : this)
		{
			double value = scalar.getValue();
			if (!Double.isNaN(value))
			{
				vMin = Math.min(vMin, value);
			}
		}
		return vMin;
	}

	/**
	 * Returns the maximum value within this scalar array.
	 * 
	 * Does not take into account eventual NaN values.
	 * 
	 * @return the maximal value within this array
	 */
	public default double maxValue()
	{
		double vMax = Double.NEGATIVE_INFINITY;
		for (Scalar scalar : this)
		{
			double value = scalar.getValue();
			if (!Double.isNaN(value))
			{
				vMax = Math.max(vMax, value);
			}
		}
        return vMax;
    }

    /**
     * Fills the array with the specified double value.
     * 
     * @param value the value to fill the array with
     */
	public default void fillValue(double value)
	{
		Iterator<? extends Scalar> iter = iterator();
		while(iter.hasNext())
		{
			iter.forward();
			iter.setValue(value);
		}
	}
	
	
    // =============================================================
    // New abstract methods

    /**
     * Gets the value at the given position as a numeric double.
     * @param pos
     *            the position, as an array of indices
     * @return the double value at the given position
     */
    public double getValue(int... pos);
    
    /**
     * Sets the value at the given position as a numeric double.
     * @param value
     *            the new value for the given position
     * @param pos
     *            the position, as an array of indices
     */
    public void setValue(double value, int... pos);

    
    // =============================================================
    // Apply arbitrary functions

    /**
     * Applies the given function to each element of the array, and return a new
     * Array with the same class.
     * 
     * @param fun
     *            the function to apply
     * @return the result array
     */
    public default ScalarArray<T> apply(UnaryOperator<Double> fun)
    {
        ScalarArray<T> res = newInstance(size());
        apply(fun, res);
        return res;
    }

    /**
     * Applies the given function to each element of the array, and return a
     * reference to the output array.
     * 
     * @param fun
     *            the function to apply
     * @param output
     *            the array to put the result in
     * @return the result array
     */
    public default ScalarArray<?> apply(UnaryOperator<Double> fun, ScalarArray<?> output)
    {
        if (!Arrays.isSameSize(this, output))
        {
            throw new IllegalArgumentException("Output array must have same size as input array");
        }
        
        for (int[] pos : positions())
        {
            output.setValue(fun.apply(this.getValue(pos)), pos);
        }
        return output;
    }
    
    
    // =============================================================
    // Implementation of comparison with scalar

    public default ScalarArray<T> min(double v)
    {
        ScalarArray<T> res = newInstance(size());
        for (int[] pos : positions())
        {
            res.setValue(Math.min(this.getValue(pos), v), pos);
        }
        return res;
    }
    
    public default ScalarArray<T> max(double v)
    {
        ScalarArray<T> res = newInstance(size());
        for (int[] pos : positions())
        {
            res.setValue(Math.max(this.getValue(pos), v), pos);
        }
        return res;
    }
    

    // =============================================================
    // Implementation of NumericArray interface

    public default ScalarArray<T> plus(double v)
    {
        ScalarArray<T> res = newInstance(size());
        for (int[] pos : positions())
        {
            res.setValue(this.getValue(pos) + v, pos);
        }
        return res;
    }

    public default ScalarArray<T> minus(double v)
    {
        ScalarArray<T> res = newInstance(size());
        for (int[] pos : positions())
        {
            res.setValue(this.getValue(pos) - v, pos);
        }
        return res;
    }

    public default ScalarArray<T> times(double k)
    {
        ScalarArray<T> res = newInstance(size());
        for (int[] pos : positions())
        {
            res.setValue(this.getValue(pos) * k, pos);
        }
        return res;
    }

    public default ScalarArray<T> divideBy(double k)
    {
        ScalarArray<T> res = newInstance(size());
        for (int[] pos : positions())
        {
            res.setValue(this.getValue(pos) / k, pos);
        }
        return res;
    }

    
    // =============================================================
	// Specialization of the Array interface

    /**
     * Creates a new scalar array with new dimensions and containing the same
     * elements.
     * 
     * This method overrides the default behavior of the Array interface to
     * simply manipulate double values.
     * 
     * </pre>{@code
     * UInt8Array array = UInt8Array2D.create(6, 4);
     * array.populateValues((x,y) -> x + 10 * y);
     * ScalarArray<?> reshaped = array.reshape(4, 3, 2);
     * double last = reshaped.getValue(new int[]{3, 2, 1}); // equals 35
     * }
     * 
     * @param newDims
     *            the dimensions of the new array
     * @return a new array with same type and containing the same values
     */
    @Override
    public default ScalarArray<T> reshape(int... newDims)
    {
        // check dimension consistency
        int n2 = 1;
        for (int dim : newDims)
        {
            n2 *= dim;
        }
        if (n2 != this.elementNumber())
        {
            throw new IllegalArgumentException("The element number should not change after reshape.");
        }
        
        // allocate memory
        ScalarArray<T> res = this.newInstance(newDims);
        
        // iterate using a pair of Iterator instances
        Iterator<T> iter1 = this.iterator();
        Iterator<T> iter2 = res.iterator();
        while(iter1.hasNext())
        {
            iter2.setNextValue(iter1.nextValue());
        }
        
        return res;
    }
   
    
	@Override
	public ScalarArray<T> newInstance(int... dims);

	@Override
	public default ScalarArray<T> duplicate()
	{
	    ScalarArray<T> dup = newInstance(size());
	    for (int[] pos : positions())
	    {
	        dup.setValue(getValue(pos), pos);
	    }
	    return dup;
	}
	
    public default ScalarArray<T> view(int[] newDims, Function<int[], int[]> coordsMapping)
    {
        return new View<T>(this, newDims, coordsMapping);
    }

    @Override
    public ScalarArray.Factory<T> getFactory();

	public ScalarArray.Iterator<T> iterator();
	
	
    // =============================================================
    // Specialization of the Factory interface

	public interface Factory<T extends Scalar> extends Array.Factory<T>
	{
        /**
         * Creates a new scalar array of the specified dimensions, initialized
         * with zeros.
         * 
         * @param dims
         *            the dimensions of the new array
         * @return a new scalar array initialized with zeros
         */
	    public ScalarArray<T> create(int[] dims);

        /**
         * Creates a new scalar array with the specified dimensions, filled with
         * the specified initial value.
         * 
         * @param dims
         *            the dimensions of the array to be created
         * @param value
         *            an instance of the initial value
         * @return a new instance of ScalarArray
         */
        public ScalarArray<T> create(int[] dims, T value);
	}
	
	
	// =============================================================
	// Inner interface

	public interface Iterator<T extends Scalar> extends Array.Iterator<T>
	{
		/**
		 * Moves this iterator to the next element and updates the value with
		 * the specified scalar value (optional operation).
		 * 
		 * @param value
		 *            the new value at the next position
		 */
		public default void setNextValue(double value)
		{
			forward();
			setValue(value);
		}
		
        /**
         * Returns the next value as a double.
         * 
         * @return the next value as a double
         */
		public default double nextValue()
		{
			return next().getValue();
		}
    
        /**
         * @return the value at the current iterator position as a double value
         */
        public double getValue();
        
        /**
         * Changes the value of the array at the current iterator position
         * (optional operation).
         * 
         * @param value
         *            the new value
         */
        public void setValue(double value);     
	}
	
    static class View<T extends Scalar> implements ScalarArray<T>
    {
        ScalarArray<T> array;
        
        int[] newDims;
        
        Function<int[], int[]> coordsMapping;

        /**
         * 
         */
        public View(ScalarArray<T> array, int[] newDims, Function<int[], int[]> coordsMapping)
        {
            this.array = (ScalarArray<T>) array;
            this.newDims = newDims;
            this.coordsMapping = coordsMapping;
        }


        // =============================================================
        // Implementation of the Array interface

        @Override
        public double getValue(int... pos)
        {
            return array.getValue(coordsMapping.apply(pos));
        }

        @Override
        public void setValue(double value, int... pos)
        {
            array.setValue(value, coordsMapping.apply(pos));
        }

        // =============================================================
        // Implementation  of the Array interface

        /* (non-Javadoc)
         * @see net.sci.array.Array#dimensionality()
         */
        @Override
        public int dimensionality()
        {
            return newDims.length;
        }

        /* (non-Javadoc)
         * @see net.sci.array.Array#getSize()
         */
        @Override
        public int[] size()
        {
            return newDims;
        }

        /* (non-Javadoc)
         * @see net.sci.array.Array#getSize(int)
         */
        @Override
        public int size(int dim)
        {
            return newDims[dim];
        }

        /* (non-Javadoc)
         * @see net.sci.array.Array#positionIterator()
         */
        @Override
        public PositionIterator positionIterator()
        {
            return new DefaultPositionIterator(newDims);
        }

        @Override
        public Class<T> dataType()
        {
            return array.dataType();
        }

        @Override
        public ScalarArray<T> newInstance(int... dims)
        {
            return array.newInstance(dims);
        }

        @Override
        public Factory<T> getFactory()
        {
            return array.getFactory();
        }

        @Override
        public T get(int... pos)
        {
            return array.get(coordsMapping.apply(pos));
        }

        @Override
        public void set(T value, int... pos)
        {
            array.set(value, coordsMapping.apply(pos));
        }

        @Override
        public Iterator<T> iterator()
        {
            return new Iterator<T>()
            {
                PositionIterator iter = positionIterator();

                @Override
                public double getValue()
                {
                    return View.this.getValue(iter.get());
                }

                @Override
                public void setValue(double value)
                {
                    View.this.setValue(value, iter.get());
                }

                @Override
                public boolean hasNext()
                {
                    return iter.hasNext();
                }

                @Override
                public void forward()
                {
                    iter.forward();
                }

                @Override
                public T next()
                {
                    iter.forward();
                    return View.this.get(iter.get());
                }

                @Override
                public T get()
                {
                    return View.this.get(iter.get());
                }

                @Override
                public void set(T value)
                {
                    View.this.set(value, iter.get());
                }
            };
        }
    }
}
