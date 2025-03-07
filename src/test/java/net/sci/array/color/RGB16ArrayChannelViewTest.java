/**
 * 
 */
package net.sci.array.color;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.numeric.UInt16Array;

/**
 * @author dlegland
 *
 */
public class RGB16ArrayChannelViewTest
{
    
    /**
     * Test method for {@link net.sci.array.color.RGB16Array#channel(int)}.
     */
    @Test
    public final void channel_Iterator()
    {
        RGB16Array array = RGB16Array.create(5, 4, 3);
        
        UInt16Array green = array.channel(1);
        UInt16Array.Iterator iter = green.iterator();
        int n = 0;
        while (iter.hasNext())
        {
            iter.forward();
            n++;
        }
        
        assertEquals(60, n);
    }
}
