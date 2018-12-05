/**
 * 
 */
package net.sci.image;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class ImageAxisZTest
{
    
    /**
     * Test method for {@link net.sci.image.NumericalAxis#duplicate()}.
     */
    @Test
    public final void testDuplicate()
    {
        NumericalAxis axis = new ImageAxis.Z(1.5, 0.5, "µm");
        NumericalAxis dup = axis.duplicate();
        
        assertTrue(dup.getType() == ImageAxis.Type.SPACE);
        assertTrue(dup instanceof ImageAxis.Z);
    }
    
}
