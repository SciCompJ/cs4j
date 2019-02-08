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
public class BinaryArray3DTest
{
    
    /**
     * Test method for {@link net.sci.array.scalar.BinaryArray3D#slices()}.
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
