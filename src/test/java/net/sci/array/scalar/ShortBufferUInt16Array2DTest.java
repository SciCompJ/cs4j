/**
 * 
 */
package net.sci.array.scalar;

import static org.junit.Assert.*;

import java.nio.ShortBuffer;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class ShortBufferUInt16Array2DTest
{
    /**
     * Test method for {@link net.sci.array.scalar.ShortBufferUInt16Array2D#ShortBufferUInt16Array2D(int, int, java.nio.ShortBuffer)}.
     */
    @Test
    public void testCreate()
    {
        ShortBufferUInt16Array2D array = createGradientArray();
        assertEquals(array.size(0), 10);
        assertEquals(array.size(1),  8);
        assertEquals(array.getInt(9, 7), 79);
    }

    /**
     * Test method for {@link net.sci.array.scalar.ShortBufferUInt16Array2D#getFloat(int[])}.
     */
    @Test
    public void testGetFloat()
    {
        ShortBufferUInt16Array2D array = createGradientArray();
        assertEquals(array.size(0), 10);
        assertEquals(array.size(1),  8);
        assertEquals(array.getInt(9, 7), 79);
    }
    
    /**
     * Test method for {@link net.sci.array.scalar.ShortBufferUInt16Array2D#setFloat(int, int, float)}.
     */
    @Test
    public void testSetInt()
    {
        ShortBufferUInt16Array2D array = createGradientArray();
        array.setInt(6, 5, 314);
        assertEquals(array.getInt(6, 5), 314); 
    }

    /**
     * Test method for {@link net.sci.array.scalar.ShortBufferUInt16Array2D#duplicate()}.
     */
    @Test
    public void testDuplicate()
    {
        ShortBufferUInt16Array2D array = createGradientArray();
        UInt16Array2D dup = array.duplicate();
        assertEquals(dup.size(0), 10);
        assertEquals(dup.size(1),  8);
        assertEquals(dup.getInt(9, 7), 79);
    }


    private ShortBufferUInt16Array2D createGradientArray()
    {
        short[] shortArray = new short[80];
        ShortBuffer buffer = ShortBuffer.wrap(shortArray);
        ShortBufferUInt16Array2D array = new ShortBufferUInt16Array2D(10, 8, buffer);
        array.fillValues((x,y) -> x + 10.0 * y);
        return array;
    }

}
