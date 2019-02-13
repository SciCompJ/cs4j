/**
 * 
 */
package net.sci.array.scalar;

import net.sci.array.Array;

/**
 * @author dlegland
 *
 */
public abstract class Float64Array3D extends ScalarArray3D<Float64> implements Float64Array
{
	// =============================================================
	// Static methods

	/**
	 * Creates a new 3D array containing Float64 values.
	 * 
	 * @param size0
	 *            the size of the array along the first dimension
	 * @param size1
	 *            the size of the array along the second dimension
	 * @param size2
	 *            the size of the array along the third dimension
	 * @return a new instance of Float64Array3D
	 */
	public static final Float64Array3D create(int size0, int size1, int size2)
	{
        if (Array.countElements(size0, size1, size2) < Integer.MAX_VALUE)
            return new BufferedFloat64Array3D(size0, size1, size2);
        else 
            return new SlicedFloat64Array3D(size0, size1, size2);
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
	protected Float64Array3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
	}

    // =============================================================
    // Management of slices

    public Float64Array2D slice(int sliceIndex)
    {
        return new SliceView(sliceIndex);
    }

    /**
     * Iterates over the slices
     * 
     * @return an iterator over 2D slices
     */
    public Iterable<? extends Float64Array2D> slices()
    {
        return new Iterable<Float64Array2D>()
        {
            @Override
            public java.util.Iterator<Float64Array2D> iterator()
            {
                return new SliceIterator();
            }
        };
    }

    public java.util.Iterator<? extends Float64Array2D> sliceIterator()
    {
        return new SliceIterator();
    }


	// =============================================================
	// Specialization of Array3D 

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#get(int, int, int)
	 */
	@Override
	public Float64 get(int x, int y, int z)
	{
		return new Float64(getValue(x, y, z));
	}


	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#set(int, int, int, java.lang.Object)
	 */
	@Override
	public void set(int x, int y, int z, Float64 value)
	{
		setValue(x, y, z, value.getValue());
	}

	
	// =============================================================
	// Implementation of Array interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.ScalarArray#newInstance(int[])
	 */
	@Override
	public Float64Array newInstance(int... dims)
	{
		return Float64Array.create(dims);
	}

    /* (non-Javadoc)
     * @see net.sci.array.data.FloatArray#duplicate()
     */
    @Override
    public Float64Array3D duplicate()
    {
        Float64Array3D res = Float64Array3D.create(size0, size1, size2);
        for (int z = 0; z < size2; z++)
        {
            for (int y = 0; y < size1; y++)
            {
                for (int x = 0; x < size0; x++)
                {
                    res.setValue(x, y, z, getValue(x, y, z));
                }
            }
        }
        return res;
    }


    private class SliceView extends Float64Array2D
    {
        int sliceIndex;
        
        protected SliceView(int slice)
        {
            super(Float64Array3D.this.size0, Float64Array3D.this.size1);
            if (slice < 0 || slice >= Float64Array3D.this.size2)
            {
                throw new IllegalArgumentException(String.format(
                        "Slice index %d must be comprised between 0 and %d", slice, Float64Array3D.this.size2));
            }
            this.sliceIndex = slice;
        }

        @Override
        public double getValue(int x, int y)
        {
            return Float64Array3D.this.getValue(x, y, this.sliceIndex);
        }

        @Override
        public void setValue(int x, int y, double value)
        {
            Float64Array3D.this.setValue(x, y, this.sliceIndex, value);            
        }

        @Override
        public net.sci.array.scalar.Float64Array.Iterator iterator()
        {
            return new Iterator();
        }

        class Iterator implements Float64Array.Iterator
        {
            int indX = -1;
            int indY = 0;
            
            public Iterator() 
            {
            }
            
            @Override
            public Float64 next()
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
            public double getValue()
            {
                return Float64Array3D.this.getValue(indX, indY, sliceIndex);
            }

            @Override
            public void setValue(double value)
            {
                Float64Array3D.this.setValue(indX, indY, sliceIndex, value);
            }
        }
    }
    
    private class SliceIterator implements java.util.Iterator<Float64Array2D> 
    {
        int sliceIndex = 0;

        @Override
        public boolean hasNext()
        {
            return sliceIndex < Float64Array3D.this.size2;
        }

        @Override
        public Float64Array2D next()
        {
            return new SliceView(sliceIndex++);
        }
    }
}
