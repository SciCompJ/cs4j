/**
 * 
 */
package net.sci.geom.geom2d;

import static org.junit.Assert.*;
import net.sci.geom.geom2d.polygon.PolygonalDomain2D;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class Box2DTest
{

	/**
	 * Test method for {@link net.sci.geom.geom2d.Bounds2D#getRectangle()}.
	 */
	@Test
	public final void testGetRectangle()
	{
		Bounds2D box = new Bounds2D(10, 50, 20, 40);
		PolygonalDomain2D poly = box.getRectangle();
		
		assertEquals(4, poly.vertexCount());
	}

	/**
	 * Test method for {@link net.sci.geom.geom2d.Bounds2D#contains(net.sci.geom.geom2d.Point2D)}.
	 */
	@Test
	public final void testContainsPoint2D()
	{
		Bounds2D box = new Bounds2D(10, 20, 10, 30);
		
		assertTrue(box.contains(new Point2D(20, 15)));
		assertFalse(box.contains(new Point2D(5, 15)));
		assertFalse(box.contains(new Point2D(25, 15)));
		assertFalse(box.contains(new Point2D(20, 5)));
		assertFalse(box.contains(new Point2D(20, 35)));
	}

}
