/**
 * 
 */
package net.sci.array.process.shape;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import net.sci.array.Array;
import net.sci.array.Array3D;
import net.sci.array.scalar.UInt8Array;
import net.sci.array.scalar.UInt8Array2D;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class CropTest
{
    /**
     * Test method for {@link net.sci.array.process.shape.Crop#process(net.sci.array.Array)}.
     */
    @Test
    public final void testProcessArrayOfT()
    {
        UInt8Array2D array = createUInt8Array2D();
        
        Crop crop = Crop.fromMinMax(new int[]{2, 3}, new int[]{8,7});
        Array<?> res = crop.process(array);
        
        assertTrue(res instanceof UInt8Array);
        assertEquals(6, res.size(0));
        assertEquals(4, res.size(1));
        UInt8Array2D res2d = UInt8Array2D.wrap((UInt8Array) res);
        assertEquals(32, res2d.getValue(0, 0), .1);
    }

    /**
     * Test method for {@link net.sci.array.process.shape.Crop#process(net.sci.array.Array, net.sci.array.Array)}.
     */
    @Test
    public final void testProcessArrayArray()
    {
        Crop crop = Crop.fromMinMax(new int[]{2, 3}, new int[]{8,7});
        
        UInt8Array2D array = createUInt8Array2D();
        UInt8Array2D output = UInt8Array2D.create(6, 4);

        crop.process(array, output);

        assertEquals(32, output.getValue(0, 0), .1);
        assertEquals(67, output.getValue(5, 3), .1);
    }


    @Test
    public final void testCreateView_StringArray3D()
    {
        Array3D<String> array = createStringArray3D();

        Crop crop = Crop.fromMinMax(new int[] {1, 1, 1}, new int[] {4, 3, 2});
        Array<?> view = crop.createView(array);
        
        assertEquals(3, view.dimensionality());
        assertEquals(3, view.size(0));
        assertEquals(2, view.size(1));
        assertEquals(1, view.size(2));

        assertEquals(array.get(1, 1, 1), view.get(new int[]{0, 0, 0}));
        
        // modifies value in array and check equality
        array.set("Hello!", 1, 1, 1);
        assertEquals(array.get(1, 1, 1), view.get(new int[]{0, 0, 0}));
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


    private UInt8Array2D createUInt8Array2D()
    {
        UInt8Array2D array = UInt8Array2D.create(10, 10);

        for (int y = 0; y < 10; y++)
        {
            for (int x = 0; x < 10; x++)
            {
                array.setValue(y * 10 + x, x, y);
            }
        }
        return array;
    }
}
