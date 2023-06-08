/**
 * 
 */
package net.sci.array;

import java.io.PrintStream;
import java.util.Locale;

/**
 * Simplification interface for 1D arrays.
 * 
 * @see Array2D
 * @see Array3D
 * 
 * @author dlegland
 */
public abstract class Array1D<T> implements Array<T>
{
	// =============================================================
	// static methods

//    public static <T> Array1D<T> create(int sizeX, int sizeY, T init)
//    {
//        return new BufferedGenericArray2D<T>(sizeX, sizeY, init);
//    }
    
    /**
     * Wraps the specified array into an instance of Array1D containing the same
     * data.
     * 
     * @param <T>
     *            the type of data within the array
     * @param array
     *            the input array
     * @return an instance of Array1D with the same data. Can be the original
     *         array if it is already a subclass of Array1D.
     */
	public static final <T> Array1D<T> wrap(Array<T> array)
	{
		if (array instanceof Array1D)
		{
			return (Array1D<T>) array;
		}
		return new Wrapper<T>(array);
	}
	

	// =============================================================
	// class members
	
	/** the size (length) of this array.*/
	protected int size0;
	
	
	// =============================================================
	// Constructors

	/**
	 * Main constructor used to setup the size.
	 * 
	 * @param size0
	 *            the size of the array
	 */
	protected Array1D(int size0)
	{
		this.size0 = size0;
	}


	// =============================================================
	// New methods
    
	/**
     * Checks if the array contains the specified position.
     * 
     * @param x
     *            the position to check
     * @return true if the array contains the specified position.
     */
	public boolean containsPosition(int x)
	{
        if (x < 0) return false;
        if (x >= this.size0) return false;
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
        for (int x = 0; x < this.size0; x++)
        {
            stream.print(String.format(Locale.ENGLISH, " %s", get(x)));
        }
        stream.println();
    }

    
    // =============================================================
    // New getter / setter
    
    /**
     * Retrieves the value of an element in the array at the position given by
     * the integer index.
     * 
     * @param x
     *            index over the array dimension
     * @return the value at the specified index
     */
    public abstract T get(int x);
    
    /**
     * Changes the value of an element in the array at the position given by
     * the integer index.
     * 
     * @param x
     *            index over the array dimension
     * @param value
     *            the new value at the specified index
     */
    public abstract void set(int x, T value);
    
    
    // =============================================================
    // Specialization of the Array interface

    @Override
    public T get(int[] pos)
    {
        return get(pos[0]);
    }
    
    @Override
    public void set(int[] pos, T value)
    {
        set(pos[0], value);
    }
    
	/* (non-Javadoc)
	 * @see net.sci.array.Array#dimensionality()
	 */
	@Override
	public int dimensionality()
	{
		return 1;
	}

	/* (non-Javadoc)
	 * @see net.sci.array.Array#getSize()
	 */
	@Override
	public int[] size()
	{
		return new int[]{this.size0};
	}

	/* (non-Javadoc)
	 * @see net.sci.array.Array#getSize(int)
	 */
	@Override
	public int size(int dim)
	{
	    if (dim == 0)
	    {
	        return this.size0;
	    }
	    throw new IllegalArgumentException("Dimension argument of 1D array cana only be 0, not " + dim);
	}

	@Override
	public abstract Array1D<T> duplicate();

	
    public PositionIterator positionIterator()
    {
        return new PositionIterator1D();
    }

	/**
     * Iterator over the positions of an array.
     * 
     * @author dlegland
     *
     */
    private class PositionIterator1D implements PositionIterator
    {
        int posX = -1;
        
        public PositionIterator1D()
        {
        }
        
        @Override
        public void forward()
        {
            posX++;
        }
        
        @Override
        public int[] get()
        {
            return new int[] { posX };
        }
        
        public int get(int dim)
        {
            if (dim == 0)
            {
                return posX;
            }
            throw new IllegalArgumentException("Dimension argument of 1D array cana only be 0, not " + dim);
        }
        
        @Override
        public boolean hasNext()
        {
            return posX < size0 - 1;
        }
        
        @Override
        public int[] next()
        {
            forward();
            return new int[] { posX };
        }
    
        @Override
        public int[] get(int[] pos)
        {
            pos[0] = posX;
            return pos;
        }
    }

    private static class Wrapper<T> extends Array1D<T>
	{
		private Array<T> array;
		
		protected Wrapper(Array<T> array)
		{
			super(0);
			if (array.dimensionality() < 1)
			{
				throw new IllegalArgumentException("Requires an array with at least one dimensions");
			}
			this.array = array;
			this.size0 = array.size(0);
		}

        @Override
        public T get(int x)
        {
            // get value at specified position
            return this.array.get(new int[] {x});
        }

        @Override
        public void set(int x, T value)
        {
            // set value at specified position
            this.array.set(new int[] {x}, value);
        }

		@Override
		public Array<T> newInstance(int... dims)
		{
			return this.array.newInstance(dims);
		}

		@Override
		public Factory<T> factory()
		{
			return this.array.factory();
		}

		@Override
		public T get(int[] pos)
		{
			// return value from specified position
			return this.array.get(pos);
		}

		@Override
		public void set(int[] pos, T value)
		{
			// set value at specified position
			this.array.set(pos, value);
		}

		@Override
		public Array1D<T> duplicate()
		{
			Array<T> tmp = this.array.newInstance(this.size0);
			if (!(tmp instanceof Array1D))
			{
				throw new RuntimeException("Can not create Array2D instance from " + this.array.getClass().getName() + " class.");
			}
			
			Array1D<T> result = (Array1D <T>) tmp;
			
			// Fill new array with input array
			for (int[] pos : result.positions())
            {
                result.set(pos, this.array.get(pos));
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
}
