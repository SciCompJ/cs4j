/**
 * 
 */
package net.sci.image.morphology.watershed;

import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import net.sci.array.numeric.Float32Array2D;
import net.sci.array.numeric.IntArray2D;
import net.sci.geom.geom2d.MultiPoint2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.image.connectivity.Connectivity2D;

/**
 * @author dlegland
 *
 */
public class Watershed2DTest
{
    
    /**
     * Test method for {@link net.sci.image.morphology.watershed.Watershed2D#process(net.sci.array.scalar.ScalarArray2D)}.
     */
    @Test
    public final void testProcess()
    {
        // initialize a set of germs
        MultiPoint2D germs = MultiPoint2D.create(5);
        germs.addPoint(new Point2D(20, 20));
        germs.addPoint(new Point2D(20, 80));
        germs.addPoint(new Point2D(80, 20));
        germs.addPoint(new Point2D(80, 80));
        germs.addPoint(new Point2D(50, 50));
        
        // compute the distance map to the closest germ
        Float32Array2D array = Float32Array2D.create(100, 100);
        array.fillValues((x,y) -> germs.distance(x, y));

        // apply watershed
        Watershed2D algo = new Watershed2D(Connectivity2D.C4);
        IntArray2D<?> basins = algo.process(array);

        // extract some labels, one in each region
        int[] labels = new int[5];
        labels[0] = basins.getInt(10, 10);
        labels[1] = basins.getInt(10, 90);
        labels[2] = basins.getInt(90, 10);
        labels[3] = basins.getInt(90, 90);
        labels[4] = basins.getInt(48, 48);
        
        // labels should not be 0
        for (int i = 0; i < 5; i++)
        {
            assertNotEquals(0, labels[i]);
        }

        // should not have twice the same label
        for (int i = 0; i < 4; i++)
        {
            for (int j = i+1; j < 5; j++)
            {
                assertNotEquals(labels[i], labels[j]);
            }
        }
    }
    
}
