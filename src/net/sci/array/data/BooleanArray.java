/**
 * 
 */
package net.sci.array.data;

import net.sci.array.data.scalar2d.BooleanArray2D;
import net.sci.array.data.scalar3d.BooleanArray3D;
import net.sci.array.type.Boolean;

/**
 * @author dlegland
 *
 */
public interface BooleanArray extends IntArray<Boolean>
{
	// =============================================================
	// Static methods

	public static BooleanArray create(int[] dims)
	{
		switch (dims.length)
		{
		case 2:
			return BooleanArray2D.create(dims[0], dims[1]);
		case 3:
			return BooleanArray3D.create(dims[0], dims[1], dims[2]);
		default:
			throw new RuntimeException("Cound not create Boolean array of dimension " + dims.length);
//			return UInt8ArrayND.create(dims);
		}
	}
	
	// =============================================================
	// New methods

	public boolean getState(int[] pos);
	
	public void setState(int[] pos, boolean state);
	
	
	// =============================================================
	// Specialization of the IntArray interface

	@Override
	public default int getInt(int[] pos)
	{
		return getState(pos) ? 1 : 0; 
	}

	@Override
	public default void setInt(int[] pos, int value)
	{
		setState(pos, value != 0);
	}

	
	// =============================================================
	// Specialization of the Array interface

	@Override
	public default BooleanArray newInstance(int... dims)
	{
		return BooleanArray.create(dims);
	}

	@Override
	public BooleanArray duplicate();

	public Iterator iterator();
	
	/**
	 * Sets the value at the specified position, by clamping the value between 0
	 * and 255.
	 */
	public default void setValue(int[] pos, double value)
	{
		setInt(pos, (int) Math.min(Math.max(value, 0), 255));
	}


	// =============================================================
	// Inner interface

	public interface Iterator extends IntArray.Iterator<Boolean>
	{
		public boolean getState();
		public void setState(boolean b);
		
		@Override
		public default int getInt()
		{
			return getState() ? 1 : 0; 
		}

		@Override
		public default void setInt(int value)
		{
			setState(value > 0);
		}

	}
}
