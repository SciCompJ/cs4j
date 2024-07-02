/**
 * 
 */
package net.sci.image.morphology.watershed;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.numeric.Int32Array1D;
import net.sci.array.numeric.IntArray1D;

/**
 * @author dlegland
 *
 */
public class Watershed1DTest
{

    /**
     * Test method for {@link net.sci.image.morphology.watershed.Watershed1D#process(net.sci.array.scalar.ScalarArray1D)}.
     */
    @Test
    public final void testProcess_ThreeRegions()
    {
        Int32Array1D array = Int32Array1D.fromIntArray(new int[] {10, 30, 20, 5, 15, 25, 40, 35, 15});
        
        Watershed1D algo = new Watershed1D();
        IntArray1D<?> res = algo.process(array);
        
//        System.out.println("res:\n" + res);
        
        assertEquals(res.size(0), array.size(0));
        assertEquals(0, res.getInt(1));
        assertEquals(0, res.getInt(6));
        assertNotEquals(res.getInt(0), res.getInt(2));
        assertEquals(res.getInt(2), res.getInt(5));
        assertNotEquals(res.getInt(5), res.getInt(7));
        assertEquals(res.getInt(7), res.getInt(8));
    }

}
