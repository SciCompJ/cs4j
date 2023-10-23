/**
 * 
 */
package net.sci.geom.geom2d;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class AffineTransform2DTest
{
    /**
     * Test method for {@link net.sci.geom.geom2d.AffineTransform2D#compose(net.sci.geom.geom2d.AffineTransform2D)}.
     */
    @Test
    public final void test_compose_TraRotTra()
    {
        AffineTransform2D tra = AffineTransform2D.createTranslation(10, 20);
        AffineTransform2D rot = AffineTransform2D.createRotation(Math.toRadians(30));
        AffineTransform2D exp = AffineTransform2D.createRotation(10, 20, Math.toRadians(30));
        
        AffineTransform2D trans = tra.compose(rot.compose(tra.inverse()));
               
        assertTrue(trans.almostEquals(exp, 0.011));
    }

    /**
     * Test method for {@link net.sci.geom.geom2d.AffineTransform2D#compose(net.sci.geom.geom2d.AffineTransform2D)}.
     */
    @Test
    public final void test_compose_associative_TraRotTra()
    {
        AffineTransform2D tra = AffineTransform2D.createTranslation(10, 20);
        AffineTransform2D rot = AffineTransform2D.createRotation(Math.toRadians(30));
        
        AffineTransform2D trans1 = tra.compose(rot.compose(tra.inverse()));
        AffineTransform2D trans2 = tra.compose(rot).compose(tra.inverse());
               
        assertTrue(trans1.almostEquals(trans2, 0.01));
    }
}
