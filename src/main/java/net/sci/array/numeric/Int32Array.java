/**
 * 
 */
package net.sci.array.numeric;

import java.util.function.Function;

import net.sci.array.Array;
import net.sci.array.impl.ArrayWrapperStub;
import net.sci.array.impl.DefaultPositionIterator;
import net.sci.array.numeric.impl.BufferedInt32Array2D;
import net.sci.array.numeric.impl.BufferedInt32Array3D;
import net.sci.array.numeric.impl.BufferedInt32ArrayND;
import net.sci.array.numeric.impl.DenseInt32ArrayFactory;

/**
 * An array containing 32-bits signed integers.
 * 
 * @see Int16Array
 * 
 * @author dlegland
 *
 */
public interface Int32Array extends IntArray<Int32>
{
    // =============================================================
    // Static variables

    public static final Factory defaultFactory = new DenseInt32ArrayFactory();
    
    // =============================================================
    // Static methods
    
    public static Int32Array create(int... dims)
    {
        return defaultFactory.create(dims);
    }
    
    public static Int32Array create(int[] dims, int[] buffer)
    {
        return switch (dims.length)
        {
            case 2 -> new BufferedInt32Array2D(dims[0], dims[1], buffer);
            case 3 -> new BufferedInt32Array3D(dims[0], dims[1], dims[2], buffer);
            default -> new BufferedInt32ArrayND(dims, buffer);
        };
    }
    
    /**
     * Converts the input array into an instance of Int32Array.
     * 
     * Can process the following cases:
     * <ul>
     * <li>instances of Int32Array (through simple class-cast)</li>
     * <li>instances of Array that contain Int32 values</li>
     * <li>instances of ScalarArray</li>
     * </ul>
     * 
     * @see UInt8Array.#convert(Array)
     * 
     * @param array
     *            the array to convert
     * @return the equivalent Int32Array
     * @throws IllegalArgumentException
     *             if the input array does not comply to the above cases
     */
    public static Int32Array convert(Array<?> array)
    {
        // Simply cast instances of Int32Array
        if (array instanceof Int32Array)
        {
            return (Int32Array) array;
        }
        // Convert array containing Int32 values
        if (array.elementClass().isAssignableFrom(Int32.class)) 
        {
            return convertArrayOfInt32(array);
        }
        // convert scalar array
        if (array instanceof ScalarArray<?>)
        {
            return convertScalarArray((ScalarArray<?>) array);
        }
        
        throw new IllegalArgumentException("Can not convert array with class: " + array.getClass());
    }
    
    private static Int32Array convertArrayOfInt32(Array<?> array)
    {
        Int32Array result = Int32Array.create(array.size());
        for (int[] pos : array.positions())
        {
            result.setInt(pos, ((Int32) array.get(pos)).intValue());
        }
        return result;
    }
    
    private static Int32Array convertScalarArray(ScalarArray<?> array)
    {
        Int32Array result = Int32Array.create(array.size());
        for (int[] pos : array.positions())
        {
            result.setValue(pos, array.getValue(pos));
        }
        return result;
    }
    
    /**
     * Encapsulates the specified array into a new Int32Array, by creating a
     * Wrapper if necessary. If the original array is already an instance of
     * Int32Array, it is returned.
     * 
     * @param array
     *            the original array
     * @return a Int32 view of the original array
     */
    @SuppressWarnings("unchecked")
    public static Int32Array wrap(Array<?> array)
    {
        if (array instanceof Int32Array)
        {
            return (Int32Array) array;
        }
        if (array instanceof ScalarArray)
        {
            return wrapScalar((ScalarArray<?>) array);
        }
        
        if (Int32.class.isAssignableFrom(array.elementClass()))
        {
            return new Wrapper((Array<Int32>) array);
        }
        
        throw new IllegalArgumentException("Can not wrap an array with class " + array.getClass() + " and type " + array.elementClass());
    }

    public static Int32Array wrapScalar(ScalarArray<?> array)
    {
        if (array instanceof Int32Array)
        {
            return (Int32Array) array;
        }
        return new ScalarArrayWrapper(array);
    }

		
    // =============================================================
    // Specialization of the ScalarArray interface

    /**
     * Sets the value at the specified position, by clamping the value between 0
     * and 255.
     */
    public default void setValue(int[] pos, double value)
    {
        setInt(pos, Int32.convert(value));
    }

    @Override
    public default Int32 typeMin()
    {
        return Int32.MIN_VALUE;
    }

    @Override
    public default Int32 typeMax()
    {
        return Int32.MAX_VALUE;
    }

    @Override
    public default Int32 createElement(double value)
    {
        return new Int32(Int32.convert(value));
    }
    
    
	// =============================================================
	// Specialization of the Array interface

    @Override
	public default Int32Array newInstance(int... dims)
	{
		return Int32Array.create(dims);
	}

	@Override
	public default Factory factory()
	{
		return defaultFactory;
	}

    /**
     * Override default behavior of Array interface to return the value
     * Int32.ZERO.
     * 
     * @return a default Int32 value.
     */
    @Override
    public default Int32 sampleElement()
    {
        return Int32.ZERO;
    }
    
    @Override
    public default Int32 get(int[] pos)
    {
        return new Int32(getInt(pos)); 
    }

    @Override
    public default void set(int[] pos, Int32 value)
    {
        setInt(pos, value.intValue());
    }

	@Override
	public default Int32Array duplicate()
	{
		// create output array
		Int32Array result = Int32Array.create(this.size());
	    for (int[] pos : positions())
	    {
	    	result.setInt(pos, getInt(pos));
	    }
		
		// return output
		return result;
	}

    public default Int32Array reshapeView(int[] newDims, Function<int[], int[]> coordsMapping)
    {
        return new ReshapeView(this, newDims, coordsMapping);
    }

	@Override
	public default Class<Int32> elementClass()
	{
		return Int32.class;
	}

    public default Iterator iterator()
    {
        return new Iterator()
        {
            PositionIterator iter = new DefaultPositionIterator(Int32Array.this.size());

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
            public Int32 next()
            {
                iter.forward();
                return Int32Array.this.get(iter.get());
            }

            @Override
            public int getInt()
            {
                return Int32Array.this.getInt(iter.get());
            }

            @Override
            public void setInt(int intValue)
            {
                Int32Array.this.setInt(iter.get(), intValue);
            }
        };
    }

    
	// =============================================================
	// Inner interface

	public interface Iterator extends IntArray.Iterator<Int32>
	{
		@Override
		public default Int32 get()
		{
			return new Int32(getInt());
		}
		
		@Override
		public default void set(Int32 value)
		{
			setInt(value.intValue());
		}
	}

    /**
     * Wraps explicitly an array containing <code>Int32</code> elements into an
     * instance of <code>Int32Array</code>.
     * 
     * Usage:
     * <pre>
     * {@code
     * Array<Int32> array = ...
     * Int32Array newArray = new Int32Array.Wrapper(array);
     * newArray.getInt(...);  
     * }
     * </pre>
     */
    static class Wrapper extends ArrayWrapperStub<Int32> implements Int32Array
    {
        Array<Int32> array;
        
        public Wrapper(Array<Int32> array)
        {
            super(array);
            this.array = array;
        }
        
        @Override
        public int getInt(int[] pos)
        {
            return array.get(pos).intValue();
        }

        @Override
        public void setInt(int[] pos, int value)
        {
            array.set(pos, new Int32(value));
        }
    }
    
    /**
     * Wraps an instance of <code>ScalarArray</code> into an instance of
     * <code>Int32Array</code> by converting performing class cast on the fly.
     */
	class ScalarArrayWrapper extends ArrayWrapperStub<Int32> implements Int32Array
	{
		ScalarArray<?> array;
		
		public ScalarArrayWrapper(ScalarArray<?> array)
		{
		    super(array);
			this.array = array;
		}
		

		// =============================================================
		// Implementation of the Int32Array interface

		@Override
		public int getInt(int[] pos)
		{
			return (int) array.getValue(pos);
		}

		@Override
		public void setInt(int[] pos, int value)
		{
			setValue(pos, value);
		}

		
		// =============================================================
		// Specialization of the Array interface

		@Override
		public Int32 get(int[] pos)
		{
			return new Int32((int) array.getValue(pos));
		}

		@Override
		public void set(int[] pos, Int32 value)
		{
			array.setValue(pos, value.value());
		}
	}

    /**
     * Utility class for creating a reshape view on an array using arbitrary
     * coordinate mapping.
     *
     * @see Int32Array#reshapeView(int[], Function)
     */
    static class ReshapeView implements Int32Array
    {
        Int32Array array;
        
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
        public ReshapeView(Int32Array array, int[] newDims, Function<int[], int[]> coordsMapping)
        {
            this.array = array;
            this.newDims = newDims;
            this.coordsMapping = coordsMapping;
        }

        /* (non-Javadoc)
         * @see net.sci.array.scalar.IntArray#getInt(int[])
         */
        @Override
        public int getInt(int[] pos)
        {
            return array.getInt(coordsMapping.apply(pos));
        }

        /* (non-Javadoc)
         * @see net.sci.array.scalar.IntArray#setInt(int[], int)
         */
        @Override
        public void setInt(int[] pos, int value)
        {
            array.setInt(coordsMapping.apply(pos), value);
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
     * Specialization of the ArrayFactory for generating instances of Int32Array.
     */
    public interface Factory extends IntArray.Factory<Int32>
    {
        /**
         * Creates a new Int32Array of the specified dimensions, initialized
         * with zeros.
         * 
         * @param dims
         *            the dimensions of the new array
         * @return a new Int32Array initialized with zeros
         */
        public Int32Array create(int... dims);

        /**
         * Creates a new Int32Array with the specified dimensions, filled with
         * the specified initial value.
         * 
         * @param dims
         *            the dimensions of the array to be created
         * @param value
         *            an instance of the initial integer value
         * @return a new instance of IntArray
         */
        public Int32Array create(int[] dims, Int32 value);
    }
}
