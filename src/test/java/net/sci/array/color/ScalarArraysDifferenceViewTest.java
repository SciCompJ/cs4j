/**
 * 
 */
package net.sci.array.color;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sci.array.numeric.UInt8Array;
import net.sci.array.numeric.UInt8Array2D;
import net.sci.array.numeric.impl.ScalarArrayUInt8View;

/**
 * @author dlegland
 *
 */
public class ScalarArraysDifferenceViewTest
{
    /**
     * Test method for {@link net.sci.array.color.ScalarArraysDifferenceView#ScalarArraysDifferenceView(net.sci.array.numeric.UInt8Array, net.sci.array.numeric.UInt8Array)}.
     */
    @Test
    public final void testScalarArraysDifferenceView()
    {
        UInt8Array2D array1 = UInt8Array2D.create(100, 100);
        UInt8Array2D array2 = UInt8Array2D.create(100, 100);
        
        for (int y = 0; y < 100; y++)
        {
            double y1 = y - 41.4;
            double y2 = y - 62.3;
            
            for (int x = 0; x < 100; x++)
            {
                double x1 = x - 60.7;
                double x2 = x - 39.6;
                
                if (Math.hypot(x1, y1) < 30.0)
                {
                    array1.setInt(x, y, 255);
                }
                if (Math.hypot(x2, y2) < 30.0)
                {
                    array2.setInt(x, y, 255);
                }
            }
        }
        
        RGB8Array rgb = new ScalarArraysDifferenceView(array1, array2);
        
        assertEquals(RGB8.BLACK, rgb.get(new int[] {10, 10}));
        assertEquals(RGB8.BLACK, rgb.get(new int[] {90, 90}));
        assertEquals(RGB8.WHITE, rgb.get(new int[] {50, 50}));
        assertEquals(RGB8.MAGENTA, rgb.get(new int[] {70, 30}));
        assertEquals(RGB8.GREEN, rgb.get(new int[] {30, 70}));
    }

    @Test
    public final void test_channel_method_return_ScalarArrayUInt8View()
    {
        UInt8Array2D array1 = UInt8Array2D.create(100, 100);
        array1.fillInts((x, y) -> 2 * x);
        UInt8Array2D array2 = UInt8Array2D.create(100, 100);
        array2.fillInts((x, y) -> 2 * y);
        
        RGB8Array rgb = new ScalarArraysDifferenceView(array1, array2);
        UInt8Array channel0 = rgb.channel(0);
        UInt8Array channel1 = rgb.channel(1);
        UInt8Array channel2 = rgb.channel(2);
        
        assertTrue(channel0 instanceof ScalarArrayUInt8View);
        assertTrue(channel1 instanceof ScalarArrayUInt8View);
        assertTrue(channel2 instanceof ScalarArrayUInt8View);
    }
 }
