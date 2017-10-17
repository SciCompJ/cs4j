/**
 * 
 */
package net.sci.array.data.color;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.data.UInt8Array;
import net.sci.array.type.UInt8;

/**
 * @author dlegland
 *
 */
public class RGB8ArrayTest
{
    
    /**
     * Test method for {@link net.sci.array.data.color.RGB8Array#create(int[])}.
     */
    @Test
    public final void testCreate()
    {
        RGB8Array array = RGB8Array.create(5, 4, 3);
        assertNotNull(array);
        assertEquals(3, array.dimensionality());
        assertEquals(5, array.getSize(0));
        assertEquals(4, array.getSize(1));
        assertEquals(3, array.getSize(2));
    }

    /**
     * Test method for {@link net.sci.array.data.color.RGB8Array#create(int[])}.
     */
    @Test
    public final void testCreateUInt8View()
    {
        RGB8Array array = RGB8Array.create(5, 4, 3);
        UInt8Array view = array.createUInt8View();
        
        assertNotNull(view);
        assertEquals(3, view.dimensionality());
        assertEquals(5, view.getSize(0));
        assertEquals(4, view.getSize(1));
        assertEquals(3, view.getSize(2));
    }
    
    /**
     * Test method for {@link net.sci.array.data.color.RGB8Array#create(int[])}.
     */
    @Test
    public final void testUInt8ViewIterator()
    {
        RGB8Array array = RGB8Array.create(5, 4, 3);
        UInt8Array view = array.createUInt8View();
        
        assertNotNull(view);
        int count = 0;
        for (@SuppressWarnings("unused") UInt8 item : view)
        {
            count++;
        }
        assertEquals(60, count);
    }
    
}
