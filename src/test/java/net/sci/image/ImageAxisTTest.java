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
public class ImageAxisTTest
{
    
    /**
     * Test method for {@link net.sci.axis.NumericalAxis#duplicate()}.
     */
    @Test
    public final void testDuplicate()
    {
        ImageAxis axis = new ImageAxis.T(1.5, 0.5, "ms");
        ImageAxis dup = axis.duplicate();
        
        assertTrue(dup.type() == ImageAxis.Type.TIME);
        assertTrue(dup instanceof ImageAxis.T);
    }
    
}
