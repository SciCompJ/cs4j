/**
 * 
 */
package net.sci.geom.geom2d;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

/**
 * 
 */
public class PrincipalAxes2DTest
{

    /**
     * Test method for {@link net.sci.geom.geom2d.PrincipalAxes2D#fromPoints(java.lang.Iterable)}.
     */
    @Test
    public final void testFromPoints()
    {
        // parameters of enclosing ellipse
        double xc = 50.0;
        double yc = 40.0;
        double r1 = 30;
        double r2 = 10;
        double angle = Math.toRadians(30.0);
        
        // mapping from unit circle to ellipse
        AffineTransform2D tra = AffineTransform2D.createTranslation(xc, yc);
        AffineTransform2D sca = AffineTransform2D.createScaling(r1, r2);
        AffineTransform2D rot = AffineTransform2D.createRotation(angle);
        AffineTransform2D transfo = tra.compose(rot).compose(sca);
        
        // generate a series of points regularly sampled within the unit circle
        // and transform them into ellipse basis
        ArrayList<Point2D> pts = new ArrayList<Point2D>();
        for (double y = -1.0; y <= 1.0; y +=.1)
        {
            for (double x = -1.0; x <= 1.0; x +=.1)
            {
                if (Math.hypot(x, y) <= 1.0)
                {
                    pts.add(new Point2D(x, y).transform(transfo));
                }
            }
        }
        
        PrincipalAxes2D axes = PrincipalAxes2D.fromPoints(pts);
        
        // should retrieve same parameters as ellipse
        Point2D center = axes.center();
        assertEquals(xc, center.x(), 0.01);
        assertEquals(yc, center.y(), 0.01);
        double[] scalings = axes.scalings();
        assertEquals(r1 * 0.5, scalings[0], 0.2);
        assertEquals(r2 * 0.5, scalings[1], 0.2);
        assertEquals(angle, axes.rotationAngle(), 0.01);
    }

}
