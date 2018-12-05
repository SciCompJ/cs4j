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
public class ImageAxisTTest
{
    
    /**
     * Test method for {@link net.sci.image.NumericalAxis#duplicate()}.
     */
    @Test
    public final void testDuplicate()
    {
        NumericalAxis axis = new ImageAxis.T(1.5, 0.5, "ms");
        NumericalAxis dup = axis.duplicate();
        
        assertTrue(dup.getType() == ImageAxis.Type.TIME);
        assertTrue(dup instanceof ImageAxis.T);
    }
    
}
