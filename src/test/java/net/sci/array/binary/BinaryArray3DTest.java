/**
 * 
 */
package net.sci.array.binary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sci.array.numeric.TriFunction;

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
    
    
    /**
     * Test method for {@link net.sci.array.Array3D#setSlice(int, net.sci.array.Array2D)}.
     */
    @Test
    public final void test_setSlice()
    {
        BinaryArray3D array = BinaryArray3D.create(10, 8, 6);
        array.fillBooleans((x,y,z) -> x >= 5 ^ y >= 4);
        BinaryArray2D slice = BinaryArray2D.create(10, 8);
        slice.fillBooleans((x,y) -> x < 5 ^ y >= 4);
        
        array.setSlice(3, slice);
        
        // out of slice
        assertFalse(array.getBoolean(0, 0, 0));
        assertTrue(array.getBoolean(9, 0, 0));
        assertTrue(array.getBoolean(0, 7, 0));
        assertFalse(array.getBoolean(9, 7, 0));
        assertFalse(array.getBoolean(0, 0, 5));
        assertTrue(array.getBoolean(9, 0, 5));
        assertTrue(array.getBoolean(0, 7, 5));
        assertFalse(array.getBoolean(9, 7, 5));
        
        // within slice
        assertTrue(array.getBoolean(0, 0, 3));
        assertFalse(array.getBoolean(9, 0, 3));
        assertFalse(array.getBoolean(0, 7, 3));
        assertTrue(array.getBoolean(9, 7, 3));
    }}
