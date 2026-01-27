/**
 * 
 */
package net.sci.geom.polygon2d;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.geom.geom2d.Bounds2D;
import net.sci.geom.geom2d.Point2D;

/**
 * 
 */
public class Polygon2DTest
{
    
    /**
     * Test method for {@link net.sci.geom.polygon2d.Polygon2D#fromBounds(net.sci.geom.geom2d.Bounds2D)}.
     */
    @Test
    public final void test_fromBounds()
    {
        Bounds2D bounds = new Bounds2D(10, 50, 20, 40);
        Polygon2D poly = Polygon2D.fromBounds(bounds);
        
        assertEquals(4, poly.vertexCount());
        
        assertTrue(bounds.almostEquals(poly.bounds(), 0.01));
    }
    
    /**
     * Test method for {@link net.sci.geom.polygon2d.Polygon2D#centroid()}.
     */
    @Test
    public final void test_centroid()
    {
        Point2D p1 = new Point2D(10, 10);
        Point2D p2 = new Point2D(50, 10);
        Point2D p3 = new Point2D(50, 30);
        Point2D p4 = new Point2D(10, 30);
        Polygon2D poly = Polygon2D.create(p1, p2, p3, p4);
        
        Point2D centroid = poly.centroid();

        assertTrue(new Point2D(30, 20).almostEquals(centroid, 0.01));
    }
    
}
