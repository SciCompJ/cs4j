/**
 * 
 */
package net.sci.array.color;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import net.sci.array.scalar.Float32Array2D;
import net.sci.array.scalar.UInt8;
import net.sci.array.scalar.UInt8Array;
import net.sci.array.scalar.UInt8Array2D;

/**
 * @author dlegland
 *
 */
public class RGB8ArrayTest
{
    
    /**
     * Test method for {@link net.sci.array.color.RGB8Array#create(int[])}.
     */
    @Test
    public final void testCreate()
    {
        RGB8Array array = RGB8Array.create(5, 4, 3);
        assertNotNull(array);
        assertEquals(3, array.dimensionality());
        assertEquals(5, array.size(0));
        assertEquals(4, array.size(1));
        assertEquals(3, array.size(2));
    }
    
    /**
     * Test method for {@link net.sci.array.color.RGB8Array#convert(net.sci.array.scalar.ScalarArray, double[], ColorMap)}.
     */
    @Test
    public final void test_convert_ScalarArray_Range_Colormap()
    {
        // create scalar array to convert
        Float32Array2D array = Float32Array2D.create(20, 20);
        array.fillValues((x,y) -> x * 10.0); // linear ramp
        
        // conversion settings
        double[] range = new double[] {0, 190};
        ColorMap colormap = ColorMaps.JET.createColorMap(256);
        
        // convert
        RGB8Array res = RGB8Array.convert(array, range, colormap);
        
        // check colors
        assertEquals(res.get(new int[] { 0,  0}), new RGB8(  0, 0, 127));
        assertEquals(res.get(new int[] { 0, 19}), new RGB8(  0, 0, 127));
        assertEquals(res.get(new int[] {19,  0}), new RGB8(131, 0,   0));
        assertEquals(res.get(new int[] {19, 19}), new RGB8(131, 0,   0));
    }

    /**
     * Test method for {@link net.sci.array.color.RGB8Array#create(int[])}.
     */
    @Test
    public final void testCreateUInt8View()
    {
        RGB8Array array = RGB8Array.create(5, 4, 3);
        UInt8Array view = array.createUInt8View();
        
        assertNotNull(view);
        assertEquals(3, view.dimensionality());
        assertEquals(5, view.size(0));
        assertEquals(4, view.size(1));
        assertEquals(3, view.size(2));
    }
    
    /**
     * Test method for {@link net.sci.array.color.RGB8Array#create(int[])}.
     */
    @Test
    public final void testUInt8ViewIterator()
    {
        RGB8Array array = RGB8Array.create(5, 4, 3);
        UInt8Array view = array.createUInt8View();
        
        assertNotNull(view);
        int count = 0;
        for (@SuppressWarnings("unused") UInt8 item : view)
        {
            count++;
        }
        assertEquals(60, count);
    }
    
    @Test
    public final void testSetChannel()
    {
        RGB8Array2D array = RGB8Array2D.create(5, 4);
        UInt8Array2D red = UInt8Array2D.create(5, 4);
        red.fillValue(50);
        UInt8Array2D green = UInt8Array2D.create(5, 4);
        green.fillValue(100);
        UInt8Array2D blue = UInt8Array2D.create(5, 4);
        blue.fillValue(150);
        
        array.setChannel(0, red);
        array.setChannel(1, green);
        array.setChannel(2, blue);
        
        int[] samples = array.getSamples(2, 2);
        
        assertEquals( 50, samples[0]);
        assertEquals(100, samples[1]);
        assertEquals(150, samples[2]);
    }

}
