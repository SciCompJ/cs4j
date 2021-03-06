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
public class UInt8Array2DTest
{
    /**
     * Test method for {@link net.sci.array.Array#select(BinaryArray)}.
     */
    @Test
    public final void testSelect()
	{
    	UInt8Array2D array = UInt8Array2D.create(6,  4);
    	BinaryArray2D mask = BinaryArray2D.create(6,  4);
    	for (int y = 0; y < 4; y++)
    	{
        	for (int x = 0; x < 6; x++)
        	{
        		array.setValue(x, y, y * 10 + x);
        	}
    	}
    	for (int y = 1; y < 3; y++)
    	{
        	for (int x = 1; x < 5; x++)
        	{
        		mask.setBoolean(x, y, true);
        	}
    	}
    	
    	double acc = 0;
    	for (UInt8 val : array.select(mask))
    	{
    		acc += val.getValue();
    	}
    	
    	assertEquals(30*4+10*2, acc, 0.01);
	}

}
