/**
 * 
 */
package net.sci.array.generic;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.Array2D;
import net.sci.array.generic.BufferedGenericArray2D;

/**
 * @author dlegland
 *
 */
public class BufferedGenericArray2DTest
{
    /**
     * Test method for {@link net.sci.array.generic.BufferedGenericArray2D#BufferedGenericArray2D(int, int, Object)}.
     */
    @Test
    public final void testCreate()
    {
        Array2D<String> array = createStringArray();
        assertEquals(10, array.size(0));
        assertEquals(6, array.size(1));
    }
    
    /**
     * Test method for {@link net.sci.array.generic.BufferedGenericArray2D#get(int, int)}.
     */
    @Test
    public final void testGetIntInt()
    {
        Array2D<String> array = createStringArray();
        String first = array.get(0, 0);
        assertTrue("AA".equals(first));
        String last = array.get(9, 5);
        assertTrue("JF".equals(last));
    }
    
    /**
     * Test method for {@link net.sci.array.generic.BufferedGenericArray2D#set(int, int, java.lang.Object)}.
     */
    @Test
    public final void testSetIntIntT()
    {
        Array2D<String> array = createStringArray();
        array.set("Hello", 0, 0);
        assertTrue("Hello".equals(array.get(0, 0)));
    }
    
    /**
     * Test method for {@link net.sci.array.generic.BufferedGenericArray2D#duplicate()}.
     */
    @Test
    public final void testDuplicate()
    {
        Array2D<String> array = createStringArray().duplicate();
        assertEquals(10, array.size(0));
        assertEquals(6, array.size(1));
        String first = array.get(0, 0);
        assertTrue("AA".equals(first));
        String last = array.get(9, 5);
        assertTrue("JF".equals(last));
    }
    
    /**
     * Test method for {@link net.sci.array.generic.BufferedGenericArray2D#iterator()}.
     */
    @Test
    public final void testIterator()
    {
        Array2D<String> array = createStringArray();
        int count = 0;
        for (@SuppressWarnings("unused") String s : array)
        {
            count++;
        }
        assertEquals(60, count);
    }
    
    private Array2D<String> createStringArray()
    {
        BufferedGenericArray2D<String> array = new BufferedGenericArray2D<String>(10, 6, " ");
        String[] digits = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        for (int y = 0; y < 6; y++)
        {
            for (int x = 0; x < 10; x++)
            {
                array.set(digits[x] + digits[y], x, y);
            }
        }
        return array;
    }
    
}
