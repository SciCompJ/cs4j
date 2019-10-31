/**
 * 
 */
package net.sci.image.morphology.strel;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.scalar.UInt8Array;
import net.sci.array.scalar.UInt8Array2D;
import net.sci.image.morphology.strel.NaiveDiskStrel;

/**
 * @author dlegland
 *
 */
public class NaiveDiskStrelTest
{
    
    /**
     * Test method for {@link net.sci.image.morphology.strel.NaiveDiskStrel#dilation(net.sci.array.scalar.ScalarArray2D)}.
     */
    @Test
    public final void testDilation()
    {
        UInt8Array2D array = UInt8Array2D.create(11, 11);
        array.setInt(5, 5, 255);
        Strel2D strel = new NaiveDiskStrel(3);
       
        UInt8Array2D res = UInt8Array2D.wrap(UInt8Array.wrap(strel.dilation(array)));
        
        assertEquals(11, res.size(0));
        assertEquals(11, res.size(1));

        assertEquals(255, res.getInt(5, 5));
        assertEquals(255, res.getInt(2, 5));
        assertEquals(255, res.getInt(8, 5));
        assertEquals(255, res.getInt(5, 2));
        assertEquals(255, res.getInt(5, 8));
        assertEquals(  0, res.getInt(2, 2));
        assertEquals(  0, res.getInt(2, 8));
        assertEquals(  0, res.getInt(8, 2));
        assertEquals(  0, res.getInt(8, 8));
    }
    
    /**
     * Test method for {@link net.sci.image.morphology.strel.NaiveDiskStrel#erosion(net.sci.array.scalar.ScalarArray2D)}.
     */
    @Test
    public final void testErosion()
    {
        UInt8Array2D array = UInt8Array2D.create(11, 11);
        array.fillValue(255);
        array.setInt(5, 5, 0);
        Strel2D strel = new NaiveDiskStrel(3);
       
        UInt8Array2D res = UInt8Array2D.wrap(UInt8Array.wrap(strel.erosion(array)));
        
        assertEquals(11, res.size(0));
        assertEquals(11, res.size(1));

        assertEquals(  0, res.getInt(5, 5));
        assertEquals(  0, res.getInt(2, 5));
        assertEquals(  0, res.getInt(8, 5));
        assertEquals(  0, res.getInt(5, 2));
        assertEquals(  0, res.getInt(5, 8));
        assertEquals(255, res.getInt(2, 2));
        assertEquals(255, res.getInt(2, 8));
        assertEquals(255, res.getInt(8, 2));
        assertEquals(255, res.getInt(8, 8));
    }
    
    /**
     * Test method for {@link net.sci.image.morphology.strel.NaiveDiskStrel#size()}.
     */
    @Test
    public final void testGetSize()
    {
        Strel2D strel = new NaiveDiskStrel(3);
        int[] size = strel.size();
        assertEquals(7, size[0]);
        assertEquals(7, size[1]);
    }
    
}
