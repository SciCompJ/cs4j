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
public class ImageAxisTTest
{
    
    /**
     * Test method for {@link net.sci.axis.NumericalAxis#duplicate()}.
     */
    @Test
    public final void testDuplicate()
    {
        NumericalAxis axis = new ImageAxis.T(1.5, 0.5, "ms");
        NumericalAxis dup = axis.duplicate();
        
        assertTrue(dup.type() == ImageAxis.Type.TIME);
        assertTrue(dup instanceof ImageAxis.T);
    }
    
}
