/**
 * 
 */
package net.sci.geom.geom2d.curve;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.geom.geom2d.Box2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.line.LineSegment2D;

/**
 * @author dlegland
 *
 */
public class CurveSet2DTest
{
    
    /**
     * Test method for {@link net.sci.geom.geom2d.curve.CurveSet2D#distance(net.sci.geom.geom2d.Point2D)}.
     */
    @Test
    public final void testDistance()
    {
        // create a curve set composed of two horizontal line segments
        LineSegment2D line1 = new LineSegment2D(new Point2D(10, 10), new Point2D(20, 10));
        LineSegment2D line2 = new LineSegment2D(new Point2D(10, 20), new Point2D(20, 20));
        CurveSet2D cs = new CurveSet2D(line1, line2);
        
        double dist1 = cs.distance(new Point2D(0, 10));
        assertEquals(10, dist1, .01);
        
        double dist2 = cs.distance(new Point2D(0, 0));
        assertEquals(10 * Math.sqrt(2), dist2, .01);

        double dist3 = cs.distance(new Point2D(30, 30));
        assertEquals(10 * Math.sqrt(2), dist3, .01);
    }
    
    /**
     * Test method for {@link net.sci.geom.geom2d.curve.CurveSet2D#boundingBox()}.
     */
    @Test
    public final void testBoundingBox()
    {
        // create a curve set composed of two horizontal line segments
        LineSegment2D line1 = new LineSegment2D(new Point2D(20, 10), new Point2D(30, 20));
        LineSegment2D line2 = new LineSegment2D(new Point2D(10, 20), new Point2D(20, 30));
        CurveSet2D cs = new CurveSet2D(line1, line2);
        
        Box2D box = cs.boundingBox();
        Box2D exp = new Box2D(10, 30, 10, 30);
        assertTrue(exp.almostEquals(box, .01));
    }
    
}
