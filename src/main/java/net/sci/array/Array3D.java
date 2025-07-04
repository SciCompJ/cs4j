/**
 * 
 */
package net.sci.array;

import java.io.PrintStream;
import java.util.Locale;

import net.sci.array.impl.BufferedGenericArray3D;
import net.sci.array.numeric.TriFunction;

/**
 * Base implementation for three-dimensional array.
 * 
 * @author dlegland
 *
 */
public abstract class Array3D<T> implements Array<T>
{
    // =============================================================
    // static factories
    
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
     * @param sizeZ
     *            the size of the array to create in the third direction
     * @param init
     *            the initial value within the array (repeated within the array)
     * @return a new array initialized with the default value.
     */
    public static <T> Array3D<T> create(int sizeX, int sizeY, int sizeZ, T init)
    {
        return new BufferedGenericArray3D<T>(sizeX, sizeY, sizeZ, init);
    }
    
    /**
     * Wraps the specified array into an instance of Array3D containing the same
     * data.
     * 
     * @param <T>
     *            the type of data within the array
     * @param array
     *            the input array
     * @return an instance of Array3D with the same data. Can be the original
     *         array if it is already a subclass of Array3D.
     */
    public final static <T> Array3D<T> wrap(Array<T> array)
    {
        if (array instanceof Array3D)
        {
            return (Array3D<T>) array;
        }
        return new Wrapper<T>(array);
    }
    
    
    // =============================================================
	// class members

	protected int size0;
	protected int size1;
	protected int size2;

	
	// =============================================================
	// Constructors

	/**
	 * Initialize the protected size variables. 
	 * 
	 * @param size0
	 *            the size of the array along the first dimension
	 * @param size1
	 *            the size of the array along the second dimension
	 * @param size2
	 *            the size of the array along the third dimension
	 */
	protected Array3D(int size0, int size1, int size2)
	{
        // keep array size
		this.size0 = size0;
		this.size1 = size1;
		this.size2 = size2;
	}

	
	// =============================================================
	// New methods

	/**
     * Populates the array using a function from three input arguments.
     * 
     * <pre>
     * {@code
     * Array3D<String> array = Array3D.create(5, 4, 3, ""); 
     * String[] digits = {"A", "B", "C", "D", "E", "F"}; 
     * array.populate((x,y,z) -> digits[z] + digits[y] + digits[x]); 
     * String res432 = array.get(4, 3, 2); // returns "CDE". 
     * }
     * </pre>
     * 
     * @param fun
     *            a function of three variables that returns an instance of type
     *            T. The three input variables correspond to the x, y, and z
     *            coordinates.
     */
    public void fill(TriFunction<Integer,Integer,Integer,T> fun)
    {
        for (int[] pos : this.positions())
        {
            this.set(pos, fun.apply(pos[0], pos[1], pos[2]));
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
     * @param z
     *            the third coordinate of the position to check
     * @return true if the array contains the specified position.
     * 
     * @see #containsPosition(int[])
     */
    public boolean containsPosition(int x, int y, int z)
    {
        if (x < 0 || y < 0 || z < 0) return false;
        if (x >= this.size0) return false;
        if (y >= this.size1) return false;
        if (z >= this.size2) return false;
        return true;
    }

    /**
     * Returns a view over the specified slice.
     * 
     * @param sliceIndex
     *            the index of the slice
     * @return a view on the specific slice, as a 2D array
     */
    public abstract Array2D<T> slice(int sliceIndex);

    /**
     * Replaces the elements of the specified slice by the elements contained in
     * the specified 2D array.
     * 
     * @param sliceIndex
     *            the slice index of elements to replace
     * @param slice
     *            the 2D array containing elements to replace.
     */
    public void setSlice(int sliceIndex, Array2D<T> slice)
    {
        // check validity of input arguments
        if (sliceIndex < 0 || sliceIndex >= this.size2)
        {
            final String pattern = "Slice index (%d) out of bound (%d ; %d)";
            throw new IllegalArgumentException(String.format(Locale.ENGLISH, pattern, sliceIndex, 0, this.size2));
        }
        if (this.size0 != slice.size0 || this.size1 != slice.size1)
        {
            throw new IllegalArgumentException("Slice dimensions must be compatible with array dimensions");
        }
        
        // iterate over elements of selected slice
        for (int y = 0; y < this.size1; y++)
        {
            for (int x = 0; x < this.size0; x++)
            {
                this.set(x, y, sliceIndex, slice.get(x, y));
            }
        }
    }
    
    /**
     * Iterates over the slices
     * 
     * @return an iterator over 2D slices
     */
    public abstract Iterable<? extends Array2D<T>> slices();

    /**
     * Creates an iterator over the slices
     * 
     * @return an iterator over 2D slices
     */
    public abstract java.util.Iterator<? extends Array2D<T>> sliceIterator();

    /**
     * Prints the content of this array on the default output stream.
     * 
     * Default behavior converts each element into a string. Specialized array
     * classed can use more relevant conversion.
     * 
     * @see #printContent(PrintStream)
     */
    public void printContent()
    {
        printContent(System.out);
    }
    
    /**
     * Prints the content of this array on the specified stream.
     * 
     * @param stream
     *            the stream to print on.
     */
    public void printContent(PrintStream stream)
    {
        for (int z = 0; z < this.size2; z++)
        {
            stream.println(String.format(Locale.ENGLISH, "slice %d/%d:", z, size2));
            Array2D<T> slice = slice(z);
            slice.printContent(stream);
        }
    }
    

    // =============================================================
    // New getter / setter
    
    /**
     * Retrieves the value of an element in the array at the position given by
     * three integer indices.
     * 
     * @param x
     *            index over the first array dimension
     * @param y
     *            index over the second array dimension
     * @param z
     *            index over the third array dimension
     * @return the new value at the specified index
     */
    public abstract T get(int x, int y, int z);
    
    /**
     * Changes the value of an element in the array at the position given by
     * three integer indices.
     * 
     * @param x
     *            index over the first array dimension
     * @param y
     *            index over the second array dimension
     * @param z
     *            index over the third array dimension
     * @param value
     *            the new value at the specified index
     */
	public abstract void set(int x, int y, int z, T value);
	
	
	// =============================================================
	// Specialization of the Array interface

    @Override
    public T get(int[] pos)
    {
        return get(pos[0], pos[1], pos[2]);
    }
    
    @Override
    public void set(int[] pos, T value)
    {
        set(pos[0], pos[1], pos[2], value);
    }
    
    @Override
    public abstract Array3D<T> duplicate();

    /* (non-Javadoc)
	 * @see net.sci.array.Array#dimensionality()
	 */
	@Override
	public int dimensionality()
	{
		return 3;
	}

	/* (non-Javadoc)
	 * @see net.sci.array.Array#getSize()
	 */
	@Override
	public int[] size()
	{
		return new int[]{this.size0, this.size1, this.size2};
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
		case 2: return this.size2;
		default:
			throw new IllegalArgumentException("Dimension argument must be comprised between 0 and 2");
		}
	}
	
    @Override
    public Iterable<int[]> positions()
    {
        return new Iterable<int[]>()
        {
            public java.util.Iterator<int[]> iterator()
            {
                return new PositionIterator3D();
            }
        };
    }
    

    @Override
    public boolean containsPosition(int[] pos)
    {
        if (pos.length != 3) return false;
        return containsPosition(pos[0], pos[1], pos[2]);
    }

	/**
     * Iterator over the positions of a 3D array.
     * 
     * @author dlegland
     *
     */
    private class PositionIterator3D implements PositionIterator
    {
        int posX = -1;
        int posY = 0;
        int posZ = 0;
        
        public PositionIterator3D()
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
                if (posY == size1)
                {
                    posY = 0;
                    posZ++;
                }
            }
        }
        
        @Override
        public int[] get()
        {
            return new int[] { posX, posY, posZ };
        }
        
        public int get(int dim)
        {
            return switch (dim)
            {
                case 0 -> posX;
                case 1 -> posY;
                case 2 -> posZ;
                default -> throw new IllegalArgumentException("Requires dimension beween 0 and 2");
            };
        }
        
        @Override
        public int[] get(int[] pos)
        {
            pos[0] = posX;
            pos[1] = posY;
            pos[2] = posZ;
            return pos;
        }

        @Override
        public boolean hasNext()
        {
            return posX < size0 - 1 || posY < size1 - 1 || posZ < size2 - 1;
        }
        
        @Override
        public int[] next()
        {
            forward();
            return new int[] { posX, posY, posZ };
        }
    }
    
    private static class Wrapper<T> extends Array3D<T>
    {
        private Array<T> array;
        
        protected Wrapper(Array<T> array)
        {
            super(0, 0, 0);
            if (array.dimensionality() < 3)
            {
                throw new IllegalArgumentException("Requires an array with at least three dimensions");
            }
            this.array = array;
            this.size0 = array.size(0);
            this.size1 = array.size(1);
            this.size2 = array.size(2);
        }

        @Override
        public Array2D<T> slice(int sliceIndex)
        {
            return new SliceView(sliceIndex);
        }

        @Override
        public Iterable<? extends Array2D<T>> slices()
        {
            return new Iterable<Array2D<T>>()
            {
                @Override
                public java.util.Iterator<Array2D<T>> iterator()
                {
                    return new SliceIterator();
                }
            };
        }

        @Override
        public java.util.Iterator<? extends Array2D<T>> sliceIterator()
        {
            return new SliceIterator();
        }

        @Override
        public T get(int x, int y, int z)
        {
            // set value at specified position
            return this.array.get(new int[] {x, y, z});
        }

        @Override
        public void set(int x, int y, int z, T value)
        {
            // set value at specified position
            this.array.set(new int[] {x, y, z}, value);
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
        public Array3D<T> duplicate()
        {
            Array<T> tmp = this.array.newInstance(this.size0, this.size1, this.size2);
            if (!(tmp instanceof Array3D))
            {
                throw new RuntimeException("Can not create Array3D instance from " + this.array.getClass().getName() + " class.");
            }
            
            Array3D<T> result = (Array3D <T>) tmp;
            
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
        
        private class SliceView extends Array2D<T>
        {
            int sliceIndex;
            
            protected SliceView(int slice)
            {
                super(Wrapper.this.size0, Wrapper.this.size1);
                if (slice < 0 || slice >= Wrapper.this.size2)
                {
                    throw new IllegalArgumentException(String.format(
                            "Slice index %d must be comprised between 0 and %d", slice, Wrapper.this.size2));
                }
                this.sliceIndex = slice;
            }

            @Override
            public T get(int x, int y)
            {
                return Wrapper.this.get(x, y, this.sliceIndex);
            }

            @Override
            public void set(int x, int y, T value)
            {
                Wrapper.this.set(x, y, this.sliceIndex, value);
            }

            @Override
            public T get(int[] pos)
            {
                return Wrapper.this.get(pos[0], pos[1], this.sliceIndex);
            }

            @Override
            public void set(int[] pos, T value)
            {
                Wrapper.this.set(pos[0], pos[1], this.sliceIndex, value);
            }

            @Override
            public Array<T> newInstance(int... dims)
            {
                return Wrapper.this.array.newInstance(dims);
            }

            @Override
            public Class<T> elementClass()
            {
                return Wrapper.this.array.elementClass();
            }

            @Override
            public Array2D<T> duplicate()
            {
                // create a new array, and ensure type is 2D
                Array2D<T> result = Array2D.wrap(Wrapper.this.array.newInstance(this.size0, this.size1));
                
                // Fill new array with input slice
                for (int y = 0; y < Wrapper.this.size1; y++)
                {
                    for (int x = 0; x < Wrapper.this.size0; x++)
                    {
                        result.set(x, y, Wrapper.this.get(x, y, sliceIndex));
                    }
                }
                                
                return result;
            }

            @Override
            public Array.Factory<T> factory()
            {
                return Wrapper.this.factory();
            }
        }
        
        private class SliceIterator implements java.util.Iterator<Array2D<T>> 
        {
            int sliceIndex = -1;

            @Override
            public boolean hasNext()
            {
                return sliceIndex < array.size(2) - 1;
            }

            @Override
            public Array2D<T> next()
            {
                sliceIndex++;
                return new SliceView(sliceIndex);
            }
        }
    }
}
