package net.sci.image.process.filter;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.data.scalar2d.UInt8Array2D;

public class BoxMinMaxFilterNaiveTest
{

	@Test
	public void testDilateDotRadius3()
	{
		UInt8Array2D image = UInt8Array2D.create(10, 10);
		image.setInt(4, 4, 200);
	
		BoxMinMaxFilterNaive dil = new BoxMinMaxFilterNaive(BoxMinMaxFilterNaive.Type.MAX, new int[]{7, 7});
		UInt8Array2D result = (UInt8Array2D) dil.process(image);
		
//		result.print(System.out);
		// Expected:
		//   0   0   0   0   0   0   0   0   0   0
		//   0 200 200 200 200 200 200 200   0   0
		//   0 200 200 200 200 200 200 200   0   0
		//   0 200 200 200 200 200 200 200   0   0
		//   0 200 200 200   X 200 200 200   0   0
		//   0 200 200 200 200 200 200 200   0   0
		//   0 200 200 200 200 200 200 200   0   0
		//   0 200 200 200 200 200 200 200   0   0
		//   0   0   0   0   0   0   0   0   0   0
		//   0   0   0   0   0   0   0   0   0   0

		for (int y = 0; y < 1; y++)
		{
			for (int x = 0; x < 10; x++)
			{
				assertEquals(0, result.getValue(x, y), .01);
			}
		}
		
		for (int y = 1; y < 8; y++)
		{
			assertEquals(0, result.getValue(0, y), .01);
			assertEquals(200, result.getValue(1, y), .01);
			assertEquals(200, result.getValue(7, y), .01);
			assertEquals(0, result.getValue(8, y), .01);
		}
		
		for (int y = 8; y < 10; y++)
		{
			for (int x = 0; x < 10; x++)
			{
				assertEquals(0, result.getValue(x, y), .01);
			}
		}
	}

	@Test
	public void testErodeDotRadius3()
	{
		UInt8Array2D image = UInt8Array2D.create(10, 10);
		image.fill(200);
		image.setInt(4, 4, 50);
//		System.out.println("Input image:");
//		image.print(System.out);

		BoxMinMaxFilterNaive op = new BoxMinMaxFilterNaive(BoxMinMaxFilterNaive.Type.MIN, new int[]{7, 7});
		UInt8Array2D result = (UInt8Array2D) op.process(image);
		
		// Expected:
		// 200 200 200 200 200 200 200 200 200 200
		// 200  50  50  50  50  50  50  50 200 200
		// 200  50  50  50  50  50  50  50 200 200
		// 200  50  50  50  50  50  50  50 200 200
		// 200  50  50  50  50  50  50  50 200 200
		// 200  50  50  50  50  50  50  50 200 200
		// 200  50  50  50  50  50  50  50 200 200
		// 200  50  50  50  50  50  50  50 200 200
		// 200 200 200 200 200 200 200 200 200 200
		// 200 200 200 200 200 200 200 200 200 200
//		System.out.println("Result:");
//		result.print(System.out);

		for (int y = 0; y < 1; y++)
		{
			for (int x = 0; x < 10; x++)
			{
				assertEquals(200, result.getValue(x, y), .01);
			}
		}
		
		for (int y = 1; y < 8; y++)
		{
			assertEquals(200, result.getValue(0, y), .01);
			assertEquals( 50, result.getValue(1, y), .01);
			assertEquals( 50, result.getValue(7, y), .01);
			assertEquals(200, result.getValue(8, y), .01);
		}
		
		for (int y = 8; y < 10; y++)
		{
			for (int x = 0; x < 10; x++)
			{
				assertEquals(200, result.getValue(x, y), .01);
			}
		}
	}

//	/**
//	 * Creates a 10-by-10 image with a 4-by-4 square in the middle.
//	 */
//	private UInt8Array2D createImage_Square4x4 () 
//	{
//		UInt8Array2D image = UInt8Array2D.create(10, 10);
//		image.fill(0);
//		
//		for (int y = 3; y < 7; y++)
//		{
//			for (int x = 3; x < 7; x++)
//			{
//				image.setValue(x, y, 255);
//			}
//		}
//		
//		return image;
//	}
}
