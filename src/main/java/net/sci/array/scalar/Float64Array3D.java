/**
 * 
 */
package net.sci.array.scalar;

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
	    return wrap(Float64Array.create(size0, size1, size2));
	}
	
    /**
     * Encapsulates the specified instance of Float64Array into a new
     * Float64Array3D, by creating a Wrapper if necessary. If the original array
     * is already an instance of Float64Array3D, it is returned.
     * 
     * @param array
     *            the original array
     * @return a Float64Array3D view of the original array
     */
    public static Float64Array3D wrap(Float64Array array)
    {
        if (array instanceof Float64Array3D)
        { 
            return (Float64Array3D) array; 
        }
        return new Wrapper(array);
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

    @Override
    public void set(int x, int y, int z, Float64 value)
    {
        setValue(x, y, z, value.value);
    }
    
    /* (non-Javadoc)
     * @see net.sci.array.scalar.ScalarArray3D#setValue(int, int, int, double)
     */
    @Override
    public void setValue(int[] pos, double value)
    {
       setValue(pos[0], pos[1], pos[2], value);
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
    
    
    // =============================================================
    // Implementation of inner classes
    
    /**
     * Wraps a Float64 array with three dimensions into a Float64Array3D.
     */
    private static class Wrapper extends Float64Array3D
    {
        Float64Array array;

        public Wrapper(Float64Array array)
        {
            super(0, 0, 0);
            if (array.dimensionality() != 3)
            {
                throw new IllegalArgumentException("Requires an array of dimensionality equal to 3.");
            }
            this.size0 = array.size(0);
            this.size1 = array.size(1);
            this.size2 = array.size(2);
            this.array = array;
        }
        
        @Override
        public void setValue(int x, int y, int z, double value)
        {
            this.array.setValue(new int[] {x, y, z}, value);
        }

        @Override
        public double getValue(int... pos)
        {
            return this.array.getValue(pos);
        }

        @Override
        public void setValue(int[] pos, double value)
        {
            this.array.setValue(pos, value);
        }

        @Override
        public Float64Array3D duplicate()
        {
            return new Wrapper(this.array.duplicate());
        }

        /**
         * Simply returns an iterator on the original array.
         */
        @Override
        public net.sci.array.scalar.Float64Array.Iterator iterator()
        {
            return this.array.iterator();
        }
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
        public void setValue(int x, int y, double value)
        {
            Float64Array3D.this.setValue(x, y, this.sliceIndex, value);
        }
        
        @Override
        public void set(int x, int y, Float64 value)
        {
            Float64Array3D.this.set(x, y, this.sliceIndex, value);
        }

        @Override
        public double getValue(int... pos)
        {
            return Float64Array3D.this.getValue(pos[0], pos[1], this.sliceIndex);
        }

        @Override
        public void setValue(int[] pos, double value)
        {
            Float64Array3D.this.setValue(pos[0], pos[1], this.sliceIndex, value);
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
