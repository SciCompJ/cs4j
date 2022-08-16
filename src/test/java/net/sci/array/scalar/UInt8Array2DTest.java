/**
 * 
 */
package net.sci.array.scalar;

import static org.junit.Assert.assertEquals;

import java.util.function.BiFunction;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class UInt8Array2DTest
{
    /**
     * Test method for {@link net.sci.array.scalar.UInt8Array2D#fillInts(BiFunction)}.
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
        
        System.out.println(array);
    }
    
}
