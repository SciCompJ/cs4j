/**
 * 
 */
package net.sci.array.vector;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.numeric.Float64VectorArray2D;
import net.sci.array.numeric.Float64VectorArray3D;

/**
 * @author dlegland
 *
 */
public class Float64VectorArray3DTest
{
    
    /**
     * Test method for {@link net.sci.array.numeric.Float64VectorArray3D#slices()}.
     */
    @Test
    public final void testSlices()
    {
        Float64VectorArray3D array = Float64VectorArray3D.create(5, 4, 3, 2);

        int n = 0;
        for(Float64VectorArray2D slice : array.slices())
        {
            assertEquals(5, slice.size(0));
            assertEquals(4, slice.size(1));
            assertEquals(2, slice.channelCount());
            n++;
        }

        assertEquals(3, n);
    }
    
}
