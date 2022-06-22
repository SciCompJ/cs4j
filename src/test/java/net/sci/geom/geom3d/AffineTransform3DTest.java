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
     * Test method for {@link net.sci.geom.geom3d.AffineTransform3D#concatenate(net.sci.geom.geom3d.AffineTransform3D)}.
     */
    @Test
    public final void testConcatenate()
    {
        AffineTransform3D tra = AffineTransform3D.createTranslation(30, 40, 50);
        AffineTransform3D rot = AffineTransform3D.createRotationOx(Math.toRadians(30));
        AffineTransform3D exp = AffineTransform3D.createRotationOx(new Point3D(30, 40, 50), Math.toRadians(30));
        
        AffineTransform3D trans = tra.concatenate(rot.concatenate(tra.inverse()));
               
        assertTrue(exp.almostEquals(trans, 0.01));
    }

    /**
     * Test method for {@link net.sci.geom.geom3d.AffineTransform3D#preConcatenate(net.sci.geom.geom3d.AffineTransform3D)}.
     */
    @Test
    public final void testPreConcatenate()
    {
        AffineTransform3D tra = AffineTransform3D.createTranslation(30, 40, 50);
        AffineTransform3D rot = AffineTransform3D.createRotationOx(Math.toRadians(30));
        AffineTransform3D exp = AffineTransform3D.createRotationOx(new Point3D(30, 40, 50), Math.toRadians(30));
        
        AffineTransform3D trans = tra.inverse().preConcatenate(rot).preConcatenate(tra);
               
        assertTrue(exp.almostEquals(trans, 0.01));
    }
    
    
    /**
     * Test method for {@link net.sci.geom.geom3d.AffineTransform3D#concatenate(net.sci.geom.geom3d.AffineTransform3D)}.
     */
    @Test
    public final void testConcatenate_RotationsZXYX()
    {
        AffineTransform3D rot1 = AffineTransform3D.createRotationOz(Math.PI/2);
        AffineTransform3D rot2 = AffineTransform3D.createRotationOx(Math.PI/2);
        AffineTransform3D rot3 = AffineTransform3D.createRotationOy(Math.PI/2);
        AffineTransform3D rot4 = AffineTransform3D.createRotationOx(-Math.PI/2);
        
        AffineTransform3D trans = rot4.concatenate(rot3).concatenate(rot2).concatenate(rot1);
        
        AffineTransform3D exp = AffineTransform3D.IDENTITY;
        assertTrue(exp.almostEquals(trans, 0.01));
    }

    /**
     * Test method for {@link net.sci.geom.geom3d.AffineTransform3D#concatenate(net.sci.geom.geom3d.AffineTransform3D)}.
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
        
        AffineTransform3D trans = rot4.concatenate(rot3).concatenate(rot2).concatenate(rot1);
        
        AffineTransform3D exp = AffineTransform3D.IDENTITY;
        assertTrue(exp.almostEquals(trans, 0.01));
    }

}
