/**
 * 
 */
package net.sci.array.numeric;

import java.util.Locale;

import net.sci.array.Array2D;
import net.sci.array.numeric.impl.BufferedUInt8Array3D;

/**
 * @author dlegland
 *
 */
public abstract class UInt8Array3D extends IntArray3D<UInt8> implements UInt8Array
{
    // =============================================================
    // Static methods

    /**
     * Creates a new 3D array containing UInt8 values. Uses the default factory,
     * and a wrapper to UInt8Array3D if necessary.
     * 
     * @param size0
     *            the size of the array along the first dimension
     * @param size1
     *            the size of the array along the second dimension
     * @param size2
     *            the size of the array along the third dimension
     * @return a new instance of UInt8Array3D
     */
    public static final UInt8Array3D create(int size0, int size1, int size2)
    {
        return wrap(UInt8Array.create(size0, size1, size2));
    }

    /**
     * Wraps the byte array into an instance of UInt8Array3D with the specified
     * dimensions. The new array will be backed by the given byte array; that
     * is, modifications to the byte buffer will cause the array to be modified
     * and vice versa.
     * 
     * The number of elements of the buffer must be at least the product of
     * array dimensions.
     * 
     * @param buffer
     *            the array to byte to encapsulate
     * @param size0
     *            the size of the array along the first dimension
     * @param size1
     *            the size of the array along the second dimension
     * @param size2
     *            the size of the array along the third dimension
     * @return a new instance of UInt8Array2D
     */
    public static final UInt8Array3D wrap(byte[] buffer, int size0, int size1, int size2)
    {
        return new BufferedUInt8Array3D(size0, size1, size2, buffer);
    }

    /**
     * Encapsulates the instance of UInt8Array into a new UInt8Array3D, by
     * creating a Wrapper if necessary. If the original array is already an
     * instance of UInt8Array3D, it is returned.
     * 
     * @param array
     *            the original array
     * @return a UInt8Array3D view of the original array
     */
    public static UInt8Array3D wrap(UInt8Array array)
    {
        if (array instanceof UInt8Array3D)
        {
            return (UInt8Array3D) array;
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
    protected UInt8Array3D(int size0, int size1, int size2)
    {
        super(size0, size1, size2);
    }
    

    // =============================================================
    // New methods

    public abstract byte getByte(int x, int y, int z);

    public abstract void setByte(int x, int y, int z, byte b);
	
    // =============================================================
    // Management of slices

    public UInt8Array2D slice(int sliceIndex)
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
     *            the 2D array containing (UInt8) elements to replace.
     */
    @Override
    public void setSlice(int sliceIndex, Array2D<UInt8> slice)
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
        UInt8Array2D slice2 = UInt8Array2D.wrap(UInt8Array.wrap(slice));
        
        // iterate over elements of selected slice
        for (int y = 0; y < this.size1; y++)
        {
            for (int x = 0; x < this.size0; x++)
            {
                this.setByte(x, y, sliceIndex, slice2.getByte(x, y));
            }
        }
    }
    
    /**
     * Iterates over the slices
     * 
     * @return an iterator over 2D slices
     */
    public Iterable<? extends UInt8Array2D> slices()
    {
        return new Iterable<UInt8Array2D>()
        {
            @Override
            public java.util.Iterator<UInt8Array2D> iterator()
            {
                return new SliceIterator();
            }
        };
    }

    public java.util.Iterator<? extends UInt8Array2D> sliceIterator()
    {
        return new SliceIterator();
    }

    
    // =============================================================
    // Implementation of the UInt8Array3D interface

    /* (non-Javadoc)
     * @see net.sci.array.scalar.UInt8Array#setByte(int[], byte)
     */
    @Override
    public byte getByte(int[] pos)
    {
        return getByte(pos[0], pos[1], pos[2]);
    }
    
    /* (non-Javadoc)
     * @see net.sci.array.scalar.UInt8Array#setByte(int[], byte)
     */
    @Override
    public void setByte(int[] pos, byte b)
    {
        setByte(pos[0], pos[1], pos[2], b);
    }
    
   
    // =============================================================
    // Specialization of the IntArray3D interface

    @Override
    public int getInt(int x, int y, int z)
    {
        return getByte(x, y, z) & 0x00FF;
    }

    @Override
    public void setInt(int x, int y, int z, int value)
    {
        setByte(x, y, z, (byte) UInt8.clamp(value));
    }

    
    // =============================================================
    // Specialization of the ScalarArray3D interface

    @Override
    public void setValue(int x, int y, int z, double value)
    {
        setByte(x, y, z, (byte) UInt8.convert(value));
    }

    
    // =============================================================
    // Specialization of Array3D interface

    @Override
    public UInt8 get(int x, int y, int z)
    {
        return new UInt8(getByte(x, y, z));
    }

    @Override
    public void set(int x, int y, int z, UInt8 value)
    {
        setByte(x, y, z, value.value);
    }

    
    // =============================================================
    // Specialization of Array interface

    @Override
    public UInt8Array3D duplicate()
    {
        UInt8Array3D res = UInt8Array3D.create(size0, size1, size2);
        for (int z = 0; z < size2; z++)
        {
            for (int y = 0; y < size1; y++)
            {
                for (int x = 0; x < size0; x++)
                {
                    res.setByte(x, y, z, getByte(x, y, z));
                }
            }
        }
        return res;
    }
    
	
    // =============================================================
    // Implementation of inner classes
    
    /**
     * Wraps a UInt8 array with three dimensions into a UInt8Array3D.
     */
    private static class Wrapper extends UInt8Array3D
    {
        UInt8Array array;
    
        public Wrapper(UInt8Array array)
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
        public byte getByte(int x, int y, int z)
        {
            return this.array.getByte(new int[] {x, y, z});
        }
    
        @Override
        public void setByte(int x, int y, int z, byte b)
        {
            this.array.setByte(new int[] {x, y, z}, b);
        }
    
        @Override
        public byte getByte(int[] pos)
        {
            return this.array.getByte(pos);
        }
    
        @Override
        public void setByte(int[] pos, byte value)
        {
            this.array.setByte(pos, value);
        }
    
        /**
         * Simply returns an iterator on the original array.
         */
        @Override
        public net.sci.array.numeric.UInt8Array.Iterator iterator()
        {
            return this.array.iterator();
        }
    }

    private class SliceView extends UInt8Array2D
    {
        int sliceIndex;
        
        protected SliceView(int slice)
        {
            super(UInt8Array3D.this.size0, UInt8Array3D.this.size1);
            if (slice < 0 || slice >= UInt8Array3D.this.size2)
            {
                throw new IllegalArgumentException(String.format(
                        "Slice index %d must be comprised between 0 and %d", slice, UInt8Array3D.this.size2));
            }
            this.sliceIndex = slice;
        }

        @Override
        public byte getByte(int x, int y)
        {
            return UInt8Array3D.this.getByte(x, y, this.sliceIndex);
        }

        @Override
        public void setByte(int x, int y, byte b)
        {
            UInt8Array3D.this.setByte(x, y, this.sliceIndex, b);
        }

        @Override
        public byte getByte(int[] pos)
        {
            return UInt8Array3D.this.getByte(pos[0], pos[1], this.sliceIndex);
        }

        @Override
        public void setByte(int[] pos, byte b)
        {
            UInt8Array3D.this.setByte(pos[0], pos[1], this.sliceIndex, b);
        }

        @Override
        public net.sci.array.numeric.UInt8Array.Iterator iterator()
        {
            return new Iterator();
        }

        class Iterator implements UInt8Array.Iterator
        {
            int indX = -1;
            int indY = 0;
            
            public Iterator() 
            {
            }
            
            @Override
            public UInt8 next()
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
            public byte getByte()
            {
                return UInt8Array3D.this.getByte(indX, indY, sliceIndex);
            }

            @Override
            public void setByte(byte b)
            {
                UInt8Array3D.this.setByte(indX, indY, sliceIndex, b);
            }
        }
    }
    
    private class SliceIterator implements java.util.Iterator<UInt8Array2D> 
    {
        int sliceIndex = 0;

        @Override
        public boolean hasNext()
        {
            return sliceIndex < UInt8Array3D.this.size2;
        }

        @Override
        public UInt8Array2D next()
        {
            return new SliceView(sliceIndex++);
        }
    }
}
