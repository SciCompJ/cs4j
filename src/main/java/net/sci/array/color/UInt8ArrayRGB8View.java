/**
 * 
 */
package net.sci.array.color;

import net.sci.array.scalar.UInt8Array;

/**
 * Wraps a UInt8 array into a RGB8 array the same size, by associating
 * values to gray colors based on the same intensity.
 * 
 * @author dlegland
 *
 */
public class UInt8ArrayRGB8View implements RGB8Array
{
    // =============================================================
    // Class variables
    
    UInt8Array baseArray;
    
    
    // =============================================================
    // Constructors

    /**
     * Creates a new view on the UInt8Array ,by converting each UInt8 element
     * into a RGB8 color representing the gray value of the element.
     * 
     * @param array
     *            the array to wrap.
     */
    public UInt8ArrayRGB8View(UInt8Array array)
    {
        this.baseArray = array;
    }
    
    
    // =============================================================
    // Methods implementing the RGB8Array interface

    @Override
    public int getMaxSample(int[] pos)
    {
        return baseArray.getInt(pos);
    }
    
    @Override
    public int getIntCode(int[] pos)
    {
        int gray = baseArray.getInt(pos);
        return RGB8.intCode(gray, gray, gray);
    }

    
    // =============================================================
    // Methods implementing the VectorArray and IntVectorArray interfaces

    @Override
    public int getSample(int[] pos, int channel)
    {
        return baseArray.getInt(pos);
    }

    @Override
    public double getValue(int[] pos, int channel)
    {
        return baseArray.getInt(pos);
    }
    

    // =============================================================
    // Methods implementing the Array interface

    @Override
    public RGB8 get(int... pos)
    {
        int gray = baseArray.getInt(pos);
        return new RGB8(gray, gray, gray);
    }

    @Override
    public void set(int[] pos, RGB8 value)
    {
        throw new RuntimeException("Can not modify values of a view class");
    }

    @Override
    public int[] size()
    {
        return baseArray.size();
    }

    @Override
    public int size(int dim)
    {
        return baseArray.size(dim);
    }

    @Override
    public int dimensionality()
    {
        return baseArray.dimensionality();
    }

}
