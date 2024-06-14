/**
 * 
 */
package net.sci.image.morphology.watershed;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import net.sci.array.binary.BinaryArray2D;
import net.sci.array.scalar.Float32Array2D;
import net.sci.array.scalar.Int32Array2D;
import net.sci.array.scalar.IntArray2D;
import net.sci.array.scalar.UInt8Array2D;
import net.sci.geom.geom2d.MultiPoint2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.image.Connectivity2D;
import net.sci.image.binary.BinaryImages;
import net.sci.image.morphology.MinimaAndMaxima;

/**
 * 
 */
public class MeyerMarkerControlledWatershed2DTest
{
    /**
     * Test method for {@link net.sci.image.morphology.watershed.MeyerMarkerControlledWatershed2D#processInPlace(net.sci.array.scalar.ScalarArray2D, net.sci.array.scalar.IntArray2D)}.
     */
    @Test
    public final void testProcessInPlace()
    {
        UInt8Array2D relief = UInt8Array2D.fromIntArray(new int[][] {
            { 4, 13, 25, 18, 6},
            { 0, 10, 30, 15, 5},
            { 8, 12, 20, 17, 7},
        });
        
        Connectivity2D conn = Connectivity2D.C4;
        BinaryArray2D minima = MinimaAndMaxima.regionalMinima(relief, conn);
        IntArray2D<?> markers = BinaryImages.componentsLabeling(minima, conn, 32);
        
        MeyerMarkerControlledWatershed2D algo = new MeyerMarkerControlledWatershed2D();
        algo.connectivity = conn;
        algo.processInPlace(relief, markers);
        
        IntArray2D<?> res = markers;
//        System.out.println("final result:");
//        res.printContent();
        for (int y = 0; y < res.size(1); y++)
        {
            assertEquals(res.getInt(0, y), 1);
            assertEquals(res.getInt(1, y), 1);
            assertEquals(res.getInt(2, y), 0);
            assertEquals(res.getInt(3, y), 2);
            assertEquals(res.getInt(4, y), 2);
		}	
    }

    @Test
    public final void test_fiveGrains()
    {
        // initialize a set of germs
        MultiPoint2D germs = MultiPoint2D.create(5);
        germs.addPoint(new Point2D(10, 10));
        germs.addPoint(new Point2D(10, 40));
        germs.addPoint(new Point2D(40, 10));
        germs.addPoint(new Point2D(40, 40));
        germs.addPoint(new Point2D(25, 25));
        
        // compute the distance map to the closest germ
        Float32Array2D relief = Float32Array2D.create(50, 50);
        relief.fillValues((x,y) -> germs.distance(x, y));

		// apply watershed
        Watershed2D algo = new Watershed2D(Connectivity2D.C4);
        IntArray2D<?> basins = algo.process(relief);

        // extract some labels, one in each region
        int[] labels = new int[5];
        labels[0] = basins.getInt( 5,  5);
        labels[1] = basins.getInt( 5, 45);
        labels[2] = basins.getInt(45,  5);
        labels[3] = basins.getInt(45, 45);
        labels[4] = basins.getInt(24, 24);
        
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
    
    @Test
    public final void test_FourCrossing_watershed()
    {
        UInt8Array2D relief = UInt8Array2D.fromIntArray(new int[][] {
            { 1,  2, 21,  5,  4},
            { 3,  4, 22,  7,  6},
            {10, 11, 50, 40, 41},
            { 6,  5, 23,  5,  7},
            { 3,  4, 24,  4,  3},
        });
        Int32Array2D labelMap =Int32Array2D.fromIntArray(new int[][] {
            {1, 0, 0, 0, 2}, 
            {0, 0, 0, 0, 0}, 
            {0, 0, 0, 0, 0}, 
            {0, 0, 0, 0, 0}, 
            {3, 0, 0, 0, 4}, 
        });
        
        Connectivity2D conn = Connectivity2D.C4;
        
        MeyerMarkerControlledWatershed2D algo = new MeyerMarkerControlledWatershed2D();
        algo.connectivity = conn;
        algo.processInPlace(relief, labelMap);
        
        assertEquals(labelMap.getInt(1, 1), 1);
        assertEquals(labelMap.getInt(3, 1), 2);
        assertEquals(labelMap.getInt(1, 3), 3);
        assertEquals(labelMap.getInt(3, 3), 4);
        assertEquals(labelMap.getInt(2, 2), 0);
//        labelMap.printContent();
    }
    
    
    /**
     * Test method for {@link net.sci.image.morphology.watershed.MeyerMarkerControlledWatershed2D#processInPlace(net.sci.array.scalar.ScalarArray2D, net.sci.array.scalar.IntArray2D)}.
     */
    @Test
    public final void test_invertedWedge()
    {
        UInt8Array2D relief = UInt8Array2D.fromIntArray(new int[][] {
            { 4, 13, 25, 18, 6},
            { 0, 60, 30, 70, 6},
            { 4, 50, 30, 80, 5},
            { 5, 40, 30, 70, 1},
            { 8, 12, 20, 17, 7},
        });
        
        Connectivity2D conn = Connectivity2D.C4;
        BinaryArray2D minima = MinimaAndMaxima.regionalMinima(relief, conn);
        
        MeyerMarkerControlledWatershed2D algo = new MeyerMarkerControlledWatershed2D();
        algo.connectivity = conn;
        IntArray2D<?> res = algo.process(relief, minima);
        
//        System.out.println("final result:");
//        res.printContent();
        
        assertEquals(res.getInt(0, 0), 1);
        assertEquals(res.getInt(0, 4), 1);
        assertEquals(res.getInt(4, 0), 2);
        assertEquals(res.getInt(4, 4), 2);
        
        assertNotEquals(res.getInt(2, 2), 0);
    }

}
