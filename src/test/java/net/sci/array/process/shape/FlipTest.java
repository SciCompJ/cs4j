/**
 * 
 */
package net.sci.array.process.shape;

import static org.junit.Assert.*;
import net.sci.array.Array;
import net.sci.array.data.scalar2d.BufferedUInt8Array2D;
import net.sci.array.data.scalar2d.UInt8Array2D;
import net.sci.array.process.shape.Flip;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class FlipTest
{

	/**
	 * Test method for {@link net.sci.array.process.shape.Flip#process(net.sci.array.Array, net.sci.array.Array)}.
	 */
	@Test
	public final void testProcess2d_X()
	{
		int sizeX = 6;
		int sizeY = 4;
		UInt8Array2D array = new BufferedUInt8Array2D(sizeX, sizeY);
		for (int y = 0; y < sizeY; y++)
		{
			for (int x = 0; x < sizeX; x++)
			{
				array.setInt(x, y, x + y * 10);
			}
		}
		
		Flip flipX = new Flip(0);
		Array<?> resFlip= flipX.process(array);
		
		assertEquals(2, resFlip.dimensionality());
		assertEquals(sizeX, resFlip.getSize(0));
		assertEquals(sizeY, resFlip.getSize(1));
		
		assertEquals(35, resFlip.getValue(new int[]{0, 3}), .1);
	}

	/**
	 * Test method for {@link net.sci.array.process.shape.Flip#process(net.sci.array.Array, net.sci.array.Array)}.
	 */
	@Test
	public final void testProcess2d_X_ND()
	{
		int sizeX = 6;
		int sizeY = 4;
		UInt8Array2D array = new BufferedUInt8Array2D(sizeX, sizeY);
		for (int y = 0; y < sizeY; y++)
		{
			for (int x = 0; x < sizeX; x++)
			{
				array.setInt(x, y, x + y * 10);
			}
		}
		
		Flip flipX = new Flip(0);
		Array<?> resFlip = array.duplicate();
		flipX.processDoubleNd(array, resFlip);
		
		assertEquals(2, resFlip.dimensionality());
		assertEquals(sizeX, resFlip.getSize(0));
		assertEquals(sizeY, resFlip.getSize(1));
		
		assertEquals(35, resFlip.getValue(new int[]{0, 3}), .1);
	}

	/**
	 * Test method for {@link net.sci.array.process.shape.Flip#process(net.sci.array.Array, net.sci.array.Array)}.
	 */
	@Test
	public final void testProcess2d_Y()
	{
		int sizeX = 6;
		int sizeY = 4;
		UInt8Array2D array = new BufferedUInt8Array2D(sizeX, sizeY);
		for (int y = 0; y < sizeY; y++)
		{
			for (int x = 0; x < sizeX; x++)
			{
				array.setInt(x, y, x + y * 10);
			}
		}
		
		Flip flipY = new Flip(1);
		Array<?> resFlip = flipY.process(array);
		
		assertEquals(2, resFlip.dimensionality());
		assertEquals(sizeX, resFlip.getSize(0));
		assertEquals(sizeY, resFlip.getSize(1));
		
		assertEquals(35, resFlip.getValue(new int[]{5, 0}), .1);
	}
	
	/**
	 * Test method for {@link net.sci.array.process.shape.Flip#process(net.sci.array.Array, net.sci.array.Array)}.
	 */
	@Test
	public final void testProcess2d_Y_ND()
	{
		int sizeX = 6;
		int sizeY = 4;
		UInt8Array2D array = new BufferedUInt8Array2D(sizeX, sizeY);
		for (int y = 0; y < sizeY; y++)
		{
			for (int x = 0; x < sizeX; x++)
			{
				array.setInt(x, y, x + y * 10);
			}
		}
		
		Flip flipY = new Flip(1);
		Array<?> resFlip = array.duplicate();
		flipY.processDoubleNd(array, resFlip);
		
		assertEquals(2, resFlip.dimensionality());
		assertEquals(sizeX, resFlip.getSize(0));
		assertEquals(sizeY, resFlip.getSize(1));
		
		assertEquals(35, resFlip.getValue(new int[]{5, 0}), .1);
	}
}
