/**
 * 
 */
package net.sci.array.scalar;

import java.util.function.Function;

import net.sci.array.DefaultPositionIterator;


/**
 * @author dlegland
 *
 */
public interface Int16Array extends IntArray<Int16>
{
    // =============================================================
    // Static variables

    public static final IntArray.Factory<Int16> factory = new IntArray.Factory<Int16>()
    {
        @Override
        public IntArray<Int16> create(int... dims)
        {
            return Int16Array.create(dims);
        }

        @Override
        public Int16Array create(int[] dims, Int16 value)
        {
            Int16Array array = Int16Array.create(dims);
            array.fill(value);
            return array;
        }
    };

	// =============================================================
	// Static methods

	public static Int16Array create(int... dims)
	{
		switch (dims.length)
		{
		case 2:
			return Int16Array2D.create(dims[0], dims[1]);
		case 3:
			return Int16Array3D.create(dims[0], dims[1], dims[2]);
		default:
			return Int16ArrayND.create(dims);
		}
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
	
	public static Int16Array convert(ScalarArray<?> array)
	{
		Int16Array result = Int16Array.create(array.size());
	    for (int[] pos : array.positions())
	    {
	    	result.setValue(array.getValue(pos), pos);
	    }
		return result;
	}
		
	public static Int16Array wrap(ScalarArray<?> array)
	{
		if (array instanceof Int16Array)
		{
			return (Int16Array) array;
		}
		return new Wrapper(array);
	}
	

	// =============================================================
	// New methods

	public short getShort(int... pos);
	
	public void setShort(short value, int... pos);
	
	
	// =============================================================
	// Specialization of the IntArray interface

	@Override
	public default int getInt(int... pos)
	{
		return getShort(pos); 
	}

	/**
	 * Sets the value at the specified position, by clamping the value between
	 * min and max admissible values.
	 */
	@Override
	public default void setInt(int value, int... pos)
	{
		setShort((short) Math.min(Math.max(value, Int16.MIN_VALUE), Int16.MAX_VALUE), pos);
	}

		
	// =============================================================
	// Specialization of the Array interface

    @Override
	public default Int16Array newInstance(int... dims)
	{
		return Int16Array.create(dims);
	}

	@Override
	public default IntArray.Factory<Int16> getFactory()
	{
		return factory;
	}

    @Override
    public default Int16 get(int... pos)
    {
        return new Int16(getShort(pos)); 
    }

    @Override
    public default void set(Int16 value, int... pos)
    {
        setShort(value.getShort(), pos);
    }

	@Override
	public default Int16Array duplicate()
	{
		// create output array
		Int16Array result = Int16Array.create(this.size());
	    for (int[] pos : positions())
	    {
	    	result.setShort(getShort(pos), pos);
	    }
		
		// return output
		return result;
	}

    public default Int16Array view(int[] newDims, Function<int[], int[]> coordsMapping)
    {
        return new View(this, newDims, coordsMapping);
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
                Int16Array.this.setShort(s, iter.get());
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
	
	class Wrapper implements Int16Array
	{
		ScalarArray<?> array;
		
		public Wrapper(ScalarArray<?> array)
		{
			this.array = array;
		}
		

		// =============================================================
		// Implementation of the Int16Array interface

		@Override
		public short getShort(int... pos)
		{
			return get(pos).getShort();
		}

		@Override
		public void setShort(short value, int... pos)
		{
			set(new Int16(value), pos);
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
		public Int16 get(int... pos)
		{
			return new Int16(Int16.clamp(array.getValue(pos)));
		}

		@Override
		public void set(Int16 value, int... pos)
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
		
		class Iterator implements Int16Array.Iterator
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
				iter.setValue(new Int16(b).getValue());
			}

			@Override
			public void forward()
			{
				this.iter.forward();
			}

			@Override
			public Int16 next()
			{
				return new Int16(Int16.clamp(iter.nextValue()));
			}

			@Override
			public boolean hasNext()
			{
				return iter.hasNext();
			}
		}
	}
	
    static class View implements Int16Array
    {
        Int16Array array;
        
        int[] newDims;
        
        Function<int[], int[]> coordsMapping;

        /**
         * 
         */
        public View(Int16Array array, int[] newDims, Function<int[], int[]> coordsMapping)
        {
            this.array = array;
            this.newDims = newDims;
            this.coordsMapping = coordsMapping;
        }

        /* (non-Javadoc)
         * @see net.sci.array.scalar.Int16Array#getShort(int[])
         */
        @Override
        public short getShort(int... pos)
        {
            return array.getShort(coordsMapping.apply(pos));
        }

        /* (non-Javadoc)
         * @see net.sci.array.scalar.Int16Array#setShort(int[], short)
         */
        @Override
        public void setShort(short shortValue, int... pos)
        {
            array.setShort(shortValue, coordsMapping.apply(pos));
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
