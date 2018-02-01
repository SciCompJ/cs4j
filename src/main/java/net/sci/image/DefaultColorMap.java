/**
 * 
 */
package net.sci.image;

import java.util.ArrayList;

import net.sci.array.type.Color;
import net.sci.array.type.RGB8;

/**
 * Color map that stores color in a fixed-size RGB8 Array.
 * @author dlegland
 *
 */
public class DefaultColorMap implements ColorMap
{
    ArrayList<Color> colors;
    
    /**
     * 
     */
    public DefaultColorMap(ArrayList<Color> colors)
    {
        this.colors = new ArrayList<Color>(colors.size());
        this.colors.addAll(colors);
    }

    /**
     * 
     */
    public DefaultColorMap(int[][] array)
    {
        this.colors = new ArrayList<Color>(array.length);
        for (int[] rgb : array)
        {
            this.colors.add(new RGB8(rgb[0], rgb[1], rgb[2]));
        }
    }

    /* (non-Javadoc)
     * @see net.sci.image.ColorMap#getColor(int)
     */
    @Override
    public Color getColor(int i)
    {
        return colors.get(i);
    }

    /* (non-Javadoc)
     * @see net.sci.image.ColorMap#size()
     */
    @Override
    public int size()
    {
        return colors.size();
    }

}
