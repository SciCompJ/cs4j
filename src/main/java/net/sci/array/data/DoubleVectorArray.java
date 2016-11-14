/**
 * 
 */
package net.sci.array.data;

import net.sci.array.ArrayFactory;
import net.sci.array.data.vector.DoubleVectorArray2D;
import net.sci.array.type.DoubleVector;

/**
 * Specialization of the interface VectorArray for arrays of vectors that
 * contains double values.
 * 
 * @author dlegland
 *
 */
public interface DoubleVectorArray extends VectorArray<DoubleVector>
{
	// =============================================================
	// Static methods

	public static DoubleVectorArray create(int[] dims, int sizeV)
	{
		switch (dims.length)
		{
		case 2:
			return DoubleVectorArray2D.create(dims[0], dims[1], sizeV);
//		case 3:
//			return UInt8Array3D.create(dims[0], dims[1], dims[2]);
		default:
			//TODO: implement the rest
			throw new RuntimeException("Can not create double vector image with dimension " + dims.length);
//			return UInt8ArrayND.create(dims);
		}
	}

	// =============================================================
	// Specialization of Array interface

	@Override
	public default DoubleVectorArray newInstance(int... dims)
	{
		return DoubleVectorArray.create(dims, this.getVectorLength());
	}

	@Override
	public default ArrayFactory<DoubleVector> getFactory()
	{
		return new ArrayFactory<DoubleVector>()
		{
			@Override
			public DoubleVectorArray create(int[] dims, DoubleVector value)
			{
				DoubleVectorArray array = DoubleVectorArray.create(dims, value.size());
				array.fill(value);
				return array;
			}
		};
	}

	@Override
	public DoubleVectorArray duplicate();

	public Iterator iterator();

	
	// =============================================================
	// Inner interface

	public interface Iterator extends VectorArray.Iterator<DoubleVector>
	{
	}
}
