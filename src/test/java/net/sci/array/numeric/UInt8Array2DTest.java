/**
 * 
 */
package net.sci.array.numeric;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.function.BiFunction;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class UInt8Array2DTest
{
    /**
     * Test method for {@link net.sci.array.numeric.UInt8Array2D#fromIntArray(int[][])}.
     */
    @Test
    public final void testFromIntArray()
    {
        int[][] values = new int[][] {{10, 11, 12, 13}, {20, 21, 22, 23}, {30, 31, 32, 33}};
        
        UInt8Array2D array = UInt8Array2D.fromIntArray(values);
        
        assertEquals(4, array.size(0));
        assertEquals(3, array.size(1));
        assertEquals(10, array.getInt(0, 0));
        assertEquals(33, array.getInt(3, 2));
    }

    /**
     * Test method for {@link net.sci.array.numeric.UInt8Array2D#fillInts(BiFunction)}.
     */
    @Test
    public final void test_fillInts_BiFunction()
    {
        UInt8Array2D array = UInt8Array2D.create(5, 4);
        
        array.fillInts((x, y) -> x + y * 10);
        
        assertEquals(0, array.getInt(0, 0));
        assertEquals(4, array.getInt(4, 0));
        assertEquals(34, array.getInt(4, 3));
    }
    
    @Test
    public final void test_values()
    {
        UInt8Array2D array = UInt8Array2D.create(10,  5);
        array.fillValue(10);
        
        int count = 0; 
        double sum = 0.0;
        for (double v : array.values())
        {
            count++;
            sum += v;
        }
        
        assertEquals(50, count);
        assertEquals(500.0, sum, 0.1);
    }
    
    @Test
    public final void test_toString()
    {
        IntArray2D<?> array = UInt8Array2D.create(5, 4);
        array.fillInts((x, y) -> x + y * 10);
        
        assertFalse(array.toString().isEmpty());
    }
    
}
