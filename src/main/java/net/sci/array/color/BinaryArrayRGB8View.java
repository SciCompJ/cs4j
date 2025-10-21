/**
 * 
 */
package net.sci.array.color;

import java.util.Collection;
import java.util.List;

import net.sci.array.Array;
import net.sci.array.binary.BinaryArray;

/**
 * Wraps a binary array into a RGB8 array the same size, by associating
 * false/true value to background/foreground colors.
 * 
 * @author dlegland
 *
 */
public class BinaryArrayRGB8View implements RGB8Array, Array.View<RGB8>
{
    // =============================================================
    // Class variables
    
    /**
     * The binary array that will be converted to RGB8.
     */
    BinaryArray baseArray;
    
    /**
     * The color associated to <code>false</code> elements.
     */
    RGB8 bgColor;
    
    /**
     * The color associated to <code>true</code> elements.
     */
    RGB8 fgColor;
    
    
    // =============================================================
    // Constructors

    /**
     * Creates a new view on a binary array, by using black color for
     * <code>false</code> elements and white color for <code>true</code>
     * elements.
     * 
     * @param array
     *            the binary array to wrap
     * @param bgColor
     *            the color associated to <code>false</code> elements
     * @param fgColor
     *            the color associated to <code>true</code> elements
     */
    public BinaryArrayRGB8View(BinaryArray array)
    {
        this(array, RGB8.BLACK, RGB8.WHITE);
    }
    
    /**
     * Creates a new view on a binary array, by specifying the colors that will
     * be associated to the <code>false</code> elements (background) and
     * <code>true</code> elements (foreground).
     * 
     * @param array
     *            the binary array to wrap
     * @param bgColor
     *            the color associated to <code>false</code> elements
     * @param fgColor
     *            the color associated to <code>true</code> elements
     */
    public BinaryArrayRGB8View(BinaryArray array, RGB8 bgColor, RGB8 fgColor)
    {
        this.baseArray = array;
        this.bgColor = bgColor;
        this.fgColor = fgColor;
    }
    
    
    // =============================================================
    // Methods implementing the Array.View interface

    @Override
    public Collection<Array<?>> parentArrays()
    {
        return List.of(baseArray);
    }   
    
    
    // =============================================================
    // Methods implementing the Array interface

    @Override
    public RGB8 get(int[] pos)
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
