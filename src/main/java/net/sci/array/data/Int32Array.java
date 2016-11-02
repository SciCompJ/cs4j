/**
 * 
 */
package net.sci.array.data;

import net.sci.array.data.scalar2d.Int32Array2D;
import net.sci.array.data.scalar3d.Int32Array3D;
import net.sci.array.type.Int32;

/**
 * @author dlegland
 *
 */
public interface Int32Array extends IntArray<Int32>
{
	// =============================================================
	// Static methods

	public static Int32Array create(int[] dims)
	{
		switch (dims.length)
		{
		case 2:
			return Int32Array2D.create(dims[0], dims[1]);
		case 3:
			return Int32Array3D.create(dims[0], dims[1], dims[2]);
		default:
			throw new IllegalArgumentException("Can not create Int32Array with " + dims.length + " dimensions");
//			return Int32ArrayND.create(dims);
		}
	}
	
		
	// =============================================================
	// Specialization of the Array interface

	@Override
	public default Int32Array newInstance(int... dims)
	{
		return Int32Array.create(dims);
	}

	@Override
	public Int32Array duplicate();

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
}
