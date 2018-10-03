/**
 * 
 */
package net.sci.array.vector;

import static org.junit.Assert.assertEquals;
import net.sci.array.scalar.Float32Array;
import net.sci.array.scalar.Float32Array3D;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class Float32VectorArray3DChannelViewTest
{
    
    /**
     * Test method for {@link net.sci.array.color.Float64VectorArray3D#channel(int)}.
     */
    @Test
    public final void channel_Iterator()
    {
        Float32VectorArray3D array = Float32VectorArray3D.create(5, 4, 3, 10);
        
        Float32Array3D green = array.channel(1);
        Float32Array.Iterator iter = green.iterator();
        int n = 0;
        while (iter.hasNext())
        {
            iter.forward();
            n++;
        }
        
        assertEquals(60, n);
    }
}
