/**
 * 
 */
package net.sci.array.data;

import net.sci.array.ArrayFactory;
import net.sci.array.data.scalar2d.UInt16Array2D;
import net.sci.array.data.scalar3d.UInt16Array3D;
import net.sci.array.type.UInt16;

/**
 * An array containing 16-bits unsigned integers.
 * 
 * @author dlegland
 */
public interface UInt16Array extends IntArray<UInt16>
{
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
			throw new IllegalArgumentException("Can not create UInt16Array with " + dims.length + " dimensions");
//		default:
//			return UInt8ArrayND.create(dims);
		}
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
	public default ArrayFactory<UInt16> getFactory()
	{
		return new ArrayFactory<UInt16>()
		{
			@Override
			public UInt16Array create(int[] dims, UInt16 value)
			{
				UInt16Array array = UInt16Array.create(dims);
				array.fill(value);
				return array;
			}
		};
	}

	@Override
	public UInt16Array duplicate();

	public Iterator iterator();
	
	/**
	 * Sets the value at the specified position, by clamping the value between 0
	 * and 2^16-1.
	 */
	public default void setValue(int[] pos, double value)
	{
		setInt(pos, (int) Math.min(Math.max(value, 0), UInt16.MAX_VALUE));
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
}
