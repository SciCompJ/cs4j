/**
 * 
 */
package net.sci.array.data;

import net.sci.array.Cursor;
import net.sci.array.CursorIterator;
import net.sci.array.data.scalar2d.BufferedUInt8Array2D;
import net.sci.array.data.scalar2d.UInt8Array2D;
import net.sci.array.data.scalar3d.BufferedUInt8Array3D;
import net.sci.array.data.scalar3d.UInt8Array3D;
import net.sci.array.data.scalarnd.BufferedUInt8ArrayND;
import net.sci.array.data.scalarnd.UInt8ArrayND;
import net.sci.array.type.UInt8;

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
		UInt8Array result = UInt8Array.create(array.getSize());
		ScalarArray.Iterator<?> iter1 = array.iterator();
		UInt8Array.Iterator iter2 = result.iterator();
		while (iter1.hasNext() && iter2.hasNext())
		{
			iter2.setNextValue(iter1.nextValue());
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
		setByte(pos, (byte) Math.min(Math.max(value, 0), 255));
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
	public default UInt8Array duplicate()
	{
		// create output array
		UInt8Array result = UInt8Array.create(this.getSize());

		// initialize iterators
		UInt8Array.Iterator iter1 = this.iterator();
		UInt8Array.Iterator iter2 = result.iterator();
		
		// copy values into output array
		while(iter1.hasNext())
		{
			iter2.forward();
			iter2.set(iter1.next());
		}
		
		// return result
		return result;
	}

	@Override
	public default Class<UInt8> getDataType()
	{
		return UInt8.class;
	}

	public Iterator iterator();
	
	/**
	 * Sets the value at the specified position, by clamping the value between 0
	 * and 255.
	 */
	public default void setValue(int[] pos, double value)
	{
		setByte(pos, (byte) UInt8.clamp(value));
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
	 * @see UInt8Array.wrap(ScalarArray)
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
		public UInt8 get(int[] pos)
		{
			return new UInt8(UInt8.clamp(array.getValue(pos)));
		}

		@Override
		public void set(int[] pos, UInt8 value)
		{
			array.setValue(pos, value.getValue());
		}

    	public CursorIterator<? extends Cursor> cursorIterator()
    	{
    		return array.cursorIterator();
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
}
