/**
 * 
 */
package net.sci.array.scalar;

import java.io.PrintStream;
import java.util.Locale;

import net.sci.array.Array2D;

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
		public ScalarArray.Factory<T> getFactory()
		{
			return this.array.getFactory();
		}

		@Override
		public T get(int x, int y)
		{
			// return value from specified position
			return this.array.get(new int[]{x, y});
		}

		@Override
		public void set(int x, int y, T value)
		{
			// set value at specified position
			this.array.set(new int[]{x, y}, value);
		}

		@Override
		public double getValue(int x, int y)
		{
			// return value from specified position
			return this.array.getValue(new int[]{x, y});
		}

		@Override
		public void setValue(int x, int y, double value)
		{
			// set value at specified position
			this.array.setValue(new int[]{x, y}, value);
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
		public Class<T> getDataType()
		{
			return array.getDataType();
		}

		@Override
		public ScalarArray.Iterator<T> iterator()
		{
			return array.iterator();
		}
	}
}