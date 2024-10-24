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
public class UInt16Array3DTest
{
    
    /**
     * Test method for {@link net.sci.array.numeric.UInt16Array3D#slices()}.
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
    
    /**
     * Test method for {@link net.sci.array.Array3D#setSlice(int, net.sci.array.Array2D)}.
     */
    @Test
    public final void test_setSlice()
    {
        UInt16Array3D array = UInt16Array3D.create(10, 8, 6);
        array.fillInts((x,y,z) -> (x+y+z));
        UInt16Array2D slice = UInt16Array2D.create(10, 8);
        slice.fillInts((x,y) -> (y*100 + x));
        
        array.setSlice(3, slice);
        
        // out of slice
        assertEquals(  0, array.getInt(0, 0, 0));
        assertEquals( 21, array.getInt(9, 7, 5));
        
        // within slice
        assertEquals(  0, array.getInt(0, 0, 3));
        assertEquals(  9, array.getInt(9, 0, 3));
        assertEquals(700, array.getInt(0, 7, 3));
        assertEquals(709, array.getInt(9, 7, 3));
    }
    

}
