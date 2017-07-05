/**
 * 
 */
package net.sci.array.data;

import net.sci.array.Array;
import net.sci.array.ArrayFactory;

/**
 * @author dlegland
 *
 */
public abstract class Array2D<T> implements Array<T>
{
	// =============================================================
	// static methods
	
	public static final <T> Array2D<T> wrap(Array<T> array)
	{
		if (array instanceof Array2D)
		{
			return (Array2D<T>) array;
		}
		return new Wrap<T>(array);
	}

	// =============================================================
	// class members

	protected int size0;
	protected int size1;
	
	// =============================================================
	// Constructors

	/**
	 * @param size0
	 *            the size of the array along the first dimension
	 * @param size1
	 *            the size of the array along the second dimension
	 */
	protected Array2D(int size0, int size1)
	{
		this.size0 = size0;
		this.size1 = size1;
	}

	// =============================================================
	// New methods

	public abstract T get(int x, int y);

	public abstract void set(int x, int y, T value);

	public abstract double getValue(int x, int y);
	
	public abstract void setValue(int x, int y, double value);
	
	// =============================================================
	// Specialization of the Array interface

	/* (non-Javadoc)
	 * @see net.sci.array.Array#dimensionality()
	 */
	@Override
	public int dimensionality()
	{
		return 2;
	}

	/* (non-Javadoc)
	 * @see net.sci.array.Array#getSize()
	 */
	@Override
	public int[] getSize()
	{
		return new int[]{this.size0, this.size1};
	}

	/* (non-Javadoc)
	 * @see net.sci.array.Array#getSize(int)
	 */
	@Override
	public int getSize(int dim)
	{
		switch(dim)
		{
		case 0: return this.size0;
		case 1: return this.size1;
		default:
			throw new IllegalArgumentException("Dimension argument must be 0 or 1");
		}
	}

	@Override
	public abstract Array2D<T> duplicate();

	public T get(int[] pos)
	{
		return get(pos[0], pos[1]);
	}

	public void set(int[] pos, T value)
	{
		set(pos[0], pos[1], value);
	}
	
	public double getValue(int[] pos)
	{
		return getValue(pos[0], pos[1]);
	}

	public void setValue(int[] pos, double value)
	{
		setValue(pos[0], pos[1], value);
	}
	
	private static class Wrap<T> extends Array2D<T>
	{
		private Array<T> array;
		
		protected Wrap(Array<T> array)
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
		public Array<T> newInstance(int... dims)
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
		public Array2D<T> duplicate()
		{
			Array<T> tmp = this.array.newInstance(this.size0, this.size1);
			if (!(tmp instanceof Array2D))
			{
				throw new RuntimeException("Can not create Array2D instance from " + this.array.getClass().getName() + " class.");
			}
			
			Array2D<T> result = (Array2D <T>) tmp;
			
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
		public Array.Iterator<T> iterator()
		{
			return new Iterator2D();
		}
		
		private class Iterator2D implements Array.Iterator<T>
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
				return Wrap.this.get(x, y);
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
				return Wrap.this.get(x, y);
			}

			@Override
			public void set(T value)
			{
				Wrap.this.set(x, y, value);
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
				return Wrap.this.getValue(x, y);
			}

			@Override
			public void setValue(double value)
			{
				Wrap.this.setValue(x, y, value);				
			}
		}

	}
}
