/**
 * 
 */
package net.sci.array.data;

import net.sci.array.ArrayFactory;
import net.sci.array.data.scalar2d.Int32Array2D;
import net.sci.array.data.scalar3d.Int32Array3D;
import net.sci.array.data.scalarnd.Int32ArrayND;
import net.sci.array.type.Int32;

/**
 * @author dlegland
 *
 */
public interface Int32Array extends IntArray<Int32>
{
	// =============================================================
	// Static methods

	public static Int32Array create(int... dims)
	{
		switch (dims.length)
		{
		case 2:
			return Int32Array2D.create(dims[0], dims[1]);
		case 3:
			return Int32Array3D.create(dims[0], dims[1], dims[2]);
		default:
			return Int32ArrayND.create(dims);
		}
	}
	
	public static Int32Array convert(ScalarArray<?> array)
	{
		Int32Array result = Int32Array.create(array.getSize());
		ScalarArray.Iterator<?> iter1 = array.iterator();
		Int32Array.Iterator iter2 = result.iterator();
		while (iter1.hasNext() && iter2.hasNext())
		{
			iter2.forward();
			iter2.setValue(iter1.nextValue());
		}
		return result;
	}
	
	public static Int32Array wrap(ScalarArray<?> array)
	{
		if (array instanceof Int32Array)
		{
			return (Int32Array) array;
		}
		return new Wrapper(array);
	}
	

		
	// =============================================================
	// Specialization of the Array interface

	@Override
	public default Int32Array newInstance(int... dims)
	{
		return Int32Array.create(dims);
	}

	@Override
	public default ArrayFactory<Int32> getFactory()
	{
		return new ArrayFactory<Int32>()
		{
			@Override
			public Int32Array create(int[] dims, Int32 value)
			{
				Int32Array array = Int32Array.create(dims);
				array.fill(value);
				return array;
			}
		};
	}

	@Override
	public default Int32Array duplicate()
	{
		// create output array
		Int32Array result = Int32Array.create(this.getSize());

		// initialize iterators
		Int32Array.Iterator iter1 = this.iterator();
		Int32Array.Iterator iter2 = result.iterator();
		
		// copy values into output array
		while(iter1.hasNext())
		{
			iter2.forward();
			iter2.set(iter1.next());
		}
		
		// return output
		return result;
	}

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

	class Wrapper implements Int32Array
	{
		ScalarArray<?> array;
		
		public Wrapper(ScalarArray<?> array)
		{
			this.array = array;
		}
		

		// =============================================================
		// Implementation of the Int32Array interface

		@Override
		public int getInt(int[] pos)
		{
			return (int) array.getValue(pos);
		}

		@Override
		public void setInt(int[] pos, int value)
		{
			setValue(pos, value);
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
		public Int32 get(int[] pos)
		{
			return new Int32((int) array.getValue(pos));
		}

		@Override
		public void set(int[] pos, Int32 value)
		{
			array.setValue(pos, value.getValue());
		}

		@Override
		public Iterator iterator()
		{
			return new Iterator(array.iterator());
		}
		
		class Iterator implements Int32Array.Iterator
		{
			ScalarArray.Iterator<?> iter;
			
			public Iterator(ScalarArray.Iterator<?> iter)
			{
				this.iter = iter;
			}

			@Override
			public int getInt()
			{
				return (int) getValue();
			}

			@Override
			public void setInt(int value)
			{
				iter.setValue(value);
			}

			@Override
			public void forward()
			{
				this.iter.forward();
			}

			@Override
			public Int32 next()
			{
				return new Int32((int) iter.nextValue());
			}

			@Override
			public boolean hasNext()
			{
				return iter.hasNext();
			}
		}
	}
}
