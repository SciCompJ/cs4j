/**
 * 
 */
package net.sci.array.scalar;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 */
public class Int16Test
{

    /**
     * Test method for {@link net.sci.array.scalar.Int16#Int16(int)}.
     */
    @Test
    public final void test_constructorFromInt()
    {
        Int16 data = new Int16(-10);
        assertEquals(-10, data.getInt());
    }

    /**
     * Test method for {@link net.sci.array.scalar.Int16#plus(net.sci.array.scalar.Int16)}.
     */
    @Test
    public final void testPlus()
    {
        Int16 v1 = new Int16(20_000);
        Int16 v2 = new Int16(15_000);
        
        Int16 res = v1.plus(v2);
        
        assertEquals(Int16.MAX_INT, res.getInt());
    }

    /**
     * Test method for {@link net.sci.array.scalar.Int16#minus(net.sci.array.scalar.Int16)}.
     */
    @Test
    public final void testMinus()
    {
        Int16 v1 = new Int16(20_000);
        Int16 v2 = new Int16(15_000);
        
        Int16 res = v1.minus(v2);
        
        assertEquals(5_000, res.getInt());
    }

    /**
     * Test method for {@link net.sci.array.scalar.Int16#minus(net.sci.array.scalar.Int16)}.
     */
    @Test
    public final void testMinus_clamp()
    {
        Int16 v1 = new Int16(120);
        Int16 v2 = new Int16(150);
        
        Int16 res = v1.minus(v2);
        
        assertEquals(-30, res.getInt());
    }

    /**
     * Test method for {@link net.sci.array.scalar.Int16#times(double)}.
     */
    @Test
    public final void testTimes()
    {
        Int16 v1 = new Int16(20_000);
        
        Int16 res = v1.times(2);
        
        assertEquals(Int16.MAX_INT, res.getInt());
    }

    /**
     * Test method for {@link net.sci.array.scalar.Int16#divideBy(double)}.
     */
    @Test
    public final void testDivideBy()
    {
        Int16 v1 = new Int16(20_000);
        
        Int16 res = v1.divideBy(2);
        
        assertEquals(10_000, res.getInt());
    }

}
