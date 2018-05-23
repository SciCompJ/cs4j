/**
 * 
 */
package net.sci.array.scalar;

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
		UInt16Array result = UInt16Array.create(array.getSize());
		ScalarArray.Iterator<?> iter1 = array.iterator();
		UInt16Array.Iterator iter2 = result.iterator();
		while (iter1.hasNext() && iter2.hasNext())
		{
			iter2.setNextValue(iter1.nextValue());
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
	public default UInt16Array duplicate()
	{
		// create output array
		UInt16Array result = UInt16Array.create(this.getSize());

		// initialize iterators
		UInt16Array.Iterator iter1 = this.iterator();
		UInt16Array.Iterator iter2 = result.iterator();
		
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
	public default Class<UInt16> getDataType()
	{
		return UInt16.class;
	}

	public Iterator iterator();
	
	/**
	 * Sets the value at the specified position, by clamping the value between 0
	 * and 2^16-1.
	 */
	public default void setValue(int[] pos, double value)
	{
		setInt(pos, (int) UInt16.clamp(value));
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
}
