/**
 * 
 */
package net.sci.image.analyze;

import static org.junit.Assert.*;
import net.sci.array.data.scalar2d.UInt8Array2D;
import net.sci.geom.geom2d.Box2D;
import net.sci.geom.geom2d.Point2D;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class RegionAnalysis2DTest
{

	/**
	 * Test method for {@link net.sci.image.analyze.RegionAnalysis2D#centroids(net.sci.array.data.scalar2d.IntArray2D, int[])}.
	 */
	@Test
	public final void testCentroids()
	{
		UInt8Array2D array = createFourRectArray();
		
		int[] labels = new int[]{1, 2, 3, 4};
		Point2D[] centroids = RegionAnalysis2D.centroids(array, labels);
		
		assertEquals(4, centroids.length);
		assertTrue(new Point2D(1.5, 1.5).almostEquals(centroids[0], .1));
		assertTrue(new Point2D(6.0, 1.5).almostEquals(centroids[1], .1));
		assertTrue(new Point2D(1.5, 5.0).almostEquals(centroids[2], .1));
		assertTrue(new Point2D(6.0, 5.0).almostEquals(centroids[3], .1));
	}

	/**
	 * Test method for {@link net.sci.image.analyze.RegionAnalysis2D#boundingBoxes(net.sci.array.data.scalar2d.IntArray2D, int[])}.
	 */
	@Test
	public final void testBoundingBoxes()
	{
		UInt8Array2D array = createFourRectArray();
		
		int[] labels = new int[]{1, 2, 3, 4};
		Box2D[] boxes = RegionAnalysis2D.boundingBoxes(array, labels);
		
		assertEquals(4, boxes.length);
		Box2D box1 = new Box2D(0.5, 2.5, 0.5, 2.5);
		assertTrue(box1.almostEquals(boxes[0], 0.1));
		Box2D box2 = new Box2D(3.5, 8.5, 0.5, 2.5);
		assertTrue(box2.almostEquals(boxes[1], 0.1));
		Box2D box3 = new Box2D(0.5, 2.5, 3.5, 6.5);
		assertTrue(box3.almostEquals(boxes[2], 0.1));
		Box2D box4 = new Box2D(3.5, 8.5, 3.5, 6.5);
		assertTrue(box4.almostEquals(boxes[3], 0.1));
	}

	private UInt8Array2D createFourRectArray()
	{
		UInt8Array2D array = UInt8Array2D.create(10, 8);
		for (int x = 1; x < 3; x++)
		{
			array.setInt(x, 1, 1);
			array.setInt(x, 2, 1);
			
			array.setInt(x, 4, 3);
			array.setInt(x, 5, 3);
			array.setInt(x, 6, 3);
		}
		for (int x = 4; x < 9; x++)
		{
			array.setInt(x, 1, 2);
			array.setInt(x, 2, 2);
			
			array.setInt(x, 4, 4);
			array.setInt(x, 5, 4);
			array.setInt(x, 6, 4);
		}
		
		return array;
	}
}
