/**
 * 
 */
package net.sci.image;

import java.util.ArrayList;

import net.sci.array.type.Color;
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
    
    public static final ColorMapFactory JET = new ColorMapFactory()
    {
        @Override
        public ColorMap createColorMap(int nColors)
        {
            ArrayList<RGB8> colors = createJetColors();
            ColorMap baseMap = new DefaultColorMap(colors);
            if (nColors == 256)
                return baseMap;
            else
                return interpolate(baseMap, nColors);
        }
        
        private ArrayList<RGB8> createJetColors() {
            // create map
            ArrayList<RGB8> map = new ArrayList<RGB8>(256);
            
            // shade of dark blue to blue
            for (int i = 0; i < 32; i++)
            {
                map.add(new RGB8(0, 0, 127 + i * 4));
            }
            for (int i = 32; i < 96; i++) 
            { 
                map.add(new RGB8(0, (i - 32) * 4, 255));
            }
            for (int i = 96; i < 160; i++) 
            { 
                map.add(new RGB8((i - 96) * 4, 255, 255 - (i - 96) * 4));
            }
            for (int i = 160; i < 224; i++) 
            { 
                map.add(new RGB8(255, 255 - (i - 160) * 4, 0));
            }
            for (int i = 224; i < 256; i++) 
            { 
                map.add(new RGB8(255 - (i - 224) * 4, 0, 0));
            }
            return map;
        }
    };
    
    /**
     * Private constructor to prevent instantiation
     */
    private ColorMaps()
    {
    }

    /**
     * Interpolates a color map to ensure a given number of colors.
     * 
     * @param colorMap
     *            the original color map
     * @param nColors
     *            the number of colors of the new color map
     * @return the interpolated color map
     */
    public static final ColorMap interpolate(ColorMap colorMap, int nColors) 
    {
        // allocate memory for new colormap
        ArrayList<RGB8> newColors = new ArrayList<RGB8>(nColors);
        
        // linear interpolation of each color of new colormap
        int n0 = colorMap.size();
        for (int i = 0; i < nColors; i++) {
            // compute color index in original colormap
            float i0 = ((float) i) * n0 / nColors;
            int i1 = (int) Math.floor(i0);
            // the ratio between the two surrounding colors
            float f = i0 - i1;
            
            // the two surrounding colors
            Color col1 = colorMap.getColor(i1);
            Color col2 = colorMap.getColor(Math.min(i1 + 1, n0 - 1));

            newColors.add(interpolateRGB8(col1, 1-f, col2, f));
        }
        return new DefaultColorMap(newColors);
    }
    
    private static final RGB8 interpolateRGB8(Color color1, double w1, Color color2, double w2)
    {
        double sum = w1 + w2;
        double red = (color1.red() * w1 + color2.red() * w2) * 255 / sum;
        double green = (color1.green() * w1 + color2.green() * w2) * 255 / sum;
        double blue = (color1.blue() * w1 + color2.blue() * w2) * 255 / sum;
        return new RGB8(red, green, blue);
    }
}
