package net.sci.image.morphology.filtering;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.numeric.UInt8Array2D;

public class BoxDilationNaiveTest
{

	@Test
	public void testDilateDotRadius3()
	{
		UInt8Array2D image = UInt8Array2D.create(10, 10);
		image.setInt(4, 4, 200);
	
		BoxDilationNaive dil = new BoxDilationNaive(new int[]{7, 7});
		UInt8Array2D result = (UInt8Array2D) dil.process(image);
		
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
//		result.print(System.out);

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
}
