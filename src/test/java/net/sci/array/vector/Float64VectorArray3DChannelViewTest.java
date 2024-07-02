/**
 * 
 */
package net.sci.array.vector;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.numeric.Float64Array;
import net.sci.array.numeric.Float64Array3D;
import net.sci.array.numeric.Float64VectorArray3D;

/**
 * @author dlegland
 *
 */
public class Float64VectorArray3DChannelViewTest
{
    
    /**
     * Test method for {@link net.sci.array.numeric.Float64VectorArray3D#channel(int)}.
     */
    @Test
    public final void channel_Iterator()
    {
        Float64VectorArray3D array = Float64VectorArray3D.create(5, 4, 3, 10);
        
        Float64Array3D green = array.channel(1);
        Float64Array.Iterator iter = green.iterator();
        int n = 0;
        while (iter.hasNext())
        {
            iter.forward();
            n++;
        }
        
        assertEquals(60, n);
    }
}
