/**
 * 
 */
package net.sci.array.scalar;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 */
public class UInt16Test
{

    /**
     * Test method for {@link net.sci.array.scalar.UInt16#UInt16(int)}.
     */
    @Test
    public final void test_constructorFromInt()
    {
        UInt16 data = new UInt16(-10);
        assertEquals(0, data.getInt());
    }

}
