/**
 * 
 */
package net.sci.image;

import java.util.ArrayList;

import net.sci.array.type.RGB8;

/**
 * A collection of color maps.
 * @author dlegland
 */
public class ColorMaps
{
    public static final ColorMapFactory RED = new ColorMapFactory()
    {
        @Override
        public ColorMap createColorMap(int nColors)
        {
            ArrayList<RGB8> colors = new ArrayList<RGB8>(nColors);
            for (int i = 0; i < nColors; i++)
            {
                colors.add(new RGB8(i * 255.0 / nColors, 0, 0));
            }
            return new DefaultColorMap(colors);
        }
    };
    
    public static final ColorMapFactory GREEN = new ColorMapFactory()
    {
        @Override
        public ColorMap createColorMap(int nColors)
        {
            ArrayList<RGB8> colors = new ArrayList<RGB8>(nColors);
            for (int i = 0; i < nColors; i++)
            {
                colors.add(new RGB8(0, i * 255.0 / nColors, 0));
            }
            return new DefaultColorMap(colors);
        }
    };
    
    public static final ColorMapFactory BLUE = new ColorMapFactory()
    {
        @Override
        public ColorMap createColorMap(int nColors)
        {
            ArrayList<RGB8> colors = new ArrayList<RGB8>(nColors);
            for (int i = 0; i < nColors; i++)
            {
                colors.add(new RGB8(0, 0, i * 255.0 / nColors));
            }
            return new DefaultColorMap(colors);
        }
    };
    
    public static final ColorMapFactory GRAY = new ColorMapFactory()
    {
        @Override
        public ColorMap createColorMap(int nColors)
        {
            ArrayList<RGB8> colors = new ArrayList<RGB8>(nColors);
            colors.add(new RGB8(0, 0, 255));
            for (int i = 1; i < nColors-1; i++)
            {
                int gray = (int) (i * 255.0 / nColors);
                colors.add(new RGB8(gray, gray, gray));
            }
            colors.add(new RGB8(255, 0, 0));
            return new DefaultColorMap(colors);
        }
    };

    public static final ColorMapFactory BLUE_GRAY_RED = new ColorMapFactory()
    {
        @Override
        public ColorMap createColorMap(int nColors)
        {
            ArrayList<RGB8> colors = new ArrayList<RGB8>(nColors);
            colors.add(new RGB8(0, 0, 255));
            for (int i = 1; i < nColors-1; i++)
            {
                int gray = (int) (i * 255.0 / nColors);
                colors.add(new RGB8(gray, gray, gray));
            }
            colors.add(new RGB8(255, 0, 0));
            return new DefaultColorMap(colors);
        }
    };
    
    /**
     * Private constructor to prevent instantiation
     */
    private ColorMaps()
    {
    }

}
