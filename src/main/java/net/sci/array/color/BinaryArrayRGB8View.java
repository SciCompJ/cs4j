/**
 * 
 */
package net.sci.array.color;

import net.sci.array.binary.BinaryArray;

/**
 * Wraps a binary array into a RGB8 array the same size, by associating
 * false/true value to Background/foreground colors.
 * 
 * @author dlegland
 *
 */
public class BinaryArrayRGB8View implements RGB8Array
{
    BinaryArray baseArray;
    
    RGB8 bgColor;
    
    RGB8 fgColor;
    
    public BinaryArrayRGB8View(BinaryArray array)
    {
        this(array, RGB8.BLACK, RGB8.WHITE);
    }
    
    public BinaryArrayRGB8View(BinaryArray array, RGB8 bgColor, RGB8 fgColor)
    {
        this.baseArray = array;
        this.bgColor = bgColor;
        this.fgColor = fgColor;
    }
    
    @Override
    public RGB8 get(int... pos)
    {
        return baseArray.getBoolean(pos) ? fgColor : bgColor;
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
