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
            assertEquals(5, slice.size(0));
            assertEquals(4, slice.size(1));
            n++;
        }

        assertEquals(3, n);
    }
    
    /**
     * Test method for {@link net.sci.array.Array3D#setSlice(int, net.sci.array.Array2D)}.
     */
    @Test
    public final void test_setSlice()
    {
        Float64Array3D array = Float64Array3D.create(10, 8, 6);
        array.fillValues((x,y,z) -> (x+y+z+0.0));
        Float64Array2D slice = Float64Array2D.create(10, 8);
        slice.fillValues((x,y) -> (y*100.0 + x));
        
        array.setSlice(3, slice);
        
        // out of slice
        assertEquals(  0, array.getValue(0, 0, 0), 0.01);
        assertEquals( 21, array.getValue(9, 7, 5), 0.01);
        
        // within slice
        assertEquals(  0, array.getValue(0, 0, 3), 0.01);
        assertEquals(  9, array.getValue(9, 0, 3), 0.01);
        assertEquals(700, array.getValue(0, 7, 3), 0.01);
        assertEquals(709, array.getValue(9, 7, 3), 0.01);
    }

}
