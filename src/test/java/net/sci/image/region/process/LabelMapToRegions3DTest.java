/**
 * 
 */
package net.sci.image.region.process;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import net.sci.array.numeric.UInt8Array3D;
import net.sci.image.region.BinaryRegion3D;

/**
 * 
 */
public class LabelMapToRegions3DTest
{

    /**
     * Test method for {@link net.sci.image.region.process.LabelMapToRegions3D#process(net.sci.array.Array)}.
     */
    @Test
    public void testProcess()
    {
        UInt8Array3D labelMap = createEightRegionsLabelMap();

        LabelMapToRegions3D algo = new LabelMapToRegions3D();
        Map<Integer, BinaryRegion3D> map = algo.process(labelMap);
        
        assertTrue(map.containsKey(1));
        assertEquals(1, map.get(1).elementCount());
        assertTrue(map.containsKey(5));
        assertEquals(5, map.get(5).elementCount());
        assertTrue(map.containsKey(6));
        assertEquals(5, map.get(6).elementCount());
        assertTrue(map.containsKey(7));
        assertEquals(5, map.get(7).elementCount());
        assertTrue(map.containsKey(10));
        assertEquals(25, map.get(10).elementCount());
        assertTrue(map.containsKey(11));
        assertEquals(25, map.get(11).elementCount());
        assertTrue(map.containsKey(12));
        assertEquals(25, map.get(12).elementCount());
        assertTrue(map.containsKey(15));
        assertEquals(125, map.get(15).elementCount());
    }
    
    /**
     * Creates an image with eight regions with various sizes:
     *
     * <ul>
     * <li> region 1 is composed of one voxel </li>
     * <li> regions 5, 6 and 7 are composed of five voxels </li>
     * <li> regions 10, 11 and 12 are composed of 5x5 voxels </li>
     * <li> region 15 is composed of 5x5x5 voxels </li>
     * </ul>
     * 
     * @return a new array of integer
     */
    private static final UInt8Array3D createEightRegionsLabelMap()
    {
        UInt8Array3D array = UInt8Array3D.create(8, 8, 8);

        // one single region with one voxel
        array.setInt(1, 1, 1, 1);
        
        // three regions with five voxels
        for (int i = 3; i < 8; i++)
        {
            array.setInt(i, 1, 1, 5);
            array.setInt(1, i, 1, 6);
            array.setInt(1, 1, i, 7);
        }
        
        // three regions with 5x5 voxels
        for (int i = 3; i < 8; i++)
        {
            for (int j = 3; j < 8; j++)
            {
                array.setInt(i, j, 1, 10);
                array.setInt(i, 1, j, 11);
                array.setInt(1, i, j, 12);
            }
        }
        
        // one regions with 5x5x5 voxels
        for (int i = 3; i < 8; i++)
        {
            for (int j = 3; j < 8; j++)
            {
                for (int k = 3; k < 8; k++)
                {
                    array.setInt(i, j, k, 15);
                }
            }
        }
        return array;
    }

}
