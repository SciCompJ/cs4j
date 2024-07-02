/**
 * 
 */
package net.sci.array.binary;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.Array2D;
import net.sci.array.impl.GenericArray2D;
import net.sci.array.numeric.Float32Array2D;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.UInt8;
import net.sci.array.numeric.UInt8Array2D;

/**
 * @author dlegland
 *
 */
public class BinaryArrayTest
{

    /**
     * Test method for {@link net.sci.array.binary.BinaryArray#convert(net.sci.array.scalar.ScalarArray)}.
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
        assertFalse(binaryArray.getBoolean(new int[] {0, 0}));
        assertTrue( binaryArray.getBoolean(new int[] {7, 0}));
        assertTrue( binaryArray.getBoolean(new int[] {0, 5}));
        assertFalse(binaryArray.getBoolean(new int[] {7, 5}));
    }

    /**
     * Test method for {@link net.sci.array.binary.BinaryArray#wrap(net.sci.array.Array)}.
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
        assertFalse(binaryArray.getBoolean(new int[] {0, 0}));
        assertTrue( binaryArray.getBoolean(new int[] {7, 0}));
        assertTrue( binaryArray.getBoolean(new int[] {0, 5}));
        assertFalse(binaryArray.getBoolean(new int[] {7, 5}));
        
        // changing the view should change original array
        binaryArray.setBoolean(new int[] {2, 2}, true);
        assertTrue(array.get(4, 3).getBoolean());
    }
    
    
    /**
     * Test method for {@link net.sci.array.binary.BinaryArray#selectElements(Array)}.
     */
    @Test
    public final void testSelect()
    {
        UInt8Array2D array = UInt8Array2D.create(6,  4);
        array.fillInts((x,y) -> y * 10 + x);
        BinaryArray2D mask = BinaryArray2D.create(6,  4);
        mask.fillBooleans((x,y) -> x >= 1 && x <= 4 && y >= 1 && y <= 2);
        
        double acc = 0;
        for (UInt8 val : mask.selectElements(array))
        {
            acc += val.getValue();
        }
        
        assertEquals(30*4+10*2, acc, 0.01);
    }


    /**
     * Test method for {@link net.sci.array.binary.BinaryArray#reshapeView(int[], java.util.function.Function)}.
     */
    @Test
    public final void testView()
    {
        BinaryArray2D array = BinaryArray2D.create(8, 6);
        array.fillBooleans((x,y) -> x >= 4 ^ y >= 3);
        
        // create a view corresponding to the transpose of the array
        BinaryArray view = array.reshapeView(new int[] {6, 8}, pos -> new int[] {pos[1], pos[0]});
        
        assertEquals(2, view.dimensionality());
        assertEquals(6, view.size(0));
        assertEquals(8, view.size(1));
        
        assertFalse(view.getBoolean(new int[] {2, 2}));
        assertFalse(view.getBoolean(new int[] {4, 6}));
        assertTrue(view.getBoolean(new int[] {2, 6}));
        assertTrue(view.getBoolean(new int[] {4, 2}));
        
        view.setBoolean(new int[] {2, 3}, true);
        assertTrue(array.getBoolean(3, 2));
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
        
        assertFalse(array.getBoolean(new int[] {5, 2}));
        assertTrue(array.getBoolean(new int[] {15, 2}));
        assertTrue(array.getBoolean(new int[] {5, 7}));
        assertFalse(array.getBoolean(new int[] {15, 7}));
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
