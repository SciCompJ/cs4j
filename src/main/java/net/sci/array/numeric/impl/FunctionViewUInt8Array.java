/**
 * 
 */
package net.sci.array.numeric.impl;

import java.util.function.Function;

import net.sci.array.numeric.UInt8;
import net.sci.array.numeric.UInt8Array;

/**
 * Virtual array of UInt8 based on a function of the coordinates.
 * 
 * @author dlegland
 */
public class FunctionViewUInt8Array implements UInt8Array
{
    // =============================================================
    // Class members
    
    /** The size of the array */
    int [] dims;
    
    /**
     * The function that computes the value of an array element from its
     * coordinates. The value is computed as a double, and is automatically
     * wrapped into an UInt8 when required.
     */
    Function<int[], Double> fun;
    
    
    // =============================================================
    // Constructor
    
    /**
     * Creates a new virtual array by computing when requested the value from a
     * function of the coordinates.
     * 
     * @param dims
     *            the size of the virtual array
     * @param fun
     *            the function that associates the integer value of the array
     *            elements from its coordinates
     */
    public FunctionViewUInt8Array(int[] dims, Function<int[], Double> fun)
    {
        this.dims = dims;
        this.fun = fun;
    }
    
    
    // =============================================================
    // Implementation of the UInt8Array interface
    
    @Override
    public byte getByte(int[] pos)
    {
        // first convert to int, then to byte
        return (byte) UInt8.convert(this.fun.apply(pos));
    }

    @Override
    public void setByte(int[] pos, byte value)
    {
        throw new RuntimeException("Can not modify a virtual array.");
    }
    
    
    // =============================================================
    // Implementation of the IntArray interface
    
    @Override
    public int getInt(int[] pos)
    {
        return UInt8.convert(this.fun.apply(pos));
    }
    
    
    // =============================================================
    // Implementation of the Array interface
    
    /**
     * Returns false, as the view can not be modified.
     * 
     * @return false
     */
    public boolean isModifiable()
    {
        return false;
    }
    
    @Override
    public int dimensionality()
    {
        return this.dims.length;
    }

    @Override
    public int[] size()
    {
        return this.dims;
    }

    @Override
    public int size(int dim)
    {
        return this.dims[dim];
    }

}
