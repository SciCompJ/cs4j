package net.sci.geom.geom2d;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class Point2DTest
{
    /*
     * Test for double getDistance(Point2D)
     */
    @Test
    public void testDistancePoint2D() {
        Point2D p1 = new Point2D(2, 3);
        Point2D p2 = new Point2D(1, 4);
        Point2D p3 = new Point2D(2, 4);

        double eps = 1e-10;
        assertEquals(p1.distance(p1), 0, eps);
        assertEquals(p2.distance(p1), Math.sqrt(2), eps);
        assertEquals(p3.distance(p1), 1, eps);
        assertEquals(p2.distance(p2), 0, eps);
        assertEquals(p3.distance(p2), 1, eps);
        assertEquals(p3.distance(p3), 0, eps);
    }

    @Test
    public void testPlusVector() {
        Point2D p1 = new Point2D(20, 30);
        Vector2D v = new Vector2D(40, 50);
        
        Point2D res = p1.add(v);
        Point2D exp = new Point2D(60, 80);
        double eps = 1e-10;
        assertTrue(res.almostEquals(exp, eps));
    }
    
    @Test
    public void testMinusVector() {
        Point2D p1 = new Point2D(60, 80);
        Vector2D v = new Vector2D(40, 50);
        
        Point2D res = p1.subtract(v);
        Point2D exp = new Point2D(20, 30);
        double eps = 1e-10;
        assertTrue(res.almostEquals(exp, eps));
    }
    
}