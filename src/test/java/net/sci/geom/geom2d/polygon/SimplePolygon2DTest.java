/**
 * 
 */
package net.sci.geom.geom2d.polygon;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import net.sci.geom.geom2d.Box2D;
import net.sci.geom.geom2d.Point2D;

/**
 * @author dlegland
 *
 */
public class SimplePolygon2DTest
{
    
    /**
     * Test method for {@link net.sci.geom.geom2d.polygon.SimplePolygon2D#signedArea()}.
     */
    @Test
    public final void testSignedArea()
    {
        SimplePolygon2D poly = createDiamondPolygon();
        double exp = 2 * 10 * 10;
        assertEquals(exp, poly.signedArea(), .01);
    }
    
    /**
     * Test method for {@link net.sci.geom.geom2d.polygon.SimplePolygon2D#signedArea()}.
     */
    @Test
    public final void testSignedArea_reverse()
    {
        SimplePolygon2D poly = createDiamondPolygon().complement();
        double exp = - 2 * 10 * 10;
        assertEquals(exp, poly.signedArea(), .01);
    }
    
    /**
     * Test method for {@link net.sci.geom.geom2d.polygon.SimplePolygon2D#contains(net.sci.geom.geom2d.Point2D)}.
     */
    @Test
    public final void testContainsPoint2D()
    {
        SimplePolygon2D poly = createDiamondPolygon();
        assertTrue(poly.contains(new Point2D(20, 20)));
        assertTrue(poly.contains(new Point2D(25, 19)));
        assertFalse(poly.contains(new Point2D(14, 14)));
    }
    
    /**
     * Test method for {@link net.sci.geom.geom2d.polygon.SimplePolygon2D#contains(net.sci.geom.geom2d.Point2D)}.
     */
    @Test
    public final void testContainsPoint2D_UShape()
    {
        ArrayList<Point2D> vertices = new ArrayList<>(4);
        vertices.add(new Point2D(10, 10));
        vertices.add(new Point2D(40, 10));
        vertices.add(new Point2D(40, 40));
        vertices.add(new Point2D(30, 40));
        vertices.add(new Point2D(30, 20));
        vertices.add(new Point2D(20, 20));
        vertices.add(new Point2D(20, 40));
        vertices.add(new Point2D(10, 40));
        SimplePolygon2D poly = new SimplePolygon2D(vertices);
        
        assertTrue(poly.contains(new Point2D(13, 13)));
        assertTrue(poly.contains(new Point2D(36, 36)));
        assertFalse(poly.contains(new Point2D(25, 25)));
        assertFalse(poly.contains(new Point2D(25, 40)));
    }
    
    /**
     * Test method for {@link net.sci.geom.geom2d.polygon.SimplePolygon2D#distance(net.sci.geom.geom2d.Point2D)}.
     */
    @Test
    public final void testDistance()
    {
        SimplePolygon2D poly = createDiamondPolygon();
        assertEquals(10, poly.distance(new Point2D(40, 20)), .01);
        assertEquals(5 * Math.sqrt(2), poly.distance(new Point2D(10, 10)), .01);
    }
    
    /**
     * Test method for {@link net.sci.geom.geom2d.polygon.SimplePolygon2D#boundingBox()}.
     */
    @Test
    public final void testBoundingBox()
    {
        SimplePolygon2D poly = createDiamondPolygon();
        Box2D box = poly.boundingBox();
        Box2D exp = new Box2D(10, 30, 10, 30);
        assertTrue(exp.almostEquals(box, .01));
    }
    
    /**
     * Creates a diamond, with center at (20,20) and four corners at a distance of 10 in each direction. 
     */
    private SimplePolygon2D createDiamondPolygon()
    {
        ArrayList<Point2D> vertices = new ArrayList<>(4);
        vertices.add(new Point2D(20, 10));
        vertices.add(new Point2D(30, 20));
        vertices.add(new Point2D(20, 30));
        vertices.add(new Point2D(10, 20));
        return new SimplePolygon2D(vertices);
    }
}