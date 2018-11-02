/**
 * 
 */
package net.sci.array.scalar;


/**
 * Base implementation for 3D arrays containing Int16 values.
 * 
 * @author dlegland
 *
 */
public abstract class Int16Array3D extends IntArray3D<Int16> implements Int16Array
{
	// =============================================================
	// Static methods

	/**
	 * @param size0
	 *            the size of the array along the first dimension
	 * @param size1
	 *            the size of the array along the second dimension
	 * @param size2
	 *            the size of the array along the third dimension
	 * @return a new instance of Int16Array3D
	 */
	public static final Int16Array3D create(int size0, int size1, int size2)
	{
		return new BufferedInt16Array3D(size0, size1, size2);
	}
	
	
	// =============================================================
	// Constructor

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
	protected Int16Array3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
	}

	
	// =============================================================
	// New methods

	/**
	 * Returns the short value at a given position.
	 * 
	 * @param x
	 *            the x-coordinate of the position
	 * @param y
	 *            the y-coordinate of the position
	 * @param z
	 *            the z-coordinate of the position
	 * @return the short value at the given position
	 */
	public abstract short getShort(int x, int y, int z);

	/**
	 * Sets the short value at a given position
	 * 
	 * @param x
	 *            the x-coordinate of the position
	 * @param y
	 *            the y-coordinate of the position
	 * @param z
	 *            the z-coordinate of the position
	 * @param value
	 *            the new short value at the given position
	 */
	public abstract void setShort(int x, int y, int z, short value);
	
	
	// =============================================================
	// Specialization of the UInt16Array interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.UInt16Array#getByte(int[])
	 */
	@Override
	public short getShort(int[] pos)
	{
		return getShort(pos[0], pos[1], pos[2]);
	}
	
	/* (non-Javadoc)
	 * @see net.sci.array.data.UInt16Array#setByte(int[], java.lang.Byte)
	 */
	@Override
	public void setShort(int[] pos, short value)
	{
		setShort(pos[0], pos[1], pos[2], value);
	}

    // =============================================================
    // Management of slices

    public Int16Array2D slice(int sliceIndex)
    {
        return new SliceView(sliceIndex);
    }

    /**
     * Iterates over the slices
     * 
     * @return an iterator over 2D slices
     */
    public Iterable<? extends Int16Array2D> slices()
    {
        return new Iterable<Int16Array2D>()
        {
            @Override
            public java.util.Iterator<Int16Array2D> iterator()
            {
                return new SliceIterator();
            }
        };
    }

    public java.util.Iterator<? extends Int16Array2D> sliceIterator()
    {
        return new SliceIterator();
    }

	
	// =============================================================
	// Specialization of IntArrayND interface

	public int getInt(int x, int y, int z)
	{
		return getShort(x, y, z); 
	}

	public void setInt(int x, int y, int z, int value)
	{
		setShort(x, y, z, (short) Int16.clamp(value));
	}

	// =============================================================
	// Specialization of Array3D interface

	@Override
	public abstract Int16Array3D duplicate();

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#get(int, int, int)
	 */
	@Override
	public Int16 get(int x, int y, int z)
	{
		return new Int16(getShort(x, y, z));
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#set(int, int, int, java.lang.Object)
	 */
	public void set(int x, int y, int z, Int16 value)
	{
		setShort(x, y, z, value.getShort());
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#getValue(int, int, int)
	 */
	@Override
	public double getValue(int x, int y, int z)
	{
		return getShort(x, y, z);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#setValue(int, int, int, double)
	 */
	@Override
	public void setValue(int x, int y, int z, double value)
	{
		setShort(x, y, z, (short) UInt16.clamp(value));
	}

	
	// =============================================================
	// Specialization of Array interface
	
	@Override
	public Int16Array newInstance(int... dims)
	{
		return Int16Array.create(dims);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.Array#get(int[])
	 */
	@Override
	public Int16 get(int[] pos)
	{
		return new Int16(getShort(pos[0], pos[1], pos[2]));
	}

	/* (non-Javadoc)
	 * @see net.sci.array.Array#set(int[], java.lang.Object)
	 */
	public void set(int[] pos, Int16 value)
	{
		setShort(pos[0], pos[1], pos[2], value.getShort());
	}
	
	
    private class SliceView extends Int16Array2D
    {
        int sliceIndex;
        
        protected SliceView(int slice)
        {
            super(Int16Array3D.this.size0, Int16Array3D.this.size1);
            if (slice < 0 || slice >= Int16Array3D.this.size2)
            {
                throw new IllegalArgumentException(String.format(
                        "Slice index %d must be comprised between 0 and %d", slice, Int16Array3D.this.size2));
            }
            this.sliceIndex = slice;
        }

        @Override
        public short getShort(int x, int y)
        {
            return Int16Array3D.this.getShort(x, y, this.sliceIndex);
        }

        @Override
        public void setShort(int x, int y, short value)
        {
            Int16Array3D.this.setShort(x, y, this.sliceIndex, value);            
        }

        @Override
        public net.sci.array.scalar.Int16Array.Iterator iterator()
        {
            return new Iterator();
        }

        class Iterator implements Int16Array.Iterator
        {
            int indX = -1;
            int indY = 0;
            
            public Iterator() 
            {
            }
            
            @Override
            public Int16 next()
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
            public short getShort()
            {
                return Int16Array3D.this.getShort(indX, indY, sliceIndex);
            }

            @Override
            public void setShort(short b)
            {
                Int16Array3D.this.setShort(indX, indY, sliceIndex, b);
            }
        }
    }
    
    private class SliceIterator implements java.util.Iterator<Int16Array2D> 
    {
        int sliceIndex = 0;

        @Override
        public boolean hasNext()
        {
            return sliceIndex < Int16Array3D.this.size2;
        }

        @Override
        public Int16Array2D next()
        {
            return new SliceView(sliceIndex++);
        }
    }
}
