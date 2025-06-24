/**
 * 
 */
package net.sci.array.color;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import net.sci.array.numeric.UInt16;
import net.sci.array.numeric.UInt16Array;

/**
 * @author dlegland
 *
 */
public class RGB16ArrayTest
{
    
    /**
     * Test method for {@link net.sci.array.color.RGB16Array#create(int[])}.
     */
    @Test
    public final void testCreate()
    {
        RGB16Array array = RGB16Array.create(5, 4, 3);
        assertNotNull(array);
        assertEquals(3, array.dimensionality());
        assertEquals(5, array.size(0));
        assertEquals(4, array.size(1));
        assertEquals(3, array.size(2));
    }

    /**
     * Test method for {@link net.sci.array.color.RGB16Array#create(int[])}.
     */
    @Test
    public final void testIterator()
    {
        RGB16Array array = RGB16Array.create(5, 4, 3);
        assertNotNull(array);
        RGB16Array.Iterator iter = array.iterator();
        int n = 0;
        while (iter.hasNext())
        {
            iter.forward();
            n++;
        }
        
        assertEquals(60, n);
    }

    /**
     * Test method for {@link net.sci.array.color.RGB816rray#create(int[])}.
     */
    @Test
    public final void testCreateUInt16View()
    {
        RGB16Array array = RGB16Array.create(5, 4, 3);
        UInt16Array view = array.createUInt16View();
        
        assertNotNull(view);
        assertEquals(3, view.dimensionality());
        assertEquals(5, view.size(0));
        assertEquals(4, view.size(1));
        assertEquals(3, view.size(2));
    }
    
    /**
     * Test method for {@link net.sci.array.color.RGB16Array#create(int[])}.
     */
    @Test
    public final void testUInt16ViewIterator()
    {
        RGB16Array array = RGB16Array.create(5, 4, 3);
        UInt16Array view = array.createUInt16View();
        
        assertNotNull(view);
        int count = 0;
        for (@SuppressWarnings("unused") UInt16 item : view)
        {
            count++;
        }
        assertEquals(60, count);
    }
    
}
