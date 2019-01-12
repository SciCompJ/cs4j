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
public interface Float64Array extends ScalarArray<Float64>
{
    // =============================================================
    // Static variables

    public static final ScalarArray.Factory<Float64> factory = new ScalarArray.Factory<Float64>()
    {
        @Override
        public ScalarArray<Float64> create(int[] dims)
        {
            return Float64Array.create(dims);
        }

        @Override
        public Float64Array create(int[] dims, Float64 value)
        {
            Float64Array array = Float64Array.create(dims);
            array.fill(value);
            return array;
        }
    };

	// =============================================================
	// Static methods

	public static Float64Array create(int... dims)
	{
		switch (dims.length)
		{
		case 2:
			return Float64Array2D.create(dims[0], dims[1]);
		case 3:
			return Float64Array3D.create(dims[0], dims[1], dims[2]);
		default:
			return Float64ArrayND.create(dims);
		}
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
	
	public static Float64Array convert(ScalarArray<?> array)
	{
		Float64Array result = Float64Array.create(array.getSize());
	    for (int[] pos : array.positions())
	    {
	    	result.setValue(pos, array.getValue(pos));
	    }
		return result;
	}
	
	public static Float64Array wrap(ScalarArray<?> array)
	{
		if (array instanceof Float64Array)
		{
			return (Float64Array) array;
		}
		return new Wrapper(array);
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
		Float64Array result = Float64Array.create(this.getSize());
	    for (int[] pos : positions())
	    {
	    	result.setValue(pos, this.getValue(pos));
	    }

        // return output
		return result;
	}

    public default Float64Array view(int[] newDims, Function<int[], int[]> coordsMapping)
    {
        return new View(this, newDims, coordsMapping);
    }

    @Override
    public default ScalarArray.Factory<Float64> getFactory()
    {
        return factory;
    }

	@Override
	public default Class<Float64> getDataType()
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

	class Wrapper implements Float64Array
	{
		ScalarArray<?> array;
		
		public Wrapper(ScalarArray<?> array)
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
		public int[] getSize()
		{
			return array.getSize();
		}

		@Override
		public int getSize(int dim)
		{
			return array.getSize(dim);
		}

		@Override
		public double getValue(int[] position)
		{
			return array.getValue(position);
		}


		@Override
		public void setValue(int[] position, double value)
		{
			array.setValue(position, value);
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

    static class View implements Float64Array
    {
        Float64Array array;
        
        int[] newDims;
        
        Function<int[], int[]> coordsMapping;

        /**
         * 
         */
        public View(Float64Array array, int[] newDims, Function<int[], int[]> coordsMapping)
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
        public int[] getSize()
        {
            return newDims;
        }

        /* (non-Javadoc)
         * @see net.sci.array.Array#getSize(int)
         */
        @Override
        public int getSize(int dim)
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
