/**
 * 
 */
package net.sci.array.color;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.numeric.UInt8;

/**
 * 
 */
public class RGB8Test
{
    
    /**
     * Test method for {@link net.sci.array.color.RGB8#toUInt8()}.
     */
    @Test
    public final void testToUInt8()
    {
        RGB8 color;
        
        color = RGB8.BLACK;
        assertEquals(new UInt8(0), color.toUInt8());
        color = RGB8.WHITE;
        assertEquals(new UInt8(255), color.toUInt8());
        
        color = RGB8.fromUInt8(new UInt8(100));
        assertEquals(new UInt8(100), color.toUInt8());

        color = RGB8.fromUInt8(new UInt8(200));
        assertEquals(new UInt8(200), color.toUInt8());
    }
    
}
