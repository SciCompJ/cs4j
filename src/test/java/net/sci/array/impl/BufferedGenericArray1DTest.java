/**
 * 
 */
package net.sci.array.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sci.array.Array1D;

/**
 * 
 */
public class BufferedGenericArray1DTest
{

    /**
     * Test method for {@link net.sci.array.impl.GenericArray1D#create(int, java.lang.Object)}.
     */
    @Test
    public final void testCreateIntT()
    {
        Array1D<String> array = createStringArray();
        assertEquals(6, array.size(0));
    }
    
    /**
     * Test method for {@link net.sci.array.impl.BufferedGenericArray1D#get(int)}.
     */
    @Test
    public final void testGetInt()
    {
        Array1D<String> array = createStringArray();
        String first = array.get(0);
        assertTrue("A".equals(first));
        String last = array.get(5);
        assertTrue("F".equals(last));
    }
    
    /**
     * Test method for {@link net.sci.array.impl.BufferedGenericArray1D#set(int, java.lang.Object)}.
     */
    @Test
    public final void testSetIntIntT()
    {
        Array1D<String> array = createStringArray();
        array.set(0, "Hello");
        assertTrue("Hello".equals(array.get(0)));
    }
    
    /**
     * Test method for {@link net.sci.array.impl.BufferedGenericArray1D#duplicate()}.
     */
    @Test
    public final void testDuplicate()
    {
        Array1D<String> array = createStringArray().duplicate();
        assertEquals(6, array.size(0));
        String first = array.get(0);
        assertTrue("A".equals(first));
        String last = array.get(5);
        assertTrue("F".equals(last));
    }
    
    /**
     * Test method for {@link net.sci.array.impl.BufferedGenericArray1D#iterator()}.
     */
    @Test
    public final void testIterator()
    {
        Array1D<String> array = createStringArray();
        int count = 0;
        for (@SuppressWarnings("unused") String s : array)
        {
            count++;
        }
        assertEquals(6, count);
    }
    
    private Array1D<String> createStringArray()
    {
        BufferedGenericArray1D<String> array = new BufferedGenericArray1D<String>(6, " ");
        String[] digits = { "A", "B", "C", "D", "E", "F" };
        for (int x = 0; x < 6; x++)
        {
            array.set(x, digits[x]);
        }
        return array;
    }

}
