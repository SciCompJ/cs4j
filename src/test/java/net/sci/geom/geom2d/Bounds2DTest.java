/**
 * 
 */
package net.sci.geom.geom2d;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;

/**
 * 
 */
public class Bounds2DTest
{

    /**
     * Test method for {@link net.sci.geom.geom2d.Bounds2D#of(java.lang.Iterable)}.
     */
    @Test
    public final void testOf()
    {
        // create a collection of points within a lozenge with diameter 20,
        // centered around (20,20)
        Collection<Point2D> pts = new ArrayList<Point2D>();
        pts.add(new Point2D(10, 20));
        pts.add(new Point2D(20, 20));
        pts.add(new Point2D(30, 20));
        pts.add(new Point2D(20, 10));
        pts.add(new Point2D(20, 30));
        
        Bounds2D bounds = Bounds2D.of(pts);
        
        assertTrue(bounds.almostEquals(new Bounds2D(10, 30, 10, 30), 0.01));
    }

    /**
     * Test method for {@link net.sci.geom.geom2d.Bounds2D#union(net.sci.geom.geom2d.Bounds2D)}.
     */
    @Test
    public final void testUnion_disjoint()
    {
        Bounds2D bounds = new Bounds2D(10, 20, 30, 40);
        Bounds2D other = new Bounds2D(30, 40, 50, 60);
        
        Bounds2D union = bounds.union(other);
        
        Bounds2D exp = new Bounds2D(10, 40, 30, 60);
        assertTrue(union.almostEquals(exp, 0.01));
    }

    /**
     * Test method for {@link net.sci.geom.geom2d.Bounds2D#union(net.sci.geom.geom2d.Bounds2D)}.
     */
    @Test
    public final void testUnion_crossed()
    {
        Bounds2D bounds = new Bounds2D(10, 40, 40, 50);
        Bounds2D other = new Bounds2D(20, 30, 30, 60);
        
        Bounds2D union = bounds.union(other);
        
        Bounds2D exp = new Bounds2D(10, 40, 30, 60);
        assertTrue(union.almostEquals(exp, 0.01));
    }

    /**
     * Test method for {@link net.sci.geom.geom2d.Bounds2D#contains(net.sci.geom.geom2d.Point2D)}.
     */
    @Test
    public final void testContainsPoint2D()
    {
        Bounds2D bounds = new Bounds2D(10, 20, 30, 40);
        
        // check within
        assertTrue(bounds.contains(new Point2D(15, 35)));
        
        // check outside
        assertFalse(bounds.contains(new Point2D( 5, 35)));
        assertFalse(bounds.contains(new Point2D(25, 35)));
        assertFalse(bounds.contains(new Point2D(15, 25)));
        assertFalse(bounds.contains(new Point2D(15, 45)));
        
        // check boundary
        assertTrue(bounds.contains(new Point2D(10, 35)));
        assertTrue(bounds.contains(new Point2D(20, 35)));
        assertTrue(bounds.contains(new Point2D(15, 30)));
        assertTrue(bounds.contains(new Point2D(15, 40)));
    }

    /**
     * Test method for {@link net.sci.geom.geom2d.Bounds2D#getSizeX()}.
     */
    @Test
    public final void testGetSizeX()
    {
        Bounds2D bounds = new Bounds2D(10, 50, 20, 30);
        assertEquals(40.0, bounds.getSizeX(), 0.01);
    }

    /**
     * Test method for {@link net.sci.geom.geom2d.Bounds2D#getSizeY()}.
     */
    @Test
    public final void testGetSizeY()
    {
        Bounds2D bounds = new Bounds2D(10, 50, 20, 30);
        assertEquals(10.0, bounds.getSizeY(), 0.01);
    }

}
