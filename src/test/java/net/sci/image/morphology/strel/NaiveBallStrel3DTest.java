/**
 * 
 */
package net.sci.image.morphology.strel;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.scalar.UInt8Array;
import net.sci.array.scalar.UInt8Array3D;
import net.sci.image.morphology.Strel3D;
import net.sci.image.morphology.strel.NaiveBallStrel3D;

/**
 * @author dlegland
 *
 */
public class NaiveBallStrel3DTest
{
    
    /**
     * Test method for {@link net.sci.image.morphology.strel.NaiveBallStrel3D#dilation(net.sci.array.scalar.ScalarArray3D)}.
     */
    @Test
    public final void testDilation()
    {
        UInt8Array3D array = UInt8Array3D.create(11, 11, 11);
        array.setInt(5, 5, 5, 255);
        Strel3D strel = new NaiveBallStrel3D(3);
       
        UInt8Array3D res = UInt8Array3D.wrap(UInt8Array.wrap(strel.dilation(array)));
        
        assertEquals(11, res.size(0));
        assertEquals(11, res.size(1));
        assertEquals(11, res.size(2));

        // central voxel
        assertEquals(255, res.getInt(5, 5, 5));
        
        // small shift in each orthogonal direction
        assertEquals(255, res.getInt(2, 5, 5));
        assertEquals(255, res.getInt(8, 5, 5));
        assertEquals(255, res.getInt(5, 2, 5));
        assertEquals(255, res.getInt(5, 8, 5));
        assertEquals(255, res.getInt(5, 5, 2));
        assertEquals(255, res.getInt(5, 5, 8));
        
        // shift by combination of two directions
        assertEquals(  0, res.getInt(2, 2, 5));
        assertEquals(  0, res.getInt(2, 8, 5));
        assertEquals(  0, res.getInt(8, 2, 5));
        assertEquals(  0, res.getInt(8, 8, 5));
        assertEquals(  0, res.getInt(2, 5, 2));
        assertEquals(  0, res.getInt(2, 5, 8));
        assertEquals(  0, res.getInt(8, 5, 2));
        assertEquals(  0, res.getInt(8, 5, 8));
        assertEquals(  0, res.getInt(5, 2, 2));
        assertEquals(  0, res.getInt(5, 2, 8));
        assertEquals(  0, res.getInt(5, 8, 2));
        assertEquals(  0, res.getInt(5, 8, 8));
    }
    
    /**
     * Test method for {@link net.sci.image.morphology.strel.NaiveBallStrel3D#erosion(net.sci.array.scalar.ScalarArray3D)}.
     */
    @Test
    public final void testErosion()
    {
        UInt8Array3D array = UInt8Array3D.create(11, 11, 11);
        array.fillValue(255);
        array.setInt(5, 5, 5, 0);
        Strel3D strel = new NaiveBallStrel3D(3);
       
        UInt8Array3D res = UInt8Array3D.wrap(UInt8Array.wrap(strel.erosion(array)));
        
        assertEquals(11, res.size(0));
        assertEquals(11, res.size(1));
        assertEquals(11, res.size(2));

        // central voxel
        assertEquals(  0, res.getInt(5, 5, 5));
        
        // small shift in each orthogonal direction
        assertEquals(  0, res.getInt(2, 5, 5));
        assertEquals(  0, res.getInt(8, 5, 5));
        assertEquals(  0, res.getInt(5, 2, 5));
        assertEquals(  0, res.getInt(5, 8, 5));
        assertEquals(  0, res.getInt(5, 5, 2));
        assertEquals(  0, res.getInt(5, 5, 8));
        
        // shift by combination of two directions
        assertEquals(255, res.getInt(2, 2, 5));
        assertEquals(255, res.getInt(2, 8, 5));
        assertEquals(255, res.getInt(8, 2, 5));
        assertEquals(255, res.getInt(8, 8, 5));
        assertEquals(255, res.getInt(2, 5, 2));
        assertEquals(255, res.getInt(2, 5, 8));
        assertEquals(255, res.getInt(8, 5, 2));
        assertEquals(255, res.getInt(8, 5, 8));
        assertEquals(255, res.getInt(5, 2, 2));
        assertEquals(255, res.getInt(5, 2, 8));
        assertEquals(255, res.getInt(5, 8, 2));
        assertEquals(255, res.getInt(5, 8, 8));
    }
    
    /**
     * Test method for {@link net.sci.image.morphology.strel.NaiveBallStrel3D#getSize()}.
     */
    @Test
    public final void testGetSize()
    {
        Strel3D strel = new NaiveBallStrel3D(3);
        int[] size = strel.getSize();
        assertEquals(7, size[0]);
        assertEquals(7, size[1]);
        assertEquals(7, size[2]);
    }
    
}
