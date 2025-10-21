/**
 * 
 */
package net.sci.array.numeric;

import java.util.Collection;
import java.util.List;

import net.sci.array.Array;
import net.sci.array.numeric.impl.BufferedInt32Array2D;

/**
 * Specialization of Array for 2D arrays of Int32 values.
 * 
 * @author dlegland
 *
 */
public abstract class Int32Array2D extends IntArray2D<Int32> implements Int32Array
{
	// =============================================================
	// Static methods

	public static final Int32Array2D create(int size0, int size1)
	{
	    return wrap(Int32Array.create(size0, size1));
	}
	
    /**
     * Wraps the int array into an instance of Int32Array2D with the specified
     * dimensions. The new array will be backed by the given int array; that is,
     * modifications to the int buffer will cause the array to be modified and
     * vice versa.
     * 
     * The number of elements of the buffer must be at least the product of
     * array dimensions.
     * 
     * @param buffer
     *            the array to int to encapsulate
     * @param size0
     *            the size of the array along the first dimension
     * @param size1
     *            the size of the array along the second dimension
     * @return a new instance of Int32Array2D
     */
    public static final Int32Array2D wrap(int[] buffer, int size0, int size1)
    {
        return new BufferedInt32Array2D(size0, size1, buffer);
    }

    /**
     * Creates a new Int32Array2D from a two-dimensional array of integers. The
     * first index of the int array is the second dimension of the result array,
     * i.e. <code>intArray[y][x]</code> is the same value as
     * <code>array.getInt(x,y)</code>.
     * 
     * @param intArray
     *            the array of integers containing the values.
     * @return a new instance of Int32Array2D initialized with the values of
     *         <code>intArray</code>
     */
    public static final Int32Array2D fromIntArray(int[][] intArray)
    {
        int size1 = intArray.length;
        int size0 = intArray[0].length;
        Int32Array2D res = Int32Array2D.create(size0, size1);
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
     * Encapsulates the specified instance of Int32Array into a new
     * Int32Array2D, by creating a Wrapper if necessary. If the original array
     * is already an instance of Int32Array3D, it is returned.
     * 
     * @param array
     *            the original array
     * @return a Int32Array2D view of the original array
     */
    public static Int32Array2D wrap(Int32Array array)
    {
        if (array instanceof Int32Array2D)
        { 
            return (Int32Array2D) array; 
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
	protected Int32Array2D(int size0, int size1)
	{
		super(size0, size1);
	}

	
	// =============================================================
	// Specialization of Array2D interface

	// =============================================================
	// Specialization of Array interface
	
    @Override
    public Int32Array2D duplicate()
    {
        // create output array
        Int32Array2D res = Int32Array2D.create(this.size0, this.size1);

        for (int y = 0; y < size1; y++)
        {
            for (int x = 0; x < size0; x++)
            {
                res.setInt(x, y, getInt(x, y));
            }
        }
        
        return res;
    }

    @Override
    public Int32 get(int x, int y)
    {
        return new Int32(getInt(x, y));
    }
    
    @Override
    public void set(int x, int y, Int32 value)
    {
        setInt(x, y, value.value);
    }
    
    
    // =============================================================
    // Implementation of inner classes
    
    /**
     * Wraps a Int32 array with two dimensions into a Int32Array2D.
     */
    private static class Wrapper extends Int32Array2D implements Array.View<Int32>
    {
        Int32Array array;

        public Wrapper(Int32Array array)
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
        public int getInt(int x, int y)
        {
            return this.array.getInt(new int[] {x, y});
        }

        @Override
        public void setInt(int x, int y, int intValue)
        {
            this.array.setInt(new int[] {x, y}, intValue);
        }

        @Override
        public int getInt(int[] pos)
        {
            return this.array.getInt(pos);
        }

        @Override
        public void setInt(int[] pos, int value)
        {
            this.array.setInt(pos, value);
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
        public net.sci.array.numeric.Int32Array.Iterator iterator()
        {
            return this.array.iterator();
        }
    }
}
