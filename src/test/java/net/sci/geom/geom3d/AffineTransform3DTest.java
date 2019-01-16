/**
 * 
 */
package net.sci.geom.geom3d;

import static org.junit.Assert.assertEquals;

import net.sci.geom.geom3d.AffineTransform3D;
import net.sci.geom.geom3d.Point3D;

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
        
        AffineTransform3D trans = tra.concatenate(rot.concatenate(tra.invert()));
               
        double[][] mat = trans.getMatrix();
        double[][] expMat = exp.getMatrix();
        
        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 4; j++)
            {
                assertEquals(expMat[i][j], mat[i][j], .01);
            }
        }
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
        
        AffineTransform3D trans = tra.invert().preConcatenate(rot).preConcatenate(tra);
               
        double[][] mat = trans.getMatrix();
        double[][] expMat = exp.getMatrix();
        
        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 4; j++)
            {
                assertEquals(expMat[i][j], mat[i][j], .01);
            }
        }
    }

}
