/**
 * 
 */
package net.sci.array.scalar;

import java.util.function.Function;

import net.sci.array.Array;
import net.sci.array.ArrayWrapperStub;

/**
 * An array containing 16-bits unsigned integers.
 * 
 * @see UInt8Array
 * @see Int16Array
 * 
 * @author dlegland
 */
public interface UInt16Array extends IntArray<UInt16>
{
    // =============================================================
    // Static variables

    public static final Factory defaultFactory = new DenseUInt16ArrayFactory();

    
	// =============================================================
	// Static methods

	public static UInt16Array create(int... dims)
	{
		return defaultFactory.create(dims);
	}
	
	public static UInt16Array create(int[] dims, short[] buffer)
	{
		switch (dims.length)
		{
		case 2:
			return new BufferedUInt16Array2D(dims[0], dims[1], buffer);
		case 3:
			return new BufferedUInt16Array3D(dims[0], dims[1], dims[2], buffer);
		default:
            return new BufferedUInt16ArrayND(dims, buffer);
		}
	}
	
    /**
     * Converts the input array into an instance of UInt16Array.
     * 
     * Can process the following cases:
     * <ul>
     * <li>instances of UInt16Array (through simple class-cast)</li>
     * <li>instances of Array that contain UInt16 values</li>
     * <li>instances of ScalarArray</li>
     * </ul>
     * 
     * @see UInt8Array.#convert(Array)
     * 
     * @param array
     *            the array to convert
     * @return the equivalent UInt16Array
     * @throws IllegalArgumentException
     *             if the input array does not comply to the above cases
     */
    public static UInt16Array convert(Array<?> array)
    {
        // Simply cast instances of UInt16Array
        if (array instanceof UInt16Array)
        {
            return (UInt16Array) array;
        }
        // Convert array containing UInt16 values
        if (array.dataType().isAssignableFrom(UInt16.class)) 
        {
            return convertArrayOfUInt16(array);
        }
        // convert scalar array
        if (array instanceof ScalarArray<?>)
        {
            return convertScalarArray((ScalarArray<?>) array);
        }
        
        throw new IllegalArgumentException("Can not convert array with class: " + array.getClass());
    }
    
    private static UInt16Array convertArrayOfUInt16(Array<?> array)
    {
        UInt16Array result = UInt16Array.create(array.size());
        for (int[] pos : array.positions())
        {
            result.setShort(pos, ((UInt16) array.get(pos)).getShort());
        }
        return result;
    }
    
    private static UInt16Array convertScalarArray(ScalarArray<?> array)
    {
        UInt16Array result = UInt16Array.create(array.size());
        for (int[] pos : array.positions())
        {
            result.setValue(pos, array.getValue(pos));
        }
        return result;
    }
        
    /**
     * Encapsulates the specified array into a new UInt16Array, by creating a
     * Wrapper if necessary. If the original array is already an instance of
     * UInt16Array, it is returned.
     * 
     * @param array
     *            the original array
     * @return a UInt16 view of the original array
     */
    @SuppressWarnings("unchecked")
    public static UInt16Array wrap(Array<?> array)
    {
        if (array instanceof UInt16Array)
        {
            return (UInt16Array) array;
        }
        if (UInt16.class.isAssignableFrom(array.dataType()))
        {
            return new Wrapper((Array<UInt16>) array);
        }
        if (array instanceof ScalarArray)
        {
            return wrapScalar((ScalarArray<?>) array);
        }
                
        throw new IllegalArgumentException("Can not wrap an array with class " + array.getClass() + " and type " + array.dataType());
    }
    
	public static UInt16Array wrapScalar(ScalarArray<?> array)
	{
		if (array instanceof UInt16Array)
		{
			return (UInt16Array) array;
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
		return getShort(pos) & 0x00FFFF; 
	}

	/**
	 * Sets the value at the specified position, by clamping the value between 0
	 * and 2^16-1.
	 */
	@Override
	public default void setInt(int[] pos, int value)
	{
		setShort(pos, (short) Math.min(Math.max(value, 0), UInt16.MAX_VALUE));
	}

	
    // =============================================================
    // Specialization of the ScalarArray interface

    @Override
    public default UInt16 createElement(double value)
    {
        return new UInt16(UInt16.convert(value));
    }
    
    
	// =============================================================
	// Specialization of the Array interface

	@Override
	public default UInt16Array newInstance(int... dims)
	{
		return UInt16Array.create(dims);
	}

	@Override
	public default Factory factory()
	{
		return defaultFactory;
	}

    /**
     * Override default behavior of Array interface to return the value
     * UInt16.ZERO.
     * 
     * @return a default UInt16 value.
     */
    @Override
    public default UInt16 sampleElement()
    {
        return UInt16.ZERO;
    }
    
    @Override
    public default UInt16 get(int[] pos)
    {
        return new UInt16(getShort(pos)); 
    }

    @Override
    public default void set(int[] pos, UInt16 value)
    {
        setShort(pos, value.getShort());
    }


    /* (non-Javadoc)
     * @see net.sci.array.data.Array2D#getValue(int, int)
     */
    @Override
    public default double getValue(int[] pos)
    {
        return getShort(pos) & 0x00FFFF;
    }

    /**
     * Sets the value at the specified position, by clamping the value between 0
     * and 2^16-1.
     * 
     * @see net.sci.array.Array2D#setValue(int, int, double)
     */
    @Override
    public default void setValue(int[] pos, double value)
    {
        setInt(pos, UInt16.convert(value));
    }

    @Override
	public default UInt16Array duplicate()
	{
		// create output array
		UInt16Array result = UInt16Array.create(this.size());
		
	    for (int[] pos : positions())
	    {
	    	result.setShort(pos, getShort(pos));
	    }

		// return output
		return result;
	}

    public default UInt16Array reshapeView(int[] newDims, Function<int[], int[]> coordsMapping)
    {
        return new ReshapeView(this, newDims, coordsMapping);
    }


	@Override
	public default Class<UInt16> dataType()
	{
		return UInt16.class;
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
            public UInt16 next()
            {
                iter.forward();
                return UInt16Array.this.get(iter.get());
            }

            @Override
            public short getShort()
            {
                return UInt16Array.this.getShort(iter.get());
            }

            @Override
            public void setShort(short s)
            {
                UInt16Array.this.setShort(iter.get(), s);
            }
        };
    }



	// =============================================================
	// Inner interface

	public interface Iterator extends IntArray.Iterator<UInt16>
	{
		public short getShort();
		public void setShort(short s);
		
		@Override
		public default int getInt()
		{
			return getShort() & 0x00FFFF; 
		}

		@Override
		public default void setInt(int value)
		{
			setShort((short) Math.min(Math.max(value, 0), UInt16.MAX_VALUE));
		}

		@Override
		public default UInt16 get()
		{
			return new UInt16(getShort());
		}
		
		@Override
		public default void set(UInt16 value)
		{
			setShort(value.getShort());
		}
	}
	
    /**
     * Wraps explicitly an array containing <code>UInt16</code> elements into an
     * instance of <code>UInt16Array</code>.
     * 
     * Usage:
     * <pre>
     * {@code
     * Array<UInt16> array = ...
     * UInt16Array newArray = new UInt16Array.Wrapper(array);
     * newArray.getInt(...);  
     * }
     * </pre>
     */
    static class Wrapper extends ArrayWrapperStub<UInt16> implements UInt16Array
    {
        Array<UInt16> array;
        
        public Wrapper(Array<UInt16> array)
        {
            super(array);
            this.array = array;
        }
        
        @Override
        public PositionIterator positionIterator()
        {
            return array.positionIterator();
        }

        @Override
        public short getShort(int[] pos)
        {
            return array.get(pos).getShort();
        }

        @Override
        public void setShort(int[] pos, short value)
        {
            array.set(pos, new UInt16(value));
        }
    }
    
    /**
     * Wraps an instance of <code>ScalarArray</code> into an instance of
     * <code>UInt16Array</code> by converting performing class cast on the fly.
     * The wrapper has same size as the inner array.
     * 
     * @see UInt16Array#wrap(net.sci.array.Array)
     */
	class ScalarArrayWrapper extends ArrayWrapperStub<UInt16> implements UInt16Array
	{
		ScalarArray<?> array;
		
		public ScalarArrayWrapper(ScalarArray<?> array)
		{
		    super(array);
			this.array = array;
		}
		

		// =============================================================
		// Implementation of the UInt16Array interface

		@Override
		public short getShort(int[] pos)
		{
			return get(pos).getShort();
		}

		@Override
		public void setShort(int[] pos, short value)
		{
			set(pos, new UInt16(value));
		}

		
		// =============================================================
		// Specialization of the Array interface

		@Override
		public UInt16 get(int[] pos)
		{
			return new UInt16(UInt16.convert(array.getValue(pos)));
		}

		@Override
		public void set(int[] pos, UInt16 value)
		{
			array.setValue(pos, value.getValue());
		}
	}
	
    /**
     * Utility class for creating a reshape view on an array using arbitrary
     * coordinate mapping.
     *
     * @see UInt16Array#reshapeView(int[], Function)
     */
    static class ReshapeView implements UInt16Array
    {
        UInt16Array array;
        
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
        public ReshapeView(UInt16Array array, int[] newDims, Function<int[], int[]> coordsMapping)
        {
            this.array = array;
            this.newDims = newDims;
            this.coordsMapping = coordsMapping;
        }

        /* (non-Javadoc)
         * @see net.sci.array.scalar.UInt16Array#getShort(int[])
         */
        @Override
        public short getShort(int[] pos)
        {
            return array.getShort(coordsMapping.apply(pos));
        }

        /* (non-Javadoc)
         * @see net.sci.array.scalar.UInt16Array#setShort(int[], short)
         */
        @Override
        public void setShort(int[] pos, short shortValue)
        {
            array.setShort(coordsMapping.apply(pos), shortValue);
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
     * Specialization of the ArrayFactory for generating instances of UInt16Array.
     */
    public interface Factory extends IntArray.Factory<UInt16>
    {
        /**
         * Creates a new UInt16Array of the specified dimensions, initialized
         * with zeros.
         * 
         * @param dims
         *            the dimensions of the new array
         * @return a new UInt16Array initialized with zeros
         */
        public UInt16Array create(int... dims);

        /**
         * Creates a new UInt16Array with the specified dimensions, filled with
         * the specified initial value.
         * 
         * @param dims
         *            the dimensions of the array to be created
         * @param value
         *            an instance of the initial integer value
         * @return a new instance of IntArray
         */
        public UInt16Array create(int[] dims, UInt16 value);
    }
}
