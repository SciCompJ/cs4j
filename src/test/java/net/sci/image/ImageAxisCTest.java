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
public class ImageAxisCTest
{
    
    /**
     * Test method for {@link net.sci.image.NumericalAxis#duplicate()}.
     */
    @Test
    public final void testDuplicate()
    {
        CategoricalAxis axis = new ImageAxis.C(new String[] {"red", "green", "blue"});
        CategoricalAxis dup = axis.duplicate();
        
        assertTrue(dup.getType() == ImageAxis.Type.CHANNEL);
        assertTrue(dup instanceof ImageAxis.C);
    }
    
}
