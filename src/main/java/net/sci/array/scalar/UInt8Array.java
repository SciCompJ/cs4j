/**
 * 
 */
package net.sci.array.scalar;

import java.util.function.Function;

import net.sci.array.DefaultPositionIterator;



/**
 * An array containing 8-bits unsigned integers.
 * 
 * @author dlegland
 */
public interface UInt8Array extends IntArray<UInt8>
{
    // =============================================================
    // Static variables

    public static final IntArray.Factory<UInt8> factory = new IntArray.Factory<UInt8>()
    {
        @Override
        public IntArray<UInt8> create(int[] dims)
        {
            return UInt8Array.create(dims);
        }

        @Override
        public UInt8Array create(int[] dims, UInt8 value)
        {
            UInt8Array array = UInt8Array.create(dims);
            array.fill(value);
            return array;
        }
    };

	// =============================================================
	// Static methods

	public static UInt8Array create(int... dims)
	{
		switch (dims.length)
		{
		case 2:
			return UInt8Array2D.create(dims[0], dims[1]);
		case 3:
			return UInt8Array3D.create(dims[0], dims[1], dims[2]);
		default:
			return UInt8ArrayND.create(dims);
		}
	}
	
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
	
    public static UInt8Array convert(ScalarArray<?> array)
    {
        UInt8Array result = UInt8Array.create(array.size());
        for (int[] pos : array.positions())
        {
            result.setValue(array.getValue(pos), pos);
        }
        return result;
    }
    
    public static UInt8Array convert(ScalarArray<?> array, double minValue, double maxValue)
    {
        double k = 255 / (maxValue - minValue);
        UInt8Array result = UInt8Array.create(array.size());
        for (int[] pos : array.positions())
        {
            result.setValue((array.getValue(pos) - minValue) * k, pos);
        }
        return result;
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
	public static UInt8Array wrap(ScalarArray<?> array)
	{
		if (array instanceof UInt8Array)
		{
			return (UInt8Array) array;
		}
		return new Wrapper(array);
	}
	

	// =============================================================
	// New methods

	public byte getByte(int... pos);
	
	public void setByte(byte value, int... pos);
	
	
	// =============================================================
	// Specialization of the IntArray interface

	@Override
	public default int getInt(int... pos)
	{
		return getByte(pos) & 0x00FF; 
	}

	@Override
	public default void setInt(int value, int... pos)
	{
		setByte((byte) Math.min(Math.max(value, 0), 255), pos);
	}


    // =============================================================
    // Specialization of the ScalarArray interface

    /* (non-Javadoc)
     * @see net.sci.array.data.Array2D#getValue(int, int)
     */
    @Override
    public default double getValue(int... pos)
    {
        return getByte(pos) & 0x00FF;
    }

    /**
     * Sets the value at the specified position, by clamping the value between 0
     * and 255.
     * 
     * @see net.sci.array.Array2D#setValue(int, int, double)
     */
    @Override
    public default void setValue(double value, int... pos)
    {
        setByte((byte) UInt8.clamp(value), pos);
    }


    // =============================================================
	// Specialization of the Array interface

	@Override
	public default UInt8Array newInstance(int... dims)
	{
		return UInt8Array.create(dims);
	}

	@Override
	public default IntArray.Factory<UInt8> getFactory()
	{
		return factory;
	}

    @Override
    public default UInt8 get(int... pos)
    {
        return new UInt8(getByte(pos)); 
    }

    @Override
    public default void set(UInt8 value, int... pos)
    {
        setByte(value.getByte(), pos);
    }

    @Override
	public default UInt8Array duplicate()
	{
		// create output array
		UInt8Array result = UInt8Array.create(this.size());

        // copy values into output array
	    for (int[] pos : positions())
	    {
	    	result.setByte(getByte(pos), pos);
	    }
				
		// return result
		return result;
	}

    public default UInt8Array view(int[] newDims, Function<int[], int[]> coordsMapping)
    {
        return new View(this, newDims, coordsMapping);
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
                UInt8Array.this.setByte(b, iter.get());
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
			setByte((byte) Math.min(Math.max(value, 0), 255));
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
	 * Wraps a scalar array into a UInt8Array with same dimension.
	 * 
	 * @see UInt8Array#wrap(ScalarArray)
	 */
	static class Wrapper implements UInt8Array
	{
	    /** The parent array */
		ScalarArray<?> array;
		
		public Wrapper(ScalarArray<?> array)
		{
			this.array = array;
		}
		

		// =============================================================
		// Implementation of the UInt8Array interface

		@Override
		public byte getByte(int... pos)
		{
			return get(pos).getByte();
		}

		@Override
		public void setByte(byte value, int... pos)
		{
			set(new UInt8(value & 0x00FF), pos);
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
		public UInt8 get(int... pos)
		{
			return new UInt8(UInt8.clamp(array.getValue(pos)));
		}

		@Override
		public void set(UInt8 value, int... pos)
		{
			array.setValue(value.getValue(), pos);
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
		
		class Iterator implements UInt8Array.Iterator
		{
			ScalarArray.Iterator<?> iter;
			
			public Iterator(ScalarArray.Iterator<?> iter)
			{
				this.iter = iter;
			}

			@Override
			public byte getByte()
			{
				return get().getByte();
			}

			@Override
			public void setByte(byte b)
			{
				iter.setValue(new UInt8(b).getValue());
			}

			@Override
			public void forward()
			{
				this.iter.forward();
			}

			@Override
			public UInt8 next()
			{
				return new UInt8(UInt8.clamp(iter.nextValue()));
			}

			@Override
			public boolean hasNext()
			{
				return iter.hasNext();
			}
		}
	}
	
	static class View implements UInt8Array
	{
	    UInt8Array array;
	    
	    int[] newDims;
	    
	    Function<int[], int[]> coordsMapping;

	    /**
	     * 
	     */
	    public View(UInt8Array array, int[] newDims, Function<int[], int[]> coordsMapping)
	    {
	        this.array = array;
	        this.newDims = newDims;
	        this.coordsMapping = coordsMapping;
	    }

	    /* (non-Javadoc)
	     * @see net.sci.array.scalar.UInt8Array#getByte(int[])
	     */
	    @Override
	    public byte getByte(int... pos)
	    {
	        return array.getByte(coordsMapping.apply(pos));
	    }

	    /* (non-Javadoc)
	     * @see net.sci.array.scalar.UInt8Array#setByte(int[], byte)
	     */
	    @Override
	    public void setByte(byte value, int... pos)
	    {
	        array.setByte(value, coordsMapping.apply(pos));
	    }

	    /* (non-Javadoc)
	     * @see net.sci.array.scalar.IntArray#getInt(int[])
	     */
	    @Override
	    public int getInt(int... pos)
	    {
	        return array.getInt(coordsMapping.apply(pos));
	    }

	    /* (non-Javadoc)
	     * @see net.sci.array.scalar.IntArray#setInt(int[], int)
	     */
	    @Override
	    public void setInt(int value, int... pos)
	    {
	        array.setInt(value, coordsMapping.apply(pos));
	    }

	    /* (non-Javadoc)
	     * @see net.sci.array.scalar.ScalarArray#getValue(int[])
	     */
	    @Override
	    public double getValue(int... pos)
	    {
	        return array.getValue(coordsMapping.apply(pos));
	    }

	    /* (non-Javadoc)
	     * @see net.sci.array.scalar.ScalarArray#setValue(int[], double)
	     */
	    @Override
	    public void setValue(double value, int... pos)
	    {
	        array.setValue(value, coordsMapping.apply(pos));
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

	    /* (non-Javadoc)
	     * @see net.sci.array.Array#positionIterator()
	     */
	    @Override
	    public net.sci.array.Array.PositionIterator positionIterator()
	    {
	        return new DefaultPositionIterator(newDims);
	    }
	}
}
