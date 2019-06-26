/**
 * 
 */
package net.sci.array.scalar;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class ScalarArrayUInt8ViewTest
{
    @Test
    public final void testConvertFloat32Array2D()
    {
        ScalarArray2D<?> array = Float32Array2D.create(100, 100);
        for (int y = 0; y < 100; y++)
        {
            for (int x = 0; x < 100; x++)
            {
                array.setValue(x,  y, x+y);
            }
        }
        
        UInt8Array res = new ScalarArrayUInt8View(array, 0, 200-2);
        
        assertEquals(100, res.size(0));
        assertEquals(100, res.size(1));
        
        double[] range = res.valueRange();
        assertEquals(0, range[0], .01);
        assertEquals(255, range[1], .01);
    }
}
