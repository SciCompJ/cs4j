/**
 * 
 */
package net.sci.array.data;

import net.sci.array.ArrayFactory;
import net.sci.array.data.scalar2d.Float64Array2D;
import net.sci.array.type.Float64;

/**
 * @author dlegland
 *
 */
public interface Float64Array extends ScalarArray<Float64>
{
	// =============================================================
	// Static methods

	public static Float64Array create(int[] dims)
	{
		switch (dims.length)
		{
		case 2:
			return Float64Array2D.create(dims[0], dims[1]);
//		case 3:
//			return UInt8Array3D.create(dims[0], dims[1], dims[2]);
		default:
			//TODO: implement the rest
			throw new IllegalArgumentException("Can not create DoubleArray with " + dims.length + " dimensions");
//			return UInt8ArrayND.create(dims);
		}
	}

	// =============================================================
	// Specialization of Array interface

	@Override
	public default Float64Array newInstance(int... dims)
	{
		return Float64Array.create(dims);
	}

	@Override
	public default ArrayFactory<Float64> getFactory()
	{
		return new ArrayFactory<Float64>()
		{
			@Override
			public Float64Array create(int[] dims, Float64 value)
			{
				Float64Array array = Float64Array.create(dims);
				array.fill(value);
				return array;
			}
		};
	}

	@Override
	public Float64Array duplicate();

	public Iterator iterator();
	
	// =============================================================
	// Inner interface

	public interface Iterator extends ScalarArray.Iterator<Float64>
	{
		@Override
		public default Float64 get()
		{
			return new Float64(getValue());
		}
		
		@Override
		public default void set(Float64 value)
		{
			setValue(value.getValue());
		}
	}
}
