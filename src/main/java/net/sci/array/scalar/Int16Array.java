/**
 * 
 */
package net.sci.array.scalar;

import java.util.function.Function;

import net.sci.array.Array;
import net.sci.array.ArrayWrapperStub;


/**
 * An array containing 16-bits signed integers.
 * 
 * @see Int32Array
 * @see UInt16Array
 * 
 * @author dlegland
 *
 */
public interface Int16Array extends IntArray<Int16>
{
    // =============================================================
    // Static variables

    public static final Factory defaultFactory = new DenseInt16ArrayFactory();

    
	// =============================================================
	// Static methods

	public static Int16Array create(int... dims)
	{
	    return defaultFactory.create(dims);
	}
	
	public static Int16Array create(int[] dims, short[] buffer)
	{
		switch (dims.length)
		{
		case 2:
			return new BufferedInt16Array2D(dims[0], dims[1], buffer);
		case 3:
			return new BufferedInt16Array3D(dims[0], dims[1], dims[2], buffer);
		default:
			return new BufferedInt16ArrayND(dims, buffer);
		}
	}
	
    /**
     * Converts the input array into an instance of Int16Array.
     * 
     * Can process the following cases:
     * <ul>
     * <li>instances of Int16Array (through simple class-cast)</li>
     * <li>instances of Array that contain Int16 values</li>
     * <li>instances of ScalarArray</li>
     * </ul>
     * 
     * @see UInt8Array.#convert(Array)
     * 
     * @param array
     *            the array to convert
     * @return the equivalent Int16Array
     * @throws IllegalArgumentException
     *             if the input array does not comply to the above cases
     */
    public static Int16Array convert(Array<?> array)
    {
        // Simply cast instances of Int16Array
        if (array instanceof Int16Array)
        {
            return (Int16Array) array;
        }
        // Convert array containing Int16 values
        if (array.dataType().isAssignableFrom(Int16.class)) 
        {
            return convertArrayOfInt16(array);
        }
        // convert scalar array
        if (array instanceof ScalarArray<?>)
        {
            return convertScalarArray((ScalarArray<?>) array);
        }
        
        throw new IllegalArgumentException("Can not convert array with class: " + array.getClass());
    }
    
    private static Int16Array convertArrayOfInt16(Array<?> array)
    {
        Int16Array result = Int16Array.create(array.size());
        for (int[] pos : array.positions())
        {
            result.setShort(pos, ((Int16) array.get(pos)).getShort());
        }
        return result;
    }
    
    private static Int16Array convertScalarArray(ScalarArray<?> array)
    {
		Int16Array result = Int16Array.create(array.size());
	    for (int[] pos : array.positions())
	    {
	    	result.setValue(pos, array.getValue(pos));
	    }
		return result;
	}
    
    /**
     * Encapsulates the specified array into a new Int16Array, by creating a
     * Wrapper if necessary. If the original array is already an instance of
     * Int16Array, it is returned.
     * 
     * @param array
     *            the original array
     * @return a Int16 view of the original array
     */
    @SuppressWarnings("unchecked")
    public static Int16Array wrap(Array<?> array)
    {
        if (array instanceof Int16Array)
        {
            return (Int16Array) array;
        }
        if (Int16.class.isAssignableFrom(array.dataType()))
        {
            return new Wrapper((Array<Int16>) array);
        }
        if (array instanceof ScalarArray)
        {
            return wrapScalar((ScalarArray<?>) array);
        }
        
        
        throw new IllegalArgumentException("Can not wrap an array with class " + array.getClass() + " and type " + array.dataType());
    }

	public static Int16Array wrapScalar(ScalarArray<?> array)
	{
		if (array instanceof Int16Array)
		{
			return (Int16Array) array;
		}
		return new ScalarArrayWrapper(array);
	}
	

	// =============================================================
	// New methods

    public default void fillShorts(Function<int[], Short> fun)
    {
        for (int[] pos : this.positions())
        {
            this.setShort(pos, fun.apply(pos));
        }
    }
    
	public short getShort(int[] pos);
	
	public void setShort(int[] pos, short value);
	
	
	// =============================================================
	// Specialization of the IntArray interface

	@Override
	public default int getInt(int[] pos)
	{
		return getShort(pos); 
	}

	/**
	 * Sets the value at the specified position, by clamping the value between
	 * min and max admissible values.
	 */
	@Override
	public default void setInt(int[] pos, int value)
	{
		setShort(pos, (short) Math.min(Math.max(value, Int16.MIN_VALUE), Int16.MAX_VALUE));
	}

		
    // =============================================================
    // Specialization of the ScalarArray interface

    @Override
    public default Int16 createElement(double value)
    {
        return new Int16(Int16.convert(value));
    }
    
    
	// =============================================================
	// Specialization of the Array interface

    @Override
	public default Int16Array newInstance(int... dims)
	{
		return Int16Array.create(dims);
	}

	@Override
	public default Factory factory()
	{
		return defaultFactory;
	}

    /**
     * Override default behavior of Array interface to return the value
     * Int16.ZERO.
     * 
     * @return a default Int16 value.
     */
    @Override
    public default Int16 sampleElement()
    {
        return Int16.ZERO;
    }
    
    @Override
    public default Int16 get(int[] pos)
    {
        return new Int16(getShort(pos)); 
    }

    @Override
    public default void set(int[] pos, Int16 value)
    {
        setShort(pos, value.getShort());
    }

	@Override
	public default Int16Array duplicate()
	{
		// create output array
		Int16Array result = Int16Array.create(this.size());
	    for (int[] pos : positions())
	    {
	    	result.setShort(pos, getShort(pos));
	    }
		
		// return output
		return result;
	}

    public default Int16Array reshapeView(int[] newDims, Function<int[], int[]> coordsMapping)
    {
        return new ReshapeView(this, newDims, coordsMapping);
    }


	@Override
	public default Class<Int16> dataType()
	{
		return Int16.class;
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
            public Int16 next()
            {
                iter.forward();
                return Int16Array.this.get(iter.get());
            }

            @Override
            public short getShort()
            {
                return Int16Array.this.getShort(iter.get());
            }

            @Override
            public void setShort(short s)
            {
                Int16Array.this.setShort(iter.get(), s);
            }
        };
    }

	


	// =============================================================
	// Inner interface

	public interface Iterator extends IntArray.Iterator<Int16>
	{
		public short getShort();
		public void setShort(short s);
		
		@Override
		public default int getInt()
		{
			return getShort(); 
		}

		@Override
		public default void setInt(int value)
		{
			setShort((short) Int16.clamp(value));
		}

		@Override
		public default Int16 get()
		{
			return new Int16(getShort());
		}
		
		@Override
		public default void set(Int16 value)
		{
			setShort(value.getShort());
		}
	}
	
    /**
     * Wraps explicitly an array containing <code>Int16</code> elements into an
     * instance of <code>Int16Array</code>.
     * 
     * Usage:
     * <pre>
     * {@code
     * Array<Int16> array = ...
     * Int16Array newArray = new Int16Array.Wrapper(array);
     * newArray.getInt(...);  
     * }
     * </pre>
     */
    static class Wrapper extends ArrayWrapperStub<Int16> implements Int16Array
    {
        Array<Int16> array;
        
        public Wrapper(Array<Int16> array)
        {
            super(array);
            this.array = array;
        }
        
        @Override
        public short getShort(int[] pos)
        {
            return array.get(pos).getShort();
        }

        @Override
        public void setShort(int[] pos, short value)
        {
            array.set(pos, new Int16(value));
        }
    }
    
	/**
     * Wraps an instance of <code>ScalarArray</code> into an instance of
     * <code>Int16Array</code> by converting performing class cast on the fly.
     * The wrapper has same size as the inner array.
     *  
     * @see Int16Array#wrap(net.sci.array.Array)
     */
	class ScalarArrayWrapper extends ArrayWrapperStub<Int16> implements Int16Array
	{
		ScalarArray<?> array;
		
		public ScalarArrayWrapper(ScalarArray<?> array)
		{
		    super(array);
			this.array = array;
		}
		

		// =============================================================
		// Implementation of the Int16Array interface

		@Override
		public short getShort(int[] pos)
		{
			return get(pos).getShort();
		}

		@Override
		public void setShort(int[] pos, short value)
		{
			set(pos, new Int16(value));
		}

		
		// =============================================================
		// Specialization of the Array interface

		@Override
		public Int16 get(int[] pos)
		{
			return new Int16(Int16.convert(array.getValue(pos)));
		}

		@Override
		public void set(int[] pos, Int16 value)
		{
			array.setValue(pos, value.getValue());
		}
	}
	
    /**
     * Utility class for creating a reshape view on an array using arbitrary
     * coordinate mapping.
     *
     * @see Int16Array#reshapeView(int[], Function)
     */
    static class ReshapeView implements Int16Array
    {
        Int16Array array;
        
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
        public ReshapeView(Int16Array array, int[] newDims, Function<int[], int[]> coordsMapping)
        {
            this.array = array;
            this.newDims = newDims;
            this.coordsMapping = coordsMapping;
        }

        /* (non-Javadoc)
         * @see net.sci.array.scalar.Int16Array#getShort(int[])
         */
        @Override
        public short getShort(int[] pos)
        {
            return array.getShort(coordsMapping.apply(pos));
        }

        /* (non-Javadoc)
         * @see net.sci.array.scalar.Int16Array#setShort(int[], short)
         */
        @Override
        public void setShort(int[] pos, short s)
        {
            array.setShort(coordsMapping.apply(pos), s);
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
     * Specialization of the ArrayFactory for generating instances of Int16Array.
     */
    public interface Factory extends IntArray.Factory<Int16>
    {
        /**
         * Creates a new Int16Array of the specified dimensions, initialized
         * with zeros.
         * 
         * @param dims
         *            the dimensions of the new array
         * @return a new Int16Array initialized with zeros
         */
        public Int16Array create(int... dims);

        /**
         * Creates a new Int16Array with the specified dimensions, filled with
         * the specified initial value.
         * 
         * @param dims
         *            the dimensions of the array to be created
         * @param value
         *            an instance of the initial integer value
         * @return a new instance of IntArray
         */
        public Int16Array create(int[] dims, Int16 value);
    }
}
