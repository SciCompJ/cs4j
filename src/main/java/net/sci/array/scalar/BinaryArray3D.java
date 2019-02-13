/**
 * 
 */
package net.sci.array.scalar;

import net.sci.array.Array;


/**
 * A three-dimensional array containing boolean values.
 * 
 * @author dlegland
 *
 */
public abstract class BinaryArray3D extends IntArray3D<Binary> implements BinaryArray
{
	// =============================================================
	// Static methods

	public static final BinaryArray3D create(int size0, int size1, int size2)
	{
	    if (Array.countElements(size0, size1, size2) < Integer.MAX_VALUE)
	        return new BufferedBinaryArray3D(size0, size1, size2);
	    else 
	        return new SlicedBinaryArray3D(size0, size1, size2);
	}
	
    public final static BinaryArray3D wrap(BinaryArray array)
    {
        if (array instanceof BinaryArray3D)
        {
            return (BinaryArray3D) array;
        }
        return new Wrapper(array);
    }

    
	// =============================================================
	// Constructor

	protected BinaryArray3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
	}

	
	// =============================================================
	// New methods

	/**
	 * Returns the logical state at a given position.
	 * 
	 * @param x
	 *            the x-coordinate of the position
	 * @param y
	 *            the y-coordinate of the position
	 * @param z
	 *            the z-coordinate of the position
	 * @return the boolean value at the given position
	 */
	public abstract boolean getBoolean(int x, int y, int z);

	/**
	 * Sets the logical state at a given position.
	 * 
	 * @param x
	 *            the x-coordinate of the position
	 * @param y
	 *            the y-coordinate of the position
	 * @param z
	 *            the z-coordinate of the position
	 * @param state
	 *            the new state at the given position
	 */
	public abstract void setBoolean(int x, int y, int z, boolean state);
	
    // =============================================================
    // Management of slices

    public BinaryArray2D slice(int sliceIndex)
    {
        return new SliceView(sliceIndex);
    }

    /**
     * Iterates over the slices
     * 
     * @return an iterator over 2D slices
     */
    public Iterable<? extends BinaryArray2D> slices()
    {
        return new Iterable<BinaryArray2D>()
        {
            @Override
            public java.util.Iterator<BinaryArray2D> iterator()
            {
                return new SliceIterator();
            }
        };
    }

    public java.util.Iterator<? extends BinaryArray2D> sliceIterator()
    {
        return new SliceIterator();
    }

	
	// =============================================================
	// Specialization of the BooleanArray interface

    /* (non-Javadoc)
     * @see net.sci.array.data.BooleanArray#complement()
     */
    @Override
    public BinaryArray3D complement()
    {
        BinaryArray3D result = BinaryArray3D.create(this.size0, this.size1, this.size2);
	    for (int[] pos : positions())
	    {
	    	result.setBoolean(pos, !getBoolean(pos));
	    }
        return result;
    }

	/* (non-Javadoc)
	 * @see net.sci.array.data.BooleanArray#getState(int[])
	 */
	@Override
	public boolean getBoolean(int[] pos)
	{
		return getBoolean(pos[0], pos[1], pos[2]);
	}
	
	/* (non-Javadoc)
	 * @see net.sci.array.data.BooleanArray#setState(int[], java.lang.Boolean)
	 */
	@Override
	public void setBoolean(int[] pos, boolean state)
	{
		setBoolean(pos[0], pos[1], pos[2], state);
	}

	
	// =============================================================
	// Specialization of IntArrayND interface

	public int getInt(int x, int y, int z)
	{
		return getBoolean(x, y, z) ? 1 : 0; 
	}

	public void setInt(int x, int y, int z, int value)
	{
		setBoolean(x, y, z, value != 0);
	}

	
	// =============================================================
	// Specialization of Array3D interface

	@Override
	public abstract BinaryArray3D duplicate();

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#get(int, int, int)
	 */
	@Override
	public Binary get(int x, int y, int z)
	{
		return new Binary(getBoolean(x, y, z));
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#set(int, int, int, java.lang.Object)
	 */
	public void set(int x, int y, int z, Binary value)
	{
		setBoolean(x, y, z, value.getBoolean());
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#getValue(int, int, int)
	 */
	@Override
	public double getValue(int x, int y, int z)
	{
		return getBoolean(x, y, z) ? 1 : 0;
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#setValue(int, int, int, double)
	 */
	@Override
	public void setValue(int x, int y, int z, double value)
	{
		setBoolean(x, y, z, value != 0);
	}

	
	// =============================================================
	// Specialization of Array interface
	
	@Override
	public BinaryArray newInstance(int... dims)
	{
		return BinaryArray.create(dims);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.Array#get(int[])
	 */
	@Override
	public Binary get(int[] pos)
	{
		return new Binary(getBoolean(pos[0], pos[1], pos[2]));
	}

	/* (non-Javadoc)
	 * @see net.sci.array.Array#set(int[], java.lang.Object)
	 */
	public void set(int[] pos, Binary value)
	{
		setBoolean(pos[0], pos[1], pos[2], value.getBoolean());
	}
	
	
    // =============================================================
    // Inner Wrapper class

    private static class Wrapper extends BinaryArray3D
    {
        private BinaryArray array;
        
        protected Wrapper(BinaryArray array)
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
        public boolean getBoolean(int x, int y, int z)
        {
            return this.array.getBoolean(new int[] {x, y, z});
        }

        @Override
        public void setBoolean(int x, int y, int z, boolean state)
        {
            this.array.setBoolean(new int[] {x, y, z}, state);
        }

        @Override
        public IntArray.Factory<Binary> getFactory()
        {
            return this.array.getFactory();
        }

        
        @Override
        public BinaryArray3D duplicate()
        {
            BinaryArray3D result = BinaryArray3D.create(array.size(0), array.size(1), array.size(2));
    	    for (int[] pos : array.positions())
    	    {
    	    	result.setBoolean(pos, array.getValue(pos) > 0);
    	    }
            return result;
        }
        
        @Override
        public Class<Binary> dataType()
        {
            return array.dataType();
        }

        @Override
        public BinaryArray.Iterator iterator()
        {
            return new Iterator3D();
        }
        
        private class Iterator3D implements BinaryArray.Iterator
        {
            int x = -1;
            int y = 0;
            int z = 0;
            
            public Iterator3D() 
            {
            }
            
            @Override
            public boolean hasNext()
            {
                return this.x < size0 - 1 || this.y < size1 - 1 || this.z < size2 - 1;
            }

            @Override
            public Binary next()
            {
                forward();
                return Wrapper.this.get(x, y, z);
            }

            @Override
            public void forward()
            {
                this.x++;
                if (this.x == size0)
                {
                    this.x = 0;
                    this.y++;
                    if (this.y == size1)
                    {
                        this.y = 0;
                        this.z++;
                    }
                }
            }

            @Override
            public Binary get()
            {
                return Wrapper.this.get(x, y, z);
            }

            @Override
            public void set(Binary value)
            {
                Wrapper.this.set(x, y, z, value);
            }

            @Override
            public boolean getBoolean()
            {
                return Wrapper.this.getBoolean(x, y, z);
            }

            @Override
            public void setBoolean(boolean b)
            {
                Wrapper.this.setBoolean(x, y, z, b);
            }
        }
    }
    
    private class SliceView extends BinaryArray2D
    {
        int sliceIndex;
        
        protected SliceView(int slice)
        {
            super(BinaryArray3D.this.size0, BinaryArray3D.this.size1);
            if (slice < 0 || slice >= BinaryArray3D.this.size2)
            {
                throw new IllegalArgumentException(String.format(
                        "Slice index %d must be comprised between 0 and %d", slice, BinaryArray3D.this.size2));
            }
            this.sliceIndex = slice;
        }

        @Override
        public boolean getBoolean(int x, int y)
        {
            return BinaryArray3D.this.getBoolean(x, y, this.sliceIndex);
        }

        @Override
        public void setBoolean(int x, int y, boolean bool)
        {
            BinaryArray3D.this.setBoolean(x, y, this.sliceIndex, bool);            
        }

        @Override
        public net.sci.array.scalar.BinaryArray.Iterator iterator()
        {
            return new Iterator();
        }

        class Iterator implements BinaryArray.Iterator
        {
            int indX = -1;
            int indY = 0;
            
            public Iterator() 
            {
            }
            
            @Override
            public Binary next()
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
            public boolean getBoolean()
            {
                return BinaryArray3D.this.getBoolean(indX, indY, sliceIndex);
            }

            @Override
            public void setBoolean(boolean b)
            {
                BinaryArray3D.this.setBoolean(indX, indY, sliceIndex, b);
            }
        }
    }
    
    private class SliceIterator implements java.util.Iterator<BinaryArray2D> 
    {
        int sliceIndex = 0;

        @Override
        public boolean hasNext()
        {
            return sliceIndex < BinaryArray3D.this.size2;
        }

        @Override
        public BinaryArray2D next()
        {
            return new SliceView(sliceIndex++);
        }
    }

}
