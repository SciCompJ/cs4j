/**
 * 
 */
package net.sci.array.scalar;

import java.util.function.Function;

import net.sci.array.Array;
import net.sci.array.DefaultPositionIterator;



/**
 * @author dlegland
 *
 */
public interface Float32Array extends ScalarArray<Float32>
{
    // =============================================================
    // Static variables

    public static final ScalarArray.Factory<Float32> factory = new ScalarArray.Factory<Float32>()
    {
        @Override
        public ScalarArray<Float32> create(int... dims)
        {
            return Float32Array.create(dims);
        }

        @Override
        public Float32Array create(int[] dims, Float32 value)
        {
            Float32Array array = Float32Array.create(dims);
            array.fill(value);
            return array;
        }
    };

    
	// =============================================================
	// Static methods

	public static Float32Array create(int... dims)
	{
		switch (dims.length)
		{
		case 2:
			return Float32Array2D.create(dims[0], dims[1]);
		case 3:
			return Float32Array3D.create(dims[0], dims[1], dims[2]);
		default:
			return Float32ArrayND.create(dims);
		}
	}

	public static Float32Array create(int[] dims, float[] buffer)
	{
		switch (dims.length)
		{
		case 2:
			return new BufferedFloat32Array2D(dims[0], dims[1], buffer);
		case 3:
			return new BufferedFloat32Array3D(dims[0], dims[1], dims[2], buffer);
		default:
			return new BufferedFloat32ArrayND(dims, buffer);
		}
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
        if (array.dataType().isAssignableFrom(Float32.class)) 
        {
            return convertArrayOfFloat32(array);
        }
        // convert scalar array
        if (array instanceof ScalarArray<?>)
        {
            convertScalarArray((ScalarArray<?>) array);
        }
        
        throw new IllegalArgumentException("Can not convert array with class: " + array.getClass());
    }
    
    private static Float32Array convertArrayOfFloat32(Array<?> array)
    {
        Float32Array result = Float32Array.create(array.size());
        for (int[] pos : array.positions())
        {
            result.setFloat(pos, ((Float32) array.get(pos)).getFloat());
        }
        return result;
    }
    
    private static Float32Array convertScalarArray(ScalarArray<?> array)
    {
        Float32Array result = Float32Array.create(array.size());
        for (int[] pos : array.positions())
        {
            result.setValue(pos, array.getValue(pos));
        }
        return result;
    }
    
	public static Float32Array wrap(ScalarArray<?> array)
	{
		if (array instanceof Float32Array)
		{
			return (Float32Array) array;
		}
		return new Wrapper(array);
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
    
    public default float getFloat(int... pos)
    {
        return (float) getValue(pos);
    }

    
    public default void setFloat(int[] pos, float value)
    {
        setValue(pos, value);
    }

    
	// =============================================================
	// Specialization of Array interface

	@Override
	public default Float32Array newInstance(int... dims)
	{
		return Float32Array.create(dims);
	}
	
	@Override
	public default ScalarArray.Factory<Float32> getFactory()
	{
		return factory;
	}

    @Override
    public default Float32 get(int... pos)
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

    public default Float32Array view(int[] newDims, Function<int[], int[]> coordsMapping)
    {
        return new View(this, newDims, coordsMapping);
    }

	@Override
	public default Class<Float32> dataType()
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
		@Override
		public default Float32 get()
		{
			return new Float32((float) getValue());
		}
		
		@Override
		public default void set(Float32 value)
		{
			setValue(value.getValue());
		}
	}
	
	class Wrapper implements Float32Array
	{
		ScalarArray<?> array;
		
		public Wrapper(ScalarArray<?> array)
		{
			this.array = array;
		}

        @Override
        public float getFloat(int... position)
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
		public double getValue(int... position)
		{
			return array.getValue(position);
		}


		@Override
		public void setValue(int[] pos, double value)
		{
			array.setValue(pos, value);
		}


		@Override
		public Float32 get(int... pos)
		{
			return new Float32((float) array.getValue(pos));
		}

		@Override
		public void set(int[] pos, Float32 value)
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
		
		class Iterator implements Float32Array.Iterator
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
			public Float32 next()
			{
				return new Float32((float) iter.nextValue());
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

    static class View implements Float32Array
    {
        Float32Array array;
        
        int[] newDims;
        
        Function<int[], int[]> coordsMapping;

        /**
         * 
         */
        public View(Float32Array array, int[] newDims, Function<int[], int[]> coordsMapping)
        {
            this.array = array;
            this.newDims = newDims;
            this.coordsMapping = coordsMapping;
        }

        @Override
        public float getFloat(int... pos)
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
        public double getValue(int... pos)
        {
            return array.getValue(coordsMapping.apply(pos));
        }

        /* (non-Javadoc)
         * @see net.sci.array.scalar.ScalarArray#setValue(int[], double)
         */
        @Override
        public void setValue(int[] pos, double value)        {
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
