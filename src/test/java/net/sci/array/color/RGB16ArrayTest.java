/**
 * 
 */
package net.sci.array.color;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

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
        assertEquals(5, array.getSize(0));
        assertEquals(4, array.getSize(1));
        assertEquals(3, array.getSize(2));
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

}
