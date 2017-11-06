package net.sci.geom.geom2d.curve;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.geom.geom2d.transform.AffineTransform2D;

public class Ellipse2DTest
{
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
