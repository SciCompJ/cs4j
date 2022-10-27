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
    UInt8Array baseArray;
    
    public UInt8ArrayRGB8View(UInt8Array array)
    {
        this.baseArray = array;
    }
    
    @Override
    public int getIntCode(int[] pos)
    {
        int gray = baseArray.getInt(pos);
        return RGB8.intCode(gray, gray, gray);
    }

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
