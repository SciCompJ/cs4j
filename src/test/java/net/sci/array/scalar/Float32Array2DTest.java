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
     * Test method for {@link net.sci.array.scalar.Float32Array2D#populateValues(java.util.function.BiFunction)}.
     */
    @Test
    public final void testPopulate()
    {
        Float32Array2D array = Float32Array2D.create(5, 5);
        array.populateValues((x, y) -> Math.hypot(x - 2, y - 2));

        assertEquals(0, array.getValue(2, 2), .1);
        assertEquals(2*Math.sqrt(2), array.getValue(0, 0), .1);
        assertEquals(2*Math.sqrt(2), array.getValue(0, 4), .1);
        assertEquals(2*Math.sqrt(2), array.getValue(4, 0), .1);
        assertEquals(2*Math.sqrt(2), array.getValue(4, 4), .1);
    }
    
}
