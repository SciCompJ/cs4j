/**
 * 
 */
package net.sci.array.numeric;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 */
public class Float32Test
{

    /**
     * Test method for {@link net.sci.array.numeric.Float32#compareTo(net.sci.array.numeric.Float32)}.
     */
    @Test
    public final void testCompareTo()
    {
        Float32 v1 = new Float32(50.5f);
        Float32 v2 = new Float32(2000.3f);
        Float32 v3 = new Float32(50.5f);
        Float32 v4 = new Float32(-100.8f);
        
        assertTrue(v1.compareTo(v2) < 0);
        assertTrue(v2.compareTo(v1) > 0);
        assertTrue(v1.compareTo(v3) == 0);
        assertTrue(v1.compareTo(v4) > 0);
        assertTrue(v4.compareTo(v1) < 0);
    }
}
