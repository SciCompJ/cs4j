/**
 * 
 */
package net.sci.array.data;

import net.sci.array.Array;
import net.sci.array.ArrayFactory;
import net.sci.array.Cursor;
import net.sci.array.CursorIterator;
import net.sci.array.data.scalar2d.BufferedFloat64Array2D;
import net.sci.array.data.scalar2d.Float64Array2D;
import net.sci.array.data.scalar3d.BufferedFloat64Array3D;
import net.sci.array.data.scalar3d.Float64Array3D;
import net.sci.array.data.scalarnd.BufferedFloat64ArrayND;
import net.sci.array.data.scalarnd.Float64ArrayND;
import net.sci.array.type.Float64;

/**
 * @author dlegland
 *
 */
public interface Float64Array extends ScalarArray<Float64>
{
	// =============================================================
	// Static methods

	public static Float64Array create(int... dims)
	{
		switch (dims.length)
		{
		case 2:
			return Float64Array2D.create(dims[0], dims[1]);
		case 3:
			return Float64Array3D.create(dims[0], dims[1], dims[2]);
		default:
			return Float64ArrayND.create(dims);
		}
	}

	public static Float64Array create(int[] dims, double[] buffer)
	{
		switch (dims.length)
		{
		case 2:
			return new BufferedFloat64Array2D(dims[0], dims[1], buffer);
		case 3:
			return new BufferedFloat64Array3D(dims[0], dims[1], dims[2], buffer);
		default:
			return new BufferedFloat64ArrayND(dims, buffer);
		}
	}
	
	public static Float64Array convert(Array<?> array)
	{
		Float64Array result = Float64Array.create(array.getSize());
		Array.Iterator<?> iter1 = array.iterator();
		Float64Array.Iterator iter2 = result.iterator();
		while (iter1.hasNext() && iter2.hasNext())
		{
			iter2.forward();
			iter2.setValue(iter1.nextValue());
		}
		return result;
	}
	
	public static Float64Array wrap(ScalarArray<?> array)
	{
		if (array instanceof Float64Array)
		{
			return (Float64Array) array;
		}
		return new Wrapper(array);
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
	public default Float64Array duplicate()
	{
		// create output array
		Float64Array result = Float64Array.create(this.getSize());

		// initialize iterators
		Float64Array.Iterator iter1 = this.iterator();
		Float64Array.Iterator iter2 = result.iterator();
		
		// copy values into output array
		while(iter1.hasNext())
		{
			iter2.forward();
			iter2.set(iter1.next());
		}
		
		// return output
		return result;
	}

	@Override
	public default Class<Float64> getDataType()
	{
		return Float64.class;
	}

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

	class Wrapper implements Float64Array
	{
		ScalarArray<?> array;
		
		public Wrapper(ScalarArray<?> array)
		{
			this.array = array;
		}

		
		// =============================================================
		// Specialization of the Array interface

		@Override
		public int dimensionality()
		{
			return array.dimensionality();
		}

		@Override
		public int[] getSize()
		{
			return array.getSize();
		}

		@Override
		public int getSize(int dim)
		{
			return array.getSize(dim);
		}

		@Override
		public double getValue(int[] position)
		{
			return array.getValue(position);
		}


		@Override
		public void setValue(int[] position, double value)
		{
			array.setValue(position, value);
		}


		@Override
		public Float64 get(int[] pos)
		{
			return new Float64(array.getValue(pos));
		}

		@Override
		public void set(int[] pos, Float64 value)
		{
			array.setValue(pos, value.getValue());
		}

    	public CursorIterator<? extends Cursor> cursorIterator()
    	{
    		return array.cursorIterator();
    	}

		@Override
		public Iterator iterator()
		{
			return new Iterator(array.iterator());
		}
		
		class Iterator implements Float64Array.Iterator
		{
			ScalarArray.Iterator<?> iter;
			
			public Iterator(ScalarArray.Iterator<?> iter)
			{
				this.iter = iter;
			}

			@Override
			public void forward()
			{
				this.iter.forward();
			}

			@Override
			public Float64 next()
			{
				return new Float64(iter.nextValue());
			}

			@Override
			public boolean hasNext()
			{
				return iter.hasNext();
			}

			@Override
			public double getValue()
			{
				return iter.getValue();
			}

			@Override
			public void setValue(double value)
			{
				iter.setValue(value);
			}
		}
	}
}
