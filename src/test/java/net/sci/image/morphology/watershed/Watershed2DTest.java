/**
 * 
 */
package net.sci.image.morphology.watershed;

import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;

import org.junit.Test;

import net.sci.array.scalar.Float32Array2D;
import net.sci.array.scalar.IntArray2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.image.data.Connectivity2D;

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
        ArrayList<Point2D> points = new ArrayList<Point2D>();
        points.add(new Point2D(20, 20));
        points.add(new Point2D(20, 80));
        points.add(new Point2D(80, 20));
        points.add(new Point2D(80, 80));
        points.add(new Point2D(50, 50));
        
        // compute the distance map to the closest germ
        Float32Array2D array = Float32Array2D.create(100, 100);
        for (int y = 0; y < 100; y++)
        {
            for (int x = 0; x < 100; x++)
            {
                double minDist = Double.POSITIVE_INFINITY;
                for (Point2D p : points)
                {
                    minDist = Math.min(minDist, p.distance(x, y));
                }
                array.setValue(x, y, minDist);
            }
        }

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
