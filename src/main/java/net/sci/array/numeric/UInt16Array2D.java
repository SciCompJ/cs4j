/**
 * 
 */
package net.sci.array.numeric;

import java.util.Collection;
import java.util.List;

import net.sci.array.Array;
import net.sci.array.numeric.impl.BufferedUInt16Array2D;

/**
 * @author dlegland
 *
 */
public abstract class UInt16Array2D extends IntArray2D<UInt16> implements UInt16Array
{
    // =============================================================
    // Static methods

    /**
     * @param size0
     *            the size of the array along the first dimension
     * @param size1
     *            the size of the array along the second dimension
     * @return a new instance of UInt16Array2D
     */
    public static final UInt16Array2D create(int size0, int size1)
    {
        return wrap(UInt16Array.create(size0, size1));
    }

    /**
     * Wraps the short array into an instance of UInt16Array2D with the
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
     * @return a new instance of UInt16Array2D
     */
    public static final UInt16Array2D wrap(short[] buffer, int size0, int size1)
    {
        return new BufferedUInt16Array2D(size0, size1, buffer);
    }

    /**
     * Creates a new UInt16Array2D from a two-dimensional array of integers. The
     * first index of the int array is the second dimension of the result array,
     * i.e. <code>intArray[y][x]</code> is the same value as
     * <code>array.getInt(x,y)</code>.
     * 
     * @param intArray
     *            the array of integers containing the values.
     * @return a new instance of UInt16Array2D initialized with the values of
     *         <code>intArray</code>
     */
    public static final UInt16Array2D fromIntArray(int[][] intArray)
    {
        int size1 = intArray.length;
        int size0 = intArray[0].length;
        UInt16Array2D res = UInt16Array2D.create(size0, size1);
        for (int y = 0; y < size1; y++)
        {
            for (int x = 0; x < size0; x++)
            {
                res.setInt(x, y, intArray[y][x]);
            }
        }
        return res;
    }
    
    
    /**
     * Encapsulates the specified instance of UInt16Array into a new
     * UInt16Array2D, by creating a Wrapper if necessary. If the original array
     * is already an instance of UInt16Array2D, it is returned.
     * 
     * @param array
     *            the original array
     * @return a UInt16Array2D view of the original array
     */
    public static UInt16Array2D wrap(UInt16Array array)
    {
        if (array instanceof UInt16Array2D)
        { 
            return (UInt16Array2D) array; 
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
     */
    protected UInt16Array2D(int size0, int size1)
    {
        super(size0, size1);
    }

    
    // =============================================================
    // New methods
    
    public abstract short getShort(int x, int y);

    public abstract void setShort(int x, int y, short s);

    
    // =============================================================
    // Specialization of the IntArray2D interface

    @Override
    public int getInt(int x, int y)
    {
        return getShort(x, y) & 0x00FFFF;
    }

    @Override
    public void setInt(int x, int y, int value)
    {
        setShort(x, y, (short) UInt16.clamp(value));
    }

    
    // =============================================================
    // Specialization of the Int16Array interface

    public short getShort(int [] pos)
    {
        return getShort(pos[0], pos[1]);
    }

    public void setShort(int [] pos, short s)
    {
        setShort(pos[0], pos[1], s);
    }


    // =============================================================
    // Specialization of Array2D interface

    @Override
    public UInt16 get(int x, int y)
    {
        return new UInt16(getShort(x, y));
    }
    
    @Override
    public void set(int x, int y, UInt16 value)
    {
        setShort(x, y, value.value);
    }

    
    // =============================================================
    // Specialization of Array interface

    @Override
    public UInt16Array newInstance(int... dims)
    {
        return UInt16Array.create(dims);
    }

    @Override
    public UInt16Array2D duplicate()
    {
        // create output array
        UInt16Array2D res = UInt16Array2D.create(this.size0, this.size1);

        for (int y = 0; y < size1; y++)
        {
            for (int x = 0; x < size0; x++)
            {
                res.setShort(x, y, getShort(x, y));
            }
        }
        
        return res;
	}

    /**
     * Wraps a UInt16 array with two dimensions into a UInt16Array2D.
     */
    private static class Wrapper extends UInt16Array2D implements Array.View<UInt16>
    {
        UInt16Array array;

        public Wrapper(UInt16Array array)
        {
            super(0, 0);
            if (array.dimensionality() != 2)
            {
                throw new IllegalArgumentException("Requires an array of dimensionality equal to 2.");
            }
            this.size0 = array.size(0);
            this.size1 = array.size(1);
            this.array = array;
        }

        @Override
        public short getShort(int x, int y)
        {
            return this.array.getShort(new int[] {x, y});
        }

        @Override
        public void setShort(int x, int y, short b)
        {
            this.array.setShort(new int[] {x, y}, b);
        }

        @Override
        public short getShort(int[] pos)
        {
            return this.array.getShort(pos);
        }

        @Override
        public void setShort(int[] pos, short b)
        {
            this.array.setShort(pos, b);
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
        public net.sci.array.numeric.UInt16Array.Iterator iterator()
        {
            return this.array.iterator();
        }
    }
}
