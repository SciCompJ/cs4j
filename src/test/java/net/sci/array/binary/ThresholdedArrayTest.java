/**
 * 
 */
package net.sci.array.binary;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sci.array.scalar.Float32Array2D;

/**
 * @author dlegland
 *
 */
public class ThresholdedArrayTest
{
    
    /**
     * Test method for {@link net.sci.array.binary.ThresholdedArray#getBoolean(int[])}.
     */
    @Test
    public final void testGetBoolean()
    {
        Float32Array2D array = Float32Array2D.create(10, 10);
        array.fillValues(pos -> 7 - Math.hypot(pos[0] - 4, pos[1] - 4));
        
        ThresholdedArray view = new ThresholdedArray(array, 3.0);
        
        // the four corners should be false
        assertFalse(view.getBoolean(new int[] {0, 0}));
        assertFalse(view.getBoolean(new int[] {9, 0}));
        assertFalse(view.getBoolean(new int[] {0, 9}));
        assertFalse(view.getBoolean(new int[] {9, 9}));
        
        // The value in the middle should be true
        assertTrue(view.getBoolean(new int[] {5, 5}));
        assertTrue(view.getBoolean(new int[] {5, 2}));
        assertTrue(view.getBoolean(new int[] {5, 6}));
        assertTrue(view.getBoolean(new int[] {2, 5}));
        assertTrue(view.getBoolean(new int[] {6, 5}));

        // the middle of image edges should be false
        assertFalse(view.getBoolean(new int[] {5, 0}));
        assertFalse(view.getBoolean(new int[] {0, 5}));
        assertFalse(view.getBoolean(new int[] {9, 5}));
        assertFalse(view.getBoolean(new int[] {5, 9}));
    }
    
}
