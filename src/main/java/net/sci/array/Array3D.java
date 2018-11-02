/**
 * 
 */
package net.sci.array;

/**
 * Base implementation for three-dimensional array.
 * 
 * @author dlegland
 *
 */
public abstract class Array3D<T> implements Array<T>
{
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
	public int[] getSize()
	{
		return new int[]{this.size0, this.size1, this.size2};
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
}
