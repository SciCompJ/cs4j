/**
 * 
 */
package net.sci.image.analyze.region2d;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.scalar.UInt8Array2D;
import net.sci.geom.geom2d.polygon.Polygon2D;

/**
 * @author dlegland
 *
 */
public class ConvexHullTest
{
    
    /**
     * Test method for {@link net.sci.image.analyze.region2d.ConvexHull#convexHull(net.sci.array.scalar.IntArray2D, int[])}.
     */
    @Test
    public final void testConvexHullIntArray2DOfQIntArray()
    {
        UInt8Array2D array = UInt8Array2D.create(2, 2);
        array.fillValue(2);

        Polygon2D[] hulls = ConvexHull.convexHull(array, new int[] { 2 });
        
        assertEquals(1, hulls.length);
        Polygon2D hull0 = hulls[0];
        assertEquals(8, hull0.vertexNumber());
    }
    
    /**
     * Test method for {@link net.sci.image.analyze.region2d.ConvexHull#convexHull(net.sci.array.scalar.IntArray2D, int[])}.
     */
    @Test
    public final void testConvexHullIntArray2DOfQIntArray_FourRegions()
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

        Polygon2D[] hulls = ConvexHull.convexHull(array, new int[] { 2, 4, 6, 8 });
        
        assertEquals(4, hulls.length);
        
        Polygon2D hull2 = hulls[0];
        assertEquals(8, hull2.vertexNumber());

        Polygon2D hull4 = hulls[1];
        assertEquals(8, hull4.vertexNumber());

        Polygon2D hull6 = hulls[2];
        assertEquals(8, hull6.vertexNumber());

        Polygon2D hull8 = hulls[3];
        assertEquals(8, hull8.vertexNumber());
    }
    
}
