package net.sci.geom.geom3d.surface;

import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import net.sci.geom.geom3d.Point3D;
import net.sci.geom.geom3d.Vector3D;
import net.sci.geom.geom3d.line.StraightLine3D;

public class Sphere3DTest
{
    
    @Test
    public final void testVolume()
    {
        double r = 5;
        Sphere3D sphere = new Sphere3D(new Point3D(2, 3, 4), r);
        
        double exp = r * r * r * 4 * Math.PI / 3;
        double vol = sphere.volume();
        assertEquals(exp, vol, .01);
    }
    
    @Test
    public final void testSurfaceArea()
    {
        double r = 5;
        Sphere3D sphere = new Sphere3D(new Point3D(2, 3, 4), r);
        
        double exp = r * r * 4 * Math.PI;
        double surf = sphere.surfaceArea();
        assertEquals(exp, surf, .01);
    }
    
    @Test
    public final void testIntersections_StraightLine3D_Ox()
    {

        Point3D center = new Point3D(5, 4, 3);
        double radius = 6;
        Sphere3D sphere = new Sphere3D(center, radius);
        StraightLine3D line = new StraightLine3D(center, new Vector3D(2, 0, 0));
        
        Collection<Point3D> inters = sphere.intersections(line);
        
        assertEquals(2, inters.size());
        
        Point3D p1 = new Point3D(center.getX()-radius, center.getY(), center.getZ());
        Point3D p2 = new Point3D(center.getX()+radius, center.getY(), center.getZ());
        assertContainsPoint(inters, p1, .001);
        assertContainsPoint(inters, p2, .001);
    }

    private void assertContainsPoint(Collection<Point3D> coll, Point3D p, double tol)
    {
        for (Point3D point : coll)
        {
            if (point.almostEquals(p, tol))
            {
                return;
            }
        }
        
        Assert.fail("Could not find the point in the collection");
    }
}
