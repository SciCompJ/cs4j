/**
 * 
 */
package net.sci.array.process.numeric;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.scalar.UInt8Array;
import net.sci.array.scalar.UInt8Array2D;

/**
 * @author dlegland
 *
 */
public class MedianProjectionTest
{

    /**
     * Test method for {@link net.sci.array.process.numeric.MedianProjection#median(double[])}.
     */
    @Test
    public final void testMedian_N10()
    {
        double[] values = new double[] {10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
        double med = MedianProjection.median(values);
        assertEquals(5.5, med, 0.1);
    }

    public final void testMedian_N11()
    {
        double[] values = new double[] {11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
        double med = MedianProjection.median(values);
        assertEquals(6.0, med, 0.1);
    }

    /**
     * Test method for {@link net.sci.array.process.numeric.MedianProjection#MedianProjection(int)}.
     */
    @Test
    public final void testMedianProjection_x()
    {
        UInt8Array2D array = UInt8Array2D.create(8, 6);
        
        UInt8Array2D res = UInt8Array2D.wrap(UInt8Array.wrap(new MedianProjection(0).processScalar(array)));
        
        assertEquals(1, res.size(0));
        assertEquals(6, res.size(1));
    }
    
    /**
     * Test method for {@link net.sci.array.process.numeric.MedianProjection#MedianProjection(int)}.
     */
    @Test
    public final void testMedianProjection_y()
    {
        UInt8Array2D array = UInt8Array2D.create(8, 6);
        
        UInt8Array2D res = UInt8Array2D.wrap(UInt8Array.wrap(new MedianProjection(1).processScalar(array)));
        
        assertEquals(8, res.size(0));
        assertEquals(1, res.size(1));
    }

}
