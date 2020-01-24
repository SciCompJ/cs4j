/**
 * 
 */
package net.sci.array.generic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sci.array.Array2D;
import net.sci.array.Array3D;

/**
 * @author dlegland
 *
 */
public class BufferedGenericArray3DTest
{
    /**
     * Test method for {@link net.sci.array.generic.BufferedGenericArray3D#BufferedGenericArray3D(int, int, int, Object)}.
     */
    @Test
    public final void testCreate()
    {
        Array3D<String> array = createStringArray();
        assertEquals(10, array.size(0));
        assertEquals(6, array.size(1));
    }
    
    /**
     * Test method for {@link net.sci.array.generic.BufferedGenericArray3D#get(int, int, int)}.
     */
    @Test
    public final void testGetIntInt()
    {
        Array3D<String> array = createStringArray();
        String first = array.get(0, 0, 0);
        assertTrue("AAA".equals(first));
        String last = array.get(9, 5, 3);
        assertTrue("JFD".equals(last));
    }
    
    /**
     * Test method for {@link net.sci.array.generic.BufferedGenericArray3D#set(int, int, int, java.lang.Object)}.
     */
    @Test
    public final void testSetIntIntT()
    {
        Array3D<String> array = createStringArray();
        array.set("Hello", 0, 0, 0);
        assertTrue("Hello".equals(array.get(0, 0, 0)));
    }
    
    /**
     * Test method for {@link net.sci.array.generic.BufferedGenericArray3D#duplicate()}.
     */
    @Test
    public final void testDuplicate()
    {
        Array3D<String> array = createStringArray().duplicate();
        assertEquals(10, array.size(0));
        assertEquals(6, array.size(1));
        assertEquals(4, array.size(2));
        String first = array.get(0, 0, 0);
        assertTrue("AAA".equals(first));
        String last = array.get(9, 5, 3);
        assertTrue("JFD".equals(last));
    }
    
    /**
     * Test method for {@link net.sci.array.generic.BufferedGenericArray3D#iterator()}.
     */
    @Test
    public final void testIterator()
    {
        Array3D<String> array = createStringArray();
        int count = 0;
        for (@SuppressWarnings("unused") String s : array)
        {
            count++;
        }
        assertEquals(240, count);
    }
    
    private Array3D<String> createStringArray()
    {
        BufferedGenericArray3D<String> array = new BufferedGenericArray3D<String>(10, 6, 4, " ");
        String[] digits = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        for (int z = 0; z < 4; z++)
        {
            for (int y = 0; y < 6; y++)
            {
                for (int x = 0; x < 10; x++)
                {
                    array.set(digits[x] + digits[y] + digits[z], x, y, z);
                }
            }
        }
        return array;
    }
    
    /**
     * Test method for {@link net.sci.array.generic.GenericArray3D#slices()}.
     */
    @Test
    public final void testSlices()
    {
        String val = "A";
        Array3D<String> array = GenericArray3D.create(5, 4, 3, val);

        int n = 0;
        for(Array2D<String> slice : array.slices())
        {
            assertEquals(5, slice.size(0));
            assertEquals(4, slice.size(1));
            n++;
        }

        assertEquals(3, n);
    }
    

}
