/**
 * 
 */
package net.sci.geom.geom2d;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class MultiPoint2DTest
{

    /**
     * Test method for {@link net.sci.geom.geom2d.MultiPoint2D#contains(net.sci.geom.geom2d.Point2D, double)}.
     */
    @Test
    public void testContains()
    {
        MultiPoint2D points = MultiPoint2D.create(4);
        points.addPoint(new Point2D(20, 20));
        points.addPoint(new Point2D(40, 20));
        points.addPoint(new Point2D(40, 40));
        points.addPoint(new Point2D(20, 40));
        
        assertTrue(points.contains(new Point2D(40, 20), 0.01));
        assertFalse(points.contains(new Point2D(41, 20), 0.01));
    }

    /**
     * Test method for {@link net.sci.geom.geom2d.MultiPoint2D#distance(double, double)}.
     */
    @Test
    public void testDistance()
    {
        MultiPoint2D points = MultiPoint2D.create(4);
        points.addPoint(new Point2D(20, 20));
        points.addPoint(new Point2D(40, 20));
        points.addPoint(new Point2D(40, 40));
        points.addPoint(new Point2D(20, 40));
        
        assertEquals(10, points.distance(new Point2D(10, 20)), 0.01);
        assertEquals(10, points.distance(new Point2D(20, 10)), 0.01);
        assertEquals(10, points.distance(new Point2D(50, 40)), 0.01);
        assertEquals(10, points.distance(new Point2D(20, 50)), 0.01);
    }

}