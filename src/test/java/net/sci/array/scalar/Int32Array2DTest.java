/**
 * 
 */
package net.sci.array.scalar;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class Int32Array2DTest
{
    /**
     * Test method for {@link net.sci.array.scalar.Int32Array2D#fromIntArray(int[][])}.
     */
    @Test
    public final void testFromIntArray()
    {
        int[][] values = new int[][] {{10, 11, 12, 13}, {20, 21, 22, 23}, {30, 31, 32, 33}};
        
        Int32Array2D array = Int32Array2D.fromIntArray(values);
        
        assertEquals(4, array.size(0));
        assertEquals(3, array.size(1));
        assertEquals(10, array.getInt(0, 0));
        assertEquals(33, array.getInt(3, 2));
    }
    

    /**
     * Test method for {@link net.sci.array.scalar.IntArray2D#getInt(int, int)}.
     */
    @Test
    public final void testGetInt_IntInt()
    {
        Int32Array2D array = Int32Array2D.create(4, 3);
        array.fillInts((x,y) -> y * 10 + x);
        
        assertEquals(23, array.getInt(3, 2));
    }
}
