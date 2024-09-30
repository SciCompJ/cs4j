/**
 * 
 */
package net.sci.image.analyze.region2d;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.binary.BinaryArray2D;
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
}
