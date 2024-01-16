/**
 * 
 */
package net.sci.array.scalar;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 */
public class UInt8Test
{

    /**
     * Test method for {@link net.sci.array.scalar.UInt8#UInt8(int)}.
     */
    @Test
    public final void test_constructorFromInt()
    {
        UInt8 data = new UInt8(-10);
        assertEquals(0, data.getInt());
    }

}
