/**
 * 
 */
package net.sci.geom.geom3d;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.geom.geom3d.impl.MatrixAffineTransform3D;

/**
 * @author dlegland
 *
 */
public class Rotation3DTest
{
    /**
     * Test method for {@link net.sci.geom.geom3d.Rotation3D}.
     */
    @Test
    public final void test_linearity_rotation_Ox()
    {
        Rotation3D rot1 = Rotation3D.fromAxisAngle(Vector3D.E_1, 0.3);
        
        Rotation3D rot = rot1.compose(rot1);
        
        Rotation3D exp = Rotation3D.fromAxisAngle(Vector3D.E_1, 0.6);
        assertTrue(exp.almostEquals(rot, 1e-10));
    }

    /**
     * Test method for {@link net.sci.geom.geom3d.Rotation3D}.
     */
    @Test
    public final void test_linearity_rotation_Oy()
    {
        Rotation3D rot1 = Rotation3D.fromAxisAngle(Vector3D.E_2, 0.3);
        
        Rotation3D rot = rot1.compose(rot1);
        
        Rotation3D exp = Rotation3D.fromAxisAngle(Vector3D.E_2, 0.6);
        assertTrue(exp.almostEquals(rot, 1e-10));
    }

    /**
     * Test method for {@link net.sci.geom.geom3d.Rotation3D}.
     */
    @Test
    public final void test_linearity_rotation_Oz()
    {
        Rotation3D rot1 = Rotation3D.fromAxisAngle(Vector3D.E_3, 0.3);
        
        Rotation3D rot = rot1.compose(rot1);
        
        Rotation3D exp = Rotation3D.fromAxisAngle(Vector3D.E_3, 0.6);
        assertTrue(exp.almostEquals(rot, 1e-10));
    }

    /**
     * Test method for {@link net.sci.geom.geom3d.Rotation3D#fromMatrix(double[][])}.
     */
    @Test
    public final void testFromMatrix_rotationOx()
    {
        AffineTransform3D rotX = AffineTransform3D.createRotationOx(0.3);
        double[][] matrix = rotX.affineMatrix();
        
        Rotation3D rot = Rotation3D.fromMatrix(matrix);
        
        Rotation3D exp = Rotation3D.fromAxisAngle(Vector3D.E_1, 0.3);
        assertTrue(exp.almostEquals(rot, 1e-10));
    }

    /**
     * Test method for {@link net.sci.geom.geom3d.Rotation3D#fromMatrix(double[][])}.
     */
    @Test
    public final void testFromMatrix_rotationOy()
    {
        AffineTransform3D rotY = AffineTransform3D.createRotationOy(0.3);
        double[][] matrix = rotY.affineMatrix();
        
        Rotation3D rot = Rotation3D.fromMatrix(matrix);
        
        Rotation3D exp = Rotation3D.fromAxisAngle(Vector3D.E_2, 0.3);
        assertTrue(exp.almostEquals(rot, 1e-10));
    }

    /**
     * Test method for {@link net.sci.geom.geom3d.Rotation3D#fromMatrix(double[][])}.
     */
    @Test
    public final void testFromMatrix_rotationOz()
    {
        AffineTransform3D rotZ = AffineTransform3D.createRotationOz(0.3);
        double[][] matrix = rotZ.affineMatrix();
        
        Rotation3D rot = Rotation3D.fromMatrix(matrix);
        
        Rotation3D exp = Rotation3D.fromAxisAngle(Vector3D.E_3, 0.3);
        assertTrue(exp.almostEquals(rot, 1e-10));
    }

    /**
     * Test method for {@link net.sci.geom.geom3d.Rotation3D#fromEulerAngles(double, double, double)}.
     */
    @Test
    public final void testFromEulerAngles()
    {
        double[] angles = new double[] {0.1, 0.2, 0.3};
        Rotation3D rotX = Rotation3D.fromAxisAngle(Vector3D.E_1, angles[0]);
        Rotation3D rotY = Rotation3D.fromAxisAngle(Vector3D.E_2, angles[1]);
        Rotation3D rotZ = Rotation3D.fromAxisAngle(Vector3D.E_3, angles[2]);
        Rotation3D rotExp = rotZ.compose(rotY).compose(rotX);
        
        Rotation3D rot = Rotation3D.fromEulerAngles(angles[0], angles[1], angles[2]);
        
        assertTrue(rotExp.almostEquals(rot, 1e-10));
    }

    /**
     * Test method for {@link net.sci.geom.geom3d.Rotation3D#fromAxisAngle(net.sci.geom.geom3d.Vector3D, double)}.
     */
    @Test
    public final void testFromAxisAngle()
    {
        Vector3D refAxis = new Vector3D(3, 2, 1);
        double refAngle = 0.2;
        
        Rotation3D rot = Rotation3D.fromAxisAngle(refAxis, refAngle);
        
        Vector3D axis = rot.axis();
        double angle = rot.angle();
        
        assertTrue(refAxis.normalize().almostEquals(axis.normalize(), 1e-8));
        assertEquals(refAngle, angle, 1e-8);
    }

    /**
     * Test method for {@link net.sci.geom.geom3d.Rotation3D#rotate(net.sci.geom.geom3d.Vector3D)}.
     */
    @Test
    public final void testRotate()
    {
        // input data
        Rotation3D rot = Rotation3D.fromEulerAngles(0.1, 0.2, 0.3);
        Vector3D v0 = new Vector3D(5, 4, 3);
        
        // compute transformed vector
        Vector3D res = rot.rotate(v0);
        
        // check result by comparing with result from affine transform
        double[][] mat = rot.affineMatrix();
        AffineTransform3D transfo = new MatrixAffineTransform3D(
                mat[0][0], mat[0][1], mat[0][2], mat[0][3], 
                mat[1][0], mat[1][1], mat[1][2], mat[1][3], 
                mat[2][0], mat[2][1], mat[2][2], mat[2][3]);
        Vector3D exp = transfo.transform(v0);
        
        assertTrue(exp.almostEquals(res, 1e-8));
    }

    /**
     * Test method for {@link net.sci.geom.geom3d.Rotation3D#eulerAngles()}.
     */
    @Test
    public final void testEulerAngles()
    {
        double[] refAngles = new double[] {0.1, 0.2, 0.3};
        Rotation3D rot = Rotation3D.fromEulerAngles(refAngles[0], refAngles[1], refAngles[2]);
        
        double[] angles = rot.eulerAngles();
        
        assertEquals(refAngles[0], angles[0], 1e-8);
        assertEquals(refAngles[1], angles[1], 1e-8);
        assertEquals(refAngles[2], angles[2], 1e-8);
    }

    /**
     * Test method for {@link net.sci.geom.geom3d.Rotation3D#affineMatrix()}.
     */
    @Test
    public final void testAffineMatrix()
    {
        Rotation3D refRot = Rotation3D.fromAxisAngle(new Vector3D(3, 2, 1), 0.3);
        
        double[][] matrix = refRot.affineMatrix();
        Rotation3D rot2 = Rotation3D.fromMatrix(matrix);
           
        assertTrue(refRot.almostEquals(rot2, 1e-10));
    }

    /**
     * Test method for {@link net.sci.geom.geom3d.Rotation3D#inverse()}.
     */
    @Test
    public final void testComposeWithInverse()
    {
        Rotation3D refRot = Rotation3D.fromAxisAngle(new Vector3D(3, 2, 1), 0.3);
        Rotation3D rot = refRot.compose(refRot.inverse());
        
        assertTrue(rot.isIdentity(1e-10));
    }
}
