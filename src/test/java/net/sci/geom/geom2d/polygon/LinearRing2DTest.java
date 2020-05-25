/**
 * 
 */
package net.sci.geom.geom2d.polygon;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Test;

import net.sci.geom.geom2d.Point2D;

/**
 * @author dlegland
 *
 */
public class LinearRing2DTest
{
    /**
     * Test method for {@link net.sci.geom.geom2d.polygon.LinearRing2D#resampleBySpacing(double)}.
     */
    @Test
    public final void testResampleBySpacing()
    {
        LinearRing2D ring = new LinearRing2D(
                new Point2D(00, 00),
                new Point2D(60, 00),
                new Point2D(60, 40),
                new Point2D(00, 40));
        LinearRing2D ring2 = ring.resampleBySpacing(10.0);
        assertEquals(20, ring2.vertexNumber());
    }
    
    /**
     * Test method for {@link net.sci.geom.geom2d.polygon.LinearRing2D#asPolyline(int)}.
     */
    @Test
    public final void testAsPolyline_Int()
    {
        LinearRing2D ring = new LinearRing2D(
                new Point2D(00, 00),
                new Point2D(60, 00),
                new Point2D(60, 40),
                new Point2D(00, 40));
        Polyline2D poly2 = ring.asPolyline(20);
        assertEquals(20, poly2.vertexNumber());
        
        double refLength = ring.length();
        assertEquals(refLength, poly2.length(), .1);
    }
    
	/**
	 * Test method for {@link net.sci.geom.geom2d.polygon.LinearRing2D#signedArea()}.
	 */
	@Test
	public final void testSignedArea_CCW()
	{
		LinearRing2D polyline = new LinearRing2D(
				new Point2D(20, 10),
				new Point2D(20, 20),
				new Point2D(10, 20),
				new Point2D(10, 10));
		assertEquals(100, polyline.signedArea(), 1e-10);
	}
	
	@Test
	public void testSignedArea_CW(){
		LinearRing2D invert = new LinearRing2D(
				new Point2D(20, 10),
				new Point2D(10, 10),
				new Point2D(10, 20),
				new Point2D(20, 20));
		assertEquals(-100, invert.signedArea(), 1e-10);
	}

	/**
	 * Test method for {@link net.sci.geom.geom2d.polygon.LinearRing2D#closestVertexIndex(net.sci.geom.geom2d.Point2D)}.
	 */
	@Test
	public final void testClosestVertexIndex()
	{
		LinearRing2D poly = new LinearRing2D(
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
     * Test method for {@link net.sci.geom.geom2d.polygon.LinearRing2D#getPoint()}.
     */
    @Test
    public final void testGetPointAtLength()
    {
        // line string with edge lengths 40, 20, and 40.
        Point2D p0 = new Point2D(10, 20);
        Point2D p1 = new Point2D(50, 20);
        Point2D p2 = new Point2D(50, 40);
        Point2D p3 = new Point2D(10, 40);
        LinearRing2D poly = new LinearRing2D(p0, p1, p2, p3);
        
        // point in the middle of first edge
        Point2D pL20 = poly.getPointAtLength(20);
        Point2D expL20 = new Point2D(30, 20);
        assertTrue(pL20.distance(expL20) < 0.001);
        
        // point in the middle of last edge
        Point2D pL80 = poly.getPointAtLength(80);
        Point2D expL80 = new Point2D(30, 40);
        assertTrue(pL80.distance(expL80) < 0.001);

        // last point of the polyline
        Point2D pL100 = poly.getPointAtLength(120);
        Point2D expL100 = new Point2D(10, 20);
        assertTrue(pL100.distance(expL100) < 0.001);
    }

	/**
	 * Test method for {@link net.sci.geom.geom2d.polygon.LinearRing2D#edgeIterator()}.
	 */
	@Test
	public final void testEdgeIterator()
	{
		LinearRing2D poly = new LinearRing2D(
				new Point2D(10, 20),
				new Point2D(50, 20),
				new Point2D(50, 40),
				new Point2D(10, 40));
		Iterator<? extends Polyline2D.Edge> iter = poly.edgeIterator();
		
		int count = 0;
		while(iter.hasNext())
		{
			iter.next();
			count++;
		}
		assertEquals(4, count);
	}

    /**
     * Test method for {@link net.sci.geom.geom2d.polygon.LinearRing2D#length()}.
     */
    @Test
    public final void testLength()
    {
        // line stringwith edge lengths 40, 20, 40, and 20.
        LinearRing2D poly = new LinearRing2D(
                new Point2D(10, 20),
                new Point2D(50, 20),
                new Point2D(50, 40),
                new Point2D(10, 40));
        double length = poly.length();
        
        assertEquals(length, 120.0, 0.01);
    }
    
	/**
	 * Test method for {@link net.sci.geom.geom2d.polygon.LinearRing2D#distance(net.sci.geom.geom2d.Point2D)}.
	 */
	@Test
	public final void testDistance()
	{
		LinearRing2D poly = new LinearRing2D(
				new Point2D(10, 20),
				new Point2D(50, 20),
				new Point2D(50, 40),
				new Point2D(10, 40));
		Point2D p1 = new Point2D(20, 15);
		assertEquals(5, poly.distance(p1), .1);
	}

	@Test
	public final void testSignedDistance()
	{
        LinearRing2D poly = new LinearRing2D(
                new Point2D(10, 20),
                new Point2D(50, 20),
                new Point2D(50, 40),
                new Point2D(10, 40));
        
        Point2D p1 = new Point2D(20, 15);
        assertEquals(5, poly.signedDistance(p1), .1);
        
        Point2D p2 = new Point2D(20, 25);
        assertEquals(-5, poly.signedDistance(p2), .1);
        
	}
}
