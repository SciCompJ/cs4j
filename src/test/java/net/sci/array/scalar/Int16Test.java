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

}
