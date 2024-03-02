/**
 * 
 */
package net.sci.array.scalar;

import java.util.function.Function;

import net.sci.array.Array;
import net.sci.array.ArrayWrapperStub;
import net.sci.array.process.type.ConvertToUInt8;


/**
 * An array containing 8-bits unsigned integers.
 * 
 * @see UInt16Array
 * 
 * @author dlegland
 */
public interface UInt8Array extends IntArray<UInt8>
{
    // =============================================================
    // Static variables

    /**
     * The default factory for UInt8Array instances.
     * 
     * @see UInt8Array.#create(int...)
     */
    public static final Factory defaultFactory = new DenseUInt8ArrayFactory();
    

	// =============================================================
	// Static methods

    /**
     * Creates a new UInt8Array with the specified dimensions. When possible,
     * the most appropriate implementation class is chosen according to the
     * dimensionality and the total size (number of elements) of the array.
     * 
     * @param dims
     *            the size of the array to create.
     */
	public static UInt8Array create(int... dims)
	{
		return defaultFactory.create(dims);
	}
	
	/**
     * Creates a new UInt8Array based on the specified byte buffer. The byte
     * buffer is not duplicated during creation of Array instance, so changing
     * values within buffer will change values within array, and vice-versa.
     * 
     * @param dims
     *            the size of the array to create
     * @param buffer
     *            the array of byte containing array values. Length must equal
     *            product of dimensions.
     * @return a UInt8Array based on the specified buffer.
     */
	public static UInt8Array create(int[] dims, byte[] buffer)
	{
		switch (dims.length)
		{
		case 2:
			return new BufferedUInt8Array2D(dims[0], dims[1], buffer);
		case 3:
			return new BufferedUInt8Array3D(dims[0], dims[1], dims[2], buffer);
		default:
			return new BufferedUInt8ArrayND(dims, buffer);
		}
	}
	
	/**
     * Converts the input array into an instance of UInt8Array.
     * 
     * Can process the following cases:
     * <ul>
     * <li>instances of UInt8Array (through simple class-cast)</li>
     * <li>instances of Array that contain UInt8 values</li>
     * <li>instances of ScalarArray</li>
     * </ul>
     * 
     * @see UInt8Array.#convert(ScalarArray, double, double)
     * 
     * @param array
     *            the array to convert
     * @return the equivalent UInt8Array
     * @throws IllegalArgumentException
     *             if the input array does not comply to the above cases
     */
    public static UInt8Array convert(Array<?> array)
    {
        // Simply cast instances of UInt8Array
        if (array instanceof UInt8Array)
        {
            return (UInt8Array) array;
        }
        
        // otherwise, call the converter class
        return new ConvertToUInt8().process(array);
    }
    
    /**
     * Converts a scalar array into a UInt8Array, by considering a value range
     * that will be mapped to 0 and 255.
     * 
     * @param array
     *            the array to convert
     * @param minValue
     *            the value within input array that will be associated to 0 in
     *            result array
     * @param maxValue
     *            the value within input array that will be associated to 255 in
     *            result array
     * @return the converted UInt8Array
     */
    public static UInt8Array convert(ScalarArray<?> array, double minValue, double maxValue)
    {
        double k = 255 / (maxValue - minValue);
        UInt8Array result = UInt8Array.create(array.size());
        for (int[] pos : array.positions())
        {
            result.setValue(pos, (array.getValue(pos) - minValue) * k);
        }
        return result;
    }
    
	/**
     * Encapsulates the specified array into a new UInt8Array, by creating a
     * Wrapper if necessary. If the original array is already an instance of
     * UInt8Array, it is returned.
     * 
     * @param array
     *            the original array
     * @return a UInt8 view of the original array
     */
	@SuppressWarnings("unchecked")
    public static UInt8Array wrap(Array<?> array)
	{
		if (array instanceof UInt8Array)
		{
			return (UInt8Array) array;
		}
		if (UInt8.class.isAssignableFrom(array.dataType()))
		{
		    return new Wrapper((Array<UInt8>) array);
        }
        if (array instanceof ScalarArray)
        {
            return wrapScalar((ScalarArray<?>) array);
        }
        
		throw new IllegalArgumentException("Can not wrap an array with class " + array.getClass() + " and type " + array.dataType());
	}
	
    /**
     * Encapsulates the instance of Scalar array into a new UInt8Array, by
     * creating a Wrapper if necessary. 
     * If the original array is already an instance of UInt8Array, it is returned.  
     * 
     * @param array
     *            the original array
     * @return a UInt8 view of the original array
     */
    public static UInt8Array wrapScalar(ScalarArray<?> array)
    {
        if (array instanceof UInt8Array)
        {
            return (UInt8Array) array;
        }
        return new ScalarArrayWrapper(array);
    }
    

	// =============================================================
	// New methods

    public default void fillBytes(Function<int[], Byte> fun)
    {
        for (int[] pos : this.positions())
        {
            this.setByte(pos, fun.apply(pos));
        }
    }
    
	public byte getByte(int[] pos);
	
	public void setByte(int[] pos, byte value);
	
	
	// =============================================================
	// Specialization of the IntArray interface

	@Override
	public default int getInt(int[] pos)
	{
		return getByte(pos) & 0x00FF; 
	}

	@Override
	public default void setInt(int[] pos, int value)
	{
		setByte(pos, (byte) UInt8.clamp(value));
	}


    // =============================================================
    // Specialization of the ScalarArray interface

    /* (non-Javadoc)
     * @see net.sci.array.data.Array2D#getValue(int, int)
     */
    @Override
    public default double getValue(int[] pos)
    {
        return getByte(pos) & 0x00FF;
    }

    /**
     * Sets the value at the specified position, by converting the value between 0
     * and 255.
     * 
     * @see net.sci.array.scalar.ScalarArray2D#setValue(int, int, double)
     */
    @Override
    public default void setValue(int[] pos, double value)
    {
        setByte(pos, (byte) UInt8.convert(value));
    }

    @Override
    public default UInt8 createElement(double value)
    {
        return new UInt8(UInt8.convert(value));
    }
    

    // =============================================================
	// Specialization of the Array interface

	@Override
	public default UInt8Array newInstance(int... dims)
	{
		return UInt8Array.create(dims);
	}

	@Override
	public default IntArray.Factory<UInt8> factory()
	{
		return defaultFactory;
	}

    /**
     * Override default behavior of Array interface to return the value
     * UInt8.ZERO.
     * 
     * @return a default UInt8 value.
     */
    @Override
    public default UInt8 sampleElement()
    {
        return UInt8.ZERO;
    }
    
    @Override
    public default UInt8 get(int[] pos)
    {
        return new UInt8(getByte(pos));
    }

    @Override
    public default void set(int[] pos, UInt8 value)
    {
        setByte(pos, value.getByte());
    }

    @Override
	public default UInt8Array duplicate()
	{
		// create output array
		UInt8Array result = UInt8Array.create(this.size());

        // copy values into output array
	    for (int[] pos : positions())
	    {
	    	result.setByte(pos, getByte(pos));
	    }
				
		// return result
		return result;
	}

    public default UInt8Array reshapeView(int[] newDims, Function<int[], int[]> coordsMapping)
    {
        return new ReshapeView(this, newDims, coordsMapping);
    }


	@Override
	public default Class<UInt8> dataType()
	{
		return UInt8.class;
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
            public UInt8 next()
            {
                iter.forward();
                return UInt8Array.this.get(iter.get());
            }

            @Override
            public byte getByte()
            {
                return UInt8Array.this.getByte(iter.get());
            }

            @Override
            public void setByte(byte b)
            {
                UInt8Array.this.setByte(iter.get(), b);
            }
        };
    }

	
	// =============================================================
	// Inner interface

	public interface Iterator extends IntArray.Iterator<UInt8>
	{
		public byte getByte();
		public void setByte(byte b);
		
		@Override
		public default int getInt()
		{
			return getByte() & 0x00FF; 
		}

		/**
		 * Sets the value at the specified position, by clamping the value between 0
		 * and 255.
		 */
		@Override
		public default void setInt(int value)
		{
			setByte((byte) UInt8.clamp(value));
		}

		@Override
		public default UInt8 get()
		{
			return new UInt8(getByte());
		}
		
		@Override
		public default void set(UInt8 value)
		{
			setByte(value.getByte());
		}
	}
	
    /**
     * Wraps explicitly an array containing <code>UInt8</code> elements into an
     * instance of <code>UInt8Array</code>.
     * 
     * Usage:
     * <pre>
     * {@code
     * Array<UInt8> array = ...
     * UInt8Array newArray = new UInt8Array.Wrapper(array);
     * newArray.getInt(...);  
     * }
     * </pre>
     */
	static class Wrapper extends ArrayWrapperStub<UInt8> implements UInt8Array
	{
	    Array<UInt8> array;
	    
	    public Wrapper(Array<UInt8> array)
	    {
	        super(array);
	        this.array = array;
	    }
	    
        @Override
        public byte getByte(int[] pos)
        {
            return array.get(pos).getByte();
        }

        @Override
        public void setByte(int[] pos, byte value)
        {
            array.set(pos, new UInt8(value));
        }
	}
	
    /**
     * Wraps an instance of <code>ScalarArray</code> into an instance of
     * <code>UInt8Array</code> by converting performing class cast on the fly.
     * The wrapper has same size as the inner array.
     *  
     * @see UInt8Array#wrap(net.sci.array.Array)
     */
	static class ScalarArrayWrapper  extends ArrayWrapperStub<UInt8> implements UInt8Array
	{
	    /** The parent array */
		ScalarArray<?> array;
		
		public ScalarArrayWrapper(ScalarArray<?> array)
		{
		    super(array);
			this.array = array;
		}
		

		// =============================================================
		// Implementation of the UInt8Array interface

		@Override
		public byte getByte(int[] pos)
		{
			return get(pos).getByte();
		}

		@Override
		public void setByte(int[] pos, byte value)
		{
			set(pos, new UInt8(value & 0x00FF));
		}

		
		// =============================================================
		// Specialization of the Array interface

		@Override
		public UInt8 get(int[] pos)
		{
			return new UInt8(UInt8.convert(array.getValue(pos)));
		}

		@Override
		public void set(int[] pos, UInt8 value)
		{
			array.setValue(pos, value.getValue());
		}
	}
	
    /**
     * Utility class for creating a reshape view on an array using arbitrary
     * coordinate mapping.
     *
     * @see UInt8Array#reshapeView(int[], Function)
     */
	static class ReshapeView implements UInt8Array
	{
	    UInt8Array array;
	    
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
	    public ReshapeView(UInt8Array array, int[] newDims, Function<int[], int[]> coordsMapping)
	    {
	        this.array = array;
	        this.newDims = newDims;
	        this.coordsMapping = coordsMapping;
	    }

	    /* (non-Javadoc)
	     * @see net.sci.array.scalar.UInt8Array#getByte(int[])
	     */
	    @Override
	    public byte getByte(int[] pos)
	    {
	        return array.getByte(coordsMapping.apply(pos));
	    }

	    /* (non-Javadoc)
	     * @see net.sci.array.scalar.UInt8Array#setByte(int[], byte)
	     */
	    @Override
	    public void setByte(int[] pos, byte value)
	    {
	        array.setByte(coordsMapping.apply(pos), value);
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
	 * Specialization of the ArrayFactory for generating instances of UInt8Array.
	 */
    public interface Factory extends IntArray.Factory<UInt8>
    {
        /**
         * Creates a new UInt8Array of the specified dimensions, initialized
         * with zeros.
         * 
         * @param dims
         *            the dimensions of the new array
         * @return a new UInt8Array initialized with zeros
         */
        public UInt8Array create(int... dims);

        /**
         * Creates a new UInt8Array with the specified dimensions, filled with
         * the specified initial value.
         * 
         * @param dims
         *            the dimensions of the array to be created
         * @param value
         *            an instance of the initial integer value
         * @return a new instance of IntArray
         */
        public UInt8Array create(int[] dims, UInt8 value);
    }
}
