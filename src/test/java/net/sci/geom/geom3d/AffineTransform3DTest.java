/**
 * 
 */
package net.sci.geom.geom3d;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class AffineTransform3DTest
{

    /**
     * Test method for {@link net.sci.geom.geom3d.AffineTransform3D#compose(net.sci.geom.geom3d.AffineTransform3D)}.
     */
    @Test
    public final void testConcatenate()
    {
        AffineTransform3D tra = AffineTransform3D.createTranslation(30, 40, 50);
        AffineTransform3D rot = AffineTransform3D.createRotationOx(Math.toRadians(30));
        AffineTransform3D exp = AffineTransform3D.createRotationOx(new Point3D(30, 40, 50), Math.toRadians(30));
        
        AffineTransform3D trans = tra.compose(rot.compose(tra.inverse()));
               
        assertTrue(exp.almostEquals(trans, 0.01));
    }

    /**
     * Test method for {@link net.sci.geom.geom3d.AffineTransform3D#compose(net.sci.geom.geom3d.AffineTransform3D)}.
     */
    @Test
    public final void testConcatenate_RotationsZXYX()
    {
        AffineTransform3D rot1 = AffineTransform3D.createRotationOz(Math.PI/2);
        AffineTransform3D rot2 = AffineTransform3D.createRotationOx(Math.PI/2);
        AffineTransform3D rot3 = AffineTransform3D.createRotationOy(Math.PI/2);
        AffineTransform3D rot4 = AffineTransform3D.createRotationOx(-Math.PI/2);
        
        AffineTransform3D trans = rot4.compose(rot3).compose(rot2).compose(rot1);
        
        AffineTransform3D exp = AffineTransform3D.IDENTITY;
        assertTrue(exp.almostEquals(trans, 0.01));
    }

    /**
     * Test method for {@link net.sci.geom.geom3d.AffineTransform3D#compose(net.sci.geom.geom3d.AffineTransform3D)}.
     */
    @Test
    public final void testConcatenate_RotationsYZXZ()
    {
        // dummy angle
        double alpha = 0.20;
        AffineTransform3D rot1 = AffineTransform3D.createRotationOy(-alpha);
        AffineTransform3D rot2 = AffineTransform3D.createRotationOz(Math.PI/2);
        AffineTransform3D rot3 = AffineTransform3D.createRotationOx(-alpha);
        AffineTransform3D rot4 = AffineTransform3D.createRotationOz(-Math.PI/2);
        
        AffineTransform3D trans = rot4.compose(rot3).compose(rot2).compose(rot1);
        
        AffineTransform3D exp = AffineTransform3D.IDENTITY;
        assertTrue(exp.almostEquals(trans, 0.01));
    }

    /**
     * Test method for {@link net.sci.geom.geom3d.AffineTransform3D#fromBasis(net.sci.geom.geom3d.Vector3D, net.sci.geom.geom3d.Vector3D, net.sci.geom.geom3d.Vector3D)}.
     */
    @Test
    public final void testFromBasisVector3DVector3DVector3D()
    {
        Vector3D v1 = new Vector3D( 3,  3, 1);
        Vector3D v2 = new Vector3D(-3,  3, 1);
        Vector3D v3 = new Vector3D( 0, -3, 1);
        
        AffineTransform3D transfo = AffineTransform3D.fromBasis(v1, v2, v3);
        
        Point3D p000 = new Point3D(0, 0, 0);
        assertTrue(p000.transform(transfo).almostEquals(new Point3D(0, 0, 0), 0.001));
        Point3D p100 = new Point3D(1, 0, 0);
        assertTrue(p100.transform(transfo).almostEquals(new Point3D(3, 3, 1), 0.001));
        Point3D p010 = new Point3D(0, 1, 0);
        assertTrue(p010.transform(transfo).almostEquals(new Point3D(-3, 3, 1), 0.001));
        Point3D p001 = new Point3D(0, 0, 1);
        assertTrue(p001.transform(transfo).almostEquals(new Point3D(0, -3, 1), 0.001));
    }
    
    /**
     * Test method for {@link net.sci.geom.geom3d.AffineTransform3D#fromBasis(net.sci.geom.geom3d.Vector3D, net.sci.geom.geom3d.Vector3D, net.sci.geom.geom3d.Vector3D, net.sci.geom.geom3d.Vector3D)}.
     */
    @Test
    public final void testFromBasisVector3DVector3DVector3DVector3D()
    {
        Vector3D v1 = new Vector3D( 3,  3, 1);
        Vector3D v2 = new Vector3D(-3,  3, 1);
        Vector3D v3 = new Vector3D( 0, -3, 1);
        Vector3D vt = new Vector3D( 4,  3, 2);
        
        AffineTransform3D transfo = AffineTransform3D.fromBasis(v1, v2, v3, vt);
        
        Point3D p000 = new Point3D(0, 0, 0);
        assertTrue(p000.transform(transfo).almostEquals(new Point3D(4, 3, 2), 0.001));
        Point3D p100 = new Point3D(1, 0, 0);
        assertTrue(p100.transform(transfo).almostEquals(new Point3D(7, 6, 3), 0.001));
        Point3D p010 = new Point3D(0, 1, 0);
        assertTrue(p010.transform(transfo).almostEquals(new Point3D(1, 6, 3), 0.001));
        Point3D p001 = new Point3D(0, 0, 1);
        assertTrue(p001.transform(transfo).almostEquals(new Point3D(4, 0, 3), 0.001));
    }
}
