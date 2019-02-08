/**
 * 
 */
package net.sci.array.scalar;

import java.util.function.Function;

import net.sci.array.DefaultPositionIterator;


/**
 * An array containing 16-bits unsigned integers.
 * 
 * @author dlegland
 */
public interface UInt16Array extends IntArray<UInt16>
{
    // =============================================================
    // Static variables

    public static final IntArray.Factory<UInt16> factory = new IntArray.Factory<UInt16>()
    {
        @Override
        public IntArray<UInt16> create(int[] dims)
        {
            return UInt16Array.create(dims);
        }

        @Override
        public UInt16Array create(int[] dims, UInt16 value)
        {
            UInt16Array array = UInt16Array.create(dims);
            array.fill(value);
            return array;
        }
    };

	// =============================================================
	// Static methods

	public static UInt16Array create(int... dims)
	{
		switch (dims.length)
		{
		case 2:
			return UInt16Array2D.create(dims[0], dims[1]);
		case 3:
			return UInt16Array3D.create(dims[0], dims[1], dims[2]);
		default:
            return UInt16ArrayND.create(dims);
		}
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
	
	public static UInt16Array convert(ScalarArray<?> array)
	{
		UInt16Array result = UInt16Array.create(array.size());
	    for (int[] pos : array.positions())
	    {
	    	result.setValue(pos, array.getValue(pos));
	    }
		return result;
	}
	
	public static UInt16Array wrap(ScalarArray<?> array)
	{
		if (array instanceof UInt16Array)
		{
			return (UInt16Array) array;
		}
		return new Wrapper(array);
	}
	

	// =============================================================
	// New methods

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
	// Specialization of the Array interface

	@Override
	public default UInt16Array newInstance(int... dims)
	{
		return UInt16Array.create(dims);
	}

	@Override
	public default IntArray.Factory<UInt16> getFactory()
	{
		return factory;
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

	/**
     * Sets the value at the specified position, by clamping the value between 0
     * and 2^16-1.
     */
    public default void setValue(int[] pos, double value)
    {
    	setInt(pos, (int) UInt16.clamp(value));
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

    public default UInt16Array view(int[] newDims, Function<int[], int[]> coordsMapping)
    {
        return new View(this, newDims, coordsMapping);
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
	
	class Wrapper implements UInt16Array
	{
		ScalarArray<?> array;
		
		public Wrapper(ScalarArray<?> array)
		{
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
		public UInt16 get(int[] pos)
		{
			return new UInt16(UInt16.clamp(array.getValue(pos)));
		}

		@Override
		public void set(int[] pos, UInt16 value)
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
		
		class Iterator implements UInt16Array.Iterator
		{
			ScalarArray.Iterator<?> iter;
			
			public Iterator(ScalarArray.Iterator<?> iter)
			{
				this.iter = iter;
			}

			@Override
			public short getShort()
			{
				return get().getShort();
			}

			@Override
			public void setShort(short b)
			{
				iter.setValue(new UInt16(b).getValue());
			}

			@Override
			public void forward()
			{
				this.iter.forward();
			}

			@Override
			public UInt16 next()
			{
				return new UInt16(UInt16.clamp(iter.nextValue()));
			}

			@Override
			public boolean hasNext()
			{
				return iter.hasNext();
			}
		}
	}
	
    static class View implements UInt16Array
    {
        UInt16Array array;
        
        int[] newDims;
        
        Function<int[], int[]> coordsMapping;

        /**
         * 
         */
        public View(UInt16Array array, int[] newDims, Function<int[], int[]> coordsMapping)
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
