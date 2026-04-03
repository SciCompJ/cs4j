/**
 * 
 */
package net.sci.geom.polygon2d;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.geom.geom2d.Point2D;

/**
 * 
 */
public class MultiLinearRing2DTest
{
    /**
     * Test method for {@link net.sci.geom.polygon2d.MultiLinearRing2D#signedDistance(net.sci.geom.geom2d.Point2D)}.
     */
    @Test
    public final void test_signedDistancePoint2D_twoRings()
    {
        MultiLinearRing2D curve = twoRings();
        
        assertEquals(0.0, curve.signedDistance(new Point2D(10, 10)), 0.01);
        assertEquals(0.0, curve.signedDistance(new Point2D(20, 20)), 0.01);

        assertEquals(-5.0, curve.signedDistance(new Point2D(15, 15)), 0.01);
        assertEquals(-5.0, curve.signedDistance(new Point2D(35, 15)), 0.01);
        
        assertEquals( 5.0, curve.signedDistance(new Point2D( 5, 15)), 0.01);
        assertEquals( 5.0, curve.signedDistance(new Point2D(25, 15)), 0.01);
        assertEquals( 5.0, curve.signedDistance(new Point2D(45, 15)), 0.01);
    }

    /**
     * Test method for {@link net.sci.geom.polygon2d.MultiLinearRing2D#signedDistance(net.sci.geom.geom2d.Point2D)}.
     */
    @Test
    public final void test_distancePoint2D_twoRings()
    {
        MultiLinearRing2D curve = twoRings();
        
        assertEquals(0.0, curve.distance(new Point2D(10, 10)), 0.01);
        assertEquals(0.0, curve.distance(new Point2D(20, 20)), 0.01);

        assertEquals( 5.0, curve.distance(new Point2D(15, 15)), 0.01);
        assertEquals( 5.0, curve.distance(new Point2D(35, 15)), 0.01);
        
        assertEquals( 5.0, curve.distance(new Point2D( 5, 15)), 0.01);
        assertEquals( 5.0, curve.distance(new Point2D(25, 15)), 0.01);
        assertEquals( 5.0, curve.distance(new Point2D(45, 15)), 0.01);
    }

    /**
     * Test method for {@link net.sci.geom.polygon2d.MultiLinearRing2D#signedDistance(net.sci.geom.geom2d.Point2D)}.
     */
    @Test
    public final void test_signedDistancePoint2D_nestedRings()
    {
        MultiLinearRing2D curve = nestedRings();
        
        assertEquals(0.0, curve.signedDistance(new Point2D(10, 10)), 0.01);
        assertEquals(0.0, curve.signedDistance(new Point2D(20, 20)), 0.01);

        assertEquals(-2.0, curve.signedDistance(new Point2D(12, 13)), 0.01);
        assertEquals(+2.0, curve.signedDistance(new Point2D(22, 23)), 0.01);
    }

    /**
     * Test method for {@link net.sci.geom.polygon2d.MultiLinearRing2D#signedDistance(net.sci.geom.geom2d.Point2D)}.
     */
    @Test
    public final void test_distancePoint2D_nestedRings()
    {
        MultiLinearRing2D curve = nestedRings();
        
        assertEquals(0.0, curve.distance(new Point2D(10, 10)), 0.01);
        assertEquals(0.0, curve.distance(new Point2D(20, 20)), 0.01);

        assertEquals(2.0, curve.distance(new Point2D(12, 13)), 0.01);
        assertEquals(2.0, curve.distance(new Point2D(22, 23)), 0.01);
    }

    /**
     * Test method for {@link net.sci.geom.polygon2d.MultiLinearRing2D#contains(net.sci.geom.geom2d.Point2D, double)}.
     */
    @Test
    public final void test_contains_twoRings()
    {
        MultiLinearRing2D curve = twoRings();
        
        assertTrue(curve.contains(new Point2D(10, 10), 0.01));
        assertTrue(curve.contains(new Point2D(40, 20), 0.01));

        assertTrue(curve.contains(new Point2D(10, 15), 0.01));
        assertTrue(curve.contains(new Point2D(30, 15), 0.01));
        
        assertFalse(curve.contains(new Point2D( 5, 15), 0.01));
        assertFalse(curve.contains(new Point2D(35, 15), 0.01));
    }

    /**
     * Test method for {@link net.sci.geom.polygon2d.MultiLinearRing2D#contains(net.sci.geom.geom2d.Point2D, double)}.
     */
    @Test
    public final void test_contains_nestedRings()
    {
        MultiLinearRing2D curve = nestedRings();
        
        assertTrue(curve.contains(new Point2D(10, 10), 0.01));
        assertTrue(curve.contains(new Point2D(40, 20), 0.01));

        assertTrue(curve.contains(new Point2D(10, 15), 0.01));
        assertTrue(curve.contains(new Point2D(20, 25), 0.01));
        
        assertFalse(curve.contains(new Point2D( 5, 15), 0.01));
        assertFalse(curve.contains(new Point2D(35, 15), 0.01));
    }

    private static final MultiLinearRing2D twoRings()
    {
        LinearRing2D ring1 = LinearRing2D.create(new Point2D(10, 10),new Point2D(20, 10),new Point2D(20, 20),new Point2D(10, 20));
        LinearRing2D ring2 = LinearRing2D.create(new Point2D(30, 10),new Point2D(40, 10),new Point2D(40, 20),new Point2D(30, 20));
        return MultiLinearRing2D.from(ring1, ring2);
    }

    private static final MultiLinearRing2D nestedRings()
    {
        LinearRing2D ring1 = LinearRing2D.create(new Point2D(10, 10),new Point2D(40, 10),new Point2D(40, 40),new Point2D(10, 40));
        LinearRing2D ring2 = LinearRing2D.create(new Point2D(20, 20),new Point2D(20, 30),new Point2D(30, 30),new Point2D(30, 20));
        return MultiLinearRing2D.from(ring1, ring2);
    }

}
