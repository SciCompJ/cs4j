/**
 * 
 */
package net.sci.array.color;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import net.sci.array.scalar.UInt8Array2D;

/**
 * @author dlegland
 *
 */
class ScalarArraysDifferenceViewTest
{

    /**
     * Test method for {@link net.sci.array.color.ScalarArraysDifferenceView#ScalarArraysDifferenceView(net.sci.array.scalar.UInt8Array, net.sci.array.scalar.UInt8Array)}.
     */
    @Test
    final void testScalarArraysDifferenceView()
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
        
        assertEquals(RGB8.BLACK, rgb.get(10, 10));
        assertEquals(RGB8.BLACK, rgb.get(90, 90));
        assertEquals(RGB8.WHITE, rgb.get(50, 50));
        assertEquals(RGB8.MAGENTA, rgb.get(70, 30));
        assertEquals(RGB8.GREEN, rgb.get(30, 70));
    }

}
