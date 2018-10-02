/**
 * 
 */
package net.sci.array.scalar;

/**
 * @author dlegland
 *
 */
public abstract class UInt8Array3D extends IntArray3D<UInt8> implements UInt8Array
{
	// =============================================================
	// Static methods

	public static final UInt8Array3D create(int size0, int size1, int size2)
	{
		return new BufferedUInt8Array3D(size0, size1, size2);
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
	protected UInt8Array3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
	}

	
	// =============================================================
	// New methods

	/**
	 * Returns the byte value at a given position.
	 * 
	 * @param x
	 *            the x-coordinate of the position
	 * @param y
	 *            the y-coordinate of the position
	 * @param z
	 *            the z-coordinate of the position
	 * @return the byte value at the given position
	 */
	public abstract byte getByte(int x, int y, int z);

	/**
	 * Sets the byte value at a given position
	 * 
	 * @param x
	 *            the x-coordinate of the position
	 * @param y
	 *            the y-coordinate of the position
	 * @param z
	 *            the z-coordinate of the position
	 * @param value
	 *            the new byte value at the given position
	 */
	public abstract void setByte(int x, int y, int z, byte value);
	
	
    // =============================================================
    // Management of slices

    public UInt8Array2D slice(int sliceIndex)
    {
        return new SliceView(sliceIndex);
    }

    /**
     * Iterates over the slices
     * 
     * @return an iterator over 2D slices
     */
    public Iterable<? extends UInt8Array2D> slices()
    {
        return new Iterable<UInt8Array2D>()
        {
            @Override
            public java.util.Iterator<UInt8Array2D> iterator()
            {
                return new SliceIterator();
            }
        };
    }

    public java.util.Iterator<? extends UInt8Array2D> sliceIterator()
    {
        return new SliceIterator();
    }


    // =============================================================
	// Specialization of the UInt8Array interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.UInt8Array#getByte(int[])
	 */
	@Override
	public byte getByte(int[] pos)
	{
		return getByte(pos[0], pos[1], pos[2]);
	}
	
	/* (non-Javadoc)
	 * @see net.sci.array.data.UInt8Array#setByte(int[], java.lang.Byte)
	 */
	@Override
	public void setByte(int[] pos, byte value)
	{
		setByte(pos[0], pos[1], pos[2], value);
	}

	// =============================================================
	// Specialization of IntArrayND interface

	public int getInt(int x, int y, int z)
	{
		return getByte(x, y, z) & 0x00FF; 
	}

	public void setInt(int x, int y, int z, int value)
	{
		setByte(x, y, z, (byte) value);
	}

	// =============================================================
	// Specialization of Array3D interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#get(int, int, int)
	 */
	@Override
	public UInt8 get(int x, int y, int z)
	{
		return new UInt8(getByte(x, y, z));
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#set(int, int, int, java.lang.Object)
	 */
	public void set(int x, int y, int z, UInt8 value)
	{
		setByte(x, y, z, value.getByte());
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#getValue(int, int, int)
	 */
	@Override
	public double getValue(int x, int y, int z)
	{
		return getByte(x, y, z) & 0x00FF;
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#setValue(int, int, int, double)
	 */
	@Override
	public void setValue(int x, int y, int z, double value)
	{
		setByte(x, y, z, (byte) value);
	}

	
	// =============================================================
	// Specialization of Array interface
	
	@Override
	public UInt8Array newInstance(int... dims)
	{
		return UInt8Array.create(dims);
	}

    @Override
    public UInt8Array3D duplicate()
    {
        UInt8Array3D res = UInt8Array3D.create(size0, size1, size2);
        for (int z = 0; z < size2; z++)
        {
            for (int y = 0; y < size1; y++)
            {
                for (int x = 0; x < size0; x++)
                {
                    res.setByte(x, y, z, getByte(x, y, z));
                }
            }
        }
        return res;
    }

	/* (non-Javadoc)
	 * @see net.sci.array.Array#get(int[])
	 */
	@Override
	public UInt8 get(int[] pos)
	{
		return new UInt8(getByte(pos[0], pos[1], pos[2]));
	}

	/* (non-Javadoc)
	 * @see net.sci.array.Array#set(int[], java.lang.Object)
	 */
	public void set(int[] pos, UInt8 value)
	{
		setByte(pos[0], pos[1], pos[2], value.getByte());
	}

	
    private class SliceView extends UInt8Array2D
    {
        int sliceIndex;
        
        protected SliceView(int slice)
        {
            super(UInt8Array3D.this.size0, UInt8Array3D.this.size1);
            if (slice < 0 || slice >= UInt8Array3D.this.size2)
            {
                throw new IllegalArgumentException(String.format(
                        "Slice index %d must be comprised between 0 and %d", slice, UInt8Array3D.this.size2));
            }
            this.sliceIndex = slice;
        }

        @Override
        public byte getByte(int x, int y)
        {
            return UInt8Array3D.this.getByte(x, y, this.sliceIndex);
        }

        @Override
        public void setByte(int x, int y, byte value)
        {
            UInt8Array3D.this.setByte(x, y, this.sliceIndex, value);            
        }

        @Override
        public net.sci.array.scalar.UInt8Array.Iterator iterator()
        {
            return new Iterator();
        }

        class Iterator implements UInt8Array.Iterator
        {
            int indX = -1;
            int indY = 0;
            
            public Iterator() 
            {
            }
            
            @Override
            public UInt8 next()
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
                return indX < size0 - 1 && indY < size1 - 1;
            }

            @Override
            public byte getByte()
            {
                return UInt8Array3D.this.getByte(indX, indY, sliceIndex);
            }

            @Override
            public void setByte(byte b)
            {
                UInt8Array3D.this.setByte(indX, indY, sliceIndex, b);
            }
        }
    }
    
    private class SliceIterator implements java.util.Iterator<UInt8Array2D> 
    {
        int sliceIndex = -1;

        @Override
        public boolean hasNext()
        {
            return sliceIndex < UInt8Array3D.this.size2;
        }

        @Override
        public UInt8Array2D next()
        {
            sliceIndex++;
            return new SliceView(sliceIndex);
        }
        
    }

}
