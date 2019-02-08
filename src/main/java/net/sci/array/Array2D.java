/**
 * 
 */
package net.sci.array;

import java.io.PrintStream;
import java.util.Locale;

import net.sci.array.generic.BufferedGenericArray2D;

/**
 * @author dlegland
 *
 */
public abstract class Array2D<T> implements Array<T>
{
	// =============================================================
	// static methods

    public static <T> Array2D<T> create(int sizeX, int sizeY, T init)
    {
        return new BufferedGenericArray2D<T>(sizeX, sizeY, init);
    }
    
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

	public boolean containsPosition(int x, int y)
	{
        if (x < 0 || y < 0) return false;
        if (x >= this.size0) return false;
        if (y >= this.size1) return false;
        return true;
	}

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
                System.out.print(String.format(Locale.ENGLISH, " %s", get(x, y)));
            }
            System.out.println();
        }
    }

    // =============================================================
    // New abstract methods

	public abstract T get(int x, int y);

	public abstract void set(int x, int y, T value);

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
	public int[] size()
	{
		return new int[]{this.size0, this.size1};
	}

	/* (non-Javadoc)
	 * @see net.sci.array.Array#getSize(int)
	 */
	@Override
	public int size(int dim)
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
	
    public PositionIterator positionIterator()
    {
        return new PositionIterator2D();
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
			this.size0 = array.size(0);
			this.size1 = array.size(1);
		}

		@Override
		public Array<T> newInstance(int... dims)
		{
			return this.array.newInstance(dims);
		}

		@Override
		public Factory<T> getFactory()
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
//      @Override
//      public double getValue(int x, int y)
//      {
//          // convert (x,y) to ND integer array
//          int nd = this.array.dimensionality();
//          int[] pos = new int[nd];
//          pos[0] = x;
//          pos[1] = y;
//          
//          // return value from specified position
//          return this.array.getValue(pos);
//      }
//
//      @Override
//      public void setValue(int x, int y, double value)
//      {
//          // convert (x,y) to ND integer array
//          int nd = this.array.dimensionality();
//          int[] pos = new int[nd];
//          pos[0] = x;
//          pos[1] = y;
//          
//          // set value at specified position
//          this.array.setValue(pos, value);
//      }

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
		public Class<T> dataType()
		{
			return array.dataType();
		}

		@Override
		public Array.Iterator<T> iterator()
		{
			return array.iterator();
		}
	}
	
    /**
     * Iterator over the positions of an array.
     * 
     * @author dlegland
     *
     */
    private class PositionIterator2D implements PositionIterator
    {
        int posX = -1;
        int posY = 0;
        
        public PositionIterator2D()
        {
        }
        
        @Override
        public void forward()
        {
            posX++;
            if (posX == size0)
            {
                posX = 0;
                posY++;
            }
        }
        
        @Override
        public int[] get()
        {
            return new int[] { posX, posY };
        }
        
        public int get(int dim)
        {
            switch (dim)
            {
            case 0:
                return posX;
            case 1:
                return posY;
            default:
                throw new IllegalArgumentException("Requires dimension beween 0 and 1");
            }
        }
        
        @Override
        public boolean hasNext()
        {
            return posX < size0 - 1 || posY < size1 - 1;
        }
        
        @Override
        public int[] next()
        {
            forward();
            return new int[] { posX, posY };
        }

        @Override
        public int[] get(int[] pos)
        {
            pos[0] = posX;
            pos[1] = posY;
            return pos;
        }
    }
}
