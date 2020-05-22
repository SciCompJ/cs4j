/**
 * 
 */
package net.sci.geom.geom2d.polygon;

import static org.junit.Assert.*;

import java.util.Iterator;

import net.sci.geom.geom2d.LineSegment2D;
import net.sci.geom.geom2d.Point2D;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class LineString2DTest
{
	/**
	 * Test method for {@link net.sci.geom.geom2d.polygon.LineString2D#closestVertexIndex(net.sci.geom.geom2d.Point2D)}.
	 */
	@Test
	public final void testClosestVertexIndex()
	{
		LineString2D poly = new LineString2D(
				new Point2D(10, 20),
				new Point2D(50, 20),
				new Point2D(50, 40),
				new Point2D(10, 40));
		Point2D p1 = new Point2D(20, 15);
		assertEquals(0, poly.closestVertexIndex(p1));
		
		Point2D p2 = new Point2D(35, 15);
		assertEquals(1, poly.closestVertexIndex(p2));
		
		Point2D p3 = new Point2D(15, 45);
		assertEquals(3, poly.closestVertexIndex(p3));
	}

    /**
     * Test method for {@link net.sci.geom.geom2d.polygon.LineString2D#resampleBySpacing()}.
     */
    @Test
    public final void testResampleBySpacing()
    {
        // line string with edge lengths 40, 20, and 40.
        LineString2D poly = new LineString2D(
                new Point2D(10, 20),
                new Point2D(50, 20),
                new Point2D(50, 40),
                new Point2D(10, 40));
        LineString2D poly2 = poly.resampleBySpacing(20);
        
        Point2D p20 = poly2.getPoint(0);
        assertTrue(p20.distance(10, 20) < 0.001);
        Point2D p21 = poly2.getPoint(poly2.vertexNumber() - 1);
        assertTrue(p21.distance(10, 40) < 0.001);
    }
    
    /**
     * Test method for {@link net.sci.geom.geom2d.polygon.LinearRing2D#asPolyline(int)}.
     */
    @Test
    public final void testAsPolyline_Int()
    {
        // line string with edge lengths 40, 20, and 40.
        LineString2D poly = new LineString2D(
                new Point2D(10, 20),
                new Point2D(50, 20),
                new Point2D(50, 40),
                new Point2D(10, 40));
        
        Polyline2D poly2 = poly.asPolyline(16);
        assertEquals(16, poly2.vertexNumber());
        
        double refLength = poly.length();
        assertEquals(refLength, poly2.length(), 0.1);
    }

    /**
     * Test method for {@link net.sci.geom.geom2d.polygon.LineString2D#length()}.
     */
    @Test
    public final void testLength()
    {
        // line string with edge lengths 40, 20, and 40.
        LineString2D poly = new LineString2D(
                new Point2D(10, 20),
                new Point2D(50, 20),
                new Point2D(50, 40),
                new Point2D(10, 40));
        double length = poly.length();
        
        assertEquals(length, 100.0, 0.01);
    }
    
    /**
     * Test method for {@link net.sci.geom.geom2d.polygon.LineString2D#getPoint()}.
     */
    @Test
    public final void testGetPointAtLength()
    {
        // line string with edge lengths 40, 20, and 40.
        Point2D p0 = new Point2D(10, 20);
        Point2D p1 = new Point2D(50, 20);
        Point2D p2 = new Point2D(50, 40);
        Point2D p3 = new Point2D(10, 40);
        LineString2D poly = new LineString2D(p0, p1, p2, p3);
        
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
     * Test method for {@link net.sci.geom.geom2d.polygon.LineString2D#getPoint()}.
     */
    @Test
    public final void testGetPoint_lastPoint()
    {
        // line string with edge lengths 40, 20, and 40.
        LineString2D poly = new LineString2D(
                new Point2D(10, 20),
                new Point2D(50, 20),
                new Point2D(50, 40),
                new Point2D(10, 40));
        Point2D lastPoint = poly.getPoint(3);
        assertTrue(lastPoint.distance(new Point2D(10, 40)) < 0.001);
    }
    
	/**
	 * Test method for {@link net.sci.geom.geom2d.polygon.LineString2D#edgeIterator()}.
	 */
	@Test
	public final void testEdgeIterator()
	{
		LineString2D poly = new LineString2D(
				new Point2D(10, 20),
				new Point2D(50, 20),
				new Point2D(50, 40),
				new Point2D(10, 40));
		Iterator<LineSegment2D> iter = poly.edgeIterator();
		
		int count = 0;
		while(iter.hasNext())
		{
			iter.next();
			count++;
		}
		assertEquals(3, count);
	}


	/**
	 * Test method for {@link net.sci.geom.geom2d.polygon.LineString2D#distance(net.sci.geom.geom2d.Point2D)}.
	 */
	@Test
	public final void testDistance()
	{
		LineString2D poly = new LineString2D(
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
