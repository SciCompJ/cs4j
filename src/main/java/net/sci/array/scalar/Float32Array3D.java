/**
 * 
 */
package net.sci.array.scalar;

import net.sci.array.Array;

/**
 * @author dlegland
 *
 */
public abstract class Float32Array3D extends ScalarArray3D<Float32> implements Float32Array
{
	// =============================================================
	// Static methods

	/**
	 * Creates a new 3D array containing Float32 values.
	 * 
	 * @param size0
	 *            the size of the array along the first dimension
	 * @param size1
	 *            the size of the array along the second dimension
	 * @param size2
	 *            the size of the array along the third dimension
	 * @return a new instance of Float32Array3D
	 */
	public static final Float32Array3D create(int size0, int size1, int size2)
	{
        if (Array.countElements(size0, size1, size2) < Integer.MAX_VALUE)
            return new BufferedFloat32Array3D(size0, size1, size2);
        else 
            return new SlicedFloat32Array3D(size0, size1, size2);
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
	protected Float32Array3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
	}


    // =============================================================
    // New methods
    
    // =============================================================
    // Management of slices

    public Float32Array2D slice(int sliceIndex)
    {
        return new SliceView(sliceIndex);
    }

    /**
     * Iterates over the slices
     * 
     * @return an iterator over 2D slices
     */
    public Iterable<? extends Float32Array2D> slices()
    {
        return new Iterable<Float32Array2D>()
        {
            @Override
            public java.util.Iterator<Float32Array2D> iterator()
            {
                return new SliceIterator();
            }
        };
    }

    public java.util.Iterator<? extends Float32Array2D> sliceIterator()
    {
        return new SliceIterator();
    }

    // =============================================================
    // Specialization of Float32Array
    

	// =============================================================
	// Specialization of Array3D 

	
	// =============================================================
	// Implementation of Array interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.ScalarArray#newInstance(int[])
	 */
	@Override
	public Float32Array newInstance(int... dims)
	{
		return Float32Array.create(dims);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.FloatArray#duplicate()
	 */
	@Override
    public Float32Array3D duplicate()
    {
        Float32Array3D res = Float32Array3D.create(size0, size1, size2);
        for (int z = 0; z < size2; z++)
        {
            for (int y = 0; y < size1; y++)
            {
                for (int x = 0; x < size0; x++)
                {
                    res.setValue(getValue(x, y, z), x, y, z);
                }
            }
        }
        return res;
    }

    private class SliceView extends Float32Array2D
    {
        int sliceIndex;
        
        protected SliceView(int slice)
        {
            super(Float32Array3D.this.size0, Float32Array3D.this.size1);
            if (slice < 0 || slice >= Float32Array3D.this.size2)
            {
                throw new IllegalArgumentException(String.format(
                        "Slice index %d must be comprised between 0 and %d", slice, Float32Array3D.this.size2));
            }
            this.sliceIndex = slice;
        }

        @Override
        public float getFloat(int... pos)
        {
            return Float32Array3D.this.getFloat(pos[0], pos[1], sliceIndex);
        }

        @Override
        public void setFloat(float value, int... pos)
        {
            Float32Array3D.this.setFloat(value, pos[0], pos[1], sliceIndex);
        }

        @Override
        public double getValue(int... pos)
        {
            return Float32Array3D.this.getValue(pos[0], pos[1], sliceIndex);
        }

        @Override
        public void setValue(double value, int... pos)
        {
            Float32Array3D.this.setValue(value, pos[0], pos[1], sliceIndex);
        }

        @Override
        public net.sci.array.scalar.Float32Array.Iterator iterator()
        {
            return new Iterator();
        }

        class Iterator implements Float32Array.Iterator
        {
            int indX = -1;
            int indY = 0;
            
            public Iterator() 
            {
            }
            
            @Override
            public Float32 next()
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
                return Float32Array3D.this.getValue(indX, indY, sliceIndex);
            }

            @Override
            public void setValue(double value)
            {
                Float32Array3D.this.setValue(value, indX, indY, sliceIndex);
            }
        }
    }
    
    private class SliceIterator implements java.util.Iterator<Float32Array2D> 
    {
        int sliceIndex = 0;

        @Override
        public boolean hasNext()
        {
            return sliceIndex < Float32Array3D.this.size2;
        }

        @Override
        public Float32Array2D next()
        {
            return new SliceView(sliceIndex++);
        }
    }
}
