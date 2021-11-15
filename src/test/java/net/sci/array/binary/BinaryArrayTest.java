/**
 * 
 */
package net.sci.array.binary;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class BinaryArrayTest
{

    /**
     * Test method for {@link net.sci.array.binary.BinaryArray#fillBooleans(java.util.function.Function)}.
     */
    @Test
    public final void testFillBooleans()
    {
        int[] dims = new int[] {20, 10};
        BinaryArray array = BinaryArray.create(dims);
        
        array.fillBooleans(pos -> pos[0] >= 10 ^ pos[1] >= 5);
        
        assertFalse(array.getBoolean(5, 2));
        assertTrue(array.getBoolean(15, 2));
        assertTrue(array.getBoolean(5, 7));
        assertFalse(array.getBoolean(15, 7));
    }

    /**
     * Test method for {@link net.sci.array.binary.BinaryArray#fill(boolean)}.
     */
    @Test
    public final void testFillBoolean_2d()
    {
        int[] dims = new int[] {5, 4};
        BinaryArray array = BinaryArray.create(dims);
        
        array.fill(true);
        
        int count = 0;
        for (int[] pos : array.positions())
        {
            if (array.getBoolean(pos))
            {
                count++;
            }
        }
        assertEquals(20, count);
    }

    /**
     * Test method for {@link net.sci.array.binary.BinaryArray#fill(boolean)}.
     */
    @Test
    public final void testFillBoolean_3d()
    {
        int[] dims = new int[] {5, 4, 3};
        BinaryArray array = BinaryArray.create(dims);
        
        array.fill(true);
        
        int count = 0;
        for (int[] pos : array.positions())
        {
            if (array.getBoolean(pos))
            {
                count++;
            }
        }
        assertEquals(60, count);
    }

}
