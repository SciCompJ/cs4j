/**
 * 
 */
package net.sci.array.scalar;

import net.sci.array.Array;


/**
 * @author dlegland
 *
 */
public abstract class Int32Array3D extends IntArray3D<Int32> implements Int32Array
{
	// =============================================================
	// Static methods

    /**
     * Creates a new 3D array containing Int32 values.
     * 
     * @param size0
     *            the size of the array along the first dimension
     * @param size1
     *            the size of the array along the second dimension
     * @param size2
     *            the size of the array along the third dimension
     * @return a new instance of Int32Array3D
     */
	public static final Int32Array3D create(int size0, int size1, int size2)
	{
        if (Array.countElements(size0, size1, size2) < Integer.MAX_VALUE)
            return new BufferedInt32Array3D(size0, size1, size2);
        else 
            return new SlicedInt32Array3D(size0, size1, size2);
	}
	
	
	// =============================================================
	// Constructor

	protected Int32Array3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
	}

    // =============================================================
    // Management of slices

    public Int32Array2D slice(int sliceIndex)
    {
        return new SliceView(sliceIndex);
    }

    /**
     * Iterates over the slices
     * 
     * @return an iterator over 2D slices
     */
    public Iterable<? extends Int32Array2D> slices()
    {
        return new Iterable<Int32Array2D>()
        {
            @Override
            public java.util.Iterator<Int32Array2D> iterator()
            {
                return new SliceIterator();
            }
        };
    }

    public java.util.Iterator<? extends Int32Array2D> sliceIterator()
    {
        return new SliceIterator();
    }

    
	// =============================================================
	// Specialization of the IntArray interface

	@Override
	public int getInt(int[] pos)
	{
		return getInt(pos[0], pos[1], pos[2]);
	}

	@Override
	public void setInt(int[] pos, int value)
	{
		setInt(pos[0], pos[1], pos[2], value);
	}
	

	// =============================================================
	// Specialization of the Array3D interface

	@Override
	public abstract Int32Array3D duplicate();

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#get(int, int, int)
	 */
	@Override
	public Int32 get(int x, int y, int z)
	{
		return new Int32(getInt(x, y, z));
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#set(int, int, int, java.lang.Object)
	 */
	public void set(int x, int y, int z, Int32 value)
	{
		setInt(x, y, z, value.getInt());
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#getValue(int, int, int)
	 */
	@Override
	public double getValue(int x, int y, int z)
	{
		return getInt(x, y, z);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#setValue(int, int, int, double)
	 */
	@Override
	public void setValue(int x, int y, int z, double value)
	{
		setInt(x, y, z, (int) value);
	}

	
	// =============================================================
	// Specialization of Array interface
	
	@Override
	public Int32Array newInstance(int... dims)
	{
		return Int32Array.create(dims);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.Array#get(int[])
	 */
	@Override
	public Int32 get(int[] pos)
	{
		return new Int32(getInt(pos[0], pos[1], pos[2]));
	}

	/* (non-Javadoc)
	 * @see net.sci.array.Array#set(int[], java.lang.Object)
	 */
	public void set(int[] pos, Int32 value)
	{
		setInt(pos[0], pos[1], pos[2], value.getInt());
	}
    
    private class SliceView extends Int32Array2D
    {
        int sliceIndex;
        
        protected SliceView(int slice)
        {
            super(Int32Array3D.this.size0, Int32Array3D.this.size1);
            if (slice < 0 || slice >= Int32Array3D.this.size2)
            {
                throw new IllegalArgumentException(String.format(
                        "Slice index %d must be comprised between 0 and %d", slice, Int32Array3D.this.size2));
            }
            this.sliceIndex = slice;
        }

        @Override
        public int getInt(int x, int y)
        {
            return Int32Array3D.this.getInt(x, y, this.sliceIndex);
        }

        @Override
        public void setInt(int x, int y, int value)
        {
            Int32Array3D.this.setInt(x, y, this.sliceIndex, value);            
        }

        @Override
        public net.sci.array.scalar.Int32Array.Iterator iterator()
        {
            return new Iterator();
        }

        class Iterator implements Int32Array.Iterator
        {
            int indX = -1;
            int indY = 0;
            
            public Iterator() 
            {
            }
            
            @Override
            public Int32 next()
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
            public int getInt()
            {
                return Int32Array3D.this.getInt(indX, indY, sliceIndex);
            }

            @Override
            public void setInt(int b)
            {
                Int32Array3D.this.setInt(indX, indY, sliceIndex, b);
            }
        }
    }
    
    private class SliceIterator implements java.util.Iterator<Int32Array2D> 
    {
        int sliceIndex = 0;

        @Override
        public boolean hasNext()
        {
            return sliceIndex < Int32Array3D.this.size2;
        }

        @Override
        public Int32Array2D next()
        {
            return new SliceView(sliceIndex++);
        }
    }
}
