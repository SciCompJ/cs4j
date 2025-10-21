/**
 * 
 */
package net.sci.array.numeric;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import net.sci.array.Array;
import net.sci.array.Array2D;
import net.sci.array.numeric.impl.BufferedInt16Array3D;

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
     * Wraps the short array into an instance of Int16Array3D with the
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
     * @return a new instance of Int16Array2D
     */
    public static final Int16Array3D wrap(short[] buffer, int size0, int size1, int size2)
    {
        return new BufferedInt16Array3D(size0, size1, size2, buffer);
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
    
    public abstract short getShort(int x, int y, int z);

    public abstract void setShort(int x, int y, int z, short s);


    // =============================================================
    // Specialization of the IntArray3D interface

    @Override
    public int getInt(int x, int y, int z)
    {
        return getShort(x, y, z);
    }

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
        setShort(x, y, z, (short) Int16.convert(value));
    }
    
    
    // =============================================================
    // Specialization of Array3D interface

    @Override
    public Int16 get(int x, int y, int z)
    {
        return new Int16(getShort(x, y, z));
    }
    
    @Override
    public void set(int x, int y, int z, Int16 value)
    {
        setShort(x, y, z, value.value);
    }

    
    // =============================================================
    // Specialization of the Int16Array interface

    public short getShort(int [] pos)
    {
        return getShort(pos[0], pos[1], pos[2]);
    }
    
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
     * Overrides the default implementation for <code>setSlice</code> by relying
     * on the lowest level data access and modifier methods.
     *
     * @param sliceIndex
     *            the slice index of elements to replace
     * @param slice
     *            the 2D array containing (Int16) elements to replace.
     */
    @Override
    public void setSlice(int sliceIndex, Array2D<Int16> slice)
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
        Int16Array2D slice2 = Int16Array2D.wrap(Int16Array.wrap(slice));
        
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
    private static class Wrapper extends Int16Array3D implements Array.View<Int16>
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

        @Override
        public Collection<Array<?>> parentArrays()
        {
            return List.of(array);
        }
        
        /**
         * Simply returns an iterator on the original array.
         */
        @Override
        public net.sci.array.numeric.Int16Array.Iterator iterator()
        {
            return this.array.iterator();
        }
    }

	
    private class SliceView extends Int16Array2D implements Array.View<Int16>
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
        public short getShort(int x, int y)
        {
            return Int16Array3D.this.getShort(x, y, this.sliceIndex);            
        }

        @Override
        public void setShort(int x, int y, short value)
        {
            Int16Array3D.this.setShort(x, y, this.sliceIndex, value);            
        }

        @Override
        public Collection<Array<?>> parentArrays()
        {
            return List.of(Int16Array3D.this);
        }
        
        @Override
        public net.sci.array.numeric.Int16Array.Iterator iterator()
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
