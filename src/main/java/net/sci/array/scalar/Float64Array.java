/**
 * 
 */
package net.sci.array.scalar;

import java.util.function.Function;

import net.sci.array.Array;


/**
 * An array containing 64-bits floating point values.
 * 
 * @see Float32Array
 * 
 * @author dlegland
 *
 */
public interface Float64Array extends ScalarArray<Float64>
{
    // =============================================================
    // Static variables

    public static final Factory defaultFactory = new DenseFloat64ArrayFactory();

    
	// =============================================================
	// Static methods

	public static Float64Array create(int... dims)
	{
	    return defaultFactory.create(dims);
	}

	public static Float64Array create(int[] dims, double[] buffer)
	{
		switch (dims.length)
		{
		case 2:
			return new BufferedFloat64Array2D(dims[0], dims[1], buffer);
		case 3:
			return new BufferedFloat64Array3D(dims[0], dims[1], dims[2], buffer);
		default:
			return new BufferedFloat64ArrayND(dims, buffer);
		}
	}
	
    /**
     * Converts the input array into an instance of Float64Array.
     * 
     * Can process the following cases:
     * <ul>
     * <li>instances of Float64Array (through simple class-cast)</li>
     * <li>instances of Array that contain Float64 values</li>
     * <li>instances of ScalarArray</li>
     * </ul>
     * 
     * @see UInt8Array.#convert(Array)
     * 
     * @param array
     *            the array to convert
     * @return the equivalent Float64Array
     * @throws IllegalArgumentException
     *             if the input array does not comply to the above cases
     */
    public static Float64Array convert(Array<?> array)
    {
        // Simply cast instances of Float64Array
        if (array instanceof Float64Array)
        {
            return (Float64Array) array;
        }
        // Convert array containing Float64 values
        if (array.dataType().isAssignableFrom(Float64.class)) 
        {
            return convertArrayOfFloat64(array);
        }
        // convert scalar array
        if (array instanceof ScalarArray<?>)
        {
            return convertScalarArray((ScalarArray<?>) array);
        }
        
        throw new IllegalArgumentException("Can not convert array with class: " + array.getClass());
    }
    
    private static Float64Array convertArrayOfFloat64(Array<?> array)
    {
        Float64Array result = Float64Array.create(array.size());
        for (int[] pos : array.positions())
        {
            result.setValue(pos, ((Float64) array.get(pos)).getValue());
        }
        return result;
    }
    
    private static Float64Array convertScalarArray(ScalarArray<?> array)
    {
        Float64Array result = Float64Array.create(array.size());
        for (int[] pos : array.positions())
        {
            result.setValue(pos, array.getValue(pos));
        }
        return result;
    }
    
    /**
     * Encapsulates the specified array into a new Float64Array, by creating a
     * Wrapper if necessary. If the original array is already an instance of
     * Float64Array, it is returned.
     * 
     * @param array
     *            the original array
     * @return a Float64 view of the original array
     */
    @SuppressWarnings("unchecked")
    public static Float64Array wrap(Array<?> array)
    {
        if (array instanceof Float64Array)
        {
            return (Float64Array) array;
        }
        if (Float64.class.isAssignableFrom(array.dataType()))
        {
            return new Wrapper((Array<Float64>) array);
        }
        
        if (array instanceof ScalarArray)
        {
            return wrapScalar((ScalarArray<?>) array);
        }
        
        throw new IllegalArgumentException("Can not wrap an array with class " + array.getClass() + " and type " + array.dataType());
    }
    
    public static Float64Array wrapScalar(ScalarArray<?> array)
	{
		if (array instanceof Float64Array)
		{
			return (Float64Array) array;
		}
		return new ScalarArrayWrapper(array);
	}
	

	// =============================================================
	// Specialization of Array interface

	@Override
	public default Float64Array newInstance(int... dims)
	{
		return Float64Array.create(dims);
	}

    @Override
    public default Float64 get(int[] pos)
    {
        return new Float64(getValue(pos)); 
    }

    @Override
    public default void set(int[] pos, Float64 value)
    {
        setValue(pos, value.getValue());
    }

	@Override
	public default Float64Array duplicate()
	{
		// create output array
		Float64Array result = Float64Array.create(this.size());
	    for (int[] pos : positions())
	    {
	    	result.setValue(pos, this.getValue(pos));
	    }

        // return output
		return result;
	}

    public default Float64Array reshapeView(int[] newDims, Function<int[], int[]> coordsMapping)
    {
        return new ReshapeView(this, newDims, coordsMapping);
    }

    @Override
    public default ScalarArray.Factory<Float64> factory()
    {
        return defaultFactory;
    }

	@Override
	public default Class<Float64> dataType()
	{
		return Float64.class;
	}

    public default Iterator iterator()
    {
        return new Iterator()
        {
            PositionIterator iter = positionIterator();

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
            public Float64 next()
            {
                iter.forward();
                return Float64Array.this.get(iter.get());
            }

            @Override
            public double getValue()
            {
                return Float64Array.this.getValue(iter.get());
            }

            @Override
            public void setValue(double value)
            {
                Float64Array.this.setValue(iter.get(), value);
            }
        };
    }

	
	// =============================================================
	// Inner interface

	public interface Iterator extends ScalarArray.Iterator<Float64>
	{
		@Override
		public default Float64 get()
		{
			return new Float64(getValue());
		}
		
		@Override
		public default void set(Float64 value)
		{
			setValue(value.getValue());
		}
	}

    /**
     * Wraps explicitly an array containing <code>Float64</code> elements into an
     * instance of <code>Float64Array</code>.
     * 
     * Usage:
     * <pre>
     * {@code
     * Array<Float64> array = ...
     * Float64Array newArray = new Float64Array.Wrapper(array);
     * newArray.getValue(...);  
     * }
     * </pre>
     */
    static class Wrapper implements Float64Array
    {
        Array<Float64> array;
        
        public Wrapper(Array<Float64> array)
        {
            this.array = array;
        }
        
        @Override
        public int dimensionality()
        {
            return array.dimensionality();
        }

        @Override
        public int[] size()
        {
            return array.size();
        }

        @Override
        public int size(int dim)
        {
            return array.size(dim);
        }

        @Override
        public PositionIterator positionIterator()
        {
            return array.positionIterator();
        }

        @Override
        public double getValue(int[] pos)
        {
            return array.get(pos).getValue();
        }

        @Override
        public void setValue(int[] pos, double value)
        {
            array.set(pos, new Float64(value));
        }
    }
    
	/**
     * Wraps an instance of <code>ScalarArray</code> into an instance of
     * <code>Float64Array</code> by converting performing class cast on the fly.
     * The wrapper has same size as the inner array.
     *  
     * @see Float64Array#wrap(net.sci.array.Array)
     */
	class ScalarArrayWrapper implements Float64Array
	{
		ScalarArray<?> array;
		
		public ScalarArrayWrapper(ScalarArray<?> array)
		{
			this.array = array;
		}
		
		// =============================================================
		// Specialization of the Array interface

		@Override
		public int dimensionality()
		{
			return array.dimensionality();
		}

		@Override
		public int[] size()
		{
			return array.size();
		}

		@Override
		public int size(int dim)
		{
			return array.size(dim);
		}

		@Override
		public double getValue(int[] position)
		{
			return array.getValue(position);
		}

		@Override
		public void setValue(int[] pos, double value)
		{
			array.setValue(pos, value);
		}

		@Override
		public Float64 get(int[] pos)
		{
			return new Float64(array.getValue(pos));
		}

		@Override
		public void set(int[] pos, Float64 value)
		{
			array.setValue(pos, value.getValue());
		}

        @Override
        public PositionIterator positionIterator()
        {
            return array.positionIterator();
        }

		@Override
		public Iterator iterator()
		{
			return new Iterator(array.iterator());
		}
		
		class Iterator implements Float64Array.Iterator
		{
			ScalarArray.Iterator<?> iter;
			
			public Iterator(ScalarArray.Iterator<?> iter)
			{
				this.iter = iter;
			}

			@Override
			public void forward()
			{
				this.iter.forward();
			}

			@Override
			public Float64 next()
			{
				return new Float64(iter.nextValue());
			}

			@Override
			public boolean hasNext()
			{
				return iter.hasNext();
			}

			@Override
			public double getValue()
			{
				return iter.getValue();
			}

			@Override
			public void setValue(double value)
			{
				iter.setValue(value);
			}
		}
	}

    /**
     * Utility class for creating a reshape view on an array using arbitrary
     * coordinate mapping.
     *
     * @see Float64Array#reshapeView(int[], Function)
     */
    static class ReshapeView implements Float64Array
    {
        Float64Array array;
        
        int[] newDims;
        
        Function<int[], int[]> coordsMapping;

        /**
         * Creates a reshape view on the specified array that keeps the type of
         * the original array.
         * 
         * @param array
         *            the array to create a view on.
         * @param newDims
         *            the dimensions of the view.
         * @param coordsMapping
         *            the mapping from coordinate in view to the coordinates in
         *            the original array.
         */
        public ReshapeView(Float64Array array, int[] newDims, Function<int[], int[]> coordsMapping)
        {
            this.array = array;
            this.newDims = newDims;
            this.coordsMapping = coordsMapping;
        }

        /* (non-Javadoc)
         * @see net.sci.array.scalar.ScalarArray#getValue(int[])
         */
        @Override
        public double getValue(int[] pos)
        {
            return array.getValue(coordsMapping.apply(pos));
        }

        /* (non-Javadoc)
         * @see net.sci.array.scalar.ScalarArray#setValue(int[], double)
         */
        @Override
        public void setValue(int[] pos, double value)
        {
            array.setValue(coordsMapping.apply(pos), value);
        }

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
    }
    

    // =============================================================
    // Specialization of the Factory interface

    /**
     * Specialization of the ArrayFactory for generating instances of Float64Array.
     */
    public interface Factory extends ScalarArray.Factory<Float64>
    {
        /**
         * Creates a new Float64Array of the specified dimensions, initialized
         * with zeros.
         * 
         * @param dims
         *            the dimensions of the new array
         * @return a new Float64Array initialized with zeros
         */
        public Float64Array create(int... dims);

        /**
         * Creates a new Float64Array with the specified dimensions, filled with
         * the specified initial value.
         * 
         * @param dims
         *            the dimensions of the array to be created
         * @param value
         *            an instance of the initial integer value
         * @return a new instance of IntArray
         */
        public Float64Array create(int[] dims, Float64 value);
    }
}
