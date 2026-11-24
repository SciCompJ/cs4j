/**
 * 
 */
package net.sci.array;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.numeric.Float32;
import net.sci.array.numeric.Float32Array;
import net.sci.array.numeric.Float32Array2D;
import net.sci.array.numeric.UInt8Array2D;

/**
 * 
 */
public class ArrayUtilsTest
{
    /**
     * Test method for {@link net.sci.array.ArrayUtils#convert(net.sci.array.Array, java.util.function.Function)}.
     */
    @Test
    public final void test_convert_intArray_to_doubleArray()
    {
        UInt8Array2D array = UInt8Array2D.of(new int[][] {
            {1, 1, 0, 2, 2, 2},
            {1, 1, 1, 0, 2, 2},
            {0, 0, 0, 0, 2, 2},
            {3, 3, 3, 0, 0, 2},
            } );
        float[] values = new float[] {0.0f, 1.2f, 2.3f, 3.4f};
        
        Array<?> res = ArrayUtils.convert(array, uint8 -> new Float32(values[uint8.intValue()]));
        
        assertEquals(2, res.dimensionality());
        assertEquals(array.size(0), res.size(0));
        assertEquals(array.size(1), res.size(1));
        assertTrue(res.elementInstanceOf(Float32.class));
        
        Float32Array2D res2d = Float32Array2D.wrap(Float32Array.wrap(res)); 
        assertEquals(0.0, res2d.getValue(2, 0), 0.1);
        assertEquals(1.2, res2d.getValue(2, 1), 0.1);
        assertEquals(0.0, res2d.getValue(3, 2), 0.1);
        assertEquals(2.3, res2d.getValue(5, 3), 0.1);
        assertEquals(3.4, res2d.getValue(2, 3), 0.1);
    }

}
