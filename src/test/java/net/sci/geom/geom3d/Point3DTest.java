/**
 * 
 */
package net.sci.geom.geom3d;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class Point3DTest
{
    @Test
    public final void test_centroid_ellipsis() 
    {
        Point3D p1 = new Point3D(20, 20, 40); 
        Point3D p2 = new Point3D(60, 30, 60); 
        Point3D p3 = new Point3D(80, 80, 30); 
        Point3D p4 = new Point3D(40, 70, 70);
        Point3D centroid = Point3D.centroid(p1, p2, p3, p4);
        
        assertEquals(50, centroid.x(), 0.01);
        assertEquals(50, centroid.y(), 0.01);
        assertEquals(50, centroid.z(), 0.01);
    }
    
    @Test
    public final void test_centroid_iterable() 
    {
        ArrayList<Point3D> pts = new ArrayList<Point3D>();  
        pts.add(new Point3D(20, 20, 40)); 
        pts.add(new Point3D(60, 30, 60)); 
        pts.add(new Point3D(80, 80, 30)); 
        pts.add(new Point3D(40, 70, 70));
        Point3D centroid = Point3D.centroid(pts);
        
        assertEquals(50, centroid.x(), 0.01);
        assertEquals(50, centroid.y(), 0.01);
        assertEquals(50, centroid.z(), 0.01);
    }
    
    /**
     * Test method for {@link net.sci.geom.geom3d.Point3D#plus(net.sci.geom.geom3d.Vector3D)}.
     */
    @Test
    public final void test_plus()
    {
        Point3D p1 = new Point3D(20, 30, 40);
        Vector3D v = new Vector3D(40, 50, 60);
        
        Point3D res = p1.plus(v);
        Point3D exp = new Point3D(60, 80, 100);
        double eps = 1e-10;
        assertTrue(res.almostEquals(exp, eps));
    }
    
    /**
     * Test method for {@link net.sci.geom.geom3d.Point3D#minus(net.sci.geom.geom3d.Vector3D)}.
     */
    @Test
    public final void test_minus()
    {
        Point3D p1 = new Point3D(60, 80, 100);
        Vector3D v = new Vector3D(40, 50, 60);
        
        Point3D res = p1.minus(v);
        Point3D exp = new Point3D(20, 30, 40);
        double eps = 1e-10;
        assertTrue(res.almostEquals(exp, eps));
    }
    
}
