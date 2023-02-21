/**
 * 
 */
package net.sci.image.morphology.watershed;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.scalar.ScalarArray1D;
import net.sci.array.scalar.UInt8Array1D;

/**
 * @author dlegland
 *
 */
public class HierarchicalWatershed1DTest
{

    /**
     * Test method for {@link net.sci.image.morphology.watershed.HierarchicalWatershed1D#process(net.sci.array.scalar.ScalarArray1D)}.
     */
    @Test
    public final void testProcess_TwoRegions()
    {
        UInt8Array1D array = UInt8Array1D.fromIntArray(new int[] {10, 30, 20});
        
        HierarchicalWatershed1D algo = new HierarchicalWatershed1D();
        ScalarArray1D<?> result = algo.process(array);
        
        // check size
        assertEquals(array.size(0), result.size(0));
        // check basins have dynamic equal to zero
        assertEquals(0.0, result.getValue(0), 0.01);
        assertEquals(0.0, result.getValue(2), 0.01);
        // check dynamic of boundary
        assertEquals(10.0, result.getValue(1), 0.01);
    }

    /**
     * Test method for {@link net.sci.image.morphology.watershed.HierarchicalWatershed1D#process(net.sci.array.scalar.ScalarArray1D)}.
     */
    @Test
    public final void testProcess_FourRegions()
    {
        UInt8Array1D array = UInt8Array1D.fromIntArray(new int[] {10, 40, 30, 80, 50, 70, 20});
        
        HierarchicalWatershed1D algo = new HierarchicalWatershed1D();
        ScalarArray1D<?> result = algo.process(array);
        
        // check size
        assertEquals(array.size(0), result.size(0));
        // check basins have dynamic equal to zero
        assertEquals(0.0, result.getValue(0), 0.01);
        assertEquals(0.0, result.getValue(2), 0.01);
        assertEquals(0.0, result.getValue(4), 0.01);
        assertEquals(0.0, result.getValue(6), 0.01);
        // check dynamic of boundaries
        assertEquals(10.0, result.getValue(1), 0.01);
        assertEquals(60.0, result.getValue(3), 0.01);
        assertEquals(20.0, result.getValue(5), 0.01);
    }

}
