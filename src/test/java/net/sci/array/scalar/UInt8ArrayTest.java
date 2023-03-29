/**
 * 
 */
package net.sci.array.scalar;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.Array2D;
import net.sci.array.generic.GenericArray2D;

/**
 * @author dlegland
 *
 */
public class UInt8ArrayTest
{

	/**
	 * Test method for {@link net.sci.array.scalar.UInt8Array#convert(net.sci.array.scalar.ScalarArray)}.
	 */
	@Test
	public final void testConvert()
	{
        ScalarArray2D<?> array = Float32Array2D.create(8, 6);
        array.fillValues((x,y) -> y * 10.0 + x);
        array.setValue(4, 3, 300);
        
        UInt8Array array8 = UInt8Array.convert(array);
        
        // array size should not change
        assertEquals(2, array8.dimensionality());
        assertEquals(8, array8.size(0));
        assertEquals(6, array8.size(1));
        
        // array content should be the same 
        assertEquals( 0, array8.getInt(new int[] {0, 0}));
        assertEquals( 7, array8.getInt(new int[] {7, 0}));
        assertEquals(50, array8.getInt(new int[] {0, 5}));
        assertEquals(57, array8.getInt(new int[] {7, 5}));
        
        // conversion from large value should clamp to range 
        assertEquals(255, array8.getInt(new int[] {4, 3}));
 	}

    /**
     * Test method for {@link net.sci.array.scalar.UInt8Array#wrap(net.sci.array.Array)}.
     */
    @Test
    public final void testWrap()
    {
        Array2D<UInt8> array = GenericArray2D.create(8, 6, new UInt8(0));
        array.fill((x,y) -> new UInt8(y * 10 + x));
        
        UInt8Array array8 = UInt8Array.wrap(array);
        
        // array size should not change
        assertEquals(2, array8.dimensionality());
        assertEquals(8, array8.size(0));
        assertEquals(6, array8.size(1));
        
        // array content should be the same 
        assertEquals(0, array8.getInt(new int[] {0, 0}));
        assertEquals(7, array8.getInt(new int[] {7, 0}));
        assertEquals(50, array8.getInt(new int[] {0, 5}));
        assertEquals(57, array8.getInt(new int[] {7, 5}));
        
        // changing the view should change original array
        array8.setInt(new int[] {4, 3}, 99);
        assertEquals(99, array.get(4, 3).getInt());
    }

	/**
	 * Test method for {@link net.sci.array.scalar.UInt8Array#wrapScalar(net.sci.array.scalar.ScalarArray)}.
	 */
	@Test
	public final void testWrapScalar()
	{
		ScalarArray2D<?> array = Float32Array2D.create(10, 10);
		array.fillValue(300);
		
		UInt8Array array8 = UInt8Array.wrapScalar(array);
		assertEquals(255, array8.getInt(new int[]{5, 5}));
		
		array.setValue(5, 5, 100);
		assertEquals(100, array8.getInt(new int[] {5, 5}));
		
		array.setValue(5, 5, -10);
		assertEquals(0, array8.getInt(new int[] {5, 5}));
	}

}
