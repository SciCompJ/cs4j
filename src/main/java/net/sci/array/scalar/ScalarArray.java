/**
 * 
 */
package net.sci.array.scalar;

import java.util.function.Function;
import java.util.function.UnaryOperator;

import net.sci.array.Array;
import net.sci.array.Arrays;
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
		for (double v : values())
		{
			if (!Double.isNaN(v))
			{
				vMin = Math.min(vMin, v);
				vMax = Math.max(vMax, v);
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
        for (double v : values())
        {
            if (Double.isFinite(v))
            {
                vMin = Math.min(vMin, v);
                vMax = Math.max(vMax, v);
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
		for (double v : values())
		{
			if (!Double.isNaN(v))
			{
				vMin = Math.min(vMin, v);
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
		for (double v : values())
		{
			if (!Double.isNaN(v))
			{
				vMax = Math.max(vMax, v);
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
	
	/**
	 * Fills the values within this array by using a function of the position (given as an integer array).
	 * 
	 * <pre>{@code
	 * Float32Array2D array = Float32Array2D.create(20, 20);
	 * array.fillValues(pos -> Math.max(Math.hypot(pos[0] - 10.0, pos[1] - 10.0), 0));
	 * }</pre>
	 * 
	 * @param fun the function that computes a value depending on the position.
	 */
    public default void fillValues(Function<int[], Double> fun)
    {
        for (int[] pos : this.positions())
        {
            this.setValue(pos, fun.apply(pos));
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
    public void setValue(int[] pos, double value);
    
    /**
     * Returns an <code>Iterable</code> over the (double) values within the
     * array. This allows to query information about the population of values
     * using constructs like the following:
     * 
     * <pre>
     * {@code
     *     double vMin = Double.POSITIVE_INFINTIY;
     *     for (double v : array.values())
     *     {
     *         vMin = Math.min(v, vMin);
     *     }
     * }
     * </pre>
     * 
     * Default behavior is to wrap a position iterator, and return values
     * according to the <code>getValue(int...)</code> method.
     * 
     * @see #positionIterator()
     * @see #get(int...)
     * 
     * @return an Iterable over the (double) values within the array.
     */
    public default Iterable<Double> values()
    {
        return new Iterable<Double>() 
        {
            @Override
            public java.util.Iterator<Double> iterator()
            {
                return new java.util.Iterator<Double>()
                {
                    PositionIterator iter = positionIterator();

                    @Override
                    public boolean hasNext()
                    {
                        return iter.hasNext();
                    }

                    @Override
                    public Double next()
                    {
                        return getValue(iter.next());
                    }
                };
            }
        };
    }


    
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
            output.setValue(pos, fun.apply(this.getValue(pos)));
        }
        return output;
    }
    
    
    // =============================================================
    // Implementation of comparison with scalar

    /**
     * Returns the array containing for each position the minimum of the value
     * in the array and the specified value.
     * 
     * @param v
     *            the value to compute the min with
     * @return the result of the min operation on the array.
     */
    public default ScalarArray<T> min(double v)
    {
        ScalarArray<T> res = newInstance(size());
        for (int[] pos : positions())
        {
            res.setValue(pos, Math.min(this.getValue(pos), v));
        }
        return res;
    }
    
    /**
     * Returns the array containing for each position the maximum of the value
     * in the array and the specified value.
     * 
     * @param v
     *            the value to compute the max with
     * @return the result of the max operation on the array.
     */
    public default ScalarArray<T> max(double v)
    {
        ScalarArray<T> res = newInstance(size());
        for (int[] pos : positions())
        {
            res.setValue(pos, Math.max(this.getValue(pos), v));
        }
        return res;
    }
    

    // =============================================================
    // Implementation of NumericArray interface

    @Override
    public default ScalarArray<T> plus(double v)
    {
        ScalarArray<T> res = newInstance(size());
        for (int[] pos : positions())
        {
            res.setValue(pos, this.getValue(pos) + v);
        }
        return res;
    }

    @Override
    public default ScalarArray<T> minus(double v)
    {
        ScalarArray<T> res = newInstance(size());
        for (int[] pos : positions())
        {
            res.setValue(pos, this.getValue(pos) - v);
        }
        return res;
    }

    @Override
    public default ScalarArray<T> times(double k)
    {
        ScalarArray<T> res = newInstance(size());
        for (int[] pos : positions())
        {
            res.setValue(pos, this.getValue(pos) * k);
        }
        return res;
    }

    @Override
    public default ScalarArray<T> divideBy(double k)
    {
        ScalarArray<T> res = newInstance(size());
        for (int[] pos : positions())
        {
            res.setValue(pos, this.getValue(pos) / k);
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
     * <pre>{@code
     * UInt8Array array = UInt8Array2D.create(6, 4);
     * array.fillValues((x,y) -> x + 10 * y);
     * ScalarArray<?> reshaped = array.reshape(4, 3, 2);
     * double last = reshaped.getValue(new int[]{3, 2, 1}); // equals 35
     * }</pre>
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
        if (n2 != this.elementCount())
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
	        dup.setValue(pos, getValue(pos));
	    }
	    return dup;
	}
	
	@Override
    public default ScalarArray<T> view(int[] newDims, Function<int[], int[]> coordsMapping)
    {
        return new View<T>(this, newDims, coordsMapping);
    }

    @Override
    public ScalarArray.Factory<T> factory();

    @Override
    public ScalarArray.Iterator<T> iterator();
	
	
    // =============================================================
    // Specialization of the Factory interface

	/**
	 * A factory for building new ScalarArray instances.
	 *
	 * @param <T> the type of Scalar.
	 */
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
	    public ScalarArray<T> create(int... dims);

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
        public default ScalarArray<T> create(int[] dims, T value)
        {
            ScalarArray<T> res = create(dims);
            res.fill(value);
            return res;
        }
	}
	
	
	// =============================================================
	// Inner interface

	/**
	 * Iterator over the Scalar objects within this array.
	 *
	 * @param <T> the type of Scalar elements
	 */
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

	/**
     * Utility class that allows to access / modify elements of an array of
     * scalars using transformation of coordinates (e.g. crop, slice, dimension
     * permutation...).
     * 
     * @see #view(int[], Function)
     * 
     * @param <T>
     *            the type of data within the array
     */
    static class View<T extends Scalar> implements ScalarArray<T>
    {
        /** 
         * The array to synchronize with. /*
         */
        ScalarArray<T> array;
        
        /** 
         * The size of the view. 
         */
        int[] newDims;
        
        /**
         * The mapping between view coordinates and inner array coordinates.
         */
        Function<int[], int[]> coordsMapping;

        /**
         * Creates a new view over the input array or a subset of the input
         * array.
         * 
         * @param array
         *            the array to synchronize with
         * @param newDims
         *            the size of the view
         * @param coordsMapping
         *            the mapping between view coordinates and inner array
         *            coordinates.
         */
        public View(ScalarArray<T> array, int[] newDims, Function<int[], int[]> coordsMapping)
        {
            this.array = (ScalarArray<T>) array;
            this.newDims = newDims;
            this.coordsMapping = coordsMapping;
        }


        // =============================================================
        // Specialization of the Array interface

        @Override
        public double getValue(int... pos)
        {
            return array.getValue(coordsMapping.apply(pos));
        }

        @Override
        public void setValue(int[] pos, double value)
        {
            array.setValue(coordsMapping.apply(pos), value);
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
        public Factory<T> factory()
        {
            return array.factory();
        }

        @Override
        public T get(int... pos)
        {
            return array.get(coordsMapping.apply(pos));
        }

        @Override
        public void set(int[] pos, T value)
        {
            array.set(coordsMapping.apply(pos), value);
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
                    View.this.setValue(iter.get(), value);
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
                    View.this.set(iter.get(), value);
                }
            };
        }
    }
}
