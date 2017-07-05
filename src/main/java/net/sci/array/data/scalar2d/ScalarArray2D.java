/**
 * 
 */
package net.sci.array.data.scalar2d;

import java.io.PrintStream;
import java.util.Locale;

import net.sci.array.ArrayFactory;
import net.sci.array.data.Array2D;
import net.sci.array.data.ScalarArray;
import net.sci.array.type.Scalar;

/**
 * @author dlegland
 *
 */
public abstract class ScalarArray2D<T extends Scalar> extends Array2D<T> implements ScalarArray<T>
{
	// =============================================================
	// Static methods

	public final static <T extends Scalar> ScalarArray2D<T> wrap(ScalarArray<T> array)
	{
		if (array instanceof ScalarArray2D)
		{
			return (ScalarArray2D<T>) array;
		}
		return new Wrapper<T>(array);

	}

	// =============================================================
	// Constructor

	protected ScalarArray2D(int size0, int size1)
	{
		super(size0, size1);
	}

	
	// =============================================================
	// Methods specific to ScalarArray2D

	/**
	 * Prints the content of this array on the specified stream.
	 * 
	 * @param stream
	 *            the stream to print on.
	 */
	public void print(PrintStream stream)
	{
		for (int y = 0; y < this.size1; y++)
		{
			for (int x = 0; x < this.size0; x++)
			{
				System.out.print(String.format(Locale.ENGLISH, " %g", getValue(x, y)));
			}
			System.out.println();
		}
	}

	// =============================================================
	// Specialization of the Array interface

	@Override
	public abstract ScalarArray2D<T> duplicate();
	
	
	// =============================================================
	// Inner Wrapper class

	private static class Wrapper<T extends Scalar> extends ScalarArray2D<T>
	{
		private ScalarArray<T> array;
		
		protected Wrapper(ScalarArray<T> array)
		{
			super(0, 0);
			if (array.dimensionality() < 2)
			{
				throw new IllegalArgumentException("Requires an array with at least two dimensions");
			}
			this.array = array;
			this.size0 = array.getSize(0);
			this.size1 = array.getSize(1);
		}

		@Override
		public ScalarArray<T> newInstance(int... dims)
		{
			return this.array.newInstance(dims);
		}

		@Override
		public ArrayFactory<T> getFactory()
		{
			return this.array.getFactory();
		}

		@Override
		public T get(int x, int y)
		{
			// convert (x,y) to ND integer array
			int nd = this.array.dimensionality();
			int[] pos = new int[nd];
			pos[0] = x;
			pos[1] = y;
			
			// return value from specified position
			return this.array.get(pos);
		}

		@Override
		public void set(int x, int y, T value)
		{
			// convert (x,y) to ND integer array
			int nd = this.array.dimensionality();
			int[] pos = new int[nd];
			pos[0] = x;
			pos[1] = y;
			
			// set value at specified position
			this.array.set(pos, value);
		}

		@Override
		public double getValue(int x, int y)
		{
			// convert (x,y) to ND integer array
			int nd = this.array.dimensionality();
			int[] pos = new int[nd];
			pos[0] = x;
			pos[1] = y;
			
			// return value from specified position
			return this.array.getValue(pos);
		}

		@Override
		public void setValue(int x, int y, double value)
		{
			// convert (x,y) to ND integer array
			int nd = this.array.dimensionality();
			int[] pos = new int[nd];
			pos[0] = x;
			pos[1] = y;
			
			// set value at specified position
			this.array.setValue(pos, value);
		}

		@Override
		public ScalarArray2D<T> duplicate()
		{
			ScalarArray<T> tmp = this.array.newInstance(this.size0, this.size1);
			if (!(tmp instanceof ScalarArray2D))
			{
				throw new RuntimeException("Can not create Array2D instance from " + this.array.getClass().getName() + " class.");
			}
			
			ScalarArray2D<T> result = (ScalarArray2D <T>) tmp;
			
			int nd = this.array.dimensionality();
			int[] pos = new int[nd];

			// Fill new array with input array
			for (int y = 0; y < this.size1; y++)
			{
				pos[1] = y;
				for (int x = 0; x < this.size0; x++)
				{
					pos[0] = x;
					result.set(x, y, this.array.get(pos));
				}
			}

			return result;
		}
		
		@Override
		public ScalarArray.Iterator<T> iterator()
		{
			return new Iterator2D();
		}
		
		private class Iterator2D implements ScalarArray.Iterator<T>
		{
			int x = -1;
			int y = 0;
			
			public Iterator2D() 
			{
			}
			
			@Override
			public boolean hasNext()
			{
				return this.x < size0 - 1 || this.y < size1 - 1;
			}

			@Override
			public T next()
			{
				this.x++;
				if (this.x == size0)
				{
					this.y++;
					this.x = 0;
				}
				return Wrapper.this.get(x, y);
			}

			@Override
			public void forward()
			{
				this.x++;
				if (this.x == size0)
				{
					this.y++;
					this.x = 0;
				}
			}

			@Override
			public T get()
			{
				return Wrapper.this.get(x, y);
			}

			@Override
			public void set(T value)
			{
				Wrapper.this.set(x, y, value);
			}
			
			@Override
			public double nextValue()
			{
				forward();
				return getValue();
			}

			@Override
			public double getValue()
			{
				return Wrapper.this.getValue(x, y);
			}

			@Override
			public void setValue(double value)
			{
				Wrapper.this.setValue(x, y, value);				
			}
		}

	}

}
