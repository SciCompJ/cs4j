/**
 * 
 */
package net.sci.array.data;

import net.sci.array.ArrayFactory;
import net.sci.array.data.scalar2d.BooleanArray2D;
import net.sci.array.data.scalar2d.BufferedBooleanArray2D;
import net.sci.array.data.scalar3d.BooleanArray3D;
import net.sci.array.data.scalar3d.BufferedBooleanArray3D;
import net.sci.array.type.Boolean;

/**
 * @author dlegland
 *
 */
public interface BooleanArray extends IntArray<Boolean>
{
	// =============================================================
	// Static methods

	public static BooleanArray create(int... dims)
	{
		switch (dims.length)
		{
		case 2:
			return BooleanArray2D.create(dims[0], dims[1]);
		case 3:
			return BooleanArray3D.create(dims[0], dims[1], dims[2]);
		default:
			throw new IllegalArgumentException("Can not create BooleanArray with " + dims.length + " dimensions");
//			return UInt8ArrayND.create(dims);
		}
	}
	
	public static BooleanArray create(int[] dims, boolean[] buffer)
	{
		switch (dims.length)
		{
		case 2:
			return new BufferedBooleanArray2D(dims[0], dims[1], buffer);
		case 3:
			return new BufferedBooleanArray3D(dims[0], dims[1], dims[2], buffer);
		default:
			throw new IllegalArgumentException("Can not create BooleanArray with " + dims.length + " dimensions");
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
	public default ArrayFactory<Boolean> getFactory()
	{
		return new ArrayFactory<Boolean>()
		{
			@Override
			public BooleanArray create(int[] dims, Boolean value)
			{
				BooleanArray array = BooleanArray.create(dims);
				array.fill(value);
				return array;
			}
		};
	}

	@Override
	public default BooleanArray duplicate()
	{
		// create output array
		BooleanArray result = BooleanArray.create(this.getSize());

		// initialize iterators
		BooleanArray.Iterator iter1 = this.iterator();
		BooleanArray.Iterator iter2 = result.iterator();
		
		// copy values into output array
		while(iter1.hasNext())
		{
			iter2.forward();
			iter2.set(iter1.next());
		}
		
		// return output
		return result;
	}

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
