/**
 * 
 */
package net.sci.array.data;

import net.sci.array.ArrayFactory;
import net.sci.array.data.scalar2d.DoubleArray2D;
import net.sci.array.type.Double;

/**
 * @author dlegland
 *
 */
public interface DoubleArray extends ScalarArray<Double>
{
	// =============================================================
	// Static methods

	public static DoubleArray create(int[] dims)
	{
		switch (dims.length)
		{
		case 2:
			return DoubleArray2D.create(dims[0], dims[1]);
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
	public default DoubleArray newInstance(int... dims)
	{
		return DoubleArray.create(dims);
	}

	@Override
	public default ArrayFactory<Double> getFactory()
	{
		return new ArrayFactory<Double>()
		{
			@Override
			public DoubleArray create(int[] dims, Double value)
			{
				DoubleArray array = DoubleArray.create(dims);
				array.fill(value);
				return array;
			}
		};
	}

	@Override
	public DoubleArray duplicate();

	public Iterator iterator();
	
	// =============================================================
	// Inner interface

	public interface Iterator extends ScalarArray.Iterator<Double>
	{
		@Override
		public default Double get()
		{
			return new Double(getValue());
		}
		
		@Override
		public default void set(Double value)
		{
			setValue(value.getValue());
		}
	}
}
