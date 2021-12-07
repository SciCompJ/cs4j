/**
 * 
 */
package net.sci.array.binary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class BinaryArray3DTest
{
    /**
     * Test method for {@link net.sci.array.binary.BinaryArray3D#fillBooleans(TriFunction)}.
     */
    @Test
    public void testFillBooleans_TriFunction()
    {
        BinaryArray3D array = BinaryArray3D.create(5, 4, 3);

        array.fillBooleans((x, y, z) -> (x + y * 10 + z * 100) > 200);
        
        assertNotNull(array);
        assertFalse(array.getBoolean(0, 0, 0));
        assertTrue(array.getBoolean(4, 3, 2));
    }

    
    /**
     * Test method for {@link net.sci.array.binary.BinaryArray3D#slices()}.
     */
    @Test
    public final void testSlices()
    {
        BinaryArray3D array = BinaryArray3D.create(5, 4, 3);

        int n = 0;
        for(BinaryArray2D slice : array.slices())
        {
            assertEquals(5, slice.size(0));
            assertEquals(4, slice.size(1));
            n++;
        }

        assertEquals(3, n);
    }
    
}
