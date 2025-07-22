/**
 * 
 */
package net.sci.array.color;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 */
public class ColorMapsTest
{
    /**
     * Test method for {@link net.sci.array.color.ColorMaps#interpolate(net.sci.array.color.ColorMap, int)}.
     */
    @Test
    public final void test_GRAY()
    {
        ColorMap map = ColorMaps.GRAY.createColorMap(256);
        
        assertEquals(256, map.size());
        assertEquals(RGB8.BLACK, map.getColor(0));
        assertEquals(RGB8.WHITE, map.getColor(255));
    }
    
    /**
     * Test method for {@link net.sci.array.color.ColorMaps#interpolate(net.sci.array.color.ColorMap, int)}.
     */
    @Test
    public final void test_HSV()
    {
        ColorMap map = ColorMaps.HSV.createColorMap(360);
        
        assertEquals(360, map.size());
        assertEquals(RGB8.RED, map.getColor(0));
        assertEquals(RGB8.YELLOW, map.getColor(60));
        assertEquals(RGB8.GREEN, map.getColor(120));
        assertEquals(RGB8.CYAN, map.getColor(180));
        assertEquals(RGB8.BLUE, map.getColor(240));
        assertEquals(RGB8.MAGENTA, map.getColor(300));
    }
}
