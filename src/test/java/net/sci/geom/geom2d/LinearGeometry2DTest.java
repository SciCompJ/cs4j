/**
 * 
 */
package net.sci.geom.geom2d;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class LinearGeometry2DTest
{
    // Nomenclature for directions:
    // * uses two characters, one for X direction, the other one for Y direction
    // * "P" stands for positive increase
    // * "N" stands for negative increase
    // * "O" stands for null increase (resulting in either vertical or horizontal direction)

    /**
     * Test method for {@link net.sci.geom.geom2d.LinearGeometry2D#intersection(net.sci.geom.geom2d.LinearGeometry2D, net.sci.geom.geom2d.LinearGeometry2D)}.
     */
    @Test
    public final void testIntersection_LinePO_LineOP()
    {
        StraightLine2D line1 = new StraightLine2D(new Point2D(5, 10), new Vector2D(3, 0));
        StraightLine2D line2 = new StraightLine2D(new Point2D(10, 5), new Vector2D(0, 3));

        Point2D inter = LinearGeometry2D.intersection(line1, line2);
        assertNotNull(inter);
        assertEquals(0, inter.distance(10, 10), .01);
    }

    /**
     * Test method for {@link net.sci.geom.geom2d.LinearGeometry2D#intersection(net.sci.geom.geom2d.LinearGeometry2D, net.sci.geom.geom2d.LinearGeometry2D)}.
     */
    @Test
    public final void testIntersection_LinePO_LineON()
    {
        StraightLine2D line1 = new StraightLine2D(new Point2D(5, 10), new Vector2D(3,  0));
        StraightLine2D line2 = new StraightLine2D(new Point2D(10, 5), new Vector2D(0, -3));

        Point2D inter = LinearGeometry2D.intersection(line1, line2);
        assertNotNull(inter);
        assertEquals(0, inter.distance(10, 10), .01);
    }

    /**
     * Test method for {@link net.sci.geom.geom2d.LinearGeometry2D#intersection(net.sci.geom.geom2d.LinearGeometry2D, net.sci.geom.geom2d.LinearGeometry2D)}.
     */
    @Test
    public final void testIntersection_LineNO_LineOP()
    {
        StraightLine2D line1 = new StraightLine2D(new Point2D(5, 10), new Vector2D(-3, 0));
        StraightLine2D line2 = new StraightLine2D(new Point2D(10, 5), new Vector2D( 0, 3));

        Point2D inter = LinearGeometry2D.intersection(line1, line2);
        assertNotNull(inter);
        assertEquals(0, inter.distance(10, 10), .01);
    }

    /**
     * Test method for {@link net.sci.geom.geom2d.LinearGeometry2D#intersection(net.sci.geom.geom2d.LinearGeometry2D, net.sci.geom.geom2d.LinearGeometry2D)}.
     */
    @Test
    public final void testIntersection_LineNO_LineON()
    {
        StraightLine2D line1 = new StraightLine2D(new Point2D(5, 10), new Vector2D(-3,  0));
        StraightLine2D line2 = new StraightLine2D(new Point2D(10, 5), new Vector2D( 0, -3));

        Point2D inter = LinearGeometry2D.intersection(line1, line2);
        assertNotNull(inter);
        assertEquals(0, inter.distance(10, 10), .01);
    }

    /**
     * Test method for {@link net.sci.geom.geom2d.LinearGeometry2D#intersection(net.sci.geom.geom2d.LinearGeometry2D, net.sci.geom.geom2d.LinearGeometry2D)}.
     */
    @Test
    public final void testIntersection_LineSegmentPO_LineSegmentOP()
    {
        LineSegment2D line1 = new LineSegment2D(new Point2D( 5, 10), new Point2D(15, 10));
        LineSegment2D line2 = new LineSegment2D(new Point2D(10,  5), new Point2D(10, 15));

        Point2D inter = LinearGeometry2D.intersection(line1, line2);
        assertNotNull(inter);
        assertEquals(0, inter.distance(10, 10), .01);
    }

    /**
     * Test method for {@link net.sci.geom.geom2d.LinearGeometry2D#intersection(net.sci.geom.geom2d.LinearGeometry2D, net.sci.geom.geom2d.LinearGeometry2D)}.
     */
    @Test
    public final void testIntersection_NoInter_LineSegmentPO_LineSegmentOP()
    {
        LineSegment2D line1 = new LineSegment2D(new Point2D( 5, 20), new Point2D(15, 20));
        LineSegment2D line2 = new LineSegment2D(new Point2D(10,  5), new Point2D(10, 15));

        Point2D inter = LinearGeometry2D.intersection(line1, line2);
        assertNull(inter);
    }

    /**
     * Test method for {@link net.sci.geom.geom2d.LinearGeometry2D#intersection(net.sci.geom.geom2d.LinearGeometry2D, net.sci.geom.geom2d.LinearGeometry2D)}.
     */
    @Test
    public final void testIntersection_LineSegmentPO_LineSegmentON()
    {
        LineSegment2D line1 = new LineSegment2D(new Point2D( 5, 10), new Point2D(15, 10));
        LineSegment2D line2 = new LineSegment2D(new Point2D(10, 15), new Point2D(10,  5));

        Point2D inter = LinearGeometry2D.intersection(line1, line2);
        assertNotNull(inter);
        assertEquals(0, inter.distance(10, 10), .01);
    }

    /**
     * Test method for {@link net.sci.geom.geom2d.LinearGeometry2D#intersection(net.sci.geom.geom2d.LinearGeometry2D, net.sci.geom.geom2d.LinearGeometry2D)}.
     */
    @Test
    public final void testIntersection_NoInter_LineSegmentPO_LineSegmentON()
    {
        LineSegment2D line1 = new LineSegment2D(new Point2D( 5, 20), new Point2D(15, 20));
        LineSegment2D line2 = new LineSegment2D(new Point2D(10, 15), new Point2D(10,  5));

        Point2D inter = LinearGeometry2D.intersection(line1, line2);
        assertNull(inter);
    }

    /**
     * Test method for {@link net.sci.geom.geom2d.LinearGeometry2D#intersection(net.sci.geom.geom2d.LinearGeometry2D, net.sci.geom.geom2d.LinearGeometry2D)}.
     */
    @Test
    public final void testIntersection_LineSegmentNO_LineSegmentOP()
    {
        LineSegment2D line1 = new LineSegment2D(new Point2D(15, 10), new Point2D( 5, 10));
        LineSegment2D line2 = new LineSegment2D(new Point2D(10,  5), new Point2D(10, 15));

        Point2D inter = LinearGeometry2D.intersection(line1, line2);
        assertNotNull(inter);
        assertEquals(0, inter.distance(10, 10), .01);
    }

    /**
     * Test method for {@link net.sci.geom.geom2d.LinearGeometry2D#intersection(net.sci.geom.geom2d.LinearGeometry2D, net.sci.geom.geom2d.LinearGeometry2D)}.
     */
    @Test
    public final void testIntersection_NoInter_LineSegmentNO_LineSegmentOP()
    {
        LineSegment2D line1 = new LineSegment2D(new Point2D(15, 20), new Point2D( 5, 20));
        LineSegment2D line2 = new LineSegment2D(new Point2D(10,  5), new Point2D(10, 15));

        Point2D inter = LinearGeometry2D.intersection(line1, line2);
        assertNull(inter);
    }

    /**
     * Test method for {@link net.sci.geom.geom2d.LinearGeometry2D#intersection(net.sci.geom.geom2d.LinearGeometry2D, net.sci.geom.geom2d.LinearGeometry2D)}.
     */
    @Test
    public final void testIntersection_LineSegmentNO_LineSegmentON()
    {
        LineSegment2D line1 = new LineSegment2D(new Point2D(15, 10), new Point2D( 5, 10));
        LineSegment2D line2 = new LineSegment2D(new Point2D(10, 15), new Point2D(10,  5));

        Point2D inter = LinearGeometry2D.intersection(line1, line2);
        assertNotNull(inter);
        assertEquals(0, inter.distance(10, 10), .01);
    }

    /**
     * Test method for {@link net.sci.geom.geom2d.LinearGeometry2D#intersection(net.sci.geom.geom2d.LinearGeometry2D, net.sci.geom.geom2d.LinearGeometry2D)}.
     */
    @Test
    public final void testIntersection_NoInter_LineSegmentNO_LineSegmentON()
    {
        LineSegment2D line1 = new LineSegment2D(new Point2D(15, 20), new Point2D( 5, 20));
        LineSegment2D line2 = new LineSegment2D(new Point2D(10, 15), new Point2D(10,  5));

        Point2D inter = LinearGeometry2D.intersection(line1, line2);
        assertNull(inter);
    }
}
