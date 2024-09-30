/**
 * 
 */
package net.sci.image.analyze.region2d;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import net.sci.array.binary.BinaryArray2D;
import net.sci.array.numeric.IntArray2D;
import net.sci.array.numeric.UInt8Array2D;
import net.sci.image.Calibration;

/**
 * @author dlegland
 *
 */
public class IntrinsicVolumes2DTest
{

    /**
     * Test method for {@link net.sci.image.analyze.region2d.IntrinsicVolumes2D#analyzeRegions(net.sci.array.scalar.IntArray2D, int[], net.sci.image.Calibration)}.
     */
    @Test
    public final void testAnalyzeRegions_DiskR10()
    {
        BinaryArray2D array = BinaryArray2D.create(30, 30);
        double x0 = 15.23;
        double y0 = 15.34;
        double r = 10.0;
        array.fillBooleans((x,y) -> Math.hypot(x-x0,  y-y0) < r);
        
        IntrinsicVolumes2D algo = new IntrinsicVolumes2D();
        IntrinsicVolumes2D.Result[] results = algo.analyzeRegions(array, new int[]{1}, new Calibration(2));
        
        assertEquals(1, results.length);
        IntrinsicVolumes2D.Result result = results[0];
        
        assertEquals(314.15, result.area, 3.0);
        assertEquals(62.82, result.perimeter, 0.6);
        assertEquals(1.0, result.eulerNumber, 0.01);
    }
    
    /**
     * Test method for {@link net.sci.image.analyze.region2d.RegionAnalyzer2D#analyzeRegions(net.sci.array.numeric.IntArray2D, net.sci.image.Calibration)}.
     */
    @Test
    public final void test_analyzeRegions_allLabels()
    {
        IntArray2D<?> array = createFourRegionsLabelMap();
        
        IntrinsicVolumes2D algo = new IntrinsicVolumes2D();
        Map<Integer, IntrinsicVolumes2D.Result> map = algo.analyzeRegions(array, new Calibration(2));
        
        assertEquals(map.size(), 4);
        IntrinsicVolumes2D.Result res3 = map.get(3);
        assertEquals(res3.area, 1, 0.01);
        assertEquals(res3.eulerNumber, 1, 0.01);
        IntrinsicVolumes2D.Result res9 = map.get(9);
        assertEquals(res9.area, 16, 0.01);
        assertEquals(res9.eulerNumber, 1, 0.01);
    }
    
    /**
     * Test method for {@link net.sci.image.analyze.region2d.RegionAnalyzer2D#analyzeRegions(net.sci.array.numeric.IntArray2D, int[], net.sci.image.Calibration)}.
     */
    @Test
    public final void test_analyzeRegions_selectedLabels()
    {
        IntArray2D<?> array = createFourRegionsLabelMap();
        int[] labels = new int[] {3, 5, 9};
        
        IntrinsicVolumes2D algo = new IntrinsicVolumes2D();
        IntrinsicVolumes2D.Result[] results = algo.analyzeRegions(array, labels, new Calibration(2));
        
        assertEquals(results.length, 3);
        IntrinsicVolumes2D.Result res3 = results[0];
        assertEquals(res3.area, 1, 0.01);
        assertEquals(res3.eulerNumber, 1, 0.01);
        IntrinsicVolumes2D.Result res9 = results[2];
        assertEquals(res9.area, 16, 0.01);
        assertEquals(res9.eulerNumber, 1, 0.01);
    }
    
    /**
     * Creates a label map containing four regions.
     * 
     * @return a label map containing four regions.
     */
    private static final UInt8Array2D createFourRegionsLabelMap()
    {
        UInt8Array2D array = UInt8Array2D.fromIntArray(new int[][] {
            {0, 0, 0, 0, 0, 0, 0, 0}, 
            {0, 3, 0, 5, 5, 5, 5, 0}, 
            {0, 0, 0, 0, 0, 0, 0, 0}, 
            {0, 8, 0, 9, 9, 9, 9, 0}, 
            {0, 8, 0, 9, 9, 9, 9, 0}, 
            {0, 8, 0, 9, 9, 9, 9, 0}, 
            {0, 8, 0, 9, 9, 9, 9, 0}, 
            {0, 0, 0, 0, 0, 0, 0, 0}, 
        });
        return array;
    }
}
