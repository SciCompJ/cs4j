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
	 * Creates a new 3D array containing Int16 values.
	 * 
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
        return wrap(Int16Array.create(size0, size1, size2));
	}
	
    /**
     * Encapsulates the specified instance of Int16Array into a new
     * Int16Array3D, by creating a Wrapper if necessary. If the original array
     * is already an instance of Int16Array3D, it is returned.
     * 
     * @param array
     *            the original array
     * @return a Int16Array3D view of the original array
     */
    public static Int16Array3D wrap(Int16Array array)
    {
        if (array instanceof Int16Array3D)
        { 
            return (Int16Array3D) array; 
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
	protected Int16Array3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
	}

	
    // =============================================================
    // New methods
    
    public abstract void setShort(int x, int y, int z, short s);


    // =============================================================
    // Specialization of the IntArray3D interface

    @Override
    public void setInt(int x, int y, int z, int value)
    {
        setShort(x, y, z, (short) Int16.clamp(value));
    }


    // =============================================================
    // Specialization of the ScalarArray3D interface


    @Override
    public void setValue(int x, int y, int z, double value)
    {
        setShort(x, y, z, (short) Int16.clamp(value));
    }
    
    
    // =============================================================
    // Specialization of Array3D interface

    @Override
    public void set(int x, int y, int z, Int16 value)
    {
        setShort(x, y, z, value.value);
    }

    
    // =============================================================
    // Specialization of the Int16Array interface

    public void setShort(int [] pos, short s)
    {
        setShort(pos[0], pos[1], pos[2], s);
    }
    
    
    // =============================================================
    // Specialization of Array3D interface

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
	// Specialization of Array interface
	
    @Override
    public Int16Array3D duplicate()
    {
        Int16Array3D res = Int16Array3D.create(this.size0, this.size1, this.size2);
        res.fillInts(pos -> this.getInt(pos));
        return res;
    }

    
    // =============================================================
    // Implementation of inner classes
    
    /**
     * Wraps a Int16 array with three dimensions into a Int16Array3D.
     */
    private static class Wrapper extends Int16Array3D
    {
        Int16Array array;

        public Wrapper(Int16Array array)
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
        public void setShort(int x, int y, int z, short s)
        {
            this.array.setShort(new int[] {x, y, z}, s);
        }

        @Override
        public short getShort(int... pos)
        {
            return this.array.getShort(pos);
        }

        @Override
        public void setShort(int[] pos, short value)
        {
            this.array.setShort(pos, value);
        }

        /**
         * Simply returns an iterator on the original array.
         */
        @Override
        public net.sci.array.scalar.Int16Array.Iterator iterator()
        {
            return this.array.iterator();
        }
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
        public void setShort(int x, int y, short value)
        {
            Int16Array3D.this.setShort(x, y, this.sliceIndex, value);            
        }

        @Override
        public short getShort(int... pos)
        {
            return Int16Array3D.this.getShort(pos[0], pos[1], this.sliceIndex);
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
            public void setShort(short s)
            {
                Int16Array3D.this.setShort(indX, indY, sliceIndex, s);
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
