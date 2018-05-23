/**
 * 
 */
package net.sci.array.scalar;

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
        public IntArray<Int16> create(int[] dims)
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
		Int16Array result = Int16Array.create(array.getSize());
		ScalarArray.Iterator<?> iter1 = array.iterator();
		Int16Array.Iterator iter2 = result.iterator();
		while (iter1.hasNext() && iter2.hasNext())
		{
			iter2.setNextValue(iter1.nextValue());
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
	public default Int16Array duplicate()
	{
		// create output array
		Int16Array result = Int16Array.create(this.getSize());

		// initialize iterators
		Int16Array.Iterator iter1 = this.iterator();
		Int16Array.Iterator iter2 = result.iterator();
		
		// copy values into output array
		while(iter1.hasNext())
		{
			iter2.forward();
			iter2.set(iter1.next());
		}
		
		// return output
		return result;
	}

	@Override
	public default Class<Int16> getDataType()
	{
		return Int16.class;
	}

	public Iterator iterator();
	
	/**
	 * Sets the value at the specified position, by clamping the value between
	 * min and max admissible values.
	 */
	public default void setValue(int[] pos, double value)
	{
		setInt(pos, (int) value);
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
		public Int16 get(int[] pos)
		{
			return new Int16(Int16.clamp(array.getValue(pos)));
		}

		@Override
		public void set(int[] pos, Int16 value)
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
}
