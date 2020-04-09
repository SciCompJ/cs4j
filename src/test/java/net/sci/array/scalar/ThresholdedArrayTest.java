/**
 * 
 */
package net.sci.array.scalar;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class ThresholdedArrayTest
{
    
    /**
     * Test method for {@link net.sci.array.scalar.ThresholdedArray#getBoolean(int[])}.
     */
    @Test
    public final void testGetBoolean()
    {
        Float32Array2D array = Float32Array2D.create(10, 10);
        array.populateValues((pos) -> 7 - Math.hypot(pos[0] - 4, pos[1] - 4));
        
        ThresholdedArray view = new ThresholdedArray(array, 3.0);
        
        // the four corners should be false
        assertFalse(view.getBoolean(0, 0));
        assertFalse(view.getBoolean(9, 0));
        assertFalse(view.getBoolean(0, 9));
        assertFalse(view.getBoolean(9, 9));
        
        // The value in the middle should be true
        assertTrue(view.getBoolean(5, 5));
        assertTrue(view.getBoolean(5, 2));
        assertTrue(view.getBoolean(5, 6));
        assertTrue(view.getBoolean(2, 5));
        assertTrue(view.getBoolean(6, 5));

        // the middle of image edges should be false
        assertFalse(view.getBoolean(5, 0));
        assertFalse(view.getBoolean(0, 5));
        assertFalse(view.getBoolean(9, 5));
        assertFalse(view.getBoolean(5, 9));
    }
    
}
