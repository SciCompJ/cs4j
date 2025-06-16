/**
 * 
 */
package net.sci.geom.geom2d.polygon;

import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.Test;

import net.sci.geom.geom2d.Point2D;

/**
 * Unit tests for Box2D  class.
 */
public class Box2DTest
{
    
    /**
     * Test method for {@link net.sci.geom.geom2d.polygon.Box2D#fromCorners(net.sci.geom.geom2d.Point2D, net.sci.geom.geom2d.Point2D)}.
     */
    @Test
    public final void testFromCornersPoint2DPoint2D()
    {
        Point2D p1 = new Point2D(50, 40);
        Point2D p2 = new Point2D(10, 20);
        
        Box2D box = Box2D.fromCorners(p1, p2);
        
        assertEquals(10.0, box.xmin, 0.01);
        assertEquals(50.0, box.xmax, 0.01);
        assertEquals(20.0, box.ymin, 0.01);
        assertEquals(40.0, box.ymax, 0.01);
    }
    
    /**
     * Test method for {@link net.sci.geom.geom2d.polygon.Box2D#center()}.
     */
    @Test
    public final void testCenter()
    {
        Point2D p1 = new Point2D(50, 40);
        Point2D p2 = new Point2D(10, 20);
        
        Box2D box = Box2D.fromCorners(p1, p2);
        Point2D center = box.center();
        
        assertTrue(new Point2D(30, 30).almostEquals(center, 0.01));
    }
    
    /**
     * Test method for {@link net.sci.geom.geom2d.polygon.Box2D#vertexPositions()}.
     */
    @Test
    public final void testVertexPositions()
    {
        Point2D p1 = new Point2D(50, 40);
        Point2D p2 = new Point2D(10, 20);
        
        Box2D box = Box2D.fromCorners(p1, p2);
        Collection<Point2D> vertices = box.vertexPositions();
        
        assertTrue(contains(vertices, new Point2D(50, 20), 0.01));
        assertTrue(contains(vertices, new Point2D(50, 40), 0.01));
        assertTrue(contains(vertices, new Point2D(10, 20), 0.01));
        assertTrue(contains(vertices, new Point2D(10, 40), 0.01));
    }
    
    private static final boolean contains(Collection<Point2D> vertices, Point2D v, double tol)
    {
        for (Point2D p : vertices)
        {
            if (p.almostEquals(v, tol)) return true;
        }
        return false;
    }
    
    /**
     * Test method for {@link net.sci.geom.geom2d.polygon.Box2D#vertexCount()}.
     */
    @Test
    public final void testVertexCount()
    {
        Point2D p1 = new Point2D(50, 40);
        Point2D p2 = new Point2D(10, 20);
        
        Box2D box = Box2D.fromCorners(p1, p2);
        
        assertEquals(4, box.vertexCount());
    }
    
}
