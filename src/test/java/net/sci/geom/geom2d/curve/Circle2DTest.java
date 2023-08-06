/**
 * 
 */
package net.sci.geom.geom2d.curve;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 */
public class Circle2DTest
{

    /**
     * Test method for {@link net.sci.geom2d.curve.Circle2D#area()}.
     */
    @Test
    public final void test_area()
    {
        Circle2D circle = new Circle2D(30, 20, 10.0);
        
        double area = circle.area();
        
        double exp = Math.PI * 10.0 * 10.0;
        assertEquals(exp, area, 0.001);
    }

    /**
     * Test method for {@link net.sci.geom2d.curve.Circle2D#area()}.
     */
    @Test
    public final void test_perimeter()
    {
        Circle2D circle = new Circle2D(30, 20, 10.0);
        
        double perim = circle.perimeter();
        
        double exp = 2 * Math.PI * 10.0;
        assertEquals(exp, perim, 0.001);
    }

    /**
     * Test method for {@link net.sci.geom2d.curve.Circle2D#radius()}.
     */
    @Test
    public final void test_radius()
    {
        Circle2D circle = new Circle2D(30, 20, 10.0);
        
        double r = circle.radius();
        
        assertEquals(10, r, 0.001);
    }

    /**
     * Test method for {@link net.sci.geom2d.curve.Circle2D#distance(double, double)}.
     */
    @Test
    public final void testD_distance_onCircle()
    {
        Circle2D circle = new Circle2D(30, 20, 10.0);
        
        double dist1 = circle.distance(30, 10);
        double dist2 = circle.distance(30, 30);
        double dist3 = circle.distance(20, 20);
        double dist4 = circle.distance(40, 20);
        
        double exp = 0.0;
        assertEquals(exp, dist1, 0.001);
        assertEquals(exp, dist2, 0.001);
        assertEquals(exp, dist3, 0.001);
        assertEquals(exp, dist4, 0.001);
    }

    /**
     * Test method for {@link net.sci.geom2d.curve.Circle2D#distance(double, double)}.
     */
    @Test
    public final void test_distance_outside()
    {
        Circle2D circle = new Circle2D(30, 20, 10.0);
        
        double dist1 = circle.distance(30, 5);
        double dist2 = circle.distance(30, 35);
        double dist3 = circle.distance(15, 20);
        double dist4 = circle.distance(45, 20);
        
        double exp = 5.0;
        assertEquals(exp, dist1, 0.001);
        assertEquals(exp, dist2, 0.001);
        assertEquals(exp, dist3, 0.001);
        assertEquals(exp, dist4, 0.001);
    }

    /**
     * Test method for {@link net.sci.geom2d.curve.Circle2D#distance(double, double)}.
     */
    @Test
    public final void test_distance_inside()
    {
        Circle2D circle = new Circle2D(30, 20, 10.0);
        
        double dist1 = circle.distance(30, 15);
        double dist2 = circle.distance(30, 25);
        double dist3 = circle.distance(25, 20);
        double dist4 = circle.distance(35, 20);
        
        double exp = 5.0;
        assertEquals(exp, dist1, 0.001);
        assertEquals(exp, dist2, 0.001);
        assertEquals(exp, dist3, 0.001);
        assertEquals(exp, dist4, 0.001);
    }

    /**
     * Test method for {@link net.sci.geom2d.curve.Circle2D#distance(double, double)}.
     */
    @Test
    public final void test_isInside_inside()
    {
        Circle2D circle = new Circle2D(30, 20, 10.0);
        
        assertTrue(circle.isInside(30, 20)); // center
        assertTrue(circle.isInside(30, 15));
        assertTrue(circle.isInside(30, 25));
        assertTrue(circle.isInside(25, 20));
        assertTrue(circle.isInside(35, 20));
    }

    /**
     * Test method for {@link net.sci.geom2d.curve.Circle2D#distance(double, double)}.
     */
    @Test
    public final void test_isInside_outside()
    {
        Circle2D circle = new Circle2D(30, 20, 10.0);
        
        assertFalse(circle.isInside(30,  5));
        assertFalse(circle.isInside(30, 35));
        assertFalse(circle.isInside(15, 20));
        assertFalse(circle.isInside(45, 20));
    }

}
