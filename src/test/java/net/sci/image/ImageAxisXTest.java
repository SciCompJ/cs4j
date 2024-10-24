/**
 * 
 */
package net.sci.image;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.axis.NumericalAxis;

/**
 * @author dlegland
 *
 */
public class ImageAxisXTest
{
    
    /**
     * Test method for {@link net.sci.axis.NumericalAxis#duplicate()}.
     */
    @Test
    public final void testDuplicate()
    {
        NumericalAxis axis = new ImageAxis.X(1.5, 0.5, "0xB5m");
        NumericalAxis dup = axis.duplicate();
        
        assertTrue(dup.type() == ImageAxis.Type.SPACE);
        assertTrue(dup instanceof ImageAxis.X);
    }
    
}
