/**
 * 
 */
package net.sci.array.process.shape;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import net.sci.array.Array;
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
        
        Crop crop = new Crop(new int[]{2, 3}, new int[]{8,7});
        Array<?> res = crop.process(array);
        
        assertTrue(res instanceof UInt8Array);
        assertEquals(6, res.getSize(0));
        assertEquals(4, res.getSize(1));
        UInt8Array2D res2d = UInt8Array2D.wrap((UInt8Array) res);
        assertEquals(32, res2d.getValue(0, 0), .1);
    }

    /**
     * Test method for {@link net.sci.array.process.shape.Crop#process(net.sci.array.Array, net.sci.array.Array)}.
     */
    @Test
    public final void testProcessArrayArray()
    {
        Crop crop = new Crop(new int[]{2, 3}, new int[]{8,7});
        
        UInt8Array2D array = createUInt8Array2D();
        UInt8Array2D output = UInt8Array2D.create(6, 4);

        crop.process(array, output);

        assertEquals(32, output.getValue(0, 0), .1);
        assertEquals(67, output.getValue(5, 3), .1);
    }

    private UInt8Array2D createUInt8Array2D()
    {
        UInt8Array2D array = UInt8Array2D.create(10, 10);

        for (int y = 0; y < 10; y++)
        {
            for (int x = 0; x < 10; x++)
            {
                array.setValue(x, y, y * 10 + x);
            }
        }
        return array;
    }
}
