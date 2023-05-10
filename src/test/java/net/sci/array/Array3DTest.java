/**
 * 
 */
package net.sci.array;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import net.sci.array.scalar.UInt8Array2D;
import net.sci.array.scalar.UInt8Array3D;

/**
 * @author dlegland
 *
 */
public class Array3DTest
{
    @Test
    public final void test_newInstance_StringArray()
    {
        Array3D<String> array = Array3D.create(5, 4, 3, "");
        
        Array<String> tmp = array.newInstance(new int[]{5, 4, 3});
        assertNotNull(tmp);
    }

    /**
     * Test method for {@link net.sci.array.Array3D#fill(net.sci.array.scalar.TriFunction)}.
     */
    @Test
    public final void test_fill_TriFunction_StringArray()
    {
        Array3D<String> array = Array3D.create(5, 4, 3, "");
        String[] digits = {"A", "B", "C", "D", "E", "F"};  
        
        array.fill((x,y,z) -> digits[z] + digits[y] + digits[x]);
        
        assertEquals(array.get(0, 0, 0), "AAA");
        assertEquals(array.get(4, 3, 2), "CDE");
    }
    
    /**
     * Test method for {@link net.sci.array.Array3D#setSlice(int, net.sci.array.Array2D)}.
     */
    @Test
    public final void test_setSlice_ArrayOfString()
    {
        Array3D<String> array = Array3D.create(5, 4, 3, "");
        String[] digits = {"A", "B", "C", "D", "E", "F"};  
        array.fill((x,y,z) -> digits[z] + digits[y] + digits[x]);
        
        Array2D<String> slice = Array2D.create(5, 4, "");
        slice.fill("ZZZ");
        
        array.setSlice(1, slice);
        
        // out of slice
        assertEquals("AAA", array.get(0, 0, 0));
        assertEquals("CDE", array.get(4, 3, 2));
        
        // within slice
        assertEquals("ZZZ", array.get(0, 0, 1));
        assertEquals("ZZZ", array.get(4, 0, 1));
        assertEquals("ZZZ", array.get(0, 3, 1));
        assertEquals("ZZZ", array.get(4, 3, 1));
    }

    /**
     * Test method for {@link net.sci.array.Array3D#setSlice(int, net.sci.array.Array2D)}.
     */
    @Test
    public final void test_setSlice_UInt8Array()
    {
        UInt8Array3D array = UInt8Array3D.create(10, 8, 6);
        array.fillInts((x,y,z) -> (x+y+z));
        UInt8Array2D slice = UInt8Array2D.create(10, 8);
        slice.fillInts((x,y) -> (y*10 + x));
        
        array.setSlice(3, slice);
        
        // out of slice
        assertEquals( 0, array.getInt(0,0,0));
        assertEquals(21, array.getInt(9, 7, 5));
        
        // within slice
        assertEquals( 0, array.getInt(0, 0, 3));
        assertEquals( 9, array.getInt(9, 0, 3));
        assertEquals(70, array.getInt(0, 7, 3));
        assertEquals(79, array.getInt(9, 7, 3));
    }


}
