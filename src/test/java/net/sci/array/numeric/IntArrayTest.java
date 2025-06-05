/**
 * 
 */
package net.sci.array.numeric;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.Array2D;

/**
 * 
 */
public class IntArrayTest
{
    /**
     * Test method for {@link net.sci.array.numeric.IntArray#wrap(net.sci.array.Array)}.
     */
    @Test
    public final void test_wrap_get_set_UInt16()
    {
        Array2D<UInt16> array = Array2D.create(10, 5, UInt16.ZERO);
        array.fill((x,y) -> new UInt16(x + y*10));
        
        IntArray<UInt16> result = IntArray.wrap(array);
        
        assertEquals( 2, result.dimensionality());
        assertEquals(10, result.size(0));
        assertEquals( 5, result.size(1));
        assertEquals( 0, result.getInt(new int[] {0, 0}));
        assertEquals( 9, result.getInt(new int[] {9, 0}));
        assertEquals(40, result.getInt(new int[] {0, 4}));
        assertEquals(49, result.getInt(new int[] {9, 4}));
        
        // test modification
        result.setInt(new int[] {3, 2}, 500);
        assertEquals(array.get(3, 2).intValue(), 500);
        
    }
    
    /**
     * Test method for {@link net.sci.array.numeric.IntArray#wrap(net.sci.array.Array)}.
     */
    @Test
    public final void test_Wrapper_newInstance_UInt16()
    {
        Array2D<UInt16> array = Array2D.create(10, 5, UInt16.ZERO);
        IntArray<UInt16> wrap = IntArray.wrap(array);
        
        IntArray<UInt16> result = wrap.newInstance(new int[] {5, 4, 3});
        
        assertEquals(3, result.dimensionality());
        assertEquals(5, result.size(0));
        assertEquals(4, result.size(1));
        assertEquals(3, result.size(2));
        
        result.fillInt(500);
        assertEquals(500, result.getInt(new int[] {3, 2, 1}));
    }
    
    /**
     * Test method for {@link net.sci.array.numeric.IntArray#wrap(net.sci.array.Array)}.
     */
    @Test
    public final void test_Wrapper_factory_UInt16()
    {
        Array2D<UInt16> array = Array2D.create(10, 5, UInt16.ZERO);
        IntArray<UInt16> wrap = IntArray.wrap(array);
        
        IntArray<UInt16> result = wrap.newInstance(new int[] {5, 4, 3});
        
        assertEquals(3, result.dimensionality());
        assertEquals(5, result.size(0));
        assertEquals(4, result.size(1));
        assertEquals(3, result.size(2));
        
        result.fillInt(500);
        assertEquals(500, result.getInt(new int[] {3, 2, 1}));
    }
    
    /**
     * Test method for {@link net.sci.array.numeric.IntArray#wrap(net.sci.array.Array)}.
     */
    @Test
    public final void test_Wrapper_iterator_UInt16()
    {
        Array2D<UInt16> array = Array2D.create(6, 4, UInt16.ZERO);
        array.fill(new UInt16(1000));
        IntArray<UInt16> wrap = IntArray.wrap(array);
        
        int count = 0;
        int sum = 0;
        IntArray.Iterator<UInt16> iter = wrap.iterator();
        while(iter.hasNext())
        {
            sum += iter.nextInt();
            count++;
        }
        
        assertEquals(count, 24);
        assertEquals(sum, 24_000);
    }
}
