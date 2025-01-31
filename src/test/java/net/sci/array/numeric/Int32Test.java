/**
 * 
 */
package net.sci.array.numeric;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * 
 */
public class Int32Test
{

    /**
     * Test method for {@link net.sci.array.numeric.Int32#convert(double)}.
     */
    @Test
    public final void testConvert()
    {
        assertEquals(Int32.convert(3.0), 3);
        assertEquals(Int32.convert(-3.0), -3);
    }

    /**
     * Test method for {@link net.sci.array.numeric.Int32#fromValue(double)}.
     */
    @Test
    public final void testFromValueDouble()
    {
        assertEquals(new Int32(0).fromValue(3.0), new Int32(3));
        assertEquals(new Int32(0).fromValue(-3.0), new Int32(-3));
    }

    /**
     * Test method for {@link net.sci.array.numeric.Int32#compareTo(net.sci.array.numeric.Int32)}.
     */
    @Test
    public final void testCompareTo()
    {
        Int32 v1 = new Int32(500);
        Int32 v2 = new Int32(200_000);
        Int32 v3 = new Int32(500);
        Int32 v4 = new Int32(-100_000);
        
        assertTrue(v1.compareTo(v2) < 0);
        assertTrue(v2.compareTo(v1) > 0);
        assertTrue(v1.compareTo(v3) == 0);
        assertTrue(v1.compareTo(v4) > 0);
        assertTrue(v4.compareTo(v1) < 0);
    }

}
