/**
 * 
 */
package net.sci.array.shape;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.Array;
import net.sci.array.Array3D;
import net.sci.array.numeric.Float32VectorArray;
import net.sci.array.numeric.Float32VectorArray3D;
import net.sci.array.numeric.UInt8;
import net.sci.array.numeric.UInt8Array3D;

/**
 * @author dlegland
 *
 */
public class SliceTest
{
    /**
     * Test method for {@link net.sci.array.shape.Slice#process(net.sci.array.Array)}.
     */
    @Test
    public final void testProcess_UInt8Array3D()
    {
        UInt8Array3D array = createUInt8Array3D();
        Slice op = new Slice(new int[]{0, 1}, new int[]{1, 1, 1});
        
        Array<?> result = op.process(array);
        assertEquals(2, result.dimensionality());
        assertEquals(array.size(0), result.size(0));
        assertEquals(array.size(1), result.size(1));

        assertEquals(array.get(3, 2, 1), result.get(new int[]{3, 2}));
    }
    
    /**
     * Test method for {@link net.sci.array.shape.Slice#createView(net.sci.array.Array)}.
     */
    @Test
    public final void test_createView_UInt8Array3D()
    {
        UInt8Array3D array = createUInt8Array3D();
        Slice op = new Slice(new int[]{0, 1}, new int[]{1, 1, 1});
        
        Array<UInt8> res = op.createView(array);
        assertEquals(2, res.dimensionality());
        assertEquals(array.size(0), res.size(0));
        assertEquals(array.size(1), res.size(1));

        assertEquals(array.get(3, 2, 1), res.get(new int[]{3, 2}));

        // modify the view, and check that original array is modified
        assertEquals(array.get(3, 2, 1), res.get(new int[]{3, 2}));
        res.set(new int[] {3, 2}, new UInt8(100));
        assertEquals(100, array.getInt(3, 2, 1));
    }
    
    /**
     * Test method for {@link net.sci.array.shape.Slice#createView(net.sci.array.Array)}.
     */
    @Test
    public final void test_createView_UInt8Array3D_zy()
    {
        UInt8Array3D array = createUInt8Array3D();
        Slice op = new Slice(new int[]{2, 1}, new int[]{1, 1, 1});
        
        Array<UInt8> res = op.createView(array);
        assertEquals(2, res.dimensionality());
        assertEquals(array.size(2), res.size(0));
        assertEquals(array.size(1), res.size(1));

        assertEquals(array.get(1, 3, 2), res.get(new int[]{2, 3}));

        // modify the view, and check that original array is modified
        assertEquals(array.get(1, 3, 2), res.get(new int[]{2, 3}));
        res.set(new int[] {2, 3}, new UInt8(100));
        assertEquals(100, array.getInt(1, 3, 2));
    }
    
    /**
     * Test method for {@link net.sci.array.shape.Slice#process(net.sci.array.Array)}.
     */
    @Test
    public final void testProcess_VectorArray3D()
    {
        Float32VectorArray3D array = createVectorArray3D();
        Slice op = new Slice(new int[]{0, 1}, new int[]{1, 1, 1});
        
        Float32VectorArray result = (Float32VectorArray) op.process(array);
        assertEquals(2, result.dimensionality());
        assertEquals(array.size(0), result.size(0));
        assertEquals(array.size(1), result.size(1));

        assertEquals(array.getValues(3, 2, 1)[0], result.getValues(new int[]{3, 2})[0], 0.01);
    }

    /**
     * Test method for {@link net.sci.array.shape.Slice#process(net.sci.array.Array)}.
     */
    @Test
    public final void testProcess_StringArray3D()
    {
        Array3D<String> array = createStringArray3D();
        Slice op = new Slice(new int[]{0, 1}, new int[]{1, 1, 1});
        
        Array<?> result = op.process(array);
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
//        Slice Slice = new Slice(2, 1);
//        Array<?> view = Slice.createView(array);
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
        array.fillValues((x, y, z) -> x + y * 10.0 + z * 100);
		return array;
	}

    //    @Test
//    public final void testCreateView_StringArray3D()
//    {
//        Array3D<String> array = createStringArray3D();
//
//        Slice Slice = new Slice(2, 1);
//        Array<?> view = Slice.createView(array);
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
                    array.set(x, y, z, str);
                }
            }
        }
        return array;
    }
}
