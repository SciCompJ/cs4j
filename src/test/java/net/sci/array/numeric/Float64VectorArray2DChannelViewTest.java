/**
 * 
 */
package net.sci.array.numeric;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class Float64VectorArray2DChannelViewTest
{
    
    /**
     * Test method for {@link net.sci.array.numeric.Float64VectorArray2D#channel(int)}.
     */
    @Test
    public final void channel_Iterator()
    {
        Float64VectorArray2D array = Float64VectorArray2D.create(5, 4, 10);
        
        Float64Array2D green = array.channel(1);
        Float64Array.Iterator iter = green.iterator();
        int n = 0;
        while (iter.hasNext())
        {
            iter.forward();
            n++;
        }
        
        assertEquals(20, n);
    }
}
