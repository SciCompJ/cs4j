/**
 * 
 */
package net.sci.geom.geom3d;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import net.sci.geom.geom3d.Plane3D;
import net.sci.geom.geom3d.Point3D;
import net.sci.geom.geom3d.StraightLine3D;
import net.sci.geom.geom3d.Vector3D;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class Plane3DTest
{
    @Test
    public final void testProjectionPoint3D_XY()
    {
        Plane3D plane = new Plane3D(new Point3D(10, 10, 10), new Vector3D(1, 0, 0), new Vector3D(0, 1, 0));
        Point3D point = new Point3D(30, 40, 50);

        Point3D exp = new Point3D(30, 40, 10);
        Point3D proj = plane.projection(point);

        assertTrue(exp.almostEquals(proj, .01));
    }
    
    @Test
    public final void testProjectionPoint3D_XZ()
    {
        Plane3D plane = new Plane3D(new Point3D(10, 10, 10), new Vector3D(1, 0, 0), new Vector3D(0, 0, 1));
        Point3D point = new Point3D(30, 40, 50);

        Point3D exp = new Point3D(30, 10, 50);
        Point3D proj = plane.projection(point);

        assertTrue(exp.almostEquals(proj, .01));
    }
    
    @Test
    public final void testProjectionPoint3D_YZ()
    {
        Plane3D plane = new Plane3D(new Point3D(10, 10, 10), new Vector3D(0, 1, 0), new Vector3D(0, 0, 1));
        Point3D point = new Point3D(30, 40, 50);

        Point3D exp = new Point3D(10, 40, 50);
        Point3D proj = plane.projection(point);

        assertTrue(exp.almostEquals(proj, .01));
    }
    
    /**
     * Test method for {@link net.sci.geom.geom3d.Plane3D#intersection(net.sci.geom.geom3d.line.Pane3D)}.
     */
    @Test
    public final void testIntersectionPlane3D()
    {
        Point3D origin = new Point3D(0, 0, 0);

        Vector3D vx1 = new Vector3D(1, 0, 1);
        Vector3D vy1 = new Vector3D(0, 1, 0);
        Plane3D plane1 = new Plane3D(origin, vx1, vy1);
        
        Vector3D vx2 = new Vector3D(0, 1, 1);
        Vector3D vy2 = new Vector3D(1, 0, 0);
        Plane3D plane2 = new Plane3D(origin, vx2, vy2);
        
        StraightLine3D exp = new StraightLine3D(new Point3D(0, 0, 0), new Vector3D(1, 1, 1));
        
        StraightLine3D line = plane1.intersection(plane2);
        
        assertTrue(plane1.contains(line.origin(), .01));
        assertTrue(plane2.contains(line.origin(), .01));
        
        assertTrue(Vector3D.isPerpendicular(plane1.normal(), line.direction(), .01));
        assertTrue(Vector3D.isPerpendicular(plane2.normal(), line.direction(), .01));
        
        assertTrue(Vector3D.isParallel(exp.direction(), line.direction(), .01));
    }

    /**
     * Test method for {@link net.sci.geom.geom3d.Plane3D#intersection(net.sci.geom.geom3d.StraightLine3D)}.
     */
    @Test
    public final void testIntersectionStraightLine3D()
    {
        Point3D origin = new Point3D(0, 0, 0);
        Vector3D vx = new Vector3D(1, 0, 0);
        Vector3D vy = new Vector3D(0, 1, 0);
        Plane3D plane = new Plane3D(origin, vx, vy);
        
        StraightLine3D line = new StraightLine3D(new Point3D(5, 6, 10), new Vector3D(0, 0, 1));
        
        Point3D point = plane.intersection(line);
        Point3D exp = new Point3D(5, 6, 0);
        assertTrue(exp.distance(point) < 1e-10);
    }

    /**
     * Test method for {@link net.sci.geom.geom3d.Plane3D#normal()}.
     */
    @Test
    public final void testNormalOz()
    {
        Point3D origin = new Point3D(0, 0, 0);
        Vector3D vx = new Vector3D(1, 0, 0);
        Vector3D vy = new Vector3D(0, 1, 0);
        Plane3D plane = new Plane3D(origin, vx, vy);
        
        Vector3D exp = new Vector3D(0, 0, 1);
        Vector3D normal = plane.normal();
        assertTrue(normal.minus(exp).norm() < 1e-10);
    }
 
    /**
     * Test method for {@link net.sci.geom.geom3d.Plane3D#normalize()}.
     */
    @Test
    public final void testNormalize_arbitraryPlane()
    {
        Point3D origin = new Point3D(5, 4, 3);
        Vector3D vx = new Vector3D(3, 2, 1);
        Vector3D vy = new Vector3D(-1, 2, -2);
        Plane3D plane = new Plane3D(origin, vx, vy);
        
        Plane3D plane2 = plane.normalize();
        
        Vector3D v1 = plane2.directionVector1(); 
        Vector3D v2 = plane2.directionVector2(); 
        assertEquals(v1.norm(), 1.0, 1e-10);
        assertEquals(v2.norm(), 1.0, 1e-10);
        assertEquals(plane2.normal().norm(), 1.0, 1e-10);
        assertEquals(v1.dotProduct(v2), 0.0, 1e-10);
        
    }
 
}
