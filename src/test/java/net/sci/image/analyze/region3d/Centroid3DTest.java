/**
 * 
 */
package net.sci.image.analyze.region3d;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.numeric.UInt8Array3D;
import net.sci.geom.geom3d.Point3D;
import net.sci.image.Calibration;

/**
 * @author dlegland
 *
 */
public class Centroid3DTest
{

    /**
     * Test method for {@link net.sci.image.analyze.region3d.Centroid3D#analyzeRegions(net.sci.array.scalar.IntArray3D, int[], net.sci.image.Calibration)}.
     */
    @Test
    public final void testAnalyzeRegions_EightCubes()
    {
        UInt8Array3D array = UInt8Array3D.create(10, 10, 10);
        int[] labels = new int[] {2, 3, 4, 7, 8, 10, 12, 15};
        for(int z = 0; z < 3; z++)
        {
            for(int y = 0; y < 3; y++)
            {
                for(int x = 0; x < 3; x++)
                {
                    array.setInt(x + 1, y + 1, z + 1, labels[0]);
                    array.setInt(x + 5, y + 1, z + 1, labels[1]);
                    array.setInt(x + 1, y + 5, z + 1, labels[2]);
                    array.setInt(x + 5, y + 5, z + 1, labels[3]);
                    array.setInt(x + 1, y + 1, z + 5, labels[4]);
                    array.setInt(x + 5, y + 1, z + 5, labels[5]);
                    array.setInt(x + 1, y + 5, z + 5, labels[6]);
                    array.setInt(x + 5, y + 5, z + 5, labels[7]);
                }
            }
        }
        
        Calibration calib = new Calibration(3);
        
        Centroid3D algo = new Centroid3D();
        Point3D[] centroids = algo.analyzeRegions(array, labels, calib);
        
        assertEquals(8, centroids.length);
        assertTrue(new Point3D(2, 2, 2).almostEquals(centroids[0], 0.01));
        assertTrue(new Point3D(6, 2, 2).almostEquals(centroids[1], 0.01));
        assertTrue(new Point3D(2, 6, 2).almostEquals(centroids[2], 0.01));
        assertTrue(new Point3D(6, 6, 2).almostEquals(centroids[3], 0.01));
        assertTrue(new Point3D(2, 2, 6).almostEquals(centroids[4], 0.01));
        assertTrue(new Point3D(6, 2, 6).almostEquals(centroids[5], 0.01));
        assertTrue(new Point3D(2, 6, 6).almostEquals(centroids[6], 0.01));
        assertTrue(new Point3D(6, 6, 6).almostEquals(centroids[7], 0.01));
    }
}
