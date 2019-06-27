/**
 * 
 */
package net.sci.util;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class RandomUtilsTest
{
    /**
     * Test method for {@link net.sci.util.RandomUtils#randomIndices(int, int)}.
     */
    @Test
    public final void testRandomSubsetIndices_5_3()
    {
        int[] inds = RandomUtils.randomSubsetIndices(5, 3);
        assertEquals(3, inds.length);
        assertTrue(inds[0] != inds[1]);
        assertTrue(inds[0] != inds[2]);
        assertTrue(inds[1] != inds[2]);
    }

    /**
     * Test method for {@link net.sci.util.RandomUtils#randomIndices(int, int)}.
     */
    @Test
    public final void testRandomSubsetIndices_13_13()
    {
        int[] inds = RandomUtils.randomSubsetIndices(13, 13);
        assertEquals(13, inds.length);
        for (int i = 0; i < 13; i++)
        {
            assertTrue(arrayContains(inds, i));
        }
    }
    
    private static final boolean arrayContains(int[] array, int value)
    {
        for (int i = 0; i < array.length; i++)
        {
            if (array[i] == value)
                return true;
        }
        return false;
    }

}
