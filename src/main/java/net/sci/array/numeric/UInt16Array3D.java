/**
 * 
 */
package net.sci.array.numeric;

import java.util.Locale;

import net.sci.array.Array2D;
import net.sci.array.numeric.impl.BufferedUInt16Array3D;
import net.sci.array.numeric.impl.SlicedUInt16Array3D;
import net.sci.util.MathUtils;

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
        if (MathUtils.prod(size0, size1, size2) < Integer.MAX_VALUE)
            return new BufferedUInt16Array3D(size0, size1, size2);
        else
            return new SlicedUInt16Array3D(size0, size1, size2);
    }

    /**
     * Wraps the short array into an instance of UInt16Array3D with the
     * specified dimensions. The new array will be backed by the given short
     * array; that is, modifications to the short buffer will cause the array to
     * be modified and vice versa.
     * 
     * The number of elements of the buffer must be at least the product of
     * array dimensions.
     * 
     * @param buffer
     *            the array to short to encapsulate
     * @param size0
     *            the size of the array along the first dimension
     * @param size1
     *            the size of the array along the second dimension
     * @param size2
     *            the size of the array along the third dimension
     * @return a new instance of UInt16Array3D
     */
    public static final UInt16Array3D wrap(short[] buffer, int size0, int size1, int size2)
    {
        return new BufferedUInt16Array3D(size0, size1, size2, buffer);
    }

    /**
     * Encapsulates the specified instance of UInt16Array into a new
     * UInt16Array3D, by creating a Wrapper if necessary. If the original array
     * is already an instance of UInt16Array3D, it is returned.
     * 
     * @param array
     *            the original array
     * @return a UInt16Array3D view of the original array
     */
    public static UInt16Array3D wrap(UInt16Array array)
    {
        if (array instanceof UInt16Array3D)
        { 
            return (UInt16Array3D) array; 
        }
        return new Wrapper(array);
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

    public abstract short getShort(int x, int y, int z);

    public abstract void setShort(int x, int y, int z, short s);

    
    // =============================================================
    // Specialization of the IntArray3D interface

    @Override
    public int getInt(int x, int y, int z)
    {
        return getShort(x, y, z) & 0x00FFFF;
    }

    @Override
    public void setInt(int x, int y, int z, int value)
    {
        setShort(x, y, z, (short) UInt16.clamp(value));
    }


    // =============================================================
    // Specialization of the ScalarArray3D interface


    @Override
    public void setValue(int x, int y, int z, double value)
    {
        setShort(x, y, z, (short) UInt16.convert(value));
    }

    // =============================================================
    // Specialization of Array3D interface

    @Override
    public UInt16 get(int x, int y, int z)
    {
        return new UInt16(getShort(x, y, z));
    }
    
    @Override
    public void set(int x, int y, int z, UInt16 value)
    {
        setShort(x, y, z, value.value);
    }


    // =============================================================
    // Specialization of the UInt16Array interface

    public short getShort(int[] pos)
    {
        return getShort(pos[0], pos[1], pos[2]);
    }

    public void setShort(int[] pos, short s)
    {
        setShort(pos[0], pos[1], pos[2], s);
    }

    
    // =============================================================
    // Management of slices

    public UInt16Array2D slice(int sliceIndex)
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
     *            the 2D array containing (UInt16) elements to replace.
     */
    @Override
    public void setSlice(int sliceIndex, Array2D<UInt16> slice)
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
        
        // wraps slice array into a Int32Array
        UInt16Array2D slice2 = UInt16Array2D.wrap(UInt16Array.wrap(slice));
        
        // iterate over elements of selected slice
        for (int y = 0; y < this.size1; y++)
        {
            for (int x = 0; x < this.size0; x++)
            {
                this.setShort(x, y, sliceIndex, slice2.getShort(x, y));
            }
        }
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
    // Specialization of Array interface

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
                    res.setShort(x, y, z, getShort(x, y, z));
                }
            }
        }
        return res;
    }

    @Override
    public UInt16Array newInstance(int... dims)
    {
        return UInt16Array.create(dims);
    }

    
    // =============================================================
    // Implementation of inner classes
    
    /**
     * Wraps a UInt16 array with three dimensions into a UInt16Array3D.
     */
    private static class Wrapper extends UInt16Array3D
    {
        UInt16Array array;

        public Wrapper(UInt16Array array)
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
        public short getShort(int x, int y, int z)
        {
            return this.array.getShort(new int[] {x, y, z});
        }

        @Override
        public void setShort(int x, int y, int z, short s)
        {
            this.array.setShort(new int[] {x, y, z}, s);
        }

        @Override
        public short getShort(int[] pos)
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
        public net.sci.array.numeric.UInt16Array.Iterator iterator()
        {
            return this.array.iterator();
        }
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
        public short getShort(int x, int y)
        {
            return UInt16Array3D.this.getShort(x, y, this.sliceIndex);
        }
        
        @Override
        public void setShort(int x, int y, short s)
        {
            UInt16Array3D.this.setShort(x, y, this.sliceIndex, s);
        }
        

        @Override
        public short getShort(int[] pos)
        {
            return UInt16Array3D.this.getShort(pos[0], pos[1], this.sliceIndex);
        }

        @Override
        public net.sci.array.numeric.UInt16Array.Iterator iterator()
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
                UInt16Array3D.this.setShort(indX, indY, sliceIndex, s);
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
