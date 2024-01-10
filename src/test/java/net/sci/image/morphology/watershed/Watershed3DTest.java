/**
 * 
 */
package net.sci.image.morphology.watershed;

import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import net.sci.array.binary.BinaryArray3D;
import net.sci.array.scalar.Float32Array3D;
import net.sci.array.scalar.IntArray3D;
import net.sci.geom.geom3d.MultiPoint3D;
import net.sci.geom.geom3d.Point3D;
import net.sci.image.Connectivity3D;
import net.sci.image.binary.BinaryImages;
import net.sci.image.morphology.extrema.RegionalExtrema3D;

/**
 * @author dlegland
 *
 */
public class Watershed3DTest
{
    
    /**
     * Test method for {@link net.sci.image.morphology.watershed.Watershed3D#process(net.sci.array.scalar.ScalarArray3D)}.
     */
    @Test
    public final void test_process()
    {
        // Initialize a 3D float array corresponding to distance map to nine
        // germs
        Float32Array3D array = createNineGermsDistanceMap();
        
        // apply watershed
        Watershed3D algo = new Watershed3D(Connectivity3D.C6);
        IntArray3D<?> basins = algo.process(array);

        // extract some labels, one in each region
        int[] labels = new int[9];
        labels[0] = basins.getInt(1, 1, 1);
        labels[1] = basins.getInt(1, 9, 1);
        labels[2] = basins.getInt(9, 1, 1);
        labels[3] = basins.getInt(9, 9, 1);
        labels[4] = basins.getInt(1, 1, 9);
        labels[5] = basins.getInt(1, 9, 9);
        labels[6] = basins.getInt(9, 1, 9);
        labels[7] = basins.getInt(9, 9, 9);
        labels[8] = basins.getInt(5, 5, 5);
        
        // labels should not be 0
        for (int i = 0; i < 9; i++)
        {
            assertNotEquals(0, labels[i]);
        }

        // should not have twice the same label
        for (int i = 0; i < 8; i++)
        {
            for (int j = i+1; j < 9; j++)
            {
                assertNotEquals(labels[i], labels[j]);
            }
        }
    }
    
    /**
     * Test method for
     * {@link net.sci.image.morphology.watershed.Watershed3D#process(net.sci.array.scalar.ScalarArray3D,net.sci.array.scalar.IntArray3D)}.
     */
    @Test
    public final void test_process_ArrayWithMarkers()
    {
        // Initialize a 3D float array corresponding to distance map to nine
        // germs
        Float32Array3D array = createNineGermsDistanceMap();
        
        // identifies markers as label map
        BinaryArray3D minima = BinaryArray3D.create(array.size(0), array.size(1), array.size(2));
        RegionalExtrema3D minimaAlgo = new RegionalExtrema3D();
        minimaAlgo.process(array, minima);
        IntArray3D<?> markers = BinaryImages.componentsLabeling(minima, Connectivity3D.C6, 32);
        
        MarkerBasedWatershed3D watershedAlgo = new MarkerBasedWatershed3D(Connectivity3D.C6);
        IntArray3D<?> basins = watershedAlgo.process(array, markers);
        
        // extract some labels, one in each region
        int[] labels = new int[9];
        labels[0] = basins.getInt(1, 1, 1);
        labels[1] = basins.getInt(1, 9, 1);
        labels[2] = basins.getInt(9, 1, 1);
        labels[3] = basins.getInt(9, 9, 1);
        labels[4] = basins.getInt(1, 1, 9);
        labels[5] = basins.getInt(1, 9, 9);
        labels[6] = basins.getInt(9, 1, 9);
        labels[7] = basins.getInt(9, 9, 9);
        labels[8] = basins.getInt(5, 5, 5);
        
        // labels should not be 0
        for (int i = 0; i < 9; i++)
        {
            assertNotEquals(0, labels[i]);
        }

        // should not have twice the same label
        for (int i = 0; i < 8; i++)
        {
            for (int j = i+1; j < 9; j++)
            {
                assertNotEquals(labels[i], labels[j]);
            }
        }
    }
    
    private static final Float32Array3D createNineGermsDistanceMap()
    {
        // initialize a set of germs
        MultiPoint3D germs = MultiPoint3D.create(9);
        germs.addPoint(new Point3D(2, 2, 2));
        germs.addPoint(new Point3D(8, 2, 2));
        germs.addPoint(new Point3D(2, 8, 2));
        germs.addPoint(new Point3D(8, 8, 2));
        germs.addPoint(new Point3D(2, 2, 8));
        germs.addPoint(new Point3D(8, 2, 8));
        germs.addPoint(new Point3D(2, 8, 8));
        germs.addPoint(new Point3D(8, 8, 8));
        germs.addPoint(new Point3D(5, 5, 5));
        
        // compute the distance map to the closest germ
        Float32Array3D array = Float32Array3D.create(10, 10, 10);
        array.fillValues((x,y,z) -> germs.distance(x, y, z));

        return array;
    }
}
