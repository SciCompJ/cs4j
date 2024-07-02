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
public class Float32VectorArray3DTest
{
    
    /**
     * Test method for {@link net.sci.array.numeric.Float32VectorArray3D#slices()}.
     */
    @Test
    public final void testSlices()
    {
        Float32VectorArray3D array = Float32VectorArray3D.create(5, 4, 3, 2);

        int n = 0;
        for(Float32VectorArray2D slice : array.slices())
        {
            assertEquals(5, slice.size(0));
            assertEquals(4, slice.size(1));
            assertEquals(2, slice.channelCount());
            n++;
        }

        assertEquals(3, n);
    }
    
}
