/**
 * 
 */
package net.sci.array.data;

import net.sci.array.Array;
import net.sci.array.ArrayFactory;
import net.sci.array.data.scalar2d.Float32Array2D;
import net.sci.array.data.scalar3d.Float32Array3D;
import net.sci.array.type.Float32;

/**
 * @author dlegland
 *
 */
public interface Float32Array extends ScalarArray<Float32>
{
	// =============================================================
	// Static methods

	public static Float32Array create(int[] dims)
	{
		switch (dims.length)
		{
		case 2:
			return Float32Array2D.create(dims[0], dims[1]);
		case 3:
			return Float32Array3D.create(dims[0], dims[1], dims[2]);
		default:
			//TODO: implement the rest
			throw new IllegalArgumentException("Can not create FloatArray with " + dims.length + " dimensions");
//			return UInt8ArrayND.create(dims);
		}
	}

	public static Float32Array convert(Array<?> array)
	{
		Float32Array result = Float32Array.create(array.getSize());
		Array.Iterator<?> iter1 = array.iterator();
		Float32Array.Iterator iter2 = result.iterator();
		while (iter1.hasNext() && iter2.hasNext())
		{
			iter2.forward();
			iter2.setValue(iter1.nextValue());
		}
		return result;
	}
	
	// =============================================================
	// Specialization of Array interface

	@Override
	public default Float32Array newInstance(int... dims)
	{
		return Float32Array.create(dims);
	}
	
	@Override
	public default ArrayFactory<Float32> getFactory()
	{
		return new ArrayFactory<Float32>()
		{
			@Override
			public Float32Array create(int[] dims, Float32 value)
			{
				Float32Array array = Float32Array.create(dims);
				array.fill(value);
				return array;
			}
		};
	}

	@Override
	public Float32Array duplicate();

	public Iterator iterator();
	
	// =============================================================
	// Inner interface

	public interface Iterator extends ScalarArray.Iterator<Float32>
	{
		//TODO: new methods getFloat() and setFloat()
		@Override
		public default Float32 get()
		{
			return new Float32((float) getValue());
		}
		
		@Override
		public default void set(Float32 value)
		{
			setValue(value.getValue());
		}
	}
}
