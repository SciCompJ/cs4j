/**
 * 
 */
package net.sci.array.data;

import net.sci.array.ArrayFactory;
import net.sci.array.data.vector.Float64VectorArray2D;
import net.sci.array.type.Float64Vector;

/**
 * Specialization of the interface VectorArray for arrays of vectors that
 * contains double values.
 * 
 * @author dlegland
 *
 */
public interface Float64VectorArray extends VectorArray<Float64Vector>
{
	// =============================================================
	// Static methods

	public static Float64VectorArray create(int[] dims, int sizeV)
	{
		switch (dims.length)
		{
		case 2:
			return Float64VectorArray2D.create(dims[0], dims[1], sizeV);
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
	public default Float64VectorArray newInstance(int... dims)
	{
		return Float64VectorArray.create(dims, this.getVectorLength());
	}

	@Override
	public default ArrayFactory<Float64Vector> getFactory()
	{
		return new ArrayFactory<Float64Vector>()
		{
			@Override
			public Float64VectorArray create(int[] dims, Float64Vector value)
			{
				Float64VectorArray array = Float64VectorArray.create(dims, value.size());
				array.fill(value);
				return array;
			}
		};
	}

	@Override
	public Float64VectorArray duplicate();

	public Iterator iterator();

	
	// =============================================================
	// Inner interface

	public interface Iterator extends VectorArray.Iterator<Float64Vector>
	{
	}
}