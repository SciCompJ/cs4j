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
public class UInt8Test
{

    /**
     * Test method for {@link net.sci.array.numeric.UInt8#UInt8(int)}.
     */
    @Test
    public final void test_constructorFromInt()
    {
        UInt8 data = new UInt8(-10);
        assertEquals(0, data.intValue());
    }

    /**
     * Test method for {@link net.sci.array.numeric.UInt8#compareTo(net.sci.array.numeric.UInt8)}.
     */
    @Test
    public final void testCompareTo()
    {
        UInt8 v1 = new UInt8(50);
        UInt8 v2 = new UInt8(200);
        UInt8 v3 = new UInt8(50);
        
        assertTrue(v1.compareTo(v2) < 0);
        assertTrue(v2.compareTo(v1) > 0);
        assertTrue(v1.compareTo(v3) == 0);
    }

    /**
     * Test method for {@link net.sci.array.numeric.UInt8#plus(net.sci.array.numeric.UInt8)}.
     */
    @Test
    public final void testPlus()
    {
        UInt8 v1 = new UInt8(120);
        UInt8 v2 = new UInt8(150);
        
        UInt8 res = v1.plus(v2);
        
        assertEquals(255, res.intValue());
    }

    /**
     * Test method for {@link net.sci.array.numeric.UInt8#minus(net.sci.array.numeric.UInt8)}.
     */
    @Test
    public final void testMinus()
    {
        UInt8 v1 = new UInt8(120);
        UInt8 v2 = new UInt8(150);
        
        UInt8 res = v1.minus(v2);
        
        assertEquals(0, res.intValue());
    }

    /**
     * Test method for {@link net.sci.array.numeric.UInt8#times(double)}.
     */
    @Test
    public final void testTimes()
    {
        UInt8 v1 = new UInt8(150);
        
        UInt8 res = v1.times(2);
        
        assertEquals(255, res.intValue());
    }

    /**
     * Test method for {@link net.sci.array.numeric.UInt8#divideBy(double)}.
     */
    @Test
    public final void testDivideBy()
    {
        UInt8 v1 = new UInt8(200);
        
        UInt8 res = v1.divideBy(2);
        
        assertEquals(100, res.intValue());
    }

}
