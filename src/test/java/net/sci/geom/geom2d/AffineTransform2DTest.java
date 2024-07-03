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
    
    /**
     * Test method for {@link net.sci.geom.geom2d.AffineTransform2D#fromBasis(net.sci.geom.geom2d.Vector2D, net.sci.geom.geom2d.Vector2D)}.
     */
    @Test
    public final void testFromBasisVector2DVector2D()
    {
        Vector2D v1 = new Vector2D( 3,  3);
        Vector2D v2 = new Vector2D(-3,  3);
        
        AffineTransform2D transfo = AffineTransform2D.fromBasis(v1, v2);
        
        Point2D p00 = new Point2D(0, 0);
        assertTrue(p00.transform(transfo).almostEquals(new Point2D(0, 0), 0.001));
        Point2D p10 = new Point2D(1, 0);
        assertTrue(p10.transform(transfo).almostEquals(new Point2D(3, 3), 0.001));
        Point2D p01 = new Point2D(0, 1);
        assertTrue(p01.transform(transfo).almostEquals(new Point2D(-3, 3), 0.001));
    }
    
    /**
     * Test method for {@link net.sci.geom.geom3d.AffineTransform3D#fromBasis(net.sci.geom.geom3d.Vector3D, net.sci.geom.geom3d.Vector3D, net.sci.geom.geom3d.Vector3D, net.sci.geom.geom3d.Vector3D)}.
     */
    @Test
    public final void testFromBasisVector3DVector3DVector3DVector3D()
    {
        Vector2D v1 = new Vector2D( 3,  3);
        Vector2D v2 = new Vector2D(-3,  3);
        Vector2D vt = new Vector2D( 4,  3);
        
        AffineTransform2D transfo = AffineTransform2D.fromBasis(v1, v2, vt);
        
        Point2D p00 = new Point2D(0, 0);
        assertTrue(p00.transform(transfo).almostEquals(new Point2D(4, 3), 0.001));
        Point2D p10 = new Point2D(1, 0);
        assertTrue(p10.transform(transfo).almostEquals(new Point2D(7, 6), 0.001));
        Point2D p01 = new Point2D(0, 1);
        assertTrue(p01.transform(transfo).almostEquals(new Point2D(1, 6), 0.001));
    }
}
