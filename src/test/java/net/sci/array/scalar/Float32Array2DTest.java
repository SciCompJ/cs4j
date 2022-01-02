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
public class Float32Array2DTest
{
    
    /**
     * Test method for {@link net.sci.array.scalar.Float32Array2D#fillValues(java.util.function.BiFunction)}.
     */
    @Test
    public final void testFillValues()
    {
        Float32Array2D array = Float32Array2D.create(5, 5);
        array.fillValues((x, y) -> Math.hypot(x - 2, y - 2));

        assertEquals(0, array.getValue(2, 2), .1);
        assertEquals(2*Math.sqrt(2), array.getValue(0, 0), .1);
        assertEquals(2*Math.sqrt(2), array.getValue(0, 4), .1);
        assertEquals(2*Math.sqrt(2), array.getValue(4, 0), .1);
        assertEquals(2*Math.sqrt(2), array.getValue(4, 4), .1);
    }
    
    @Test
    public final void testValues()
    {
        Float32Array2D array = Float32Array2D.create(10, 5);
        array.fillValue(10.1);
        
        int count = 0; 
        double sum = 0.0;
        for (double v : array.values())
        {
            count++;
            sum += v;
        }
        
        assertEquals(50, count);
        assertEquals(505.0, sum, 0.1);
    }
}
