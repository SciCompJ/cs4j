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
public class UInt16Test
{

    /**
     * Test method for {@link net.sci.array.numeric.UInt16#UInt16(int)}.
     */
    @Test
    public final void test_constructorFromInt()
    {
        UInt16 data = new UInt16(-10);
        assertEquals(0, data.getInt());
    }

    /**
     * Test method for {@link net.sci.array.numeric.UInt16#compareTo(net.sci.array.numeric.UInt16)}.
     */
    @Test
    public final void testCompareTo()
    {
        UInt16 v1 = new UInt16(500);
        UInt16 v2 = new UInt16(40000);
        UInt16 v3 = new UInt16(500);
        
        assertTrue(v1.compareTo(v2) < 0);
        assertTrue(v2.compareTo(v1) > 0);
        assertTrue(v1.compareTo(v3) == 0);
    }

    /**
     * Test method for {@link net.sci.array.numeric.UInt16#plus(net.sci.array.numeric.UInt16)}.
     */
    @Test
    public final void testPlus()
    {
        UInt16 v1 = new UInt16(30_000);
        UInt16 v2 = new UInt16(40_000);
        
        UInt16 res = v1.plus(v2);
        
        assertEquals(UInt16.MAX_INT, res.getInt());
    }

    /**
     * Test method for {@link net.sci.array.numeric.UInt16#minus(net.sci.array.numeric.UInt16)}.
     */
    @Test
    public final void testMinus()
    {
        UInt16 v1 = new UInt16(50_000);
        UInt16 v2 = new UInt16(30_000);
        
        UInt16 res = v1.minus(v2);
        
        assertEquals(20_000, res.getInt());
    }

    /**
     * Test method for {@link net.sci.array.numeric.UInt16#minus(net.sci.array.numeric.UInt16)}.
     */
    @Test
    public final void testMinus_clamp()
    {
        UInt16 v1 = new UInt16(120);
        UInt16 v2 = new UInt16(150);
        
        UInt16 res = v1.minus(v2);
        
        assertEquals(0, res.getInt());
    }

    /**
     * Test method for {@link net.sci.array.numeric.UInt16#times(double)}.
     */
    @Test
    public final void testTimes()
    {
        UInt16 v1 = new UInt16(50_000);
        
        UInt16 res = v1.times(2);
        
        assertEquals(UInt16.MAX_INT, res.getInt());
    }

    /**
     * Test method for {@link net.sci.array.numeric.UInt16#divideBy(double)}.
     */
    @Test
    public final void testDivideBy()
    {
        UInt16 v1 = new UInt16(40_000);
        
        UInt16 res = v1.divideBy(2);
        
        assertEquals(20_000, res.getInt());
    }


}
