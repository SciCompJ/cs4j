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
        array.setValue(2, 0, 0);
        array.setValue(2, 0, 1);
        array.setValue(2, 1, 0);
        array.setValue(2, 1, 1);
        for (int i = 2; i < 5; i++)
        {
            array.setValue(4, 0, i);
            array.setValue(4, 1, i);
            array.setValue(6, i, 0);
            array.setValue(6, i, 1);
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
