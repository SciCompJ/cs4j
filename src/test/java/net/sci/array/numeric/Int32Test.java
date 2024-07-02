/**
 * 
 */
package net.sci.array.numeric;

import static org.junit.Assert.assertEquals;

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

}
