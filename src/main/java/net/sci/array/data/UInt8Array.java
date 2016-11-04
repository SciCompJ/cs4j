/**
 * 
 */
package net.sci.array.data;

import net.sci.array.data.scalar2d.UInt8Array2D;
import net.sci.array.data.scalar3d.UInt8Array3D;
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
	public UInt8Array duplicate();

	public Iterator iterator();
	
	/**
	 * Sets the value at the specified position, by clamping the value between 0
	 * and 255.
	 */
	public default void setValue(int[] pos, double value)
	{
		setByte(pos, (byte) Math.min(Math.max(value, 0), 255));
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

		@Override
		public default void setInt(int value)
		{
			setByte((byte) value);
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
}
