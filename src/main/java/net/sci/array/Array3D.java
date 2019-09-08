/**
 * 
 */
package net.sci.array;

import net.sci.array.generic.BufferedGenericArray3D;

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

    public static <T> Array3D<T> create(int sizeX, int sizeY, int sizeZ, T init)
    {
        return new BufferedGenericArray3D<T>(sizeX, sizeY, sizeZ, init);
    }
    
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
		this.size0 = size0;
		this.size1 = size1;
		this.size2 = size2;
	}

	// =============================================================
	// New methods

    /**
     * Returns a view over the specified slice.
     * 
     * @param sliceIndex
     *            the index of the slice
     * @return a view on the specific slice, as a 2D array
     */
    public abstract Array2D<T> slice(int sliceIndex);

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


    // =============================================================
    // New getter / setter

    public abstract T get(int x, int y, int z);

	public abstract void set(int x, int y, int z, T value);

	
	// =============================================================
	// Specialization of the Array interface

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

	public T get(int[] pos)
	{
		return get(pos[0], pos[1], pos[2]);
	}

	public void set(int[] pos, T value)
	{
		set(pos[0], pos[1], pos[2], value);
	}
	
	public PositionIterator positionIterator()
    {
        return new PositionIterator3D();
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
            switch (dim)
            {
            case 0:
                return posX;
            case 1:
                return posY;
            case 2:
                return posZ;
            default:
                throw new IllegalArgumentException("Requires dimension beween 0 and 2");
            }
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
        public T get(int x, int y, int z)
        {
            // convert (x,y,z) to ND integer array
            int nd = this.array.dimensionality();
            int[] pos = new int[nd];
            pos[0] = x;
            pos[1] = y;
            pos[2] = z;
            
            // return value from specified position
            return this.array.get(pos);
        }

        @Override
        public void set(int x, int y, int z, T value)
        {
            // convert (x,y) to ND integer array
            int nd = this.array.dimensionality();
            int[] pos = new int[nd];
            pos[0] = x;
            pos[1] = y;
            pos[2] = z;
            
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
            
            int nd = this.array.dimensionality();
            int[] pos = new int[nd];

            // Fill new array with input array
            for (int z = 0; z < this.size2; z++)
            {
                pos[2] = z;
                for (int y = 0; y < this.size1; y++)
                {
                    pos[1] = y;
                    for (int x = 0; x < this.size0; x++)
                    {
                        pos[0] = x;
                        result.set(x, y, z, this.array.get(pos));
                    }
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
            public Array<T> newInstance(int... dims)
            {
                return Wrapper.this.array.newInstance(dims);
            }

            @Override
            public Class<T> dataType()
            {
                return Wrapper.this.array.dataType();
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
            public Array.Factory<T> getFactory()
            {
                return Wrapper.this.getFactory();
            }

            @Override
            public Array.Iterator<T> iterator()
            {
                return new Iterator();
            }

            class Iterator implements Array.Iterator<T>
            {
                int indX = -1;
                int indY = 0;
                
                public Iterator() 
                {
                }
                
                @Override
                public T next()
                {
                    forward();
                    return get();
                }

                @Override
                public void forward()
                {
                    indX++;
                    if (indX >= size0)
                    {
                        indX = 0;
                        indY++;
                    }
                }

                @Override
                public boolean hasNext()
                {
                    return indX < size0 - 1 || indY < size1 - 1;
                }

                @Override
                public T get()
                {
                    return Wrapper.this.get(indX, indY, sliceIndex);
                }

                @Override
                public void set(T value)
                {
                    Wrapper.this.set(indX, indY, sliceIndex, value);
                }
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
