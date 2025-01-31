/**
 * 
 */
package net.sci.array.numeric;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 */
public class Float64Test
{

    /**
     * Test method for {@link net.sci.array.numeric.Float64#compareTo(net.sci.array.numeric.Float64)}.
     */
    @Test
    public final void testCompareTo()
    {
        Float64 v1 = new Float64(50.5);
        Float64 v2 = new Float64(2000.3);
        Float64 v3 = new Float64(50.5);
        Float64 v4 = new Float64(-100.8);
        
        assertTrue(v1.compareTo(v2) < 0);
        assertTrue(v2.compareTo(v1) > 0);
        assertTrue(v1.compareTo(v3) == 0);
        assertTrue(v1.compareTo(v4) > 0);
        assertTrue(v4.compareTo(v1) < 0);
    }
}
