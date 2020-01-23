/**
 * 
 */
package net.sci.array.scalar;

import net.sci.array.Array;


/**
 * Base implementation for 3D arrays containing Int16 values.
 * 
 * @author dlegland
 *
 */
public abstract class UInt16Array3D extends IntArray3D<UInt16> implements UInt16Array
{
	// =============================================================
	// Static methods

    /**
     * Creates a new 3D array containing UInt16 values.
     * 
     * @param size0
     *            the size of the array along the first dimension
     * @param size1
     *            the size of the array along the second dimension
     * @param size2
     *            the size of the array along the third dimension
     * @return a new instance of UInt16Array3D
     */
	public static final UInt16Array3D create(int size0, int size1, int size2)
	{
        if (Array.countElements(size0, size1, size2) < Integer.MAX_VALUE)
            return new BufferedUInt16Array3D(size0, size1, size2);
        else 
            return new SlicedUInt16Array3D(size0, size1, size2);
	}
	
	
	// =============================================================
	// Constructor
	/**
	 * @param size0
	 *            the size of the array along the first dimension
	 * @param size1
	 *            the size of the array along the second dimension
	 * @param size2
	 *            the size of the array along the third dimension
	 */
	protected UInt16Array3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
	}

	
	// =============================================================
	// New methods

	
    // =============================================================
    // Management of slices

    public UInt16Array2D slice(int sliceIndex)
    {
        return new SliceView(sliceIndex);
    }

    /**
     * Iterates over the slices
     * 
     * @return an iterator over 2D slices
     */
    public Iterable<? extends UInt16Array2D> slices()
    {
        return new Iterable<UInt16Array2D>()
        {
            @Override
            public java.util.Iterator<UInt16Array2D> iterator()
            {
                return new SliceIterator();
            }
        };
    }

    public java.util.Iterator<? extends UInt16Array2D> sliceIterator()
    {
        return new SliceIterator();
    }

	// =============================================================
	// Specialization of the UInt16Array interface


	// =============================================================
	// Specialization of IntArrayND interface


	// =============================================================
	// Specialization of Array3D interface

	@Override
    public UInt16Array3D duplicate()
    {
        // create output array
        UInt16Array3D res = UInt16Array3D.create(this.size0, this.size1, this.size2);

        for (int z = 0; z < size2; z++)
        {
            for (int y = 0; y < size1; y++)
            {
                for (int x = 0; x < size0; x++)
                {
                    res.setShort(getShort(x, y, z), x, y, z);
                }
            }
        }
        return res;
    }

	
	// =============================================================
	// Specialization of Array interface
	
	@Override
	public UInt16Array newInstance(int... dims)
	{
		return UInt16Array.create(dims);
	}
	
    private class SliceView extends UInt16Array2D
    {
        int sliceIndex;
        
        protected SliceView(int slice)
        {
            super(UInt16Array3D.this.size0, UInt16Array3D.this.size1);
            if (slice < 0 || slice >= UInt16Array3D.this.size2)
            {
                throw new IllegalArgumentException(String.format(
                        "Slice index %d must be comprised between 0 and %d", slice, UInt16Array3D.this.size2));
            }
            this.sliceIndex = slice;
        }

        @Override
        public short getShort(int... pos)
        {
            return UInt16Array3D.this.getShort(pos[0], pos[1], this.sliceIndex);
        }

        @Override
        public void setShort(short value, int... pos)
        {
            UInt16Array3D.this.setShort(value, pos[0], pos[1], this.sliceIndex);            
        }

        @Override
        public net.sci.array.scalar.UInt16Array.Iterator iterator()
        {
            return new Iterator();
        }

        class Iterator implements UInt16Array.Iterator
        {
            int indX = -1;
            int indY = 0;
            
            public Iterator() 
            {
            }
            
            @Override
            public UInt16 next()
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
                return UInt16Array3D.this.getShort(indX, indY, sliceIndex);
            }

            @Override
            public void setShort(short s)
            {
                UInt16Array3D.this.setShort(s, indX, indY, sliceIndex);
            }
        }
    }
    
    private class SliceIterator implements java.util.Iterator<UInt16Array2D> 
    {
        int sliceIndex = 0;

        @Override
        public boolean hasNext()
        {
            return sliceIndex < UInt16Array3D.this.size2;
        }

        @Override
        public UInt16Array2D next()
        {
            return new SliceView(sliceIndex++);
        }
        
    }
}
