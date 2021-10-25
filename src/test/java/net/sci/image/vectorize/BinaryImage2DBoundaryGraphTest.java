/**
 * 
 */
package net.sci.image.vectorize;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import net.sci.array.binary.BinaryArray2D;
import net.sci.geom.graph.Graph2D;
import net.sci.image.vectorize.BinaryImage2DBoundaryGraph.IntPoint2D;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class BinaryImage2DBoundaryGraphTest
{
	@Test
	public final void test_SinglePixel()
	{
		BinaryArray2D array = BinaryArray2D.create(3, 3);
		array.setBoolean(1, 1, true);
		
		BinaryImage2DBoundaryGraph op = new BinaryImage2DBoundaryGraph();
		Graph2D graph = op.process(array);
		
		assertEquals(4, graph.vertexCount());
		assertEquals(4, graph.edgeCount());
	}
	
	@Test
	public final void test_SmallCross()
	{
		BinaryArray2D array = BinaryArray2D.create(6, 6);
		for (int i = 1; i < 5; i++)
		{
			array.setBoolean(i, 2, true);
			array.setBoolean(i, 3, true);
			array.setBoolean(2, i, true);
			array.setBoolean(3, i, true);
		}
//		array.print(System.out);
		
		BinaryImage2DBoundaryGraph op = new BinaryImage2DBoundaryGraph();
		Graph2D graph = op.process(array);
		
		assertEquals(16, graph.vertexCount());
		assertEquals(16, graph.edgeCount());
	}
	
	/**
	 * Test method for {@link net.sci.image.vectorize.BinaryImage2DBoundaryGraph#process(net.sci.array.binary.BinaryArray2D)}.
	 */
	@Test
	public final void testIntPoint2DComparator()
	{
		IntPoint2D p1 = new IntPoint2D(10, 10);
		assertTrue(p1.compareTo(new IntPoint2D(11, 10)) < 0);
		assertTrue(p1.compareTo(new IntPoint2D(10, 11)) < 0);
		assertTrue(p1.compareTo(new IntPoint2D(10, 10)) == 0);
		assertTrue(p1.compareTo(new IntPoint2D(9, 10)) > 0);
		assertTrue(p1.compareTo(new IntPoint2D(10, 9)) > 0);
	}

}
