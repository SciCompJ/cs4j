/**
 * 
 */
package net.sci.geom.geom3d.surface;

import static org.junit.Assert.*;

import org.junit.Test;
import net.sci.geom.geom3d.Bounds3D;
import net.sci.geom.geom3d.Point3D;

/**
 * @author dlegland
 *
 */
public class Ellipsoid3DTest
{

    /**
     * Test method for {@link net.sci.geom.geom3d.surface.Ellipsoid3D#distance(double, double, double)}.
     */
    @Test
    public final void testDistance()
    {
        Ellipsoid3D elli = new Ellipsoid3D(50, 40, 30, 30, 20, 10, 0, 0, 0);
        
        Point3D pxp = new Point3D(50+30+10, 40, 30);
        Point3D pxn = new Point3D(50-30-10, 40, 30);
        Point3D pyp = new Point3D(50, 40+20+10, 30);
        Point3D pyn = new Point3D(50, 40-20-10, 30);
        Point3D pzp = new Point3D(50, 40, 30+10+10);
        Point3D pzn = new Point3D(50, 40, 30-10-10);
        
        assertEquals(10.0, elli.distance(pxp), 0.01);
        assertEquals(10.0, elli.distance(pxn), 0.01);
        assertEquals(10.0, elli.distance(pyp), 0.01);
        assertEquals(10.0, elli.distance(pyn), 0.01);
        assertEquals(10.0, elli.distance(pzp), 0.01);
        assertEquals(10.0, elli.distance(pzn), 0.01);
    }

    /**
     * Test method for {@link net.sci.geom.geom3d.surface.Ellipsoid3D#contains(net.sci.geom.geom3d.Point3D, double)}.
     */
    @Test
    public final void testContains()
    {
        Ellipsoid3D elli = new Ellipsoid3D(50, 40, 30, 30, 20, 10, 0, 0, 0);
        
        Point3D pxp = new Point3D(50+30, 40, 30);
        Point3D pxn = new Point3D(50-30, 40, 30);
        Point3D pyp = new Point3D(50, 40+20, 30);
        Point3D pyn = new Point3D(50, 40-20, 30);
        Point3D pzp = new Point3D(50, 40, 30+10);
        Point3D pzn = new Point3D(50, 40, 30-10);
        
        assertTrue(elli.contains(pxp, 0.01));
        assertTrue(elli.contains(pxn, 0.01));
        assertTrue(elli.contains(pyp, 0.01));
        assertTrue(elli.contains(pyn, 0.01));
        assertTrue(elli.contains(pzp, 0.01));
        assertTrue(elli.contains(pzn, 0.01));
    }

    /**
     * Test method for {@link net.sci.geom.geom3d.surface.Ellipsoid3D#bounds()}.
     */
    @Test
    public final void testBounds()
    {
        Ellipsoid3D elli = new Ellipsoid3D(50, 40, 30, 30, 20, 10, 0, 0, 0);
        Bounds3D exp = new Bounds3D(50-30, 50+30, 40-20, 40+20, 30-10, 30+10);
        
        Bounds3D bounds = elli.bounds();
        assertEquals(exp.xMin(), bounds.xMin(), 0.01);
        assertEquals(exp.xMax(), bounds.xMax(), 0.01);
        assertEquals(exp.yMin(), bounds.yMin(), 0.01);
        assertEquals(exp.yMax(), bounds.yMax(), 0.01);
        assertEquals(exp.zMin(), bounds.zMin(), 0.01);
        assertEquals(exp.zMax(), bounds.zMax(), 0.01);
    }

}
