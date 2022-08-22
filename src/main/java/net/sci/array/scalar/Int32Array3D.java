/**
 * 
 */
package net.sci.array.scalar;


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
        return wrap(Int32Array.create(size0, size1, size2));
	}
	
    /**
     * Encapsulates the specified instance of Int32Array into a new
     * Int32Array3D, by creating a Wrapper if necessary. If the original array
     * is already an instance of Int32Array3D, it is returned.
     * 
     * @param array
     *            the original array
     * @return a Int32Array3D view of the original array
     */
    public static Int32Array3D wrap(Int32Array array)
    {
        if (array instanceof Int32Array3D)
        { 
            return (Int32Array3D) array; 
        }
        return new Wrapper(array);
    }
    
    
	// =============================================================
	// Constructor

	protected Int32Array3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
	}
	

    // =============================================================
    // Specialization of the Array3D interface

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

    @Override
    public void set(int x, int y, int z, Int32 value)
    {
        setInt(x, y, z, value.value);
    }
    

    // =============================================================
    // Specialization of Array interface
       
	@Override
	public abstract Int32Array3D duplicate();
	

    // =============================================================
    // Implementation of inner classes
    
    /**
     * Wraps a Int32 array with three dimensions into a Int32Array3D.
     */
    private static class Wrapper extends Int32Array3D
    {
        Int32Array array;

        public Wrapper(Int32Array array)
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
        public void setInt(int x, int y, int z, int s)
        {
            this.array.setInt(new int[] {x, y, z}, s);
        }

        @Override
        public int getInt(int... pos)
        {
            return this.array.getInt(pos);
        }

        @Override
        public void setInt(int[] pos, int value)
        {
            this.array.setInt(pos, value);
        }

        @Override
        public Int32Array3D duplicate()
        {
            return new Wrapper(this.array.duplicate());
        }

        /**
         * Simply returns an iterator on the original array.
         */
        @Override
        public net.sci.array.scalar.Int32Array.Iterator iterator()
        {
            return this.array.iterator();
        }
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
        public void setInt(int x, int y, int value)
        {
            Int32Array3D.this.setInt(x, y, this.sliceIndex, value);            
        }

        @Override
        public int getInt(int... pos)
        {
            return Int32Array3D.this.getInt(pos[0], pos[1], this.sliceIndex);
        }

        @Override
        public void setInt(int[] pos, int value)
        {
            Int32Array3D.this.setInt(pos[0], pos[1], this.sliceIndex, value);            
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
            public void setInt(int value)
            {
                Int32Array3D.this.setInt(value, indX, indY, sliceIndex);
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
