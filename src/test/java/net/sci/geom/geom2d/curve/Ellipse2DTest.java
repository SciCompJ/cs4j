package net.sci.geom.geom2d.curve;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.geom.geom2d.AffineTransform2D;
import net.sci.geom.geom2d.Bounds2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.polygon.LinearRing2D;

/**
 * Unit tests for Ellipse2D  class.
 */
public class Ellipse2DTest
{
    /**
     * Test method for {@link net.sci.geom.geom2d.curve.Ellipse2D#fromCorners(net.sci.geom.geom2d.Point2D, net.sci.geom.geom2d.Point2D)}.
     */
    @Test
    public final void testFromCornersPoint2DPoint2D()
    {
        Point2D p1 = new Point2D(50, 40);
        Point2D p2 = new Point2D(10, 20);
        
        Ellipse2D elli = Ellipse2D.fromCorners(p1, p2);
        
        Bounds2D bounds = elli.bounds();
        assertEquals(10.0, bounds.xMin(), 0.01);
        assertEquals(50.0, bounds.xMax(), 0.01);
        assertEquals(20.0, bounds.yMin(), 0.01);
        assertEquals(40.0, bounds.yMax(), 0.01);
    }
    
    /**
     * Test method for {@link net.sci.geom.geom2d.curve.Ellipse2D#asPolyline(int)}.
     */
    @Test
    public final void testAsPolyline()
    {
        Ellipse2D elli = new Ellipse2D(50, 50, 40, 20, 30);
        
        LinearRing2D ring = elli.asPolyline(120);
        
        assertEquals(120, ring.vertexCount());
    }
    
    @Test
    public final void test_transform_Rotation_Centered()
    {
        Ellipse2D ell0 = new Ellipse2D(0, 0, 20, 10, 0);
        double theta = Math.PI/3;
        AffineTransform2D rot60 = AffineTransform2D.createRotation(theta);
        
        Ellipse2D resRot = ell0.transform(rot60);
        
        Ellipse2D expRot = new Ellipse2D(0, 0, 20, 10, 60);
        assertEquals(resRot.xc, expRot.xc, 1e-3);
        assertEquals(resRot.yc, expRot.yc, 1e-3);
        assertEquals(resRot.r1, expRot.r1, 1e-3);
        assertEquals(resRot.r2, expRot.r2, 1e-3);
        assertEquals(resRot.theta, expRot.theta, 1e-3);
    }

    @Test
    public final void test_transform_Scaling_Centered()
    {
        Ellipse2D ell0 = new Ellipse2D(0, 0, 20, 10, 0);
        double sx = 2.5; double sy = 3;
        AffineTransform2D sca = AffineTransform2D.createScaling(sx, sy);
        
        Ellipse2D resSca = ell0.transform(sca);
        
        Ellipse2D expSca = new Ellipse2D(0, 0, 20.0 * sx, 10.0 * sy, 0);
        assertEquals(resSca.xc, expSca.xc, 1e-3);
        assertEquals(resSca.yc, expSca.yc, 1e-3);
        assertEquals(resSca.r1, expSca.r1, 1e-3);
        assertEquals(resSca.r2, expSca.r2, 1e-3);
        assertEquals(resSca.theta, expSca.theta, 1e-3);
    }
    
    @Test
    public final void test_transform_RotateAndUniformScale()
    {
        Ellipse2D ell0 = new Ellipse2D(40, 30, 20, 10, 0);
        double theta = Math.toRadians(90);
        AffineTransform2D rot = AffineTransform2D.createRotation(theta);
        double k = 2.5;
        AffineTransform2D sca = AffineTransform2D.createScaling(k, k);

        Ellipse2D resSca = ell0.transform(rot).transform(sca);
        
        Ellipse2D expSca = new Ellipse2D(-75, 100, 50, 25, 90);
        assertEquals(resSca.xc, expSca.xc, 1e-3);
        assertEquals(resSca.yc, expSca.yc, 1e-3);
        assertEquals(resSca.r1, expSca.r1, 1e-3);
        assertEquals(resSca.r2, expSca.r2, 1e-3);
        assertEquals(resSca.theta, expSca.theta, 1e-3);
    }
    
    /**
     * Test method for {@link net.sci.geom.geom2d.curve.Ellipse2D#distance(double, double)}.
     */
    @Test
    public final void test_distance_centered_aligned()
    {
        Ellipse2D elli = new Ellipse2D(0, 0, 30, 20, 0);
        double dist;
        
        // positive x axis (outside)
        dist = elli.distance(new Point2D(35, 0));
        assertEquals(5.0, dist, 0.001);
        
        // positive x axis (inside)
        dist = elli.distance(new Point2D(25, 0));
        assertEquals(5.0, dist, 0.001);
        
        // negative x axis (outside)
        dist = elli.distance(new Point2D(-35, 0));
        assertEquals(5.0, dist, 0.001);
        
        // negative x axis (inside)
        dist = elli.distance(new Point2D(-25, 0));
        assertEquals(5.0, dist, 0.001);
        
        // positive y axis (outside)
        dist = elli.distance(new Point2D(0, 25));
        assertEquals(5.0, dist, 0.001);
        
        // positive y axis (inside)
        dist = elli.distance(new Point2D(0, 15));
        assertEquals(5.0, dist, 0.001);
        
        // negative y axis (outside)
        dist = elli.distance(new Point2D(0, -25));
        assertEquals(5.0, dist, 0.001);
        
        // negative y axis (inside)
        dist = elli.distance(new Point2D(0, -15));
        assertEquals(5.0, dist, 0.001);
    }
    
    /**
     * Test method for
     * {@link net.sci.geom.geom2d.curve.Ellipse2D#distance(double, double)}.
     * 
     * Computes distance with various points, and compare with a polyline with
     * "large enough" number of vertices
     */
    @Test
    public final void test_distance_centered_aligned_variousPoints()
    {
        Ellipse2D elli = new Ellipse2D(0, 0, 30, 20, 0);
        LinearRing2D ring = elli.asPolyline(2000);
        Point2D point;
        
        
        // several points outside of the ellipse
        
        point = new Point2D(35, 10);
        assertEquals(ring.distance(point), elli.distance(point), 0.0001);
        
        point = new Point2D(25, 25);
        assertEquals(ring.distance(point), elli.distance(point), 0.0001);
        
        point = new Point2D(-25, 25);
        assertEquals(ring.distance(point), elli.distance(point), 0.0001);
        
        point = new Point2D(25, -25);
        assertEquals(ring.distance(point), elli.distance(point), 0.0001);
        
        point = new Point2D(25, -25);
        assertEquals(ring.distance(point), elli.distance(point), 0.0001);
        
        
        // several points inside the ellipse
        
        point = new Point2D(18, 4);
        assertEquals(ring.distance(point), elli.distance(point), 0.0001);
        
        point = new Point2D(-18, 4);
        assertEquals(ring.distance(point), elli.distance(point), 0.0001);
        
        point = new Point2D(18, -4);
        assertEquals(ring.distance(point), elli.distance(point), 0.0001);
        
        point = new Point2D(-18, -4);
        assertEquals(ring.distance(point), elli.distance(point), 0.0001);
        
        
        // several points on the ellipse
        
        point = elli.point(0.2);
        assertEquals(0.0, elli.distance(point), 0.0001);
        
        point = elli.point(0.8);
        assertEquals(0.0, elli.distance(point), 0.0001);
        
        point = elli.point(2.1);
        assertEquals(0.0, elli.distance(point), 0.0001);
        
        point = elli.point(4.3);
        assertEquals(0.0, elli.distance(point), 0.0001);
        
        point = elli.point(6.0);
        assertEquals(0.0, elli.distance(point), 0.0001);
    }
    
    /**
     * Test method for
     * {@link net.sci.geom.geom2d.curve.Ellipse2D#distance(double, double)}.
     * 
     * Computes distance with various points, and compare with a polyline with
     * "large enough" number of vertices
     */
    @Test
    public final void test_distance_50_30_40_20_30_variousPoints()
    {
        Ellipse2D elli = new Ellipse2D(50, 30, 40, 20, 30);
        LinearRing2D ring = elli.asPolyline(1440);
        Point2D point;
        
        AffineTransform2D tra = AffineTransform2D.createTranslation(50, 30); 
        AffineTransform2D rot = AffineTransform2D.createRotation(Math.toRadians(30));
        AffineTransform2D transfo = tra.compose(rot); 

        
        // several points outside of the ellipse
        
        point = new Point2D(45, 0).transform(transfo);
        assertEquals(ring.distance(point), elli.distance(point), 0.0001);
        
        point = new Point2D(25, 25).transform(transfo);
        assertEquals(ring.distance(point), elli.distance(point), 0.0001);
        
        point = new Point2D(-25, 25).transform(transfo);
        assertEquals(ring.distance(point), elli.distance(point), 0.0001);
        
        point = new Point2D(25, -25).transform(transfo);
        assertEquals(ring.distance(point), elli.distance(point), 0.0001);
        
        point = new Point2D(25, -25).transform(transfo);
        assertEquals(ring.distance(point), elli.distance(point), 0.0001);
        
        
        // several points inside the ellipse
        
        point = new Point2D(18, 4).transform(transfo);
        assertEquals(ring.distance(point), elli.distance(point), 0.0001);
        
        point = new Point2D(-18, 4).transform(transfo);
        assertEquals(ring.distance(point), elli.distance(point), 0.0001);
        
        point = new Point2D(18, -4).transform(transfo);
        assertEquals(ring.distance(point), elli.distance(point), 0.0001);
        
        point = new Point2D(-18, -4).transform(transfo);
        assertEquals(ring.distance(point), elli.distance(point), 0.0001);
        
        
        // several points on the ellipse
        
        point = elli.point(0.2);
        assertEquals(0.0, elli.distance(point), 0.0001);
        
        point = elli.point(0.8);
        assertEquals(0.0, elli.distance(point), 0.0001);
        
        point = elli.point(2.1);
        assertEquals(0.0, elli.distance(point), 0.0001);
        
        point = elli.point(4.3);
        assertEquals(0.0, elli.distance(point), 0.0001);
        
        point = elli.point(6.0);
        assertEquals(0.0, elli.distance(point), 0.0001);
    }
    

    /**
     * Test method for {@link net.sci.geom.geom2d.curve.Ellipse2D#signedDistance(net.sci.geom.geom2d.Point2D)}.
     */
    @Test
    public final void test_signedDistance_centered_aligned()
    {
        Ellipse2D elli = new Ellipse2D(0, 0, 30, 20, 0);
        double dist;
        
        // positive x axis (outside)
        dist = elli.signedDistance(new Point2D(35, 0));
        assertEquals(5.0, dist, 0.001);
        
        // positive x axis (inside, close to apex)
        dist = elli.signedDistance(new Point2D(25, 0));
        assertEquals(-5.0, dist, 0.001);
        
        // positive x axis (inside, close to center)
        dist = elli.signedDistance(new Point2D(5, 0));
        assertEquals(-19.5, dist, 0.01);
        
        
        // negative x axis (outside)
        dist = elli.signedDistance(new Point2D(-35, 0));
        assertEquals(5.0, dist, 0.001);
        
        // negative x axis (inside, close to apex)
        dist = elli.signedDistance(new Point2D(-25, 0));
        assertEquals(-5.0, dist, 0.001);
        
        // negative x axis (inside, close to center)
        dist = elli.signedDistance(new Point2D(-5, 0));
        assertEquals(-19.5, dist, 0.01);
        
        
        // negative x axis (outside)
        dist = elli.signedDistance(new Point2D(-35, 0));
        assertEquals( 5.0, dist, 0.001);
        
        // negative x axis (inside)
        dist = elli.signedDistance(new Point2D(-25, 0));
        assertEquals(-5.0, dist, 0.001);
        
        
        // positive y axis (outside)
        dist = elli.signedDistance(new Point2D(0, 25));
        assertEquals(5.0, dist, 0.001);
        
        // positive y axis (inside)
        dist = elli.signedDistance(new Point2D(0, 15));
        assertEquals(-5.0, dist, 0.001);
        
        
        // negative y axis (outside)
        dist = elli.signedDistance(new Point2D(0, -25));
        assertEquals(5.0, dist, 0.001);
        
        // negative y axis (inside)
        dist = elli.signedDistance(new Point2D(0, -15));
        assertEquals(-5.0, dist, 0.001);
    }
    
    /**
     * Test method for
     * {@link net.sci.geom.geom2d.curve.Ellipse2D#signedDistance(net.sci.geom.geom2d.Point2D)}.
     * 
     * Computes distance with various points, and compare with a polyline with
     * "large enough" number of vertices
     */
    @Test
    public final void test_signedDistance_50_30_40_20_30_variousPoints()
    {
        Ellipse2D elli = new Ellipse2D(50, 30, 40, 20, 30);
        LinearRing2D ring = elli.asPolyline(1440);
        Point2D point;
        
        AffineTransform2D tra = AffineTransform2D.createTranslation(50, 30); 
        AffineTransform2D rot = AffineTransform2D.createRotation(Math.toRadians(30));
        AffineTransform2D transfo = tra.compose(rot); 

        
        // several points outside of the ellipse
        // -> signed distance is positive
        
        point = new Point2D(45, 0).transform(transfo);
        assertEquals(ring.signedDistance(point), elli.signedDistance(point), 0.0001);
        
        point = new Point2D(25, 25).transform(transfo);
        assertEquals(ring.signedDistance(point), elli.signedDistance(point), 0.0001);
        
        point = new Point2D(-25, 25).transform(transfo);
        assertEquals(ring.signedDistance(point), elli.signedDistance(point), 0.0001);
        
        point = new Point2D(25, -25).transform(transfo);
        assertEquals(ring.signedDistance(point), elli.signedDistance(point), 0.0001);
        
        point = new Point2D(25, -25).transform(transfo);
        assertEquals(ring.signedDistance(point), elli.signedDistance(point), 0.0001);
        
        
        // several points inside the ellipse
        // -> signed distance is negative
        
        point = new Point2D(18, 4).transform(transfo);
        assertEquals(ring.signedDistance(point), elli.signedDistance(point), 0.0001);
        
        point = new Point2D(-18, 4).transform(transfo);
        assertEquals(ring.signedDistance(point), elli.signedDistance(point), 0.0001);
        
        point = new Point2D(18, -4).transform(transfo);
        assertEquals(ring.signedDistance(point), elli.signedDistance(point), 0.0001);
        
        point = new Point2D(-18, -4).transform(transfo);
        assertEquals(ring.signedDistance(point), elli.signedDistance(point), 0.0001);
        
        point = new Point2D(5, 0.0001).transform(transfo);
        assertEquals(ring.signedDistance(point), elli.signedDistance(point), 0.0001);
        point = new Point2D(5, 0).transform(transfo);
        assertEquals(ring.signedDistance(point), elli.signedDistance(point), 0.0001);
        
        point = new Point2D(-5, 0).transform(transfo);
        assertEquals(ring.signedDistance(point), elli.signedDistance(point), 0.0001);
        
        point = new Point2D(0, 5).transform(transfo);
        assertEquals(ring.signedDistance(point), elli.signedDistance(point), 0.0001);
        
        point = new Point2D(0, -5).transform(transfo);
        assertEquals(ring.signedDistance(point), elli.signedDistance(point), 0.0001);
        
        
        // several points on the ellipse
        // -> signed distance is zero
        
        point = elli.point(0.2);
        assertEquals(0.0, elli.signedDistance(point), 0.0001);
        
        point = elli.point(0.8);
        assertEquals(0.0, elli.signedDistance(point), 0.0001);
        
        point = elli.point(2.1);
        assertEquals(0.0, elli.signedDistance(point), 0.0001);
        
        point = elli.point(4.3);
        assertEquals(0.0, elli.signedDistance(point), 0.0001);
        
        point = elli.point(6.0);
        assertEquals(0.0, elli.signedDistance(point), 0.0001);
    }
    
    /**
     * Test method for
     * {@link net.sci.geom.geom2d.curve.Ellipse2D#signedDistance(net.sci.geom.geom2d.Point2D)}.
     * 
     * Case of an ellipse with R2 > R1.
     * Computes distance with various points, and compare with a polyline with
     * "large enough" number of vertices.
     */
    @Test
    public final void test_signedDistance_50_30_20_40_30_variousPoints()
    {
        Ellipse2D elli = new Ellipse2D(50, 30, 20, 40, 30);
        LinearRing2D ring = elli.asPolyline(1440);
        Point2D point;
        
        AffineTransform2D tra = AffineTransform2D.createTranslation(50, 30); 
        AffineTransform2D rot = AffineTransform2D.createRotation(Math.toRadians(30));
        AffineTransform2D transfo = tra.compose(rot); 

        
        // several points outside of the ellipse
        // -> signed distance is positive
        
        point = new Point2D(25, 0).transform(transfo);
        assertEquals(ring.signedDistance(point), elli.signedDistance(point), 0.0001);
        
        point = new Point2D(0, 45).transform(transfo);
        assertEquals(ring.signedDistance(point), elli.signedDistance(point), 0.0001);
        
        point = new Point2D(25, 25).transform(transfo);
        assertEquals(ring.signedDistance(point), elli.signedDistance(point), 0.0001);
        
        point = new Point2D(-25, 25).transform(transfo);
        assertEquals(ring.signedDistance(point), elli.signedDistance(point), 0.0001);
        
        point = new Point2D(25, -25).transform(transfo);
        assertEquals(ring.signedDistance(point), elli.signedDistance(point), 0.0001);
        
        point = new Point2D(25, -25).transform(transfo);
        assertEquals(ring.signedDistance(point), elli.signedDistance(point), 0.0001);
        
        
        // several points inside the ellipse
        // -> signed distance is negative
        
        point = new Point2D(18, 4).transform(transfo);
        assertEquals(ring.signedDistance(point), elli.signedDistance(point), 0.0001);
        
        point = new Point2D(-18, 4).transform(transfo);
        assertEquals(ring.signedDistance(point), elli.signedDistance(point), 0.0001);
        
        point = new Point2D(18, -4).transform(transfo);
        assertEquals(ring.signedDistance(point), elli.signedDistance(point), 0.0001);
        
        point = new Point2D(-18, -4).transform(transfo);
        assertEquals(ring.signedDistance(point), elli.signedDistance(point), 0.0001);
        
        point = new Point2D(5, 0.0001).transform(transfo);
        assertEquals(ring.signedDistance(point), elli.signedDistance(point), 0.0001);
        point = new Point2D(5, 0).transform(transfo);
        assertEquals(ring.signedDistance(point), elli.signedDistance(point), 0.0001);
        
        point = new Point2D(-5, 0).transform(transfo);
        assertEquals(ring.signedDistance(point), elli.signedDistance(point), 0.0001);
        
        point = new Point2D(0, 5).transform(transfo);
        assertEquals(ring.signedDistance(point), elli.signedDistance(point), 0.0001);
        
        point = new Point2D(0, -5).transform(transfo);
        assertEquals(ring.signedDistance(point), elli.signedDistance(point), 0.0001);
        
        
        // several points on the ellipse
        // -> signed distance is zero
        
        point = elli.point(0.2);
        assertEquals(0.0, elli.signedDistance(point), 0.0001);
        
        point = elli.point(0.8);
        assertEquals(0.0, elli.signedDistance(point), 0.0001);
        
        point = elli.point(2.1);
        assertEquals(0.0, elli.signedDistance(point), 0.0001);
        
        point = elli.point(4.3);
        assertEquals(0.0, elli.signedDistance(point), 0.0001);
        
        point = elli.point(6.0);
        assertEquals(0.0, elli.signedDistance(point), 0.0001);
    }
    
    /**
     * Test method for
     * {@link net.sci.geom.geom2d.curve.Ellipse2D#project(net.sci.geom.geom2d.Point2D)}.
     * 
     * Case of an ellipse with R2 > R1.
     * Computes distance with various points, and compare with a polyline with
     * "large enough" number of vertices.
     */
    @Test
    public final void test_project_centeredAndAligned_variousPoints()
    {
        Ellipse2D elli = new Ellipse2D(0, 0, 40, 20, 0);
        Point2D point, proj;
        
        // points close to extremity on x-axis
        point = new Point2D(45, 10);
        proj = elli.project(point);
        assertEquals(elli.distance(point), proj.distance(point), 0.001);
        
        point = new Point2D(45, -10);
        proj = elli.project(point);
        assertEquals(elli.distance(point), proj.distance(point), 0.001);
        
        point = new Point2D(-45, 10);
        proj = elli.project(point);
        assertEquals(elli.distance(point), proj.distance(point), 0.001);
        
        point = new Point2D(-45, -10);
        proj = elli.project(point);
        assertEquals(elli.distance(point), proj.distance(point), 0.001);
        
        
        // points close to extremity on y-axis
        point = new Point2D(5, 25);
        proj = elli.project(point);
        assertEquals(elli.distance(point), proj.distance(point), 0.001);
        
        point = new Point2D(-5, 25);
        proj = elli.project(point);
        assertEquals(elli.distance(point), proj.distance(point), 0.001);
        
        point = new Point2D(5, -25);
        proj = elli.project(point);
        assertEquals(elli.distance(point), proj.distance(point), 0.001);
        
        point = new Point2D(-5, -25);
        proj = elli.project(point);
        assertEquals(elli.distance(point), proj.distance(point), 0.001);

        
        // points inside the ellipse
        point = new Point2D(30, 5);
        proj = elli.project(point);
        assertEquals(elli.distance(point), proj.distance(point), 0.001);
        
        point = new Point2D(-30, 5);
        proj = elli.project(point);
        assertEquals(elli.distance(point), proj.distance(point), 0.001);
        
        point = new Point2D(30, -5);
        proj = elli.project(point);
        assertEquals(elli.distance(point), proj.distance(point), 0.001);
        
        point = new Point2D(-30, -5);
        proj = elli.project(point);
        assertEquals(elli.distance(point), proj.distance(point), 0.001);
        
        
        // several points on the ellipse
        point = elli.point(0.2);
        proj = elli.project(point);
        assertEquals(elli.distance(point), proj.distance(point), 0.001);
        
        point = elli.point(0.8);
        proj = elli.project(point);
        assertEquals(elli.distance(point), proj.distance(point), 0.001);

        point = elli.point(2.1);
        proj = elli.project(point);
        assertEquals(elli.distance(point), proj.distance(point), 0.001);

        point = elli.point(4.3);
        proj = elli.project(point);
        assertEquals(elli.distance(point), proj.distance(point), 0.001);

        point = elli.point(5.8);
        proj = elli.project(point);
        assertEquals(elli.distance(point), proj.distance(point), 0.001);
    }
    
    
    /**
     * Test method for
     * {@link net.sci.geom.geom2d.curve.Ellipse2D#project(net.sci.geom.geom2d.Point2D)}.
     * 
     * Case of an ellipse with R2 > R1.
     * Computes distance with various points, and compare with a polyline with
     * "large enough" number of vertices.
     */
    @Test
    public final void test_project_50_30_40_20_30_variousPoints()
    {
        Ellipse2D elli = new Ellipse2D(50, 30, 40, 20, 30);
        Point2D point, proj;
        
        AffineTransform2D tra = AffineTransform2D.createTranslation(50, 30); 
        AffineTransform2D rot = AffineTransform2D.createRotation(Math.toRadians(30));
        AffineTransform2D transfo = tra.compose(rot); 
        
        // points close to extremity on x-axis
        point = new Point2D(45, 0).transform(transfo);
        proj = elli.project(point);
        assertEquals(elli.distance(point), proj.distance(point), 0.001);
        
        point = new Point2D(35, 0).transform(transfo);
        proj = elli.project(point);
        assertEquals(elli.distance(point), proj.distance(point), 0.001);
        
        point = new Point2D(45, 10).transform(transfo);
        proj = elli.project(point);
        assertEquals(elli.distance(point), proj.distance(point), 0.001);
        
        point = new Point2D(45, -10).transform(transfo);
        proj = elli.project(point);
        assertEquals(elli.distance(point), proj.distance(point), 0.001);
        
        point = new Point2D(-45, 0).transform(transfo);
        proj = elli.project(point);
        assertEquals(elli.distance(point), proj.distance(point), 0.001);
        
        point = new Point2D(-45, 10).transform(transfo);
        proj = elli.project(point);
        assertEquals(elli.distance(point), proj.distance(point), 0.001);
        
        point = new Point2D(-45, -10).transform(transfo);
        proj = elli.project(point);
        assertEquals(elli.distance(point), proj.distance(point), 0.001);
        
        
        // points close to extremity on y-axis
        point = new Point2D(0, 25).transform(transfo);
        proj = elli.project(point);
        assertEquals(elli.distance(point), proj.distance(point), 0.001);
        
        point = new Point2D(5, 25).transform(transfo);
        proj = elli.project(point);
        assertEquals(elli.distance(point), proj.distance(point), 0.001);
        
        point = new Point2D(-5, 25).transform(transfo);
        proj = elli.project(point);
        assertEquals(elli.distance(point), proj.distance(point), 0.001);
        
        point = new Point2D(0, -25).transform(transfo);
        proj = elli.project(point);
        assertEquals(elli.distance(point), proj.distance(point), 0.001);
        
        point = new Point2D(5, -25).transform(transfo);
        proj = elli.project(point);
        assertEquals(elli.distance(point), proj.distance(point), 0.001);
        
        point = new Point2D(-5, -25).transform(transfo);
        proj = elli.project(point);
        assertEquals(elli.distance(point), proj.distance(point), 0.001);

        
        // points inside the ellipse
        point = new Point2D(20, 0).transform(transfo);
        proj = elli.project(point);
        assertEquals(elli.distance(point), proj.distance(point), 0.001);
        
        point = new Point2D(-20, 0).transform(transfo);
        proj = elli.project(point);
        assertEquals(elli.distance(point), proj.distance(point), 0.001);
        
        point = new Point2D(0, 15).transform(transfo);
        proj = elli.project(point);
        assertEquals(elli.distance(point), proj.distance(point), 0.001);
        
        point = new Point2D(0, -15).transform(transfo);
        proj = elli.project(point);
        assertEquals(elli.distance(point), proj.distance(point), 0.001);
        
        point = new Point2D(30, 5).transform(transfo);
        proj = elli.project(point);
        assertEquals(elli.distance(point), proj.distance(point), 0.001);
        
        point = new Point2D(-30, 5).transform(transfo);
        proj = elli.project(point);
        assertEquals(elli.distance(point), proj.distance(point), 0.001);
        
        point = new Point2D(30, -5).transform(transfo);
        proj = elli.project(point);
        assertEquals(elli.distance(point), proj.distance(point), 0.001);
        
        point = new Point2D(-30, -5).transform(transfo);
        proj = elli.project(point);
        assertEquals(elli.distance(point), proj.distance(point), 0.001);
        
        // several points on the ellipse
        point = elli.point(0.2).transform(transfo);
        proj = elli.project(point);
        assertEquals(elli.distance(point), proj.distance(point), 0.001);
        
        point = elli.point(0.8).transform(transfo);
        proj = elli.project(point);
        assertEquals(elli.distance(point), proj.distance(point), 0.001);

        point = elli.point(2.1).transform(transfo);
        proj = elli.project(point);
        assertEquals(elli.distance(point), proj.distance(point), 0.001);

        point = elli.point(4.3).transform(transfo);
        proj = elli.project(point);
        assertEquals(elli.distance(point), proj.distance(point), 0.001);

        point = elli.point(5.8).transform(transfo);
        proj = elli.project(point);
        assertEquals(elli.distance(point), proj.distance(point), 0.001);
    }
}
