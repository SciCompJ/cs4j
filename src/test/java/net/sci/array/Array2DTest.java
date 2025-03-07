/**
 * 
 */
package net.sci.array;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import net.sci.array.numeric.UInt8;
import net.sci.array.numeric.UInt8Array;

/**
 * @author dlegland
 *
 */
public class Array2DTest
{
    @Test
    public final void testNewInstance_String()
    {
        Array2D<String> array = Array2D.create(5, 4, "");
        
        Array<String> tmp = array.newInstance(new int[]{5, 4});
        assertNotNull(tmp);
    }
    
	/**
	 * Test method for {@link net.sci.array.Array2D#wrap(net.sci.array.Array)}.
	 */
	@Test
	public final void testWrap()
	{
		Array<UInt8> array = UInt8Array.create(new int[]{5, 4});
		for (int y = 0; y < 4; y++)
		{
			for (int x = 0; x < 5; x++)
			{
				int[] pos = new int[]{x, y};
				array.set(pos, new UInt8(y * 10 + x));
			}
		}
		
		Array2D<UInt8> res = Array2D.wrap(array);
		assertEquals(5, res.size(0));
		assertEquals(4, res.size(1));
		
		assertEquals(new UInt8(34), res.get(4, 3));
	}

	/**
	 * Test method for {@link net.sci.array.Array2D#wrap(net.sci.array.Array)}.
	 */
	@Test
	public final void testWrap_3d()
	{
		Array<UInt8> array = UInt8Array.create(new int[]{5, 4, 3});
		for (int z = 0; z < 3; z++)
		{
			for (int y = 0; y < 4; y++)
			{
				for (int x = 0; x < 5; x++)
				{
					int[] pos = new int[]{x, y, z};
					array.set(pos, new UInt8(z * 100 + y * 10 + x));
				}
			}
		}
		
		Array3D<UInt8> res = Array3D.wrap(array);
		assertEquals(5, res.size(0));
        assertEquals(4, res.size(1));
        assertEquals(3, res.size(2));
		
		assertEquals(new UInt8(234), res.get(4, 3, 2));
	}
	
    /**
     * Test method for {@link net.sci.array.Array2D#fill(java.util.function.BiFunction)}.
     */
    @Test
    public final void testFill()
    {
        Array2D<String> array = Array2D.create(5, 4, "");
        String[] digits = {"A", "B", "C", "D", "E", "F"};  
        
        array.fill((x,y) -> digits[x] + digits[y]);
        
        assertEquals(array.get(0, 0), "AA");
        assertEquals(array.get(4, 3), "ED");
    }
}
