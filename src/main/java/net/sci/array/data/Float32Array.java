/**
 * 
 */
package net.sci.array.data;

import net.sci.array.Array;
import net.sci.array.Cursor;
import net.sci.array.CursorIterator;
import net.sci.array.data.scalar2d.BufferedFloat32Array2D;
import net.sci.array.data.scalar2d.Float32Array2D;
import net.sci.array.data.scalar3d.BufferedFloat32Array3D;
import net.sci.array.data.scalar3d.Float32Array3D;
import net.sci.array.data.scalarnd.BufferedFloat32ArrayND;
import net.sci.array.data.scalarnd.Float32ArrayND;
import net.sci.array.type.Float32;

/**
 * @author dlegland
 *
 */
public interface Float32Array extends ScalarArray<Float32>
{
    // =============================================================
    // Static variables

    public static final ScalarArray.Factory<Float32> factory = new ScalarArray.Factory<Float32>()
    {
        @Override
        public ScalarArray<Float32> create(int[] dims)
        {
            return Float32Array.create(dims);
        }

        @Override
        public Float32Array create(int[] dims, Float32 value)
        {
            Float32Array array = Float32Array.create(dims);
            array.fill(value);
            return array;
        }
    };

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
			return Float32ArrayND.create(dims);
		}
	}

	public static Float32Array create(int[] dims, float[] buffer)
	{
		switch (dims.length)
		{
		case 2:
			return new BufferedFloat32Array2D(dims[0], dims[1], buffer);
		case 3:
			return new BufferedFloat32Array3D(dims[0], dims[1], dims[2], buffer);
		default:
			return new BufferedFloat32ArrayND(dims, buffer);
		}
	}
	
	public static Float32Array convert(Array<?> array)
	{
		Float32Array result = Float32Array.create(array.getSize());
		Array.Iterator<?> iter1 = array.iterator();
		Float32Array.Iterator iter2 = result.iterator();
		while (iter1.hasNext() && iter2.hasNext())
		{
			iter2.setNextValue(iter1.nextValue());
		}
		return result;
	}
	
	public static Float32Array wrap(ScalarArray<?> array)
	{
		if (array instanceof Float32Array)
		{
			return (Float32Array) array;
		}
		return new Wrapper(array);
	}
	

	// =============================================================
	// Specialization of Array interface

	@Override
	public default Float32Array newInstance(int... dims)
	{
		return Float32Array.create(dims);
	}
	
	@Override
	public default ScalarArray.Factory<Float32> getFactory()
	{
		return factory;
	}

	@Override
	public default Float32Array duplicate()
	{
		// create output array
		Float32Array result = Float32Array.create(this.getSize());

		// initialize iterators
		Float32Array.Iterator iter1 = this.iterator();
		Float32Array.Iterator iter2 = result.iterator();
		
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
	public default Class<Float32> getDataType()
	{
		return Float32.class;
	}

	public Iterator iterator();
	
	// =============================================================
	// Inner interface

	public interface Iterator extends ScalarArray.Iterator<Float32>
	{
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
	
	class Wrapper implements Float32Array
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
		public Float32 get(int[] pos)
		{
			return new Float32((float) array.getValue(pos));
		}

		@Override
		public void set(int[] pos, Float32 value)
		{
			array.setValue(pos, value.getValue());
		}

    	public CursorIterator<? extends Cursor> cursorIterator()
    	{
    		return array.cursorIterator();
    	}

        @Override
        public PositionIterator positionIterator()
        {
            return array.positionIterator();
        }

		@Override
		public Iterator iterator()
		{
			return new Iterator(array.iterator());
		}
		
		class Iterator implements Float32Array.Iterator
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
			public Float32 next()
			{
				return new Float32((float) iter.nextValue());
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
