/**
 * 
 */
package net.sci.array.scalar;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class UInt8Array3DTest
{

    /**
     * Test method for {@link net.sci.array.scalar.UInt8Array3D#slice(int)}.
     */
    @Test
    public final void testSlice()
    {
        UInt8Array3D array = UInt8Array3D.create(5, 4, 3);
        for (int z = 0; z < 3; z++)
        {
            for (int y = 0; y < 4; y++)
            {
                for (int x = 0; x < 5; x++)
                {
                    array.setValue(x, y, z, z*100 + y*10 + x);
                }
            }
        }
        assertEquals(array.getValue(3, 2, 1), 123, .01);

        UInt8Array2D slice = array.slice(1);
        assertEquals(slice.getValue(3, 2), 123, .01);

        slice.setValue(3, 2, 200);
        assertEquals(array.getValue(3, 2, 1), 200, .01);
    }

    /**
     * Test method for {@link net.sci.array.scalar.UInt8Array3D#slice(int)}.
     */
    @Test
    public final void testSlice_Iterator()
    {
        UInt8Array3D array = UInt8Array3D.create(5, 4, 3);
        array.fillValue(10);

        UInt8Array2D slice = array.slice(1);

        UInt8Array.Iterator iter = slice.iterator();
        int n = 0; 
        double sum = 0;
        while (iter.hasNext())
        {
            sum += iter.nextValue();
            n++;
        }

        assertEquals(20, n);
        assertEquals(10.0*20, sum, .01);
    }

    /**
     * Test method for {@link net.sci.array.scalar.UInt8Array3D#sliceIterator()}.
     */
    @Test
    public final void test_SliceIterator()
    {
        UInt8Array3D array = UInt8Array3D.create(5, 4, 3);

        int n = 0;
        for(UInt8Array2D slice : array.slices())
        {
            assertEquals(5, slice.getSize(0));
            assertEquals(4, slice.getSize(1));
            n++;
        }

        assertEquals(3, n);
    }
}
