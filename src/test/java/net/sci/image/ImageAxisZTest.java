/**
 * 
 */
package net.sci.image;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class ImageAxisZTest
{
    
    /**
     * Test method for {@link net.sci.axis.NumericalAxis#duplicate()}.
     */
    @Test
    public final void testDuplicate()
    {
        ImageAxis axis = new ImageAxis.Z(1.5, 0.5, "0xB5m");
        ImageAxis dup = axis.duplicate();
        
        assertTrue(dup.type() == ImageAxis.Type.SPACE);
        assertTrue(dup instanceof ImageAxis.Z);
    }
    
}
