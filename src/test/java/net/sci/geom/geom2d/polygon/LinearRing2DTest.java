/**
 * 
 */
package net.sci.geom.geom2d.polygon;

import static org.junit.Assert.*;

import java.util.Iterator;

import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.line.LineSegment2D;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class LinearRing2DTest
{

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
		Iterator<LineSegment2D> iter = poly.edgeIterator();
		
		int count = 0;
		while(iter.hasNext())
		{
			iter.next();
			count++;
		}
		assertEquals(4, count);
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

}