package net.sci.image.filtering;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sci.array.Array;
import net.sci.array.Array2D;
import net.sci.array.color.RGB8;
import net.sci.array.color.RGB8Array2D;
import net.sci.array.impl.GenericArray2D;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.UInt8Array2D;
import net.sci.array.numeric.UInt8Array3D;
import net.sci.geom.geom2d.Vector2D;

public class BoxFilterTest
{

	@Test
	public void testProcessScalar2D()
	{
		UInt8Array2D array = UInt8Array2D.create(8, 7);
		for (int y = 2; y < 6; y++)
		{
			for (int x = 2; x < 7; x++)
			{
				array.setInt(x, y, 10);
			}
		}
		
		int[] diameters = new int[] {3, 3};
		BoxFilter filter = new BoxFilter(diameters);
		
		ScalarArray<?> result = (ScalarArray<?>) filter.process(array);
		
		assertTrue(result instanceof ScalarArray);
		assertEquals(2, result.dimensionality());
		
		assertEquals(10, result.getValue(new int[] {3, 3}), .01);
	}

	@Test
	public void testProcessScalar3D()
	{
		UInt8Array3D array = UInt8Array3D.create(6, 5, 4);
		array.fillInt(10);
		
		int[] diameterList = new int[]{5, 5, 3};
		BoxFilter filter = new BoxFilter(diameterList);
		
		Array<?> result = filter.process(array);
		
		assertTrue(result instanceof ScalarArray);
		assertEquals(3, result.dimensionality());
	}

	@Test
	public void testProcessColor2D()
	{
		RGB8Array2D array = RGB8Array2D.create(6, 5);
		for (int y = 0; y < 5; y++)
		{
			for (int x = 0; x < 6; x++)
			{
				array.set(x, y, new RGB8(x * 5, y * 5, 0));
			}
		}
		
		int[] diameterList = new int[]{3, 3};
		BoxFilter filter = new BoxFilter(diameterList);
		
		Array<?> result = filter.process(array);
		
		assertTrue(result instanceof RGB8Array2D);
		assertEquals(2, result.dimensionality());
	}

    @Test
    public void testProcessNumeric2D()
    {
        GenericArray2D<Vector2D> array = GenericArray2D.create(8, 6, new Vector2D());
        array.set(4, 3, new Vector2D(18, -9));
        
        int[] diameters = new int[] {3, 3};
        BoxFilter filter = new BoxFilter(diameters);
        
        Array2D<Vector2D> result = Array2D.wrap(filter.processNumeric(array));
        
        assertEquals(2, result.dimensionality());
        Vector2D v_4_3 = result.get(4, 3);
        assertTrue(new Vector2D(2, -1).almostEquals(v_4_3, 0.01));
    }
}
