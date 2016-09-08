/**
 * 
 */
package net.sci.array.data;

import net.sci.array.data.scalar2d.FloatArray2D;
import net.sci.array.type.Float;

/**
 * @author dlegland
 *
 */
public interface FloatArray extends ScalarArray<Float>
{
	// =============================================================
	// Static methods

	public static FloatArray create(int[] dims)
	{
		switch (dims.length)
		{
		case 2:
			return FloatArray2D.create(dims[0], dims[1]);
//		case 3:
//			return UInt8Array3D.create(dims[0], dims[1], dims[2]);
		default:
			//TODO: implement the rest
			throw new RuntimeException("Can not create such image");
//			return UInt8ArrayND.create(dims);
		}
	}

	// =============================================================
	// Specialization of Array interface

	@Override
	public default FloatArray newInstance(int... dims)
	{
		return FloatArray.create(dims);
	}
	
	@Override
	public FloatArray duplicate();

	public Iterator iterator();
	
	// =============================================================
	// Inner interface

	public interface Iterator extends ScalarArray.Iterator<Float>
	{
	}
}
