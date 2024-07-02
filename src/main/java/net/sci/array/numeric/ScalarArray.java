/**
 * 
 */
package net.sci.array.numeric;

import java.util.function.Function;
import java.util.function.UnaryOperator;

import net.sci.array.Array;
import net.sci.array.Arrays;
import net.sci.array.impl.ArrayWrapperStub;

/**
 * Specialization of the Array interface that contains Scalar values.
 * 
 * Provides methods for accessing and modifying values as double, and populating
 * the array with floating point values.
 * 
 * @param <S>
 *            the type of Scalar.
 * @author dlegland
 *
 */
public interface ScalarArray<S extends Scalar<S>> extends NumericArray<S>
{
    // =============================================================
    // static methods

    /**
     * Wraps the specified array containing Scalar data into an instance of
     * ScalarArray.
     * 
     * Example of use:
     * {@snippet lang="java" :
     *  if (Scalar.class.isAssignableFrom(array.elementClass()))
     *  {
     *      @SuppressWarnings({ "unchecked", "rawtypes" })
     *      ScalarArray<?> scalarArray = ScalarArray.wrap((Array<? extends Scalar>) array);
     *      // do something with scalarArray
     *  }
     *  else
     *      throw new RuntimeException("Array does not contain scalar type data");
     * }
     * 
     * @param <S>
     *            the type of scalar data stored within the array
     * @param array
     *            the array to wrap
     * @return an instance of ScalarArray. If the input array is already an
     *         instance of ScalarArray, it is returned.
     */
    public static <S extends Scalar<S>> ScalarArray<S> wrap(Array<S> array)
    {
        if (array instanceof  ScalarArray) return (ScalarArray<S>) array;
        
        return new Wrapper<S>(array);
    }
    
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
		Iterator<S> iter = iterator();
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
    public double getValue(int[] pos);
    
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
     * @see net.sci.array.process.numeric.ApplyFunction
     */
    public default ScalarArray<S> apply(UnaryOperator<Double> fun)
    {
        ScalarArray<S> res = newInstance(size());
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
    public default ScalarArray<S> min(double v)
    {
        ScalarArray<S> res = newInstance(size());
        res.fillValues(pos -> Math.min(this.getValue(pos), v));
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
    public default ScalarArray<S> max(double v)
    {
        ScalarArray<S> res = newInstance(size());
        res.fillValues(pos -> Math.max(this.getValue(pos), v));
        return res;
    }
    

    // =============================================================
    // Extends NumericArray interface to work with double values
    
    /**
     * Adds a floating-point value to each element of this scalar array, and
     * returns the resulting array.
     * 
     * @param v
     *            the value to add
     * @return a new array where each element is added the specified value
     */
    public default ScalarArray<S> plus(double v)
    {
        ScalarArray<S> res = newInstance(size());
        res.fillValues(pos -> this.getValue(pos) + v);
        return res;
    }

    /**
     * Subtracts a floating-point value from each element of this scalar array,
     * and returns the resulting array.
     * 
     * @param v
     *            the value to subtract
     * @return a new array where the specified value is subtracted from each
     *         element
     */
    public default ScalarArray<S> minus(double v)
    {
        ScalarArray<S> res = newInstance(size());
        res.fillValues(pos -> this.getValue(pos) - v);
        return res;
    }
    
    /**
     * Create a new element compatible with elements within this array, from a
     * floating point value.
     * 
     * @param value
     *            the numeric value of the new element
     * @return an instance of the element type of the array corresponding to the
     *         specified value.
     */
    public default S createElement(double value)
    {
        return sampleElement().fromValue(value);
    }
    
    /**
     * Returns the smallest value (closest or equal to negative infinity) that
     * can be represented with the type contained within this array.
     * 
     * @return the smallest value that can be represented with the type
     *         contained within this array
     */
    public default S typeMin()
    {
        return sampleElement().typeMin();
    }
    
    /**
     * Returns the largest value (closest or equal to positive infinity) that
     * can be represented with the type contained within this array.
     * 
     * @return the largest value that can be represented with the type contained
     *         within this array
     */
    public default S typeMax()
    {
        return sampleElement().typeMax();
    }
    

    // =============================================================
    // Implementation of NumericArray interface

    /**
     * Adds a Scalar value to each element of this Scalar array, and returns
     * the result array.
     * 
     * @param v
     *            the value to add
     * @return a new array with the value added
     */
    public default ScalarArray<S> plus(S v)
    {
        return plus(v.getValue());
    }

    /**
     * Subtracts a Scalar value from each element of this Scalar array, and
     * returns the result array.
     * 
     * @param v
     *            the value to subtract
     * @return a new array with the value subtracted
     */
    public default ScalarArray<S> minus(S v)
    {
        return minus(v.getValue());
    }
    
    @Override
    public default ScalarArray<S> times(double k)
    {
        ScalarArray<S> res = newInstance(size());
        res.fillValues(pos -> this.getValue(pos) * k);
        return res;
    }

    @Override
    public default ScalarArray<S> divideBy(double k)
    {
        ScalarArray<S> res = newInstance(size());
        res.fillValues(pos -> this.getValue(pos) / k);
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
    public default ScalarArray<S> reshape(int... newDims)
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
        ScalarArray<S> res = this.newInstance(newDims);
        
        // iterate using a pair of Iterator instances
        Iterator<S> iter1 = this.iterator();
        Iterator<S> iter2 = res.iterator();
        while(iter1.hasNext())
        {
            iter2.setNextValue(iter1.nextValue());
        }
        
        return res;
    }
   
    
	@Override
	public ScalarArray<S> newInstance(int... dims);

	@Override
	public default ScalarArray<S> duplicate()
	{
	    ScalarArray<S> dup = newInstance(size());
	    dup.fillValues(pos -> getValue(pos));
	    return dup;
	}
	
	@Override
    public default ScalarArray<S> reshapeView(int[] newDims, Function<int[], int[]> coordsMapping)
    {
        return new ReshapeView<S>(this, newDims, coordsMapping);
    }

    @Override
    public ScalarArray.Factory<S> factory();

    /**
     * Returns an iterator over the elements of the array, for implementing the
     * Iterable interface.
     * 
     * Provides a default implementation based on the
     * position iterator.
     */
    @Override
    public default ScalarArray.Iterator<S> iterator()
    {
        return new Iterator<S>()
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
            public S get()
            {
                return ScalarArray.this.get(iter.get(pos));
            }

            @Override
            public void set(S value)
            {
                ScalarArray.this.set(iter.get(pos), value);
            }

            @Override
            public boolean hasNext()
            {
                return iter.hasNext();
            }

            @Override
            public double getValue()
            {
                return ScalarArray.this.getValue(iter.get(pos));
            }

            @Override
            public void setValue(double value)
            {
                ScalarArray.this.setValue(iter.get(pos), value);
            }
        };
    }
	
	
    // =============================================================
    // Specialization of the Factory interface

	/**
	 * A factory for building new ScalarArray instances.
	 *
	 * @param <S> the type of Scalar.
	 */
	public interface Factory<S extends Scalar<S>> extends Array.Factory<S>
	{
        /**
         * Creates a new scalar array of the specified dimensions, initialized
         * with zeros.
         * 
         * @param dims
         *            the dimensions of the new array
         * @return a new scalar array initialized with zeros
         */
	    public ScalarArray<S> create(int... dims);

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
        public default ScalarArray<S> create(int[] dims, S value)
        {
            ScalarArray<S> res = create(dims);
            res.fill(value);
            return res;
        }
	}
	
	
	// =============================================================
	// Inner interface

	/**
	 * Iterator over the Scalar objects within this array.
	 *
	 * @param <S> the type of Scalar elements
	 */
	public interface Iterator<S extends Scalar<S>> extends Array.Iterator<S>
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
         * Returns the value at the current iterator position as a double value
         * 
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
     * Utility class the wraps an array of <code>Scalar</code> into an instance of
     * <code>ScalarArray</code>.
     * 
     * @param <S>
     *            the type of Scalar contained in array.
     */
	static class Wrapper<S extends Scalar<S>> extends ArrayWrapperStub<S> implements ScalarArray<S>
	{
        /**
         * the array to wrap. Already stored in super class, but store it here
         * as well to keep type of generic.
         */
	    Array<S> array;
	    
	    /**
         * Keep a sample element to allow the creation of generic arrays, and to
         * retrieve type-related information.
         */
	    S sample;
	    
        protected Wrapper(Array<S> array)
        {
            super(array);
            this.array = array;
            this.sample = array.sampleElement();
        }

        @SuppressWarnings("unchecked")
        @Override
        public Class<S> elementClass()
        {
            return (Class<S>) sample.getClass();
        }

        @Override
        public S createElement(double value)
        {
            return sample.fromValue(value);
        }
        
        @Override
        public S typeMin()
        {
            return sample.typeMin();
        }
        
        @Override
        public S typeMax()
        {
            return sample.typeMax();
        }
        
        @Override
        public double getValue(int[] pos)
        {
            return array.get(pos).getValue();
        }

        @Override
        public void setValue(int[] pos, double value)
        {
            array.set(pos, sample.fromValue(value));
        }

        @Override
        public S get(int[] pos)
        {
            return array.get(pos);
        }

        @Override
        public void set(int[] pos, S value)
        {
            array.set(pos, value);
        }

        @Override
        public ScalarArray<S> newInstance(int... dims)
        {
            return ScalarArray.wrap(array.newInstance(array.size()));
        }

        @Override
        public net.sci.array.numeric.ScalarArray.Factory<S> factory()
        {
            return new net.sci.array.numeric.ScalarArray.Factory<S>() 
            {
                @Override
                public ScalarArray<S> create(int... dims)
                {
                    return ScalarArray.wrap(array.newInstance(array.size()));
                }
            };
        }
	}
	
    /**
     * Specialization of the Array.ReshapeView class that allows to access /
     * modify elements of an array of scalars using transformation of
     * coordinates (e.g. crop, slice, dimension permutation...).
     * 
     * @see #reshapeView(int[], Function)
     * 
     * @param <S>
     *            the type of (scalar) data within the array
     */
    static class ReshapeView<S extends Scalar<S>> extends Array.ReshapeView<S> implements ScalarArray<S>
    {
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
        public ReshapeView(ScalarArray<S> array, int[] newDims, Function<int[], int[]> coordsMapping)
        {
            super(array, newDims, coordsMapping);
        }


        // =============================================================
        // Specialization of the ScalarArray interface

        @Override
        public double getValue(int[] pos)
        {
            return ((ScalarArray<?>) array).getValue(coordsMapping.apply(pos));
        }

        @Override
        public void setValue(int[] pos, double value)
        {
            ((ScalarArray<?>) array).setValue(coordsMapping.apply(pos), value);
        }

        @Override
        public S createElement(double value)
        {
            return ((ScalarArray<S>) array).createElement(value);
        }
        
        
        // =============================================================
        // Specialization of the Array interface

        @Override
        public ScalarArray<S> newInstance(int... dims)
        {
            return ((ScalarArray<S>) array).newInstance(dims);
        }

        @Override
        public ScalarArray.Factory<S> factory()
        {
            return ((ScalarArray<S>) array).factory();
        }
    }
}
