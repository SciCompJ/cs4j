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
public class UInt16Array3DTest
{
    
    /**
     * Test method for {@link net.sci.array.scalar.UInt16Array3D#slices()}.
     */
    @Test
    public final void testSlices()
    {
        UInt16Array3D array = UInt16Array3D.create(5, 4, 3);

        int n = 0;
        for(UInt16Array2D slice : array.slices())
        {
            assertEquals(5, slice.size(0));
            assertEquals(4, slice.size(1));
            n++;
        }

        assertEquals(3, n);
    }
    
}
