/**
 * 
 */
package net.sci.array;

import java.io.PrintStream;
import java.util.Locale;
import java.util.function.BiFunction;

import net.sci.array.generic.BufferedGenericArray2D;

/**
 * Base implementation for 2D arrays.
 * 
 * Provides implementation for array interface, some of them relying on newly defined methods.
 * 
 * @author dlegland
 *
 */
public abstract class Array2D<T> implements Array<T>
{
	// =============================================================
	// static methods

    /**
     * Creates a new array with the specified size, containing the specified
     * initial value.
     * 
     * @param <T>
     *            the type of data
     * @param sizeX
     *            the size of the array to create in the first direction
     * @param sizeY
     *            the size of the array to create in the second direction
     * @param init
     *            the initial value within the array (repeated within the array)
     * @return a new array initialized with the default value.
     */
    public static <T> Array2D<T> create(int sizeX, int sizeY, T init)
    {
        return new BufferedGenericArray2D<T>(sizeX, sizeY, init);
    }
    
    /**
     * Wraps the specified array into an instance of Array2D containing the same
     * data.
     * 
     * @param <T>
     *            the type of data within the array
     * @param array
     *            the input array
     * @return an instance of Array2D with the same data. Can be the original
     *         array if it is already a subclass of Array2D.
     */
	public static final <T> Array2D<T> wrap(Array<T> array)
	{
		if (array instanceof Array2D)
		{
			return (Array2D<T>) array;
		}
		return new Wrapper<T>(array);
	}

	// =============================================================
	// class members

	/** the size of the array along the first dimension */
	protected int size0;
	
    /** the size of the array along the second dimension */
	protected int size1;
	
	// =============================================================
	// Constructors

	/**
     * Main constructor used to setup the size.
     * 
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

    /**
     * Populates the array using a function from two input arguments.
     * 
     * <pre>
     * {@code
     * Array2D<String> array = Array3D.create(5, 4, ""); 
     * String[] digits = {"A", "B", "C", "D", "E", "F"}; 
     * array.fill((x,y) -> digits[y] + digits[x]);
     * String res43 = array.get(4, 3); // returns "CD". 
     * }
     * </pre>
     * 
     * @param fun
     *            a function of two variables that returns an instance of type
     *            T. The two input variables correspond to the x and y coordinates.
     */
    public void fill(BiFunction<Integer,Integer,T> fun)
    {
        for (int[] pos : this.positions())
        {
            this.set(pos, fun.apply(pos[0], pos[1]));
        }
    }
    
    /**
     * Checks if the array contains the specified position, i.e. if all
     * coordinates are comprised between 0 and the size minus one in the
     * corresponding dimension.
     * 
     * @param x
     *            the first coordinate of the position to check
     * @param y
     *            the second coordinate of the position to check
     * @return true if the array contains the specified position.
     */
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
                stream.print(String.format(Locale.ENGLISH, " %s", get(x, y)));
            }
            stream.println();
        }
    }

    
    // =============================================================
    // New getter / setter
    
    /**
     * Retrieves the value of an element in the array at the position given by
     * two integer indices.
     * 
     * @param x
     *            index over the first array dimension
     * @param y
     *            index over the second array dimension
     * @return the new value at the specified index
     */
    public abstract T get(int x, int y);
    
    /**
     * Changes the value of an element in the array at the position given by
     * two integer indices.
     * 
     * @param x
     *            index over the first array dimension
     * @param y
     *            index over the second array dimension
     * @param value
     *            the new value at the specified index
     */
    public abstract void set(int x, int y, T value);
    
	
	// =============================================================
	// Specialization of the Array interface

    @Override
    public T get(int[] pos)
    {
        return get(pos[0], pos[1]);
    }
    
    @Override
    public void set(int[] pos, T value)
    {
        set(pos[0], pos[1], value);
    }
    
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

	
    public PositionIterator positionIterator()
    {
        return new PositionIterator2D();
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

    private static class Wrapper<T> extends Array2D<T>
	{
		private Array<T> array;
		
		protected Wrapper(Array<T> array)
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
        public T get(int x, int y)
        {
            // set value at specified position
            return this.array.get(new int[] {x, y});
        }

        @Override
        public void set(int x, int y, T value)
        {
            // set value at specified position
            this.array.set(new int[] {x, y}, value);
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
		public Array2D<T> duplicate()
		{
			Array<T> tmp = this.array.newInstance(this.size0, this.size1);
			if (!(tmp instanceof Array2D))
			{
				throw new RuntimeException("Can not create Array2D instance from " + this.array.getClass().getName() + " class.");
			}
			
			Array2D<T> result = (Array2D <T>) tmp;
			
			// Fill new array with input array
			for (int[] pos : result.positions())
            {
                result.set(pos, this.array.get(pos));
            }
            
			return result;
		}
		
		@Override
		public Class<T> elementClass()
		{
			return array.elementClass();
		}
		
		@Override
		public Array.Iterator<T> iterator()
		{
			return array.iterator();
		}
	}
}
