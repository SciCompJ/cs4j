/**
 * 
 */
package net.sci.array.scalar;

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
        public IntArray<Int32> create(int[] dims)
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
		Int32Array result = Int32Array.create(array.getSize());
	    for (int[] pos : array.positions())
	    {
	    	result.setValue(pos, array.getValue(pos));
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
	public default Int32Array duplicate()
	{
		// create output array
		Int32Array result = Int32Array.create(this.getSize());
	    for (int[] pos : positions())
	    {
	    	result.setInt(pos, getInt(pos));
	    }
		
		// return output
		return result;
	}

	@Override
	public default Class<Int32> getDataType()
	{
		return Int32.class;
	}

	public Iterator iterator();
	
	/**
	 * Sets the value at the specified position, by clamping the value between 0
	 * and 255.
	 */
	public default void setValue(int[] pos, double value)
	{
		setInt(pos, (int) value);
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
		public int getInt(int[] pos)
		{
			return (int) array.getValue(pos);
		}

		@Override
		public void setInt(int[] pos, int value)
		{
			setValue(pos, value);
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
		public Int32 get(int[] pos)
		{
			return new Int32((int) array.getValue(pos));
		}

		@Override
		public void set(int[] pos, Int32 value)
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
}
