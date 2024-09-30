/**
 * 
 */
package net.sci.image.analyze.region3d;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import net.sci.array.binary.BinaryArray3D;
import net.sci.array.numeric.IntArray3D;
import net.sci.array.numeric.UInt8Array3D;
import net.sci.image.Calibration;

/**
 * @author dlegland
 *
 */
public class IntrinsicVolumes3DTest
{

    /**
     * Test method for {@link net.sci.image.analyze.region3d.IntrinsicVolumes3D#analyzeRegions(net.sci.array.scalar.IntArray3D, int[], net.sci.image.Calibration)}.
     */
    @Test
    public final void testAnalyzeRegions_BallR10()
    {
        BinaryArray3D array = BinaryArray3D.create(25, 25, 25);
        double x0 = 12.23;
        double y0 = 12.34;
        double z0 = 12.45;
        double r = 10.0;
        array.fillBooleans((x,y,z) -> Math.hypot(Math.hypot(x-x0,  y-y0), z-z0) < r);
        
        IntrinsicVolumes3D algo = new IntrinsicVolumes3D();
        IntrinsicVolumes3D.Result[] results = algo.analyzeRegions(array, new int[]{1}, new Calibration(3));
        
        assertEquals(1, results.length);
        IntrinsicVolumes3D.Result result = results[0];
        
        assertEquals(4188.0, result.volume, 80.0);
        assertEquals(1256.6, result.surfaceArea, 60.0);
        assertEquals(20.0, result.meanBreadth, 1.0);
        assertEquals(1.0, result.eulerNumber, 0.01);
    }
    
    /**
     * Test method for {@link net.sci.image.analyze.region3d.RegionAnalyzer3D#analyzeRegions(net.sci.array.numeric.IntArray3D, net.sci.image.Calibration)}.
     */
    @Test
    public final void test_analyzeRegions_allLabels()
    {
        IntArray3D<?> array = createEightRegionsLabelMap();
        
        IntrinsicVolumes3D algo = new IntrinsicVolumes3D();
        Map<Integer, IntrinsicVolumes3D.Result> map = algo.analyzeRegions(array, new Calibration(3));
        
        assertEquals(map.size(), 8);
        IntrinsicVolumes3D.Result res3 = map.get(3);
        assertEquals(res3.volume, 1, 0.01);
        assertEquals(res3.eulerNumber, 1, 0.01);
        IntrinsicVolumes3D.Result res9 = map.get(17);
        assertEquals(res9.volume, 64, 0.01);
        assertEquals(res9.eulerNumber, 1, 0.01);
    }
    
    /**
     * Test method for {@link net.sci.image.analyze.region3d.RegionAnalyzer3D#analyzeRegions(net.sci.array.numeric.IntArray3D, int[], net.sci.image.Calibration)}.
     */
    @Test
    public final void test_analyzeRegions_selectedLabels()
    {
        IntArray3D<?> array = createEightRegionsLabelMap();
        int[] labels = new int[] {3, 5, 9, 13, 17};
        
        IntrinsicVolumes3D algo = new IntrinsicVolumes3D();
        IntrinsicVolumes3D.Result[] results = algo.analyzeRegions(array, labels, new Calibration(3));
        
        assertEquals(results.length, 5);
        IntrinsicVolumes3D.Result res3 = results[0];
        assertEquals(res3.volume, 1, 0.01);
        assertEquals(res3.eulerNumber, 1, 0.01);
        IntrinsicVolumes3D.Result res5 = results[1];
        assertEquals(res5.volume, 4, 0.01);
        assertEquals(res5.eulerNumber, 1, 0.01);
        IntrinsicVolumes3D.Result res9 = results[2];
        assertEquals(res9.volume, 16, 0.01);
        assertEquals(res9.eulerNumber, 1, 0.01);
        IntrinsicVolumes3D.Result res17 = results[4];
        assertEquals(res17.volume, 64, 0.01);
        assertEquals(res17.eulerNumber, 1, 0.01);
    }
    
    
    /**
     * Creates a label map containing eight regions, with labels
     * 3, 5, 7, 9, 11, 13, 15, 17.
     * 
     * @return a label map containing eight regions.
     */
    private static final UInt8Array3D createEightRegionsLabelMap()
    {
        UInt8Array3D array = UInt8Array3D.create(8, 8, 8);
        
        // single voxel region
        array.setInt(1, 1, 1, 3);
        
        // lines of four voxels
        for (int i = 3; i < 7; i++)
        {
            array.setInt(i, 1, 1,  5);
            array.setInt(1, i, 1,  7);
            array.setInt(1, 1, i, 11);
        }
        
        // 4x4 voxels planes
        for (int i = 3; i < 7; i++)
        {
            for (int j = 3; j < 7; j++)
            {
                array.setInt(i, j, 1,  9);
                array.setInt(i, 1, j, 13);
                array.setInt(1, i, j, 15);
            }
        }        
        // 4x4x4 cubic region
        for (int i = 3; i < 7; i++)
        {
            for (int j = 3; j < 7; j++)
            {
                for (int k = 3; k < 7; k++)
                {
                    array.setInt(i, j, k, 17);
                }
            }
        }        
        return array;
    }
}
