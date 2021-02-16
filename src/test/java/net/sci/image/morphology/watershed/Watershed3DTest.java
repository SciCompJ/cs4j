/**
 * 
 */
package net.sci.image.morphology.watershed;

import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import net.sci.array.scalar.Float32Array3D;
import net.sci.array.scalar.IntArray3D;
import net.sci.geom.geom3d.MultiPoint3D;
import net.sci.geom.geom3d.Point3D;
import net.sci.image.data.Connectivity3D;

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
    public final void testProcess()
    {
        // initialize a set of germs
        MultiPoint3D germs = MultiPoint3D.create(5);
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
        array.populateValues((x,y,z) -> germs.distance(x, y, z));
        
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
    
}
