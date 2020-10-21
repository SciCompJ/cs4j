/**
 * 
 */
package net.sci.image.analyze.region2d;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import net.sci.array.scalar.UInt8Array2D;
import net.sci.geom.geom2d.Point2D;

/**
 * @author dlegland
 *
 */
public class RegionBoundariesTest
{
    
    /**
     * Test method for {@link net.sci.image.analyze.region2d.RegionBoundaries#boundaryPixelsMiddleEdges(net.sci.array.scalar.IntArray2D, int[])}.
     */
    @Test
    public final void testBoundaryPixelsMiddleEdgesIntArray2DOfQIntArray_singleRegions()
    {
        UInt8Array2D array = UInt8Array2D.create(2, 2);
        array.fillValue(2);
        
        ArrayList<Point2D>[] arrays = RegionBoundaries.boundaryPixelsMiddleEdges(array, new int[] {2});
        
        assertEquals(arrays.length, 1);
        ArrayList<Point2D> points = arrays[0];
        assertEquals(points.size(), 8);
    }
    
    /**
     * Test method for {@link net.sci.image.analyze.region2d.ConvexHull#convexHull(net.sci.array.scalar.IntArray2D, int[])}.
     */
    @Test
    public final void testBoundaryPixelsMiddleEdgesIntArray2DOfQIntArray_FourRegions()
    {
        UInt8Array2D array = UInt8Array2D.create(5, 5);
        array.fillValue(8);
        array.setValue(0, 0, 2);
        array.setValue(0, 1, 2);
        array.setValue(1, 0, 2);
        array.setValue(1, 1, 2);
        for (int i = 2; i < 5; i++)
        {
            array.setValue(0, i, 4);
            array.setValue(1, i, 4);
            array.setValue(i, 0, 6);
            array.setValue(i, 1, 6);
        }

        ArrayList<Point2D>[] arrays = RegionBoundaries.boundaryPixelsMiddleEdges(array, new int[] { 2, 4, 6, 8 });
        
        assertEquals(4, arrays.length);
        
        ArrayList<Point2D> pts2 = arrays[0];
        assertEquals(8, pts2.size());

        ArrayList<Point2D> pts4 = arrays[1];
        assertEquals(10, pts4.size());

        ArrayList<Point2D> pts6 = arrays[2];
        assertEquals(10, pts6.size());

        ArrayList<Point2D> pts8 = arrays[3];
        assertEquals(12, pts8.size());
    }
}
