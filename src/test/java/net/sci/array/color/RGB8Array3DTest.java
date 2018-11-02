/**
 * 
 */
package net.sci.array.color;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class RGB8Array3DTest
{
    
    /**
     * Test method for {@link net.sci.array.scalar.RGB8Array3D#slices()}.
     */
    @Test
    public final void testSlices()
    {
        RGB8Array3D array = RGB8Array3D.create(5, 4, 3);

        int n = 0;
        for(RGB8Array2D slice : array.slices())
        {
            assertEquals(5, slice.getSize(0));
            assertEquals(4, slice.getSize(1));
            n++;
        }

        assertEquals(3, n);
    }
    
}
