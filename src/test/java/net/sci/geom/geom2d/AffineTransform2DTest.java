/**
 * 
 */
package net.sci.geom.geom2d;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class AffineTransform2DTest
{

    /**
     * Test method for {@link net.sci.geom.geom2d.AffineTransform2D#concatenate(net.sci.geom.geom2d.AffineTransform2D)}.
     */
    @Test
    public final void testConcatenate_TraRotTra()
    {
        AffineTransform2D tra = AffineTransform2D.createTranslation(10, 20);
        AffineTransform2D rot = AffineTransform2D.createRotation(Math.toRadians(30));
        AffineTransform2D exp = AffineTransform2D.createRotation(10, 20, Math.toRadians(30));
        
        AffineTransform2D trans = tra.concatenate(rot.concatenate(tra.inverse()));
               
        double[][] mat = trans.affineMatrix();
        double[][] expMat = exp.affineMatrix();
        
        for(int i = 0; i < 2; i++)
        {
            for(int j = 0; j < 3; j++)
            {
                assertEquals(expMat[i][j], mat[i][j], .01);
            }
        }
    }

    /**
     * Test method for {@link net.sci.geom.geom2d.AffineTransform2D#preConcatenate(net.sci.geom.geom2d.AffineTransform2D)}.
     */
    @Test
    public final void testPreConcatenate_TraRotTra()
    {
        AffineTransform2D tra = AffineTransform2D.createTranslation(10, 20);
        AffineTransform2D rot = AffineTransform2D.createRotation(Math.toRadians(30));
        AffineTransform2D exp = AffineTransform2D.createRotation(10, 20, Math.toRadians(30));
        
        AffineTransform2D trans = tra.inverse().preConcatenate(rot).preConcatenate(tra); 
                
        double[][] mat = trans.affineMatrix();
        double[][] expMat = exp.affineMatrix();
        
        for(int i = 0; i < 2; i++)
        {
            for(int j = 0; j < 3; j++)
            {
                assertEquals(expMat[i][j], mat[i][j], .01);
            }
        }
    }

}
