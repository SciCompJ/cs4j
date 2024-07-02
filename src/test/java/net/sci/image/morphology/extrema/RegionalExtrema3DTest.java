/**
 * 
 */
package net.sci.image.morphology.extrema;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.binary.BinaryArray3D;
import net.sci.array.numeric.UInt8Array3D;
import net.sci.image.Connectivity3D;
import net.sci.image.morphology.MinimaAndMaxima;

/**
 * @author dlegland
 *
 */
public class RegionalExtrema3DTest
{
    /**
     * Test method for {@link net.sci.image.morphology.extrema.RegionalExtrema3D#process(net.sci.array.scalar.ScalarArray, net.sci.array.binary.BinaryArray)}.
     */
    @Test
    public final void testProcess_Minima_FewHoles_C6()
    {
        UInt8Array3D array = UInt8Array3D.create(5,  5, 5);
        array.fillInt(100);
        array.setInt(1, 1, 1, 10);
        array.setInt(3, 1, 1, 20);
        array.setInt(1, 3, 1, 30);
        array.setInt(3, 3, 1, 40);
        array.setInt(1, 1, 3, 50);
        array.setInt(3, 1, 3, 60);
        array.setInt(1, 3, 3, 70);
        array.setInt(3, 3, 3, 80);
        RegionalExtrema3D algo = new RegionalExtrema3D(MinimaAndMaxima.Type.MINIMA, Connectivity3D.C6);
        
        BinaryArray3D res = algo.processScalar(array);
        
        // The minima should have value TRUE
        assertTrue(res.getBoolean(1, 1, 1));
        assertTrue(res.getBoolean(3, 1, 1));
        assertTrue(res.getBoolean(1, 3, 1));
        assertTrue(res.getBoolean(3, 3, 1));
        assertTrue(res.getBoolean(1, 1, 3));
        assertTrue(res.getBoolean(3, 1, 3));
        assertTrue(res.getBoolean(1, 3, 3));
        assertTrue(res.getBoolean(3, 3, 3));
        // other elements should have value FALSE
        assertFalse(res.getBoolean(0, 0, 0));
        assertFalse(res.getBoolean(4, 0, 0));
        assertFalse(res.getBoolean(0, 4, 0));
        assertFalse(res.getBoolean(4, 4, 0));
        assertFalse(res.getBoolean(0, 0, 4));
        assertFalse(res.getBoolean(4, 0, 4));
        assertFalse(res.getBoolean(0, 4, 4));
        assertFalse(res.getBoolean(4, 4, 4));
    }

    /**
     * Test method for {@link net.sci.image.morphology.extrema.RegionalExtrema3D#process(net.sci.array.scalar.ScalarArray, net.sci.array.binary.BinaryArray)}.
     */
    @Test
    public final void testProcess_Minima_FewHoles_C26()
    {
        UInt8Array3D array = UInt8Array3D.create(5,  5, 5);
        array.fillInt(100);
        array.setInt(1, 1, 1, 10);
        array.setInt(3, 1, 1, 20);
        array.setInt(1, 3, 1, 30);
        array.setInt(3, 3, 1, 40);
        array.setInt(1, 1, 3, 50);
        array.setInt(3, 1, 3, 60);
        array.setInt(1, 3, 3, 70);
        array.setInt(3, 3, 3, 80);
        RegionalExtrema3D algo = new RegionalExtrema3D(MinimaAndMaxima.Type.MINIMA, Connectivity3D.C26);
        
        BinaryArray3D res = algo.processScalar(array);
        
        // The minima should have value TRUE
        assertTrue(res.getBoolean(1, 1, 1));
        assertTrue(res.getBoolean(3, 1, 1));
        assertTrue(res.getBoolean(1, 3, 1));
        assertTrue(res.getBoolean(3, 3, 1));
        assertTrue(res.getBoolean(1, 1, 3));
        assertTrue(res.getBoolean(3, 1, 3));
        assertTrue(res.getBoolean(1, 3, 3));
        assertTrue(res.getBoolean(3, 3, 3));
        // other elements should have value FALSE
        assertFalse(res.getBoolean(0, 0, 0));
        assertFalse(res.getBoolean(4, 0, 0));
        assertFalse(res.getBoolean(0, 4, 0));
        assertFalse(res.getBoolean(4, 4, 0));
        assertFalse(res.getBoolean(0, 0, 4));
        assertFalse(res.getBoolean(4, 0, 4));
        assertFalse(res.getBoolean(0, 4, 4));
        assertFalse(res.getBoolean(4, 4, 4));
    }
}
