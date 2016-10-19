/**
 * 
 */
package net.sci.array.data;

import static org.junit.Assert.*;
import net.sci.array.Array;
import net.sci.array.data.scalarnd.UInt8ArrayND;
import net.sci.array.type.UInt8;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class Array2DTest
{

	/**
	 * Test method for {@link net.sci.array.data.Array2D#createView(net.sci.array.Array)}.
	 */
	@Test
	public final void testCreateView()
	{
		Array<UInt8> array = UInt8ArrayND.create(new int[]{5, 4});
		for (int y = 0; y < 4; y++)
		{
			for (int x = 0; x < 5; x++)
			{
				int[] pos = new int[]{x, y};
				array.set(pos, new UInt8(y * 10 + x));
			}
		}
		
		Array2D<UInt8> res = Array2D.createView(array);
		assertEquals(5, res.getSize(0));
		assertEquals(4, res.getSize(1));
		
		assertEquals(34, res.getValue(4, 3), .1);
	}

	/**
	 * Test method for {@link net.sci.array.data.Array2D#createView(net.sci.array.Array)}.
	 */
	@Test
	public final void testCreateView_3d()
	{
		Array<UInt8> array = UInt8ArrayND.create(new int[]{5, 4, 3});
		for (int z = 0; z < 3; z++)
		{
			for (int y = 0; y < 4; y++)
			{
				for (int x = 0; x < 5; x++)
				{
					int[] pos = new int[]{x, y};
					array.set(pos, new UInt8(y * 10 + x));
				}
			}
		}
		
		Array2D<UInt8> res = Array2D.createView(array);
		assertEquals(5, res.getSize(0));
		assertEquals(4, res.getSize(1));
		
		assertEquals(34, res.getValue(4, 3), .1);
	}

}
