/**
 * 
 */
package net.sci.geom.polygon2d;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;

import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.StraightLine2D;
import net.sci.geom.geom2d.Vector2D;

/**
 * @author dlegland
 *
 */
public class LinearRing2DTest
{
    @Test
    public final void testMergeDuplicateVertices_Double_Rectangle()
    {
        LinearRing2D ring = LinearRing2D.create(10);
        ring.addVertex(new Point2D(10.0, 10.0));
        ring.addVertex(new Point2D(20.0, 10.0));
        ring.addVertex(new Point2D(20.0, 10.0));
        ring.addVertex(new Point2D(20.0, 10.0));
        ring.addVertex(new Point2D(20.0, 20.0));
        ring.addVertex(new Point2D(10.0, 20.0));
        ring.addVertex(new Point2D(10.0, 20.0));
        ring.addVertex(new Point2D(10.0, 20.0));
        
        LinearRing2D ring2 = ring.mergeMultipleVertices(0.01);
        
        assertEquals(ring2.vertexCount(), 4);
    }
    
    
    @Test
    public final void testInterpolate_LinearRing2D_LinearRing2D_TwoRectangles()
    {
        LinearRing2D ring1 = LinearRing2D.create(
                new Point2D(10, 20),
                new Point2D(40, 20),
                new Point2D(40, 30),
                new Point2D(10, 30));
        LinearRing2D ring2 = LinearRing2D.create(
                new Point2D(20, 10),
                new Point2D(30, 10),
                new Point2D(30, 40),
                new Point2D(20, 40));
        
        LinearRing2D res05 = LinearRing2D.interpolate(ring1, ring2, 0.5);
        assertTrue(res05.vertexPosition(0).almostEquals(new Point2D(15.0, 15.0), 0.1));
        assertTrue(res05.vertexPosition(1).almostEquals(new Point2D(35.0, 15.0), 0.1));
        assertTrue(res05.vertexPosition(2).almostEquals(new Point2D(35.0, 35.0), 0.1));
        assertTrue(res05.vertexPosition(3).almostEquals(new Point2D(15.0, 35.0), 0.1));

        LinearRing2D res02 = LinearRing2D.interpolate(ring1, ring2, 0.2);
        assertTrue(res02.vertexPosition(0).almostEquals(new Point2D(12.0, 18.0), 0.1));
        assertTrue(res02.vertexPosition(1).almostEquals(new Point2D(38.0, 18.0), 0.1));
        assertTrue(res02.vertexPosition(2).almostEquals(new Point2D(38.0, 32.0), 0.1));
        assertTrue(res02.vertexPosition(3).almostEquals(new Point2D(12.0, 32.0), 0.1));
    }
    
    /**
     * Test method for {@link net.sci.geom.polygon2d.LinearRing2D#resampleBySpacing(double)}.
     */
    @Test
    public final void testResampleBySpacing()
    {
        LinearRing2D ring = LinearRing2D.create(
                new Point2D(00, 00),
                new Point2D(60, 00),
                new Point2D(60, 40),
                new Point2D(00, 40));
        LinearRing2D ring2 = ring.resampleBySpacing(10.0);
        assertEquals(20, ring2.vertexCount());
    }
    
    /**
     * Test method for {@link net.sci.geom.polygon2d.LinearRing2D#asPolyline(int)}.
     */
    @Test
    public final void testAsPolyline_Int()
    {
        LinearRing2D ring = LinearRing2D.create(
                new Point2D(00, 00),
                new Point2D(60, 00),
                new Point2D(60, 40),
                new Point2D(00, 40));
        Polyline2D poly2 = ring.asPolyline(20);
        assertEquals(20, poly2.vertexCount());
        
        double refLength = ring.length();
        assertEquals(refLength, poly2.length(), .1);
    }
    
	/**
	 * Test method for {@link net.sci.geom.polygon2d.LinearRing2D#signedArea()}.
	 */
	@Test
	public final void testSignedArea_CCW()
	{
		LinearRing2D polyline = LinearRing2D.create(
				new Point2D(20, 10),
				new Point2D(20, 20),
				new Point2D(10, 20),
				new Point2D(10, 10));
		assertEquals(100, polyline.signedArea(), 1e-10);
	}
	
	@Test
	public void testSignedArea_CW(){
		LinearRing2D invert = LinearRing2D.create(
				new Point2D(20, 10),
				new Point2D(10, 10),
				new Point2D(10, 20),
				new Point2D(20, 20));
		assertEquals(-100, invert.signedArea(), 1e-10);
	}

	/**
	 * Test method for {@link net.sci.geom.polygon2d.LinearRing2D#closestVertexIndex(net.sci.geom.geom2d.Point2D)}.
	 */
	@Test
	public final void testClosestVertexIndex()
	{
		LinearRing2D poly = LinearRing2D.create(
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
     * Test method for {@link net.sci.geom.polygon2d.LinearRing2D#getPoint()}.
     */
    @Test
    public final void testGetPointAtLength()
    {
        // line string with edge lengths 40, 20, and 40.
        Point2D p0 = new Point2D(10, 20);
        Point2D p1 = new Point2D(50, 20);
        Point2D p2 = new Point2D(50, 40);
        Point2D p3 = new Point2D(10, 40);
        LinearRing2D poly = LinearRing2D.create(p0, p1, p2, p3);
        
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
     * Test method for {@link net.sci.geom.polygon2d.LineString2D#vertices()}.
     */
    @Test
    public final void testVertices()
    {
        LinearRing2D poly = LinearRing2D.create(
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
	 * Test method for {@link net.sci.geom.polygon2d.LinearRing2D#edges()}.
	 */
	@Test
	public final void testEdges()
	{
		LinearRing2D poly = LinearRing2D.create(
				new Point2D(10, 20),
				new Point2D(50, 20),
				new Point2D(50, 40),
				new Point2D(10, 40));
		
        int count = 0;
        for (@SuppressWarnings("unused") Polyline2D.Edge edge : poly.edges())
        {
            count++;
        }
		assertEquals(4, count);
	}

	@Test
    public final void testIntersectionsStraightLine2D_square_LineH()
	{
		LinearRing2D ring = LinearRing2D.create(
				new Point2D(10, 20),
				new Point2D(50, 20),
				new Point2D(50, 40),
				new Point2D(10, 40));
		
		StraightLine2D line = new StraightLine2D(new Point2D(0, 30), new Vector2D(3.3, 0));
		Collection<Point2D> intersections = ring.intersections(line);
		
		assertEquals(2, intersections.size());
		assertTrue(containsPoint(intersections, new Point2D(10, 30), 0.01));
		assertTrue(containsPoint(intersections, new Point2D(50, 30), 0.01));
	}
	
	@Test
    public final void testIntersectionsStraightLine2D_square_LineV()
	{
		LinearRing2D ring = LinearRing2D.create(
				new Point2D(10, 20),
				new Point2D(50, 20),
				new Point2D(50, 40),
				new Point2D(10, 40));
		
		StraightLine2D line = new StraightLine2D(new Point2D(20, 0), new Vector2D(0.0, 3.3));
		Collection<Point2D> intersections = ring.intersections(line);
		
		assertEquals(2, intersections.size());
		assertTrue(containsPoint(intersections, new Point2D(20, 20), 0.01));
		assertTrue(containsPoint(intersections, new Point2D(20, 40), 0.01));
	}
	
	@Test
    public final void testIntersectionsStraightLine2D_polygon_LineH()
	{
		LinearRing2D ring = LinearRing2D.create(
				new Point2D(10, 20),
				new Point2D(30, 60),
				new Point2D(40, 40),
				new Point2D(70, 70),
				new Point2D(90, 20));
		
		StraightLine2D line = new StraightLine2D(new Point2D(4.5, 50), new Vector2D(1.3, 0));
		Collection<Point2D> intersections = ring.intersections(line);
		
		assertEquals(4, intersections.size());
		assertTrue(containsPoint(intersections, new Point2D(25, 50), 0.01));
		assertTrue(containsPoint(intersections, new Point2D(35, 50), 0.01));
		assertTrue(containsPoint(intersections, new Point2D(50, 50), 0.01));
		assertTrue(containsPoint(intersections, new Point2D(78, 50), 0.01));
	}
	
	@Test
    public final void testIntersectionsStraightLine2D_polygon_LineH2()
	{
		LinearRing2D ring = LinearRing2D.create(
				new Point2D(10, 20),
				new Point2D(30, 60),
				new Point2D(40, 40),
				new Point2D(70, 70),
				new Point2D(90, 20));
		
		StraightLine2D line = new StraightLine2D(new Point2D(4.5, 40), new Vector2D(1.3, 0));
		Collection<Point2D> intersections = ring.intersections(line);
		
		assertEquals(4, intersections.size());
//		assertTrue(containsPoint(intersections, new Point2D(25, 50), 0.01));
//		assertTrue(containsPoint(intersections, new Point2D(35, 50), 0.01));
//		assertTrue(containsPoint(intersections, new Point2D(50, 50), 0.01));
//		assertTrue(containsPoint(intersections, new Point2D(78, 50), 0.01));
	}
	
	private boolean containsPoint(Collection<Point2D> points, Point2D q, double eps)
	{
		for (Point2D p : points)
		{
			if (p.almostEquals(q, eps))
			{
				return true;
			}
		}
		return false;
	}

    /**
     * Test method for {@link net.sci.geom.polygon2d.LinearRing2D#length()}.
     */
    @Test
    public final void testLength()
    {
        // line stringwith edge lengths 40, 20, 40, and 20.
        LinearRing2D poly = LinearRing2D.create(
                new Point2D(10, 20),
                new Point2D(50, 20),
                new Point2D(50, 40),
                new Point2D(10, 40));
        double length = poly.length();
        
        assertEquals(length, 120.0, 0.01);
    }
    
	/**
	 * Test method for {@link net.sci.geom.polygon2d.LinearRing2D#distance(net.sci.geom.geom2d.Point2D)}.
	 */
	@Test
	public final void testDistance()
	{
		LinearRing2D poly = LinearRing2D.create(
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
        LinearRing2D poly = LinearRing2D.create(
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
