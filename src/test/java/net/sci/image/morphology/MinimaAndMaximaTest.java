/**
 * 
 */
package net.sci.image.morphology;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.binary.BinaryArray2D;
import net.sci.array.scalar.UInt8Array2D;
import net.sci.image.Connectivity2D;

/**
 * 
 */
public class MinimaAndMaximaTest
{
    /**
     * Test method for {@link net.sci.image.morphology.MinimaAndMaxima#regionalMaxima(net.sci.array.scalar.ScalarArray2D)}.
     */
    @Test
    public final void testRegionalMaxima_ScalarArray2D_C4()
    {
        int[][] data = new int[][]{
            { 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10},
            { 10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10},
            { 10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10},
            { 10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10},
            { 10, 10, 10, 10, 40, 40, 40, 10, 10, 10, 10},
            { 10, 10, 10, 10, 40, 40, 40, 10, 10, 10, 10},
            { 10, 10, 10, 10, 40, 40, 40, 10, 10, 10, 10},
            { 10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10},
            { 10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10},
            { 10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10},
            { 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10}
        };
        
        UInt8Array2D image = UInt8Array2D.fromIntArray(data);
        
        BinaryArray2D maxima = MinimaAndMaxima.regionalMaxima(image, Connectivity2D.C4);
        
        assertFalse(maxima.getBoolean(0, 0));
        assertTrue(maxima.getBoolean(1, 1));
        assertTrue(maxima.getBoolean(9, 1));
        assertTrue(maxima.getBoolean(5, 5));
        assertFalse(maxima.getBoolean(10, 10));
    }

    /**
     * Test method for {@link net.sci.image.morphology.MinimaAndMaxima#regionalMaxima(net.sci.array.scalar.ScalarArray2D)}.
     */
    @Test
    public final void testRegionalMaxima_ScalarArray2D_C8()
    {
        int[][] data = new int[][]{
            { 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10},
            { 10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10},
            { 10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10},
            { 10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10},
            { 10, 10, 10, 10, 40, 40, 40, 10, 10, 10, 10},
            { 10, 10, 10, 10, 40, 40, 40, 10, 10, 10, 10},
            { 10, 10, 10, 10, 40, 40, 40, 10, 10, 10, 10},
            { 10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10},
            { 10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10},
            { 10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10},
            { 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10}
        };
        
        UInt8Array2D image = UInt8Array2D.fromIntArray(data);
        
        BinaryArray2D maxima = MinimaAndMaxima.regionalMaxima(image, Connectivity2D.C8);
        
        assertFalse(maxima.getBoolean(0, 0));
        assertFalse(maxima.getBoolean(1, 1));
        assertFalse(maxima.getBoolean(9, 1));
        assertTrue(maxima.getBoolean(5, 5));
        assertFalse(maxima.getBoolean(10, 10));
    }

    /**
     * Test method for {@link net.sci.image.morphology.MinimaAndMaxima#regionalMinima(net.sci.array.scalar.ScalarArray2D)}.
     */
    @Test
    public final void testRegionalMinima_ScalarArray2D_C4()
    {
        int[][] data = new int[][]{
            {50, 50, 50, 50, 50},
            {50, 10, 50, 50, 50},
            {50, 50, 10, 50, 50},
            {50, 50, 50, 10, 50},
            {50, 50, 20, 30, 50},
            {50, 50, 50, 50, 50},
        };
        
        UInt8Array2D image = UInt8Array2D.fromIntArray(data);
        
        BinaryArray2D maxima = MinimaAndMaxima.regionalMinima(image, Connectivity2D.C4);
        
        assertFalse(maxima.getBoolean(0, 0));
        assertTrue(maxima.getBoolean(1, 1));
        assertTrue(maxima.getBoolean(2, 2));
        assertTrue(maxima.getBoolean(3, 3));
        assertTrue(maxima.getBoolean(2, 4));
        assertFalse(maxima.getBoolean(3, 4));
    }

    /**
     * Test method for {@link net.sci.image.morphology.MinimaAndMaxima#regionalMinima(net.sci.array.scalar.ScalarArray2D)}.
     */
    @Test
    public final void testRegionalMinima_ScalarArray2D_C8()
    {
        int[][] data = new int[][]{
            {50, 50, 50, 50, 50},
            {50, 10, 50, 50, 50},
            {50, 50, 10, 50, 50},
            {50, 50, 50, 10, 50},
            {50, 50, 20, 30, 50},
            {50, 50, 50, 50, 50},
        };
        
        UInt8Array2D image = UInt8Array2D.fromIntArray(data);
        
        BinaryArray2D maxima = MinimaAndMaxima.regionalMinima(image, Connectivity2D.C8);
        
        assertFalse(maxima.getBoolean(0, 0));
        assertTrue(maxima.getBoolean(1, 1));
        assertTrue(maxima.getBoolean(2, 2));
        assertTrue(maxima.getBoolean(3, 3));
        assertFalse(maxima.getBoolean(2, 4));
        assertFalse(maxima.getBoolean(3, 4));
    }
    
    /**
     * Test method for {@link net.sci.image.morphology.MinimaAndMaxima#regionalMaxima(net.sci.array.scalar.ScalarArray2D)}.
     */
    @Test
    public final void testExtendedMaxima_ScalarArray2D_C4()
    {
        int[][] data = new int[][]{
            { 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10},
            { 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10},
            { 10, 10, 10, 40, 40, 40, 40, 40, 10, 10, 10},
            { 10, 10, 10, 40, 35, 35, 35, 40, 10, 10, 10},
            { 10, 10, 10, 40, 35, 50, 35, 40, 10, 10, 10},
            { 10, 10, 10, 40, 35, 35, 35, 40, 10, 10, 10},
            { 10, 10, 10, 40, 40, 40, 40, 40, 10, 10, 10},
            { 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10},
            { 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10},
            { 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10}
        };
        UInt8Array2D array = UInt8Array2D.fromIntArray(data);
        
        BinaryArray2D maxima = MinimaAndMaxima.extendedMaxima(array, 20, Connectivity2D.C4);
        
        assertFalse(maxima.getBoolean(0, 0));
        assertTrue(maxima.getBoolean(3, 3));
        assertTrue(maxima.getBoolean(7, 3));
        assertTrue(maxima.getBoolean(5, 5));
        assertFalse(maxima.getBoolean(9, 9));
    }

    /**
     * Test method for {@link net.sci.image.morphology.MinimaAndMaxima#regionalMaxima(net.sci.array.scalar.ScalarArray2D)}.
     */
    @Test
    public final void testExtendedMinima_ScalarArray2D_C4()
    {
        int[][] data = new int[][]{
            { 50, 50, 50, 50, 50, 50, 50, 50, 50},
            { 50, 50, 50, 50, 50, 50, 50, 50, 50},
            { 50, 50, 20, 20, 20, 20, 20, 50, 50},
            { 50, 50, 20, 30, 30, 30, 20, 50, 50},
            { 50, 50, 20, 30, 10, 30, 20, 50, 50},
            { 50, 50, 20, 30, 30, 30, 20, 50, 50},
            { 50, 50, 20, 20, 20, 20, 20, 50, 50},
            { 50, 50, 50, 50, 50, 50, 50, 50, 50},
            { 50, 50, 50, 50, 50, 50, 50, 50, 50},
        };
        UInt8Array2D array = UInt8Array2D.fromIntArray(data);
        
        BinaryArray2D minima = MinimaAndMaxima.extendedMinima(array, 30, Connectivity2D.C4);
        
        assertFalse(minima.getBoolean(0, 0));
        assertTrue(minima.getBoolean(2, 2));
        assertTrue(minima.getBoolean(6, 2));
        assertTrue(minima.getBoolean(4, 4));
        assertTrue(minima.getBoolean(2, 6));
        assertTrue(minima.getBoolean(6, 6));
        assertFalse(minima.getBoolean(7, 7));
    }

}
