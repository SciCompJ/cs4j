/**
 * 
 */
package net.sci.array.binary;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.Array2D;
import net.sci.array.generic.GenericArray2D;
import net.sci.array.scalar.Float32Array2D;
import net.sci.array.scalar.ScalarArray2D;

/**
 * @author dlegland
 *
 */
public class BinaryArrayTest
{

    /**
     * Test method for {@link net.sci.array.scalar.BinaryArray#convert(net.sci.array.scalar.ScalarArray)}.
     */
    @Test
    public final void testConvert()
    {
        ScalarArray2D<?> array = Float32Array2D.create(8, 6);
        array.fillValues((x,y) -> y >= 4 ^ x >= 3 ? 10.0 : 0.0);
        
        BinaryArray binaryArray = BinaryArray.convert(array);
        
        // array size should not change
        assertEquals(2, binaryArray.dimensionality());
        assertEquals(8, binaryArray.size(0));
        assertEquals(6, binaryArray.size(1));
        
        // array content should be the same 
        assertFalse(binaryArray.getBoolean(0, 0));
        assertTrue( binaryArray.getBoolean(7, 0));
        assertTrue( binaryArray.getBoolean(0, 5));
        assertFalse(binaryArray.getBoolean(7, 5));
    }

    /**
     * Test method for {@link net.sci.array.scalar.BinaryArray#wrap(net.sci.array.Array)}.
     */
    @Test
    public final void testWrap()
    {
        Array2D<Binary> array = GenericArray2D.create(8, 6, new Binary(0));
        array.fill((x,y) -> y >= 4 ^ x >= 3 ? Binary.TRUE : Binary.FALSE);
        
        BinaryArray binaryArray = BinaryArray.wrap(array);
        
        // array size should not change
        assertEquals(2, binaryArray.dimensionality());
        assertEquals(8, binaryArray.size(0));
        assertEquals(6, binaryArray.size(1));
        
        // array content should be the same
        assertFalse(binaryArray.getBoolean(0, 0));
        assertTrue( binaryArray.getBoolean(7, 0));
        assertTrue( binaryArray.getBoolean(0, 5));
        assertFalse(binaryArray.getBoolean(7, 5));
        
        // changing the view should change original array
        binaryArray.setBoolean(new int[] {2, 2}, true);
        assertTrue(array.get(4, 3).getBoolean());
    }

    /**
     * Test method for {@link net.sci.array.binary.BinaryArray#fillBooleans(java.util.function.Function)}.
     */
    @Test
    public final void testFillBooleans()
    {
        int[] dims = new int[] {20, 10};
        BinaryArray array = BinaryArray.create(dims);
        
        array.fillBooleans(pos -> pos[0] >= 10 ^ pos[1] >= 5);
        
        assertFalse(array.getBoolean(5, 2));
        assertTrue(array.getBoolean(15, 2));
        assertTrue(array.getBoolean(5, 7));
        assertFalse(array.getBoolean(15, 7));
    }

    /**
     * Test method for {@link net.sci.array.binary.BinaryArray#fill(boolean)}.
     */
    @Test
    public final void testFillBoolean_2d()
    {
        int[] dims = new int[] {5, 4};
        BinaryArray array = BinaryArray.create(dims);
        
        array.fill(true);
        
        int count = 0;
        for (int[] pos : array.positions())
        {
            if (array.getBoolean(pos))
            {
                count++;
            }
        }
        assertEquals(20, count);
    }

    /**
     * Test method for {@link net.sci.array.binary.BinaryArray#fill(boolean)}.
     */
    @Test
    public final void testFillBoolean_3d()
    {
        int[] dims = new int[] {5, 4, 3};
        BinaryArray array = BinaryArray.create(dims);
        
        array.fill(true);
        
        int count = 0;
        for (int[] pos : array.positions())
        {
            if (array.getBoolean(pos))
            {
                count++;
            }
        }
        assertEquals(60, count);
    }

}
