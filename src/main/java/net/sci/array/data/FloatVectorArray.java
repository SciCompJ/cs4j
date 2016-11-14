/**
 * 
 */
package net.sci.array.data;

import net.sci.array.ArrayFactory;
import net.sci.array.data.vector.FloatVectorArray2D;
import net.sci.array.data.vector.FloatVectorArray3D;
import net.sci.array.type.FloatVector;

/**
 * Specialization of the interface VectorArray for arrays of vectors that
 * contains 32-bits floating point values.
 * 
 * @author dlegland
 *
 */
public interface FloatVectorArray extends VectorArray<FloatVector>
{
	// =============================================================
	// Static methods

	public static FloatVectorArray create(int[] dims, int sizeV)
	{
		switch (dims.length)
		{
		case 2:
			return FloatVectorArray2D.create(dims[0], dims[1], sizeV);
		case 3:
			return FloatVectorArray3D.create(dims[0], dims[1], dims[2], sizeV);
		default:
			//TODO: implement the rest
			throw new RuntimeException("Can not create float vector image with dimension " + dims);
//			return UInt8ArrayND.create(dims);
		}
	}

	// =============================================================
	// Specialization of Array interface

	@Override
	public default FloatVectorArray newInstance(int... dims)
	{
		return FloatVectorArray.create(dims, this.getVectorLength());
	}

	@Override
	public default ArrayFactory<FloatVector> getFactory()
	{
		return new ArrayFactory<FloatVector>()
		{
			@Override
			public FloatVectorArray create(int[] dims, FloatVector value)
			{
				FloatVectorArray array = FloatVectorArray.create(dims, value.size());
				array.fill(value);
				return array;
			}
		};
	}

	@Override
	public FloatVectorArray duplicate();

	public Iterator iterator();

	
	// =============================================================
	// Inner interface

	public interface Iterator extends VectorArray.Iterator<FloatVector>
	{
	}
}
