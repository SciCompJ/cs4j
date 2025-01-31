package net.sci.array.binary;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BinaryTest
{
    @Test
    public void testEquals_TRUE()
    {
        Binary b = new Binary(true);
        assertTrue(b.equals(Binary.TRUE));
    }

    @Test
    public void testIsAssignable()
    {
        BinaryArray array = BinaryArray2D.create(6, 4);
        assertTrue(Binary.class.isAssignableFrom(array.elementClass()));
    }

    /**
     * Test method for
     * {@link net.sci.array.binary.Binary#compareTo(net.sci.array.binary.Binary)}.
     */
    @Test
    public final void testCompareTo()
    {
        Binary v1 = new Binary(true);
        Binary v2 = new Binary(false);
        Binary v3 = new Binary(true);
        
        assertTrue(v1.compareTo(v2) > 0);
        assertTrue(v2.compareTo(v1) < 0);
        assertTrue(v1.compareTo(v3) == 0);
    }

}
