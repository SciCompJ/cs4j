/**
 * 
 */
package net.sci.array.scalar;

import static org.junit.Assert.*;

import net.sci.array.scalar.Float32Array2D;
import net.sci.array.scalar.ScalarArray2D;
import net.sci.array.scalar.UInt8Array;

import org.junit.Test;

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
		ScalarArray2D<?> array = Float32Array2D.create(10, 10);
		array.fillValue(300);
		
		UInt8Array array8 = UInt8Array.convert(array);
		assertEquals(255, array8.getInt(new int[]{5, 5}));
	}


	/**
	 * Test method for {@link net.sci.array.scalar.UInt8Array#wrap(net.sci.array.scalar.ScalarArray)}.
	 */
	@Test
	public final void testWrap()
	{
		ScalarArray2D<?> array = Float32Array2D.create(10, 10);
		array.fillValue(300);
		
		UInt8Array array8 = UInt8Array.wrap(array);
		assertEquals(255, array8.getInt(new int[]{5, 5}));
		
		array.setValue(100, 5, 5);
		assertEquals(100, array8.getInt(5, 5));
		
		array.setValue(-10, 5, 5);
		assertEquals(0, array8.getInt(5, 5));
	}

}
