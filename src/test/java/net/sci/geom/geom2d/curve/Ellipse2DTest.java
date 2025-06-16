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
}
