/**
 * 
 */
package net.sci.image.filtering;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class MedianLocalHistogramUInt8Test
{

    /**
     * Test method for {@link net.sci.image.filtering.MedianLocalHistogramUInt8#getMedianInt()}.
     */
    @Test
    public final void testGetMedianInt()
    {
        int totalCount = 9;
        int initValue = 10;
        int newValue = 50;
        
        MedianLocalHistogramUInt8 histo = new MedianLocalHistogramUInt8(totalCount, initValue);
        
        for (int i = 0; i < 4; i++)
        {
            histo.replace(initValue, newValue);
        }
        assertEquals(initValue, histo.getMedianInt());
        
        histo.replace(initValue, newValue);
        assertEquals(initValue, histo.getMedianInt());
    }

}
