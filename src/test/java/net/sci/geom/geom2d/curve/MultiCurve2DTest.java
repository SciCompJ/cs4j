/**
 * 
 */
package net.sci.geom.geom2d.curve;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.geom.geom2d.Bounds2D;
import net.sci.geom.geom2d.LineSegment2D;
import net.sci.geom.geom2d.Point2D;

/**
 * @author dlegland
 *
 */
public class MultiCurve2DTest
{
    
    /**
     * Test method for {@link net.sci.geom.geom2d.curve.MultiCurve2D#distance(net.sci.geom.geom2d.Point2D)}.
     */
    @Test
    public final void testDistance()
    {
        // create a curve set composed of two horizontal line segments
        LineSegment2D line1 = new LineSegment2D(new Point2D(10, 10), new Point2D(20, 10));
        LineSegment2D line2 = new LineSegment2D(new Point2D(10, 20), new Point2D(20, 20));
        MultiCurve2D cs = new MultiCurve2D(line1, line2);
        
        double dist1 = cs.distance(new Point2D(0, 10));
        assertEquals(10, dist1, 0.01);
        
        double dist2 = cs.distance(new Point2D(0, 0));
        assertEquals(10 * Math.sqrt(2), dist2, 0.01);

        double dist3 = cs.distance(new Point2D(30, 30));
        assertEquals(10 * Math.sqrt(2), dist3, 0.01);
    }
    
    /**
     * Test method for {@link net.sci.geom.geom2d.curve.MultiCurve2D#bounds()}.
     */
    @Test
    public final void testBoundingBox()
    {
        // create a curve set composed of two horizontal line segments
        LineSegment2D line1 = new LineSegment2D(new Point2D(20, 10), new Point2D(30, 20));
        LineSegment2D line2 = new LineSegment2D(new Point2D(10, 20), new Point2D(20, 30));
        MultiCurve2D cs = new MultiCurve2D(line1, line2);
        
        Bounds2D box = cs.bounds();
        Bounds2D exp = new Bounds2D(10, 30, 10, 30);
        assertTrue(exp.almostEquals(box, 0.01));
    }
    
}
