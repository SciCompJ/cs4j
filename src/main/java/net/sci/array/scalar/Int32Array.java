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
public interface Int32Array extends IntArray<Int32>
{
    // =============================================================
    // Static variables

    public static final IntArray.Factory<Int32> factory = new IntArray.Factory<Int32>()
    {
        @Override
        public IntArray<Int32> create(int... dims)
        {
            return Int32Array.create(dims);
        }

        @Override
        public Int32Array create(int[] dims, Int32 value)
        {
            Int32Array array = Int32Array.create(dims);
            array.fill(value);
            return array;
        }
    };

	// =============================================================
	// Static methods

	public static Int32Array create(int... dims)
	{
		switch (dims.length)
		{
		case 2:
			return Int32Array2D.create(dims[0], dims[1]);
		case 3:
			return Int32Array3D.create(dims[0], dims[1], dims[2]);
		default:
			return Int32ArrayND.create(dims);
		}
	}
	
	public static Int32Array create(int[] dims, int[] buffer)
	{
		switch (dims.length)
		{
		case 2:
			return new BufferedInt32Array2D(dims[0], dims[1], buffer);
		case 3:
			return new BufferedInt32Array3D(dims[0], dims[1], dims[2], buffer);
		default:
			return new BufferedInt32ArrayND(dims, buffer);
		}
	}
	
	public static Int32Array convert(ScalarArray<?> array)
	{
		Int32Array result = Int32Array.create(array.size());
	    for (int[] pos : array.positions())
	    {
	    	result.setValue(array.getValue(pos), pos);
	    }
		return result;
	}
	
	public static Int32Array wrap(ScalarArray<?> array)
	{
		if (array instanceof Int32Array)
		{
			return (Int32Array) array;
		}
		return new Wrapper(array);
	}
	
		
	// =============================================================
	// Specialization of the Array interface

	/**
     * Sets the value at the specified position, by clamping the value between 0
     * and 255.
     */
	public default void setValue(double value, int... pos)
    {
    	setInt((int) value, pos);
    }

    @Override
	public default Int32Array newInstance(int... dims)
	{
		return Int32Array.create(dims);
	}

	@Override
	public default IntArray.Factory<Int32> getFactory()
	{
		return factory;
	}

    @Override
    public default Int32 get(int... pos)
    {
        return new Int32(getInt(pos)); 
    }

    @Override
    public default void set(Int32 value, int... pos)
    {
        setInt(value.getInt(), pos);
    }

	@Override
	public default Int32Array duplicate()
	{
		// create output array
		Int32Array result = Int32Array.create(this.size());
	    for (int[] pos : positions())
	    {
	    	result.setInt(getInt(pos), pos);
	    }
		
		// return output
		return result;
	}

    public default Int32Array view(int[] newDims, Function<int[], int[]> coordsMapping)
    {
        return new View(this, newDims, coordsMapping);
    }

	@Override
	public default Class<Int32> dataType()
	{
		return Int32.class;
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
                Int32Array.this.setInt(intValue, iter.get());
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
			setInt(value.getInt());
		}
	}

	class Wrapper implements Int32Array
	{
		ScalarArray<?> array;
		
		public Wrapper(ScalarArray<?> array)
		{
			this.array = array;
		}
		

		// =============================================================
		// Implementation of the Int32Array interface

		@Override
		public int getInt(int... pos)
		{
			return (int) array.getValue(pos);
		}

		@Override
		public void setInt(int value, int... pos)
		{
			setValue(value, pos);
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
		public Int32 get(int... pos)
		{
			return new Int32((int) array.getValue(pos));
		}

		@Override
		public void set(Int32 value, int... pos)
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
		
		class Iterator implements Int32Array.Iterator
		{
			ScalarArray.Iterator<?> iter;
			
			public Iterator(ScalarArray.Iterator<?> iter)
			{
				this.iter = iter;
			}

			@Override
			public int getInt()
			{
				return (int) getValue();
			}

			@Override
			public void setInt(int value)
			{
				iter.setValue(value);
			}

			@Override
			public void forward()
			{
				this.iter.forward();
			}

			@Override
			public Int32 next()
			{
				return new Int32((int) iter.nextValue());
			}

			@Override
			public boolean hasNext()
			{
				return iter.hasNext();
			}
		}
	}

    static class View implements Int32Array
    {
        Int32Array array;
        
        int[] newDims;
        
        Function<int[], int[]> coordsMapping;

        /**
         * 
         */
        public View(Int32Array array, int[] newDims, Function<int[], int[]> coordsMapping)
        {
            this.array = array;
            this.newDims = newDims;
            this.coordsMapping = coordsMapping;
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
