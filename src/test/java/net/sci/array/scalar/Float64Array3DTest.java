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
public class Float64Array3DTest
{
    
    /**
     * Test method for {@link net.sci.array.scalar.Float64Array3D#slices()}.
     */
    @Test
    public final void testSlices()
    {
        Float64Array3D array = Float64Array3D.create(5, 4, 3);

        int n = 0;
        for(Float64Array2D slice : array.slices())
        {
            assertEquals(5, slice.getSize(0));
            assertEquals(4, slice.getSize(1));
            n++;
        }

        assertEquals(3, n);
    }
    
}
