/**
 * 
 */
package net.sci.array.color;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.numeric.UInt16;

/**
 * 
 */
public class RGB16Test
{
    
    /**
     * Test stability of conversion from gray to RGB16, then back to gray. Due
     * to rounding effect, the comparison are performed up to an arbitrary
     * error.
     * 
     * Test method for {@link net.sci.array.color.RGB16#toUInt16()}.
     */
    @Test
    public final void testToUInt16()
    {
        RGB16 color;
        int grayValue;
        UInt16 exp;
        
        color = RGB16.BLACK; 
        exp = new UInt16(0);
        assertEquals(exp, color.toUInt16());
        
        grayValue = UInt16.MAX_INT;
        color = RGB16.WHITE;
        exp = new UInt16(grayValue);
        assertEquals(exp.intValue(), color.toUInt16().intValue(), 15);
       
        grayValue = 100;
        color = RGB16.fromUInt16(new UInt16(grayValue));
        exp = new UInt16(grayValue);
        assertEquals(exp.intValue(), color.toUInt16().intValue(), 15);

        grayValue = 1000;
        color = RGB16.fromUInt16(new UInt16(grayValue));
        exp = new UInt16(grayValue);
        assertEquals(exp.intValue(), color.toUInt16().intValue(), 15);

        grayValue = 10000;
        color = RGB16.fromUInt16(new UInt16(grayValue));
        exp = new UInt16(grayValue);
        assertEquals(exp.intValue(), color.toUInt16().intValue(), 15);
    }
    
}
