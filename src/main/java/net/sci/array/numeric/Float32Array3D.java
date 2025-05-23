/**
 * 
 */
package net.sci.array.numeric;

import java.util.Locale;

import net.sci.array.Array2D;
import net.sci.array.numeric.impl.BufferedFloat32Array3D;

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
        return wrap(Float32Array.create(size0, size1, size2));
    }

    /**
     * Wraps the float array into an instance of Float32Array3D with the
     * specified dimensions. The new array will be backed by the given float
     * array; that is, modifications to the float buffer will cause the array to
     * be modified and vice versa.
     * 
     * The number of elements of the buffer must be at least the product of
     * array dimensions.
     * 
     * @param buffer
     *            the array to float to encapsulate
     * @param size0
     *            the size of the array along the first dimension
     * @param size1
     *            the size of the array along the second dimension
     * @param size2
     *            the size of the array along the third dimension
     * @return a new instance of Float32Array2D
     */
    public static final Float32Array3D wrap(float[] buffer, int size0, int size1, int size2)
    {
        return new BufferedFloat32Array3D(size0, size1, size2, buffer);
    }

    /**
     * Encapsulates the specified instance of Float32Array into a new
     * Float32Array3D, by creating a Wrapper if necessary. If the original array
     * is already an instance of Float32Array3D, it is returned.
     * 
     * @param array
     *            the original array
     * @return a Float32Array2D view of the original array
     */
    public static Float32Array3D wrap(Float32Array array)
    {
        if (array instanceof Float32Array3D)
        { 
            return (Float32Array3D) array; 
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
    protected Float32Array3D(int size0, int size1, int size2)
    {
        super(size0, size1, size2);
    }

    
    // =============================================================
    // New methods
    
    public abstract float getFloat(int x, int y, int z);
    
    public abstract void setFloat(int x, int y, int z, float value);
    
    
    // =============================================================
    // Specialization of FloatArray 

    @Override
    public float getFloat(int[] pos)
    {
        return getFloat(pos[0], pos[1], pos[2]);
    }

    @Override
    public void setFloat(int[] pos, float floatValue)
    {
        setFloat(pos[0], pos[1], pos[2], floatValue);
    }


    // =============================================================
    // Management of slices

    public Float32Array2D slice(int sliceIndex)
    {
        return new SliceView(sliceIndex);
    }
    
    /**
     * Overrides the default implementation for <code>setSlice</code> by relying
     * on the lowest level data access and modifier methods.
     *
     * @param sliceIndex
     *            the slice index of elements to replace
     * @param slice
     *            the 2D array containing (Float32) elements to replace.
     */
    @Override
    public void setSlice(int sliceIndex, Array2D<Float32> slice)
    {
        // check validity of input arguments
        if (sliceIndex < 0 || sliceIndex >= this.size2)
        {
            final String pattern = "Slice index (%d) out of bound (%d ; %d)";
            throw new IllegalArgumentException(String.format(Locale.ENGLISH, pattern, sliceIndex, 0, this.size2));
        }
        if (this.size0 != slice.size(0) || this.size1 != slice.size(1))
        {
            throw new IllegalArgumentException("Slice dimensions must be compatible with array dimensions");
        }
        
        // wraps slice array into a Float32Array
        Float32Array2D slice2 = Float32Array2D.wrap(Float32Array.wrap(slice));
        
        // iterate over elements of selected slice
        for (int y = 0; y < this.size1; y++)
        {
            for (int x = 0; x < this.size0; x++)
            {
                this.setFloat(x, y, sliceIndex, slice2.getFloat(x, y));
            }
        }
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
    // Specialization of ScalarArray3D 

    @Override
    public double getValue(int x, int y, int z)
    {
        return getFloat(x, y, z);
    }
    
    @Override
    public void setValue(int x, int y, int z, double value)
    {
        setFloat(x, y, z, (float) value);
    }

    // =============================================================
    // Specialization of Array3D 

    @Override
    public Float32 get(int x, int y, int z)
    {
        return new Float32(getFloat(x, y, z));
    }
    
    @Override
    public void set(int x, int y, int z, Float32 value)
    {
        setFloat(x, y, z, value.value);
    }
    

    // =============================================================
    // Specialization of ScalarArray 

    /* (non-Javadoc)
     * @see net.sci.array.scalar.ScalarArray3D#setValue(int, int, int, double)
     */
    @Override
    public void setValue(int[] pos, double value)
    {
       setFloat(pos[0], pos[1], pos[2], (float) value);
    }

    
    // =============================================================
    // Implementation of Array interface

    /*
     * (non-Javadoc)
     * 
     * @see net.sci.array.data.ScalarArray#newInstance(int[])
     */
    @Override
    public Float32Array newInstance(int... dims)
    {
        return Float32Array.create(dims);
    }

    /*
     * (non-Javadoc)
     * 
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
                    res.setValue(x, y, z, getValue(x, y, z));
                }
            }
        }
        return res;
    }

    // =============================================================
    // Implementation of inner classes
    
    /**
     * Wraps a Float32 array with three dimensions into a Float32Array3D.
     */
    private static class Wrapper extends Float32Array3D
    {
        Float32Array array;

        public Wrapper(Float32Array array)
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
        public float getFloat(int x, int y, int z)
        {
            return this.array.getFloat(new int[] {x, y, z});
        }

        @Override
        public void setFloat(int x, int y, int z, float f)
        {
            this.array.setFloat(new int[] {x, y, z}, f);
        }

        @Override
        public float getFloat(int[] pos)
        {
            return this.array.getFloat(pos);
        }

        @Override
        public void setFloat(int[] pos, float value)
        {
            this.array.setFloat(pos, value);
        }

        /**
         * Simply returns an iterator on the original array.
         */
        @Override
        public Float32Array.Iterator iterator()
        {
            return this.array.iterator();
        }
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
        public float getFloat(int x, int y)
        {
            return Float32Array3D.this.getFloat(x, y, sliceIndex);
        }
        
        @Override
        public void setFloat(int x, int y, float value)
        {
            Float32Array3D.this.setFloat(x, y, sliceIndex, value);
        }
        
        @Override
        public void setValue(int x, int y, double value)
        {
            Float32Array3D.this.setValue(x, y, this.sliceIndex, value);
        }
        
        @Override
        public void set(int x, int y, Float32 value)
        {
            Float32Array3D.this.set(x, y, this.sliceIndex, value);
        }

        @Override
        public float getFloat(int[] pos)
        {
            return Float32Array3D.this.getFloat(pos[0], pos[1], sliceIndex);
        }

        @Override
        public void setFloat(int[] pos, float value)
        {
            Float32Array3D.this.setFloat(pos[0], pos[1], sliceIndex, value);
        }

        @Override
        public double getValue(int[] pos)
        {
            return Float32Array3D.this.getValue(pos[0], pos[1], sliceIndex);
        }

        @Override
        public void setValue(int[] pos, double value)
        {
            Float32Array3D.this.setValue(pos[0], pos[1], sliceIndex, value);
        }

        @Override
        public net.sci.array.numeric.Float32Array.Iterator iterator()
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
            public float getFloat()
            {
                return Float32Array3D.this.getFloat(indX, indY, sliceIndex);
            }

            @Override
            public void setFloat(float value)
            {
                Float32Array3D.this.setFloat(indX, indY, sliceIndex, value);
            }
            
            @Override
            public double getValue()
            {
                return Float32Array3D.this.getValue(indX, indY, sliceIndex);
            }

            @Override
            public void setValue(double value)
            {
                Float32Array3D.this.setValue(indX, indY, sliceIndex, value);
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
