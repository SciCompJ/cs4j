/**
 * 
 */
package net.sci.array.shape;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.Array;
import net.sci.array.Arrays;
import net.sci.array.numeric.UInt16Array;
import net.sci.array.numeric.UInt8;
import net.sci.array.numeric.UInt8Array2D;
import net.sci.array.numeric.impl.BufferedUInt8Array2D;

/**
 * @author dlegland
 *
 */
public class FlipTest
{

	/**
	 * Test method for {@link net.sci.array.shape.Flip#process(net.sci.array.Array)}.
	 */
	@Test
	public final void testProcessArray_X()
	{
		int sizeX = 6;
		int sizeY = 4;
		UInt8Array2D array = new BufferedUInt8Array2D(sizeX, sizeY);
		array.fillValues((x,y) -> (double) x + y * 10.0);
		
		Flip flipX = new Flip(0);
		Array<?> resFlip= flipX.process(array);
		
		assertEquals(2, resFlip.dimensionality());
		assertEquals(sizeX, resFlip.size(0));
		assertEquals(sizeY, resFlip.size(1));
		
		assertEquals(new UInt8(35), resFlip.get(new int[]{0, 3}));
	}

	/**
-	 * Test method for {@link net.sci.array.shape.Flip#process(net.sci.array.Array, net.sci.array.Array)}.
	 */
	@Test
	public final void testProcessArrayArray_X()
	{
		int sizeX = 6;
		int sizeY = 4;
		UInt8Array2D array = new BufferedUInt8Array2D(sizeX, sizeY);
        array.fillValues((x,y) -> (double) x + y * 10.0);
		
		Flip flipX = new Flip(0);
		UInt8Array2D resFlip = array.duplicate();
		flipX.process(array, resFlip);
		
		assertEquals(2, resFlip.dimensionality());
		assertEquals(sizeX, resFlip.size(0));
		assertEquals(sizeY, resFlip.size(1));
		
		assertEquals(35, resFlip.getValue(new int[]{0, 3}), .1);
	}

    /**
     * Test method for {@link net.sci.array.shape.Flip#process(net.sci.array.Array, net.sci.array.Array)}.
     */
    @Test
    public final void testProcessArray_Y()
    {
        int sizeX = 6;
        int sizeY = 4;
        UInt8Array2D array = new BufferedUInt8Array2D(sizeX, sizeY);
        array.fillValues((x,y) -> (double) x + y * 10.0);
        
        Flip flipY = new Flip(1);
        Array<?> resFlip = flipY.process(array);
        
        assertEquals(2, resFlip.dimensionality());
        assertEquals(sizeX, resFlip.size(0));
        assertEquals(sizeY, resFlip.size(1));
        
        assertEquals(new UInt8(35), resFlip.get(new int[]{5, 0}));
    }
    
    /**
     * Test method for {@link net.sci.array.shape.Flip#process(net.sci.array.Array, net.sci.array.Array)}.
     */
    @Test
    public final void testProcessArray_Dim2_Flip0()
    {
        int[] dims = new int[] {6, 4};
        UInt16Array array = UInt16Array.create(dims);
        for (int[] pos : array.positions())
        {
            int value = 0;
            for (int d = 0; d < 2; d++)
            {
                value += pos[d] * Math.pow(10, d);
            }
            array.setValue(pos, value);
        }
        
        Flip flip = new Flip(0);
        Array<?> resFlip = flip.process(array);
        
        assertEquals(2, resFlip.dimensionality());
        assertEquals(dims[0], resFlip.size(0));
        assertEquals(dims[1], resFlip.size(1));
        
        assertEquals(array.get(new int[] {4, 3}), resFlip.get(new int[] {1, 3}));
        assertEquals(array.get(new int[] {2, 1}), resFlip.get(new int[] {3, 1}));
    }
    
    /**
     * Test method for {@link net.sci.array.shape.Flip#process(net.sci.array.Array, net.sci.array.Array)}.
     */
    @Test
    public final void testProcessArray_Dim4_Flip2()
    {
        int[] dims = new int[] {5, 4, 3, 2};
        UInt16Array array = UInt16Array.create(dims);
        
        for (int[] pos : array.positions())
        {
            int value = 0;
            for (int d = 0; d < 4; d++)
            {
                value += pos[d] * Math.pow(10, d);
            }
            array.setValue(pos, value);
        }
        
        Flip flip = new Flip(2);
        Array<?> resFlip = flip.process(array);
        
        assertEquals(4, resFlip.dimensionality());
        assertEquals(dims[0], resFlip.size(0));
        assertEquals(dims[1], resFlip.size(1));
        assertEquals(dims[2], resFlip.size(2));
        assertEquals(dims[3], resFlip.size(3));
        
        assertEquals(array.get(new int[] {4, 3, 2, 1}), resFlip.get(new int[] {4, 3, 0, 1}));
        assertEquals(array.get(new int[] {2, 3, 2, 0}), resFlip.get(new int[] {2, 3, 0, 0}));
    }
    
	/**
	 * Test method for {@link net.sci.array.shape.Flip#process(net.sci.array.Array, net.sci.array.Array)}.
	 */
	@Test
	public final void testProcessArrayArray_Y()
	{
		int sizeX = 6;
		int sizeY = 4;
		UInt8Array2D array = new BufferedUInt8Array2D(sizeX, sizeY);
        array.fillValues((x,y) -> (double) x + y * 10.0);
		
		Flip flipY = new Flip(1);
		UInt8Array2D resFlip = array.duplicate();
		flipY.process(array, resFlip);
		
		assertEquals(2, resFlip.dimensionality());
		assertEquals(sizeX, resFlip.size(0));
		assertEquals(sizeY, resFlip.size(1));
		
		assertEquals(35, resFlip.getValue(5, 0), .1);
	}

    /**
     * Test method for {@link net.sci.array.shape.Flip#process(net.sci.array.Array)}.
     */
    @Test
    public final void testProcessArray_UInt8_2D_View()
    {
        int sizeX = 6;
        int sizeY = 4;
        UInt8Array2D array = UInt8Array2D.create(sizeX, sizeY);
        array.fillValues((x,y) -> (double) x + y * 10.0);
        
        Flip flipX = new Flip(0);
        Array<?> resFlip = flipX.createView(array);
        
        assertEquals(2, resFlip.dimensionality());
        assertEquals(sizeX, resFlip.size(0));
        assertEquals(sizeY, resFlip.size(1));
        
        assertEquals(new UInt8(35), resFlip.get(new int[] {0, 3}));
    }

    /**
     * Test method for {@link net.sci.array.shape.Flip#process(net.sci.array.Array)}.
     */
    @Test
    public final void testProcessArray_String_2D_View()
    {
        int sizeX = 6;
        int sizeY = 4;
        Array<String> array = Arrays.create(new int[]{sizeX, sizeY}, "");
        String[] digits = new String[]{"A", "B", "C", "D", "E", "F"};
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                String str = digits[y] + digits[x];
                array.set(new int[] {x, y}, str);
            }
        }
        
        Flip flipX = new Flip(0);
        Array<?> resFlip = flipX.createView(array);
        
        assertEquals(2, resFlip.dimensionality());
        assertEquals(sizeX, resFlip.size(0));
        assertEquals(sizeY, resFlip.size(1));
        
        assertEquals("DF", resFlip.get(new int[] {0, 3}));
        assertEquals("DA", resFlip.get(new int[] {5, 3}));
    }

}
