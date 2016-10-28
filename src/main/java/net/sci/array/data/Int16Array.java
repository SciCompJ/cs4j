/**
 * 
 */
package net.sci.array.data;

import net.sci.array.data.scalar2d.Int16Array2D;
import net.sci.array.type.Int16;

/**
 * @author dlegland
 *
 */
public interface Int16Array extends IntArray<Int16>
{
	// =============================================================
	// Static methods

	public static Int16Array create(int[] dims)
	{
		switch (dims.length)
		{
		case 2:
			return Int16Array2D.create(dims[0], dims[1]);
//		case 3:
//			return Int32Array3D.create(dims[0], dims[1], dims[2]);
		default:
			throw new IllegalArgumentException("Can not create Int16Array with " + dims.length + " dimensions");
//			return Int32ArrayND.create(dims);
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
	public Int16Array duplicate();

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
			return getShort() & 0x00FFFF; 
		}

		@Override
		public default void setInt(int value)
		{
			setShort((short) Math.min(Math.max(value, Int16.MIN_VALUE), Int16.MAX_VALUE));
		}
	}
}
