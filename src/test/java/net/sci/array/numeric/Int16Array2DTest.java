/**
 * 
 */
package net.sci.array.numeric;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class Int16Array2DTest
{

    /**
     * Test method for {@link net.sci.array.numeric.Int16Array2D#fromIntArray(int[][])}.
     */
    @Test
    public final void testFromIntArray()
    {
        int[][] values = new int[][] {{10, 11, 12, 13}, {20, 21, 22, 23}, {30, 31, 32, 33}};
        
        Int16Array2D array = Int16Array2D.fromIntArray(values);
        
        assertEquals(4, array.size(0));
        assertEquals(3, array.size(1));
        assertEquals(10, array.getInt(0, 0));
        assertEquals(33, array.getInt(3, 2));
    }

}
