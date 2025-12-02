/**
 * 
 */
package net.sci.geom.geom3d.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import net.sci.geom.geom3d.Bounds3D;
import net.sci.geom.geom3d.Point3D;

/**
 * 
 */
public class DefaultPolygon3DTest
{
    
    /**
     * Test method for {@link net.sci.geom.geom3d.impl.DefaultPolygon3D#closestVertexIndex(net.sci.geom.geom3d.Point3D)}.
     */
    @Test
    public final void testClosestVertexIndex()
    {
        ArrayList<Point3D> points = new ArrayList<Point3D>();
        points.add(new Point3D(10, 10, 20));
        points.add(new Point3D(20, 10, 20));
        points.add(new Point3D(30, 20, 20));
        points.add(new Point3D(30, 30, 20));
        points.add(new Point3D(20, 40, 20));
        points.add(new Point3D(10, 40, 20));
        points.add(new Point3D( 0, 30, 20));
        points.add(new Point3D( 0, 20, 20));
        DefaultPolygon3D poly = new DefaultPolygon3D(points);
        
        Point3D p0 = new Point3D(5, 5, 20);
        assertEquals(0, poly.closestVertexIndex(p0));
        Point3D p3 = new Point3D(35, 35, 20);
        assertEquals(3, poly.closestVertexIndex(p3));
        Point3D p7 = new Point3D(0, 15, 20);
        assertEquals(7, poly.closestVertexIndex(p7));
    }
    
    /**
     * Test method for {@link net.sci.geom.geom3d.impl.DefaultPolygon3D#contains(net.sci.geom.geom3d.Point3D, double)}.
     */
    @Test
    public final void testContains()
    {
        ArrayList<Point3D> points = new ArrayList<Point3D>();
        points.add(new Point3D(10, 10, 20));
        points.add(new Point3D(20, 10, 20));
        points.add(new Point3D(20, 20, 20));
        points.add(new Point3D(10, 20, 20));
        DefaultPolygon3D poly = new DefaultPolygon3D(points);
        
        assertTrue(poly.contains(new Point3D(15, 15, 20), 0.1));
        assertFalse(poly.contains(new Point3D(25, 15, 20), 0.1));
    }
    
    /**
     * Test method for {@link net.sci.geom.geom3d.impl.DefaultPolygon3D#distance(double, double, double)}.
     */
    @Test
    public final void testDistance_diagonal_samePlane()
    {
        ArrayList<Point3D> points = new ArrayList<Point3D>();
        points.add(new Point3D(10, 10, 20));
        points.add(new Point3D(20, 10, 20));
        points.add(new Point3D(20, 20, 20));
        points.add(new Point3D(10, 20, 20));
        DefaultPolygon3D poly = new DefaultPolygon3D(points);
        
        double diag10 = 10 * Math.sqrt(2);
        assertEquals(poly.distance(new Point3D(0, 0, 20)), diag10, 0.01);
    }
    
    /**
     * Test method for {@link net.sci.geom.geom3d.impl.DefaultPolygon3D#distance(double, double, double)}.
     */
    @Test
    public final void testDistance_diagonal_otherPlane()
    {
        ArrayList<Point3D> points = new ArrayList<Point3D>();
        points.add(new Point3D(10, 10, 20));
        points.add(new Point3D(20, 10, 20));
        points.add(new Point3D(20, 20, 20));
        points.add(new Point3D(10, 20, 20));
        DefaultPolygon3D poly = new DefaultPolygon3D(points);
        
        double diag10 = 10 * Math.sqrt(3);
        assertEquals(poly.distance(new Point3D(30, 30, 30)), diag10, 0.01);
    }
    
    /**
     * Test method for {@link net.sci.geom.geom3d.impl.DefaultPolygon3D#bounds()}.
     */
    @Test
    public final void testBounds()
    {
        ArrayList<Point3D> points = new ArrayList<Point3D>();
        points.add(new Point3D(20, 10, 20));
        points.add(new Point3D(20, 20, 10));
        points.add(new Point3D(10, 20, 20));
        DefaultPolygon3D poly = new DefaultPolygon3D(points);
        
        Bounds3D bounds = poly.bounds();
        assertEquals(bounds.xMin(), 10, 0.01);
        assertEquals(bounds.xMax(), 20, 0.01);
        assertEquals(bounds.yMin(), 10, 0.01);
        assertEquals(bounds.yMax(), 20, 0.01);
        assertEquals(bounds.zMin(), 10, 0.01);
        assertEquals(bounds.zMax(), 20, 0.01);
    }
    
}
