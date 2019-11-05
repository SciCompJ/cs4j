/**
 * 
 */
package net.sci.image.morphology.strel;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class LocalHistogramDoubleTest
{
    
    /**
     * Test method for {@link net.sci.image.morphology.strel.LocalHistogramDouble#getMaxValue()}.
     */
    @Test
    public final void testGetMaxValue()
    {
        LocalHistogramDouble localHisto = new LocalHistogramDouble(3, 0.0);
        localHisto.replace(0.0, 15.0);
        localHisto.replace(0.0, 5.0);
        localHisto.replace(0.0, 10.0);
        
        assertEquals(15.0, localHisto.getMaxValue(), .01);

        localHisto.replace(15.0, 5.0);
        assertEquals(10.0, localHisto.getMaxValue(), .01);
    }
    
    /**
     * Test method for {@link net.sci.image.morphology.strel.LocalHistogramDouble#getMinValue()}.
     */
    @Test
    public final void testGetMinValue()
    {
        LocalHistogramDouble localHisto = new LocalHistogramDouble(3, 50.0);
        localHisto.replace(50.0, 10.0);
        localHisto.replace(50.0, 30.0);
        localHisto.replace(50.0, 20.0);
        
        assertEquals(10.0, localHisto.getMinValue(), .01);

        localHisto.replace(10.0, 40.0);
        assertEquals(20.0, localHisto.getMinValue(), .01);

        localHisto.replace(30.0, 25.0);
        assertEquals(20.0, localHisto.getMinValue(), .01);
    }
    
}
