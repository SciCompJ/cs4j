/**
 * 
 */
package net.sci.array.process.shape;

import static org.junit.Assert.assertEquals;
import net.sci.array.Array;
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
public class SlicerTest
{
	/**
	 * Test static method in slicer.
	 * 
	 * Test method for {@link net.sci.array.process.shape.Slicer#getSlice(net.sci.array.Array, net.sci.array.Array)}.
	 */
	@Test
	public final void testGetSlice()
	{
		UInt8Array3D array = create543Array();
		UInt8Array2D result = UInt8Array2D.create(5, 4);
		
		Slicer.getSlice(array, result, 2, 1);

		assertEquals(array.getValue(3, 2, 1), result.getValue(new int[]{3, 2}), .1);
	}

    @Test
    public final void testSlice2d()
    {
        UInt8Array3D array = create543Array();
        int[] refPos = new int[]{1, 1, 1};

        Array<UInt8> resXY = Slicer.slice2d(array, 0, 1, refPos);
        assertEquals(2, resXY.dimensionality());
        assertEquals(5, resXY.getSize(0));
        assertEquals(4, resXY.getSize(1));

        Array<UInt8> resYZ = Slicer.slice2d(array, 1, 2, refPos);
        assertEquals(2, resYZ.dimensionality());
        assertEquals(4, resYZ.getSize(0));
        assertEquals(3, resYZ.getSize(1));

        Array<UInt8> resXZ = Slicer.slice2d(array, 0, 2, refPos);
        assertEquals(2, resXZ.dimensionality());
        assertEquals(5, resXZ.getSize(0));
        assertEquals(3, resXZ.getSize(1));
    }

    @Test
    public final void testSlice2d_VectorArray3D()
    {
        Float32VectorArray3D array = createVectorArray3D();
        int[] refPos = new int[]{1, 1, 1};

        Array<Float32Vector> resXY = Slicer.slice2d(array, 0, 1, refPos);
        assertEquals(2, resXY.dimensionality());
        assertEquals(5, resXY.getSize(0));
        assertEquals(4, resXY.getSize(1));

        Array<Float32Vector> resYZ = Slicer.slice2d(array, 1, 2, refPos);
        assertEquals(2, resYZ.dimensionality());
        assertEquals(4, resYZ.getSize(0));
        assertEquals(3, resYZ.getSize(1));

        Array<Float32Vector> resXZ = Slicer.slice2d(array, 0, 2, refPos);
        assertEquals(2, resXZ.dimensionality());
        assertEquals(5, resXZ.getSize(0));
        assertEquals(3, resXZ.getSize(1));
    }

	/**
	 * Test method for {@link net.sci.array.process.shape.Slicer#process(net.sci.array.Array, net.sci.array.Array)}.
	 */
	@Test
	public final void testProcess_Array()
	{
		UInt8Array3D array = create543Array();
		Slicer slicer = new Slicer(2, 1);
		
		Array<?> result = slicer.process(array);
		assertEquals(2, result.dimensionality());
		assertEquals(array.getSize(0), result.getSize(0));
		assertEquals(array.getSize(1), result.getSize(1));

		assertEquals(array.get(3, 2, 1), result.get(new int[]{3, 2}));
	}

	private UInt8Array3D create543Array()
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
