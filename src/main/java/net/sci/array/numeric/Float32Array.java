/**
 * 
 */
package net.sci.array.numeric;

import java.util.function.Function;

import net.sci.array.Array;
import net.sci.array.impl.ArrayWrapperStub;
import net.sci.array.numeric.impl.BufferedFloat32Array2D;
import net.sci.array.numeric.impl.BufferedFloat32Array3D;
import net.sci.array.numeric.impl.BufferedFloat32ArrayND;
import net.sci.array.numeric.impl.DenseFloat32ArrayFactory;


/**
 * An array containing 32-bits floating point values.
 * 
 * @see Float64Array
 * 
 * @author dlegland
 *
 */
public interface Float32Array extends ScalarArray<Float32>
{
    // =============================================================
    // Static variables

    public static final Factory defaultFactory = new DenseFloat32ArrayFactory();

    
    // =============================================================
    // Static methods
    
    public static Float32Array create(int... dims)
    {
        return defaultFactory.create(dims);
    }
    
    public static Float32Array create(int[] dims, float[] buffer)
    {
        return switch (dims.length)
        {
            case 2 -> new BufferedFloat32Array2D(dims[0], dims[1], buffer);
            case 3 -> new BufferedFloat32Array3D(dims[0], dims[1], dims[2], buffer);
            default -> new BufferedFloat32ArrayND(dims, buffer);
        };
    }
    
    /**
     * Converts the input array into an instance of Float32Array.
     * 
     * Can process the following cases:
     * <ul>
     * <li>instances of Float32Array (through simple class-cast)</li>
     * <li>instances of Array that contain Float32 values</li>
     * <li>instances of ScalarArray</li>
     * </ul>
     * 
     * @see UInt8Array.#convert(Array)
     * 
     * @param array
     *            the array to convert
     * @return the equivalent Float32Array
     * @throws IllegalArgumentException
     *             if the input array does not comply to the above cases
     */
    public static Float32Array convert(Array<?> array)
    {
        // Simply cast instances of Float32Array
        if (array instanceof Float32Array)
        {
            return (Float32Array) array;
        }
        // Convert array containing Float32 values
        if (array.elementClass().isAssignableFrom(Float32.class)) 
        {
            return convertArrayOfFloat32(array);
        }
        // convert scalar array
        if (array instanceof ScalarArray<?>)
        {
            return convertScalarArray((ScalarArray<?>) array);
        }
        
        throw new IllegalArgumentException("Can not convert array with class: " + array.getClass());
    }
    
    private static Float32Array convertArrayOfFloat32(Array<?> array)
    {
        Float32Array result = Float32Array.create(array.size());
        result.fillFloats(pos -> ((Float32) array.get(pos)).getFloat());
        return result;
    }
    
    private static Float32Array convertScalarArray(ScalarArray<?> array)
    {
        Float32Array result = Float32Array.create(array.size());
        result.fillValues(pos -> ((Scalar<?>) array.get(pos)).getValue());
        return result;
    }
    
    /**
     * Encapsulates the specified array into a new Float32Array, by creating a
     * Wrapper if necessary. If the original array is already an instance of
     * Float32Array, it is returned.
     * 
     * @param array
     *            the original array
     * @return a Float32 view of the original array
     */
    @SuppressWarnings("unchecked")
    public static Float32Array wrap(Array<?> array)
    {
        if (array instanceof Float32Array)
        {
            return (Float32Array) array;
        }
        if (array instanceof ScalarArray)
        {
            return wrapScalar((ScalarArray<?>) array);
        }
        
        if (Float32.class.isAssignableFrom(array.elementClass()))
        {
            return new Wrapper((Array<Float32>) array);
        }
        
        throw new IllegalArgumentException("Can not wrap an array with class " + array.getClass() + " and type " + array.elementClass());
    }
    
    public static Float32Array wrapScalar(ScalarArray<?> array)
    {
        if (array instanceof Float32Array)
        {
            return (Float32Array) array;
        }
        return new ScalarArrayWrapper(array);
    }
    
    
    // =============================================================
    // New methods
	
    public default void fillFloats(Function<int[], Float> fun)
    {
        for (int[] pos : this.positions())
        {
            this.setFloat(pos, fun.apply(pos));
        }
    }
    
    public float getFloat(int[] pos);
    
    public void setFloat(int[] pos, float value);


    // =============================================================
    // Specialization of ScalarArray interface

    @Override
    public default double getValue(int[] pos)
    {
        return getFloat(pos);
    }
    
    @Override
    public default void setValue(int[] pos, double value)
    {
        setFloat(pos, (float) value);
    }

    @Override
    public default Float32 typeMin()
    {
        return Float32.MIN_VALUE;
    }

    @Override
    public default Float32 typeMax()
    {
        return Float32.MAX_VALUE;
    }

    @Override
    public default Float32 createElement(double value)
    {
        return new Float32((float) value);
    }
    
    
	// =============================================================
	// Specialization of Array interface

	@Override
	public default Float32Array newInstance(int... dims)
	{
		return Float32Array.create(dims);
	}
	
	@Override
	public default ScalarArray.Factory<Float32> factory()
	{
		return defaultFactory;
	}

    /**
     * Override default behavior of Array interface to return the value
     * Float32.ZERO.
     * 
     * @return a default Float32 value.
     */
    @Override
    public default Float32 sampleElement()
    {
        return Float32.ZERO;
    }
    
    @Override
    public default Float32 get(int[] pos)
    {
        return new Float32(getFloat(pos)); 
    }

    @Override
    public default void set(int [] pos, Float32 value)
    {
        setFloat(pos, value.getFloat());
    }

	@Override
	public default Float32Array duplicate()
	{
		// create output array
		Float32Array result = Float32Array.create(this.size());
	    for (int[] pos : positions())
	    {
	    	result.setValue(pos, this.getValue(pos));
	    }
		
		// return output
		return result;
	}

    public default Float32Array reshapeView(int[] newDims, Function<int[], int[]> coordsMapping)
    {
        return new ReshapeView(this, newDims, coordsMapping);
    }

	@Override
	public default Class<Float32> elementClass()
	{
		return Float32.class;
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
            public Float32 next()
            {
                iter.forward();
                return Float32Array.this.get(iter.get());
            }

            @Override
            public float getFloat()
            {
                return Float32Array.this.getFloat(iter.get());
            }

            @Override
            public void setFloat(float value)
            {
                Float32Array.this.setFloat(iter.get(), value);
            }

            @Override
            public double getValue()
            {
                return Float32Array.this.getValue(iter.get());
            }

            @Override
            public void setValue(double value)
            {
                Float32Array.this.setValue(iter.get(), value);
            }
        };
    }

    
	// =============================================================
	// Inner interface

	public interface Iterator extends ScalarArray.Iterator<Float32>
	{
        public float getFloat();

        public void setFloat(float value);
        
        @Override
        public default double getValue()
        {
            return getFloat();
        }
        
        @Override
        public default void setValue(double value)
        {
            setFloat((float) value);
        }
        
		@Override
		public default Float32 get()
		{
			return new Float32(getFloat());
		}
		
		@Override
		public default void set(Float32 value)
		{
			setFloat(value.getFloat());
		}
	}
	
    /**
     * Wraps explicitly an array containing <code>Float32</code> elements into an
     * instance of <code>Float32Array</code>.
     * 
     * Usage:
     * <pre>
     * {@code
     * Array<Float32> array = ...
     * Float32Array newArray = new Float32Array.Wrapper(array);
     * newArray.getFloat(...);  
     * }
     * </pre>
     */
    static class Wrapper extends ArrayWrapperStub<Float32> implements Float32Array
    {
        Array<Float32> array;
        
        public Wrapper(Array<Float32> array)
        {
            super(array);
            this.array = array;
        }
        
        @Override
        public float getFloat(int[] pos)
        {
            return array.get(pos).getFloat();
        }

        @Override
        public void setFloat(int[] pos, float value)
        {
            array.set(pos, new Float32(value));
        }

        @Override
        public double getValue(int[] pos)
        {
            return array.get(pos).getValue();
        }

        @Override
        public void setValue(int[] pos, double value)
        {
            array.set(pos, new Float32((float) value));
        }
    }
    
	/**
     * Wraps an instance of <code>ScalarArray</code> into an instance of
     * <code>Float32Array</code> by converting performing class cast on the fly.
     * The wrapper has same size as the inner array.
     *  
     * @see Float32Array#wrap(net.sci.array.Array)
     */
	class ScalarArrayWrapper extends ArrayWrapperStub<Float32> implements Float32Array
	{
		ScalarArray<?> array;
		
		public ScalarArrayWrapper(ScalarArray<?> array)
		{
		    super(array);
			this.array = array;
		}

        @Override
        public float getFloat(int[] position)
        {
            return (float) array.getValue(position);
        }

        @Override
        public void setFloat(int[] pos, float value)
        {
            array.setValue(pos, value);
        }


	    // =============================================================
		// Specialization of the Array interface

		@Override
		public void setValue(int[] pos, double value)
		{
			array.setValue(pos, value);
		}


		@Override
		public Float32 get(int[] pos)
		{
			return new Float32((float) array.getValue(pos));
		}

		@Override
		public void set(int[] pos, Float32 value)
		{
			array.setValue(pos, value.getValue());
		}
	}

    /**
     * Utility class for creating a reshape view on an array using arbitrary
     * coordinate mapping.
     *
     * @see Float32Array#reshapeView(int[], Function)
     */
    static class ReshapeView implements Float32Array
    {
        Float32Array array;
        
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
        public ReshapeView(Float32Array array, int[] newDims, Function<int[], int[]> coordsMapping)
        {
            this.array = array;
            this.newDims = newDims;
            this.coordsMapping = coordsMapping;
        }

        @Override
        public float getFloat(int[] pos)
        {
            return array.getFloat(coordsMapping.apply(pos));
        }

        @Override
        public void setFloat(int[] pos, float value)
        {
            array.setFloat(coordsMapping.apply(pos), value);
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
     * Specialization of the ArrayFactory for generating instances of Float32Array.
     */
    public interface Factory extends ScalarArray.Factory<Float32>
    {
        /**
         * Creates a new Float32Array of the specified dimensions, initialized
         * with zeros.
         * 
         * @param dims
         *            the dimensions of the new array
         * @return a new Float32Array initialized with zeros
         */
        public Float32Array create(int... dims);

        /**
         * Creates a new Float32Array with the specified dimensions, filled with
         * the specified initial value.
         * 
         * @param dims
         *            the dimensions of the array to be created
         * @param value
         *            an instance of the initial integer value
         * @return a new instance of IntArray
         */
        public Float32Array create(int[] dims, Float32 value);
    }
}
