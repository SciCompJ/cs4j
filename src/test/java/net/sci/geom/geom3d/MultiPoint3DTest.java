/**
 * 
 */
package net.sci.geom.geom3d;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class MultiPoint3DTest
{

    /**
     * Test method for {@link net.sci.geom.geom3d.MultiPoint3D#contains(net.sci.geom.geom3d.Point3D, double)}.
     */
    @Test
    public void testContains()
    {
        MultiPoint3D points = MultiPoint3D.create(8);
        points.addPoint(new Point3D(20, 20, 20));
        points.addPoint(new Point3D(40, 20, 20));
        points.addPoint(new Point3D(40, 40, 20));
        points.addPoint(new Point3D(20, 40, 20));
        points.addPoint(new Point3D(20, 20, 40));
        points.addPoint(new Point3D(40, 20, 40));
        points.addPoint(new Point3D(40, 40, 40));
        points.addPoint(new Point3D(20, 40, 40));
        
        assertTrue(points.contains(new Point3D(40, 20, 20), 0.01));
        assertFalse(points.contains(new Point3D(41, 20, 20), 0.01));
        assertTrue(points.contains(new Point3D(40, 20, 40), 0.01));
        assertFalse(points.contains(new Point3D(41, 20, 40), 0.01));
    }

    /**
     * Test method for {@link net.sci.geom.geom3d.MultiPoint3D#distance(double, double)}.
     */
    @Test
    public void testDistance()
    {
        MultiPoint3D points = MultiPoint3D.create(8);
        points.addPoint(new Point3D(20, 20, 20));
        points.addPoint(new Point3D(40, 20, 20));
        points.addPoint(new Point3D(40, 40, 20));
        points.addPoint(new Point3D(20, 40, 20));
        points.addPoint(new Point3D(20, 20, 40));
        points.addPoint(new Point3D(40, 20, 40));
        points.addPoint(new Point3D(40, 40, 40));
        points.addPoint(new Point3D(20, 40, 40));
        
        assertEquals(10.0, points.distance(new Point3D(10, 20, 20)), 0.01);
        assertEquals(10.0, points.distance(new Point3D(50, 40, 40)), 0.01);
        assertEquals(10.0, points.distance(new Point3D(20, 10, 20)), 0.01);
        assertEquals(10.0, points.distance(new Point3D(40, 50, 40)), 0.01);
        assertEquals(10.0, points.distance(new Point3D(20, 20, 10)), 0.01);
        assertEquals(10.0, points.distance(new Point3D(40, 40, 50)), 0.01);
    }

}
