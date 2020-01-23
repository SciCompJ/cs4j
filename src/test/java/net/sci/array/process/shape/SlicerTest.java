/**
 * 
 */
package net.sci.array.process.shape;

import static org.junit.Assert.assertEquals;
import net.sci.array.Array;
import net.sci.array.Array3D;
import net.sci.array.scalar.UInt8Array3D;
import net.sci.array.vector.Float32VectorArray;
import net.sci.array.vector.Float32VectorArray3D;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class SlicerTest
{
	/**
	 * Test method for {@link net.sci.array.process.shape.Slicer#process(net.sci.array.Array)}.
	 */
	@Test
	public final void testProcess_UInt8Array3D()
	{
		UInt8Array3D array = createUInt8Array3D();
		Slicer slicer = new Slicer(new int[]{0, 1}, new int[]{1, 1, 1});
		
		Array<?> result = slicer.process(array);
		assertEquals(2, result.dimensionality());
		assertEquals(array.size(0), result.size(0));
		assertEquals(array.size(1), result.size(1));

		assertEquals(array.get(3, 2, 1), result.get(new int[]{3, 2}));
	}
	
    /**
     * Test method for {@link net.sci.array.process.shape.Slicer#process(net.sci.array.Array)}.
     */
    @Test
    public final void testProcess_VectorArray3D()
    {
        Float32VectorArray3D array = createVectorArray3D();
        Slicer slicer = new Slicer(new int[]{0, 1}, new int[]{1, 1, 1});
        
        Float32VectorArray result = (Float32VectorArray) slicer.process(array);
        assertEquals(2, result.dimensionality());
        assertEquals(array.size(0), result.size(0));
        assertEquals(array.size(1), result.size(1));

        assertEquals(array.getValues(3, 2, 1)[0], result.getValues(new int[]{3, 2})[0], 0.01);
    }

    /**
     * Test method for {@link net.sci.array.process.shape.Slicer#process(net.sci.array.Array)}.
     */
    @Test
    public final void testProcess_StringArray3D()
    {
        Array3D<String> array = createStringArray3D();
        Slicer slicer = new Slicer(new int[]{0, 1}, new int[]{1, 1, 1});
        
        Array<?> result = slicer.process(array);
        assertEquals(2, result.dimensionality());
        assertEquals(array.size(0), result.size(0));
        assertEquals(array.size(1), result.size(1));

        assertEquals(array.get(3, 2, 1), result.get(new int[]{3, 2}));
    }
//    @Test
//    public final void testCreateView_UInt8Array3D()
//    {
//        UInt8Array3D array = createUInt8Array3D();
//
//        Slicer slicer = new Slicer(2, 1);
//        Array<?> view = slicer.createView(array);
//        
//        assertEquals(2, view.dimensionality());
//        assertEquals(array.size(0), view.size(0));
//        assertEquals(array.size(1), view.size(1));
//
//        assertEquals(array.get(3, 2, 1), view.get(new int[]{3, 2}));
//        
//        // modifies value in array and check equality
//        array.setValue(3, 2, 1, 25.0);
//        assertEquals(array.get(3, 2, 1), view.get(new int[]{3, 2}));
//    }

	private UInt8Array3D createUInt8Array3D()
	{
        UInt8Array3D array = UInt8Array3D.create(5, 4, 3);
        array.populateValues((x, y, z) -> x + y * 10 + z * 100);
		return array;
	}

    //    @Test
//    public final void testCreateView_StringArray3D()
//    {
//        Array3D<String> array = createStringArray3D();
//
//        Slicer slicer = new Slicer(2, 1);
//        Array<?> view = slicer.createView(array);
//        
//        assertEquals(2, view.dimensionality());
//        assertEquals(array.size(0), view.size(0));
//        assertEquals(array.size(1), view.size(1));
//
//        assertEquals(array.get(3, 2, 1), view.get(new int[]{3, 2}));
//        
//        // modifies value in array and check equality
//        array.set(3, 2, 1, "Hello!");
//        assertEquals(array.get(3, 2, 1), view.get(new int[]{3, 2}));
//    }

	private Float32VectorArray3D createVectorArray3D()
    {
	    Float32VectorArray3D array = Float32VectorArray3D.create(5, 4, 3, 3);
        for (int z = 0; z < 3; z++)
        {
            for (int y = 0; y < 4; y++)
            {
                for (int x = 0; x < 5; x++)
                {
                    array.setValues(x, y, z, new double[]{x, y, z});
                }
            }
        }
        return array;
    }

    private Array3D<String> createStringArray3D()
    {
        String[] digits = new String[]{"a", "b", "c", "d", "e"};
        Array3D<String> array = Array3D.create(5, 4, 3, "");
        for (int z = 0; z < 3; z++)
        {
            for (int y = 0; y < 4; y++)
            {
                for (int x = 0; x < 5; x++)
                {
                    String str = digits[z] + digits[y] + digits[x];
                    array.set(str, x, y, z);
                }
            }
        }
        return array;
    }
}
