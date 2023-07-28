/**
 * 
 */
package net.sci.geom.geom2d.polygon;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.Vector2D;

/**
 * @author dlegland
 *
 */
public class DefaultLineString2DTest
{
    @Test
    public final void testMergeDuplicateVertices_Double_Rectangle()
    {
        DefaultLineString2D poly = new DefaultLineString2D(10);
        poly.addVertex(new Point2D(10.0, 10.0));
        poly.addVertex(new Point2D(20.0, 10.0));
        poly.addVertex(new Point2D(20.0, 10.0));
        poly.addVertex(new Point2D(20.0, 10.0));
        poly.addVertex(new Point2D(20.0, 20.0));
        poly.addVertex(new Point2D(10.0, 20.0));
        poly.addVertex(new Point2D(10.0, 20.0));
        poly.addVertex(new Point2D(10.0, 20.0));
        
        LineString2D poly2 = poly.mergeMultipleVertices(0.01);
        
        assertEquals(poly2.vertexCount(), 4);
    }

    @Test
    public final void testMergeDuplicateVertices_Double_Rectangle_SameExtremity()
    {
        DefaultLineString2D poly = new DefaultLineString2D(10);
        poly.addVertex(new Point2D(10.0, 10.0));
        poly.addVertex(new Point2D(20.0, 10.0));
        poly.addVertex(new Point2D(20.0, 10.0));
        poly.addVertex(new Point2D(20.0, 10.0));
        poly.addVertex(new Point2D(20.0, 20.0));
        poly.addVertex(new Point2D(10.0, 20.0));
        poly.addVertex(new Point2D(10.0, 20.0));
        poly.addVertex(new Point2D(10.0, 20.0));
        poly.addVertex(new Point2D(10.0, 10.0));
        poly.addVertex(new Point2D(10.0, 10.0));
        
        LineString2D poly2 = poly.mergeMultipleVertices(0.01);
        
        assertEquals(poly2.vertexCount(), 5);
    }
 
    @Test
    public final void testVertexNormal()
    {
        DefaultLineString2D poly = new DefaultLineString2D(
                new Point2D(10, 20),
                new Point2D(50, 20),
                new Point2D(50, 40),
                new Point2D(10, 40));
        poly.computeNormals();
        
        Polyline2D.Vertex v0 = poly.vertex(0);
        Vector2D n0 = v0.normal();
        assertEquals( 0.000, n0.x(), 0.001);
        assertEquals(-1.000, n0.y(), 0.001);
        
        Polyline2D.Vertex v1 = poly.vertex(1);
        Vector2D n1 = v1.normal();
        assertEquals( 0.707, n1.x(), 0.001);
        assertEquals(-0.707, n1.y(), 0.001);
        
        Polyline2D.Vertex v2 = poly.vertex(2);
        Vector2D n2 = v2.normal();
        assertEquals( 0.707, n2.x(), 0.001);
        assertEquals( 0.707, n2.y(), 0.001);
        
        Polyline2D.Vertex v3 = poly.vertex(3);
        Vector2D n3 = v3.normal();
        assertEquals( 0.000, n3.x(), 0.001);
        assertEquals(+1.000, n3.y(), 0.001);
    }
    
    
	/**
	 * Test method for {@link net.sci.geom.geom2d.polygon.DefaultLineString2D#closestVertexIndex(net.sci.geom.geom2d.Point2D)}.
	 */
	@Test
	public final void testClosestVertexIndex()
	{
		DefaultLineString2D poly = new DefaultLineString2D(
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
     * Test method for {@link net.sci.geom.geom2d.polygon.DefaultLineString2D#resampleBySpacing()}.
     */
    @Test
    public final void testResampleBySpacing()
    {
        // line string with edge lengths 40, 20, and 40.
        DefaultLineString2D poly = new DefaultLineString2D(
                new Point2D(10, 20),
                new Point2D(50, 20),
                new Point2D(50, 40),
                new Point2D(10, 40));
        LineString2D poly2 = poly.resampleBySpacing(20);
        
        Point2D p20 = poly2.getPoint(0);
        assertTrue(p20.distance(10, 20) < 0.001);
        Point2D p21 = poly2.getPoint(poly2.vertexCount() - 1);
        assertTrue(p21.distance(10, 40) < 0.001);
    }
    
    /**
     * Test method for {@link net.sci.geom.geom2d.polygon.LinearRing2D#asPolyline(int)}.
     */
    @Test
    public final void testAsPolyline_Int()
    {
        // line string with edge lengths 40, 20, and 40.
        DefaultLineString2D poly = new DefaultLineString2D(
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
     * Test method for {@link net.sci.geom.geom2d.polygon.DefaultLineString2D#length()}.
     */
    @Test
    public final void testLength()
    {
        // line string with edge lengths 40, 20, and 40.
        DefaultLineString2D poly = new DefaultLineString2D(
                new Point2D(10, 20),
                new Point2D(50, 20),
                new Point2D(50, 40),
                new Point2D(10, 40));
        double length = poly.length();
        
        assertEquals(length, 100.0, 0.01);
    }
    
    /**
     * Test method for {@link net.sci.geom.geom2d.polygon.DefaultLineString2D#getPoint()}.
     */
    @Test
    public final void testGetPointAtLength()
    {
        // line string with edge lengths 40, 20, and 40.
        Point2D p0 = new Point2D(10, 20);
        Point2D p1 = new Point2D(50, 20);
        Point2D p2 = new Point2D(50, 40);
        Point2D p3 = new Point2D(10, 40);
        DefaultLineString2D poly = new DefaultLineString2D(p0, p1, p2, p3);
        
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
     * Test method for {@link net.sci.geom.geom2d.polygon.DefaultLineString2D#getPoint()}.
     */
    @Test
    public final void testGetPoint_lastPoint()
    {
        // line string with edge lengths 40, 20, and 40.
        DefaultLineString2D poly = new DefaultLineString2D(
                new Point2D(10, 20),
                new Point2D(50, 20),
                new Point2D(50, 40),
                new Point2D(10, 40));
        Point2D lastPoint = poly.getPoint(3);
        assertTrue(lastPoint.distance(new Point2D(10, 40)) < 0.001);
    }
    
	/**
	 * Test method for {@link net.sci.geom.geom2d.polygon.DefaultLineString2D#edges()}.
	 */
	@Test
	public final void testEdges()
	{
		DefaultLineString2D poly = new DefaultLineString2D(
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
	 * Test method for {@link net.sci.geom.geom2d.polygon.DefaultLineString2D#distance(net.sci.geom.geom2d.Point2D)}.
	 */
	@Test
	public final void testDistance()
	{
		DefaultLineString2D poly = new DefaultLineString2D(
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
