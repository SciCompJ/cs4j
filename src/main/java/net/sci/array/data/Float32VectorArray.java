/**
 * 
 */
package net.sci.array.data;

import net.sci.array.ArrayFactory;
import net.sci.array.data.vector.Float32VectorArray2D;
import net.sci.array.data.vector.Float32VectorArray3D;
import net.sci.array.type.Float32Vector;

/**
 * Specialization of the interface VectorArray for arrays of vectors that
 * contains 32-bits floating point values.
 * 
 * @author dlegland
 *
 */
public interface Float32VectorArray extends VectorArray<Float32Vector>
{
	// =============================================================
	// Static methods

	public static Float32VectorArray create(int[] dims, int sizeV)
	{
		switch (dims.length)
		{
		case 2:
			return Float32VectorArray2D.create(dims[0], dims[1], sizeV);
		case 3:
			return Float32VectorArray3D.create(dims[0], dims[1], dims[2], sizeV);
		default:
			//TODO: implement the rest
			throw new RuntimeException("Can not create float vector image with dimension " + dims);
//			return UInt8ArrayND.create(dims);
		}
	}

	// =============================================================
	// Specialization of Array interface

	@Override
	public default Float32VectorArray newInstance(int... dims)
	{
		return Float32VectorArray.create(dims, this.getVectorLength());
	}

	@Override
	public default ArrayFactory<Float32Vector> getFactory()
	{
		return new ArrayFactory<Float32Vector>()
		{
			@Override
			public Float32VectorArray create(int[] dims, Float32Vector value)
			{
				Float32VectorArray array = Float32VectorArray.create(dims, value.size());
				array.fill(value);
				return array;
			}
		};
	}

	@Override
	public Float32VectorArray duplicate();

	public Iterator iterator();

	
	// =============================================================
	// Inner interface

	public interface Iterator extends VectorArray.Iterator<Float32Vector>
	{
	}
}
