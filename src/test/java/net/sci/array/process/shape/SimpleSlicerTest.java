/**
 * 
 */
package net.sci.array.process.shape;

import static org.junit.Assert.assertEquals;
import net.sci.array.Array;
import net.sci.array.Array3D;
import net.sci.array.scalar.UInt8;
import net.sci.array.scalar.UInt8Array2D;
import net.sci.array.scalar.UInt8Array3D;
import net.sci.array.vector.Float32Vector;
import net.sci.array.vector.Float32VectorArray3D;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class SimpleSlicerTest
{
	/**
	 * Test static method in slicer.
	 * 
	 * Test method for {@link net.sci.array.process.shape.SimpleSlicer#getSlice(net.sci.array.Array, net.sci.array.Array)}.
	 */
	@Test
	public final void testGetSlice()
	{
		UInt8Array3D array = createUInt8Array3D();
		UInt8Array2D result = UInt8Array2D.create(5, 4);
		
		SimpleSlicer.getSlice(array, result, 2, 1);

		assertEquals(array.getValue(3, 2, 1), result.getValue(new int[]{3, 2}), .1);
	}

    @Test
    public final void testSlice2d()
    {
        UInt8Array3D array = createUInt8Array3D();
        int[] refPos = new int[]{1, 1, 1};

        Array<UInt8> resXY = SimpleSlicer.slice2d(array, 0, 1, refPos);
        assertEquals(2, resXY.dimensionality());
        assertEquals(5, resXY.size(0));
        assertEquals(4, resXY.size(1));

        Array<UInt8> resYZ = SimpleSlicer.slice2d(array, 1, 2, refPos);
        assertEquals(2, resYZ.dimensionality());
        assertEquals(4, resYZ.size(0));
        assertEquals(3, resYZ.size(1));

        Array<UInt8> resXZ = SimpleSlicer.slice2d(array, 0, 2, refPos);
        assertEquals(2, resXZ.dimensionality());
        assertEquals(5, resXZ.size(0));
        assertEquals(3, resXZ.size(1));
    }

    @Test
    public final void testSlice2d_String()
    {
        Array3D<String> array = createStringArray3D();
        int[] refPos = new int[]{1, 1, 1};
        
//        Array<String> tmp = array.newInstance(new int[]{5, 4});

        Array<String> resXY = SimpleSlicer.slice2d(array, 0, 1, refPos);
        assertEquals(2, resXY.dimensionality());
        assertEquals(5, resXY.size(0));
        assertEquals(4, resXY.size(1));

        Array<String> resYZ = SimpleSlicer.slice2d(array, 1, 2, refPos);
        assertEquals(2, resYZ.dimensionality());
        assertEquals(4, resYZ.size(0));
        assertEquals(3, resYZ.size(1));

        Array<String> resXZ = SimpleSlicer.slice2d(array, 0, 2, refPos);
        assertEquals(2, resXZ.dimensionality());
        assertEquals(5, resXZ.size(0));
        assertEquals(3, resXZ.size(1));
    }

    @Test
    public final void testSlice2d_VectorArray3D()
    {
        Float32VectorArray3D array = createVectorArray3D();
        int[] refPos = new int[]{1, 1, 1};

        Array<Float32Vector> resXY = SimpleSlicer.slice2d(array, 0, 1, refPos);
        assertEquals(2, resXY.dimensionality());
        assertEquals(5, resXY.size(0));
        assertEquals(4, resXY.size(1));

        Array<Float32Vector> resYZ = SimpleSlicer.slice2d(array, 1, 2, refPos);
        assertEquals(2, resYZ.dimensionality());
        assertEquals(4, resYZ.size(0));
        assertEquals(3, resYZ.size(1));

        Array<Float32Vector> resXZ = SimpleSlicer.slice2d(array, 0, 2, refPos);
        assertEquals(2, resXZ.dimensionality());
        assertEquals(5, resXZ.size(0));
        assertEquals(3, resXZ.size(1));
    }

	/**
	 * Test method for {@link net.sci.array.process.shape.SimpleSlicer#process(net.sci.array.Array, net.sci.array.Array)}.
	 */
	@Test
	public final void testProcess_UInt8Array3D()
	{
		UInt8Array3D array = createUInt8Array3D();
		SimpleSlicer slicer = new SimpleSlicer(2, 1);
		
		Array<?> result = slicer.process(array);
		assertEquals(2, result.dimensionality());
		assertEquals(array.size(0), result.size(0));
		assertEquals(array.size(1), result.size(1));

		assertEquals(array.get(3, 2, 1), result.get(new int[]{3, 2}));
	}

    @Test
    public final void testCreateView_UInt8Array3D()
    {
        UInt8Array3D array = createUInt8Array3D();

        SimpleSlicer slicer = new SimpleSlicer(2, 1);
        Array<?> view = slicer.createView(array);
        
        assertEquals(2, view.dimensionality());
        assertEquals(array.size(0), view.size(0));
        assertEquals(array.size(1), view.size(1));

        assertEquals(array.get(3, 2, 1), view.get(new int[]{3, 2}));
        
        // modifies value in array and check equality
        array.setValue(3, 2, 1, 25.0);
        assertEquals(array.get(3, 2, 1), view.get(new int[]{3, 2}));
    }

	private UInt8Array3D createUInt8Array3D()
	{
		UInt8Array3D array = UInt8Array3D.create(5, 4, 3);
		for (int z = 0; z < 3; z++)
		{
			for (int y = 0; y < 4; y++)
			{
				for (int x = 0; x < 5; x++)
				{
					array.setInt(x, y, z, z * 100 + y * 10 + x);
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

    @Test
    public final void testCreateView_StringArray3D()
    {
        Array3D<String> array = createStringArray3D();

        SimpleSlicer slicer = new SimpleSlicer(2, 1);
        Array<?> view = slicer.createView(array);
        
        assertEquals(2, view.dimensionality());
        assertEquals(array.size(0), view.size(0));
        assertEquals(array.size(1), view.size(1));

        assertEquals(array.get(3, 2, 1), view.get(new int[]{3, 2}));
        
        // modifies value in array and check equality
        array.set(3, 2, 1, "Hello!");
        assertEquals(array.get(3, 2, 1), view.get(new int[]{3, 2}));
    }

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
}
