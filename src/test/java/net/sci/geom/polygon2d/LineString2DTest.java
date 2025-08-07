/**
 * 
 */
package net.sci.geom.polygon2d;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sci.geom.geom2d.Point2D;

/**
 * @author dlegland
 *
 */
public class LineString2DTest
{
    @Test
    public final void testInterpolate_LineString2D_LineString2D_TwoRectangles()
    {
        LineString2D ring1 = LineString2D.create(
                new Point2D(10, 20),
                new Point2D(40, 20),
                new Point2D(40, 30),
                new Point2D(10, 30));
        LineString2D ring2 = LineString2D.create(
                new Point2D(20, 10),
                new Point2D(30, 10),
                new Point2D(30, 40),
                new Point2D(20, 40));
        
        LineString2D res05 = LineString2D.interpolate(ring1, ring2, 0.5);
        assertTrue(res05.vertexPosition(0).almostEquals(new Point2D(15.0, 15.0), 0.1));
        assertTrue(res05.vertexPosition(1).almostEquals(new Point2D(35.0, 15.0), 0.1));
        assertTrue(res05.vertexPosition(2).almostEquals(new Point2D(35.0, 35.0), 0.1));
        assertTrue(res05.vertexPosition(3).almostEquals(new Point2D(15.0, 35.0), 0.1));

        LineString2D res02 = LineString2D.interpolate(ring1, ring2, 0.2);
        assertTrue(res02.vertexPosition(0).almostEquals(new Point2D(12.0, 18.0), 0.1));
        assertTrue(res02.vertexPosition(1).almostEquals(new Point2D(38.0, 18.0), 0.1));
        assertTrue(res02.vertexPosition(2).almostEquals(new Point2D(38.0, 32.0), 0.1));
        assertTrue(res02.vertexPosition(3).almostEquals(new Point2D(12.0, 32.0), 0.1));
    }
    

    /**
     * Test method for {@link net.sci.geom.polygon2d.LineString2D#resampleBySpacing()}.
     */
    @Test
    public final void testResampleBySpacing()
    {
        // line string with edge lengths 40, 20, and 40.
        LineString2D poly = LineString2D.create(
                new Point2D(10, 20),
                new Point2D(50, 20),
                new Point2D(50, 40),
                new Point2D(10, 40));
        LineString2D poly2 = poly.resampleBySpacing(20);
        
        Point2D p20 = poly2.point(0);
        assertTrue(p20.distance(10, 20) < 0.001);
        Point2D p21 = poly2.point(poly2.vertexCount() - 1);
        assertTrue(p21.distance(10, 40) < 0.001);
    }
    
    /**
     * Test method for {@link net.sci.geom.polygon2d.LinearRing2D#asPolyline(int)}.
     */
    @Test
    public final void testAsPolyline_Int()
    {
        // line string with edge lengths 40, 20, and 40.
        LineString2D poly = LineString2D.create(
                new Point2D(10, 20),
                new Point2D(50, 20),
                new Point2D(50, 40),
                new Point2D(10, 40));
        
        Polyline2D poly2 = poly.asPolyline(16);
        assertEquals(16, poly2.vertexCount());
        
        double refLength = poly.length();
        assertEquals(refLength, poly2.length(), 0.1);
    }

    /**
     * Test method for {@link net.sci.geom.polygon2d.LineString2D#length()}.
     */
    @Test
    public final void testLength()
    {
        // line string with edge lengths 40, 20, and 40.
        LineString2D poly = LineString2D.create(
                new Point2D(10, 20),
                new Point2D(50, 20),
                new Point2D(50, 40),
                new Point2D(10, 40));
        double length = poly.length();
        
        assertEquals(length, 100.0, 0.01);
    }
    
    /**
     * Test method for {@link net.sci.geom.polygon2d.LineString2D#getPoint()}.
     */
    @Test
    public final void testGetPointAtLength()
    {
        // line string with edge lengths 40, 20, and 40.
        Point2D p0 = new Point2D(10, 20);
        Point2D p1 = new Point2D(50, 20);
        Point2D p2 = new Point2D(50, 40);
        Point2D p3 = new Point2D(10, 40);
        LineString2D poly = LineString2D.create(p0, p1, p2, p3);
        
        // point in the middle of first edge
        Point2D pL20 = poly.getPointAtLength(20);
        Point2D expL20 = new Point2D(30, 20);
        assertTrue(pL20.distance(expL20) < 0.001);
        
        // point in the middle of last edge
        Point2D pL80 = poly.getPointAtLength(80);
        Point2D expL80 = new Point2D(30, 40);
        assertTrue(pL80.distance(expL80) < 0.001);

        // last point of the polyline
        Point2D pL100 = poly.getPointAtLength(100);
        Point2D expL100 = new Point2D(10, 40);
        assertTrue(pL100.distance(expL100) < 0.001);
    }

    /**
     * Test method for {@link net.sci.geom.polygon2d.LineString2D#getPoint()}.
     */
    @Test
    public final void testGetPoint_lastPoint()
    {
        // line string with edge lengths 40, 20, and 40.
        LineString2D poly = LineString2D.create(
                new Point2D(10, 20),
                new Point2D(50, 20),
                new Point2D(50, 40),
                new Point2D(10, 40));
        Point2D lastPoint = poly.point(3);
        assertTrue(lastPoint.distance(new Point2D(10, 40)) < 0.001);
    }
    
    /**
     * Test method for {@link net.sci.geom.polygon2d.LineString2D#vertices()}.
     */
    @Test
    public final void testVertices()
    {
        LineString2D poly = LineString2D.create(
                new Point2D(10, 20),
                new Point2D(50, 20),
                new Point2D(50, 40),
                new Point2D(10, 40));
        
        int count = 0;
        for (@SuppressWarnings("unused") Polyline2D.Vertex v : poly.vertices())
        {
            count++;
        }
        assertEquals(4, count);
    }

    /**
     * Test method for {@link net.sci.geom.polygon2d.LineString2D#edges()}.
     */
    @Test
    public final void testEdges()
    {
        LineString2D poly = LineString2D.create(
                new Point2D(10, 20),
                new Point2D(50, 20),
                new Point2D(50, 40),
                new Point2D(10, 40));
        
        int count = 0;
        for (@SuppressWarnings("unused") Polyline2D.Edge edge : poly.edges())
        {
            count++;
        }
        assertEquals(3, count);
    }


	/**
	 * Test method for {@link net.sci.geom.polygon2d.LineString2D#distance(net.sci.geom.geom2d.Point2D)}.
	 */
	@Test
	public final void testDistance()
	{
		LineString2D poly = LineString2D.create(
				new Point2D(10, 20),
				new Point2D(50, 20),
				new Point2D(50, 40),
				new Point2D(10, 40));
		
		Point2D p1 = new Point2D(20, 15);
		assertEquals(5, poly.distance(p1), .1);

		Point2D p2 = new Point2D(5, 35);
		assertEquals(Math.sqrt(50), poly.distance(p2), .1);
	}

}
