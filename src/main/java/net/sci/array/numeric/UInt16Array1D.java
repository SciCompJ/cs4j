/**
 * 
 */
package net.sci.array.numeric;

import java.util.Collection;
import java.util.List;

import net.sci.array.Array;

/**
 * Specialization of Array for 1D arrays of UInt16 values.
 * 
 * @author dlegland
 *
 */
public abstract class UInt16Array1D extends IntArray1D<UInt16> implements UInt16Array
{
	// =============================================================
	// Static methods

	public static final UInt16Array1D create(int size0)
	{
	    return wrap(UInt16Array.create(size0));
	}
	
    /**
     * Creates a new UInt16Array1D from an array of integers. 
     * 
     * @param intArray
     *            the array of integers containing the values.
     * @return a new instance of UInt16Array1D initialized with the values of
     *         <code>intArray</code>
     */
    public static final UInt16Array1D fromIntArray(int[] intArray)
    {
        int size0 = intArray.length;
        UInt16Array1D res = UInt16Array1D.create(size0);
        for (int x = 0; x < size0; x++)
        {
            res.setInt(x, intArray[x]);
        }
        return res;
    }
    
    /**
     * Encapsulates the specified instance of UInt16Array into a new
     * UInt16Array1D, by creating a Wrapper if necessary. If the original array
     * is already an instance of UInt16Array1D, it is returned.
     * 
     * @param array
     *            the original array
     * @return a UInt16Array1D view of the original array
     */
    public static UInt16Array1D wrap(UInt16Array array)
    {
        if (array instanceof UInt16Array1D)
        { 
            return (UInt16Array1D) array; 
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
	protected UInt16Array1D(int size0)
	{
		super(size0);
	}
	
	
    // =============================================================
    // New method(s)
    
    public abstract short getShort(int pos);
    
    public abstract void setShort(int pos, short value);
    
    
    // =============================================================
    // Specialization of UInt16Array interface
    
    public short getShort(int[] pos)
    {
        return getShort(pos[0]);
    }
    
    public void setShort(int pos[], short value)
    {
        setShort(pos[0], value);
    }
    
    
    // =============================================================
    // Specialization of the IntArray1D interface

    @Override
    public int getInt(int x)
    {
        return getShort(x) & 0x00FFFF;
    }

    @Override
    public void setInt(int x, int value)
    {
        setShort(x, (short) UInt16.clamp(value));
    }

    
	// =============================================================
	// Specialization of Array interface
	
    @Override
    public UInt16Array1D duplicate()
    {
        // create output array
        UInt16Array1D res = UInt16Array1D.wrap(this.factory().create(this.size0));
        for (int x = 0; x < size0; x++)
        {
            res.setInt(x, getInt(x));
        }
        return res;
    }
    
    @Override
    public UInt16 get(int x)
    {
        return new UInt16(getShort(x));
    }

    @Override
    public void set(int x, UInt16 value)
    {
        setInt(x, value.value);
    }
    
    
    // =============================================================
    // Implementation of inner classes
    
    /**
     * Wraps a UInt16 array with two dimensions into a UInt16Array1D.
     */
    private static class Wrapper extends UInt16Array1D implements Array.View<UInt16>
    {
        UInt16Array array;

        public Wrapper(UInt16Array array)
        {
            super(0);
            this.size0 = array.size(0);
            this.array = array;
        }
        
        @Override
        public short getShort(int pos)
        {
            return this.array.getShort(new int[pos]);
        }

        @Override
        public void setShort(int pos, short value)
        {
            this.array.setShort(new int[pos], value);
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
        public void setInt(int x, int intValue)
        {
            this.array.setInt(new int[] {x}, intValue);
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
        public net.sci.array.numeric.UInt16Array.Iterator iterator()
        {
            return this.array.iterator();
        }
    }
}
