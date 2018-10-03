/**
 * 
 */
package net.sci.array.color;

import static org.junit.Assert.assertEquals;
import net.sci.array.scalar.UInt8Array;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class RGB8ArrayChannelViewTest
{
    
    /**
     * Test method for {@link net.sci.array.color.RGB8Array#channel(int)}.
     */
    @Test
    public final void channel_Iterator()
    {
        RGB8Array array = RGB8Array.create(5, 4, 3);
        
        UInt8Array green = array.channel(1);
        UInt8Array.Iterator iter = green.iterator();
        int n = 0;
        while (iter.hasNext())
        {
            iter.forward();
            n++;
        }
        
        assertEquals(60, n);
    }
}
