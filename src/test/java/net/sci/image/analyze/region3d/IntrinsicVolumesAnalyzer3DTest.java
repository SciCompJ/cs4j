/**
 * 
 */
package net.sci.image.analyze.region3d;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.binary.BinaryArray3D;
import net.sci.image.Calibration;

/**
 * @author dlegland
 *
 */
public class IntrinsicVolumesAnalyzer3DTest
{

    /**
     * Test method for {@link net.sci.image.analyze.region3d.IntrinsicVolumesAnalyzer3D#analyzeRegions(net.sci.array.scalar.IntArray3D, int[], net.sci.image.Calibration)}.
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
        
        IntrinsicVolumesAnalyzer3D algo = new IntrinsicVolumesAnalyzer3D();
        IntrinsicVolumesAnalyzer3D.Result[] results = algo.analyzeRegions(array, new int[]{1}, new Calibration(3));
        
        assertEquals(1, results.length);
        IntrinsicVolumesAnalyzer3D.Result result = results[0];
        
        assertEquals(4188.0, result.volume, 80.0);
        assertEquals(1256.6, result.surfaceArea, 60.0);
        assertEquals(20.0, result.meanBreadth, 1.0);
        assertEquals(1.0, result.eulerNumber, 0.01);
    }
}
