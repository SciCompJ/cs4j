/**
 * 
 */
package net.sci.image.label;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Test;

import net.sci.array.numeric.UInt8Array2D;
import net.sci.array.numeric.impl.BufferedUInt8Array2D;
import net.sci.image.label.RegionAdjacencies.LabelPair;

/**
 * @author dlegland
 *
 */
public class RegionAdjacenciesTest
{
    
    /**
     * Test method for {@link net.sci.image.label.RegionAdjacencies#computeAdjacencies(net.sci.array.scalar.IntArray2D)}.
     */
    @Test
    public final void testComputeAdjacenciesIntArray2D_FiveRegions()
    {
        byte[] data = new byte[]{
                1, 1, 1, 0, 2, 2, 2, 
                1, 1, 0, 5, 0, 2, 2, 
                1, 0, 5, 5, 5, 0, 2, 
                0, 5, 5, 5, 5, 5, 0,
                3, 0, 5, 5, 5, 0, 4, 
                3, 3, 0, 5, 0, 4, 4, 
                3, 3, 3, 0, 4, 4, 4
        };
        UInt8Array2D image = new BufferedUInt8Array2D(7, 7, data);
        
        Set<LabelPair> adjacencies = RegionAdjacencies.computeAdjacencies(image);
        assertEquals(8, adjacencies.size());
        
        assertTrue(adjacencies.contains(new LabelPair(1, 2)));
        assertTrue(adjacencies.contains(new LabelPair(1, 3)));
        assertFalse(adjacencies.contains(new LabelPair(1, 4)));
        assertTrue(adjacencies.contains(new LabelPair(1, 5)));
        assertFalse(adjacencies.contains(new LabelPair(2, 3)));
        assertTrue(adjacencies.contains(new LabelPair(2, 4)));
        assertTrue(adjacencies.contains(new LabelPair(2, 5)));
        assertTrue(adjacencies.contains(new LabelPair(3, 4)));
        assertTrue(adjacencies.contains(new LabelPair(3, 5)));
        assertTrue(adjacencies.contains(new LabelPair(4, 5)));
    }
    
}
