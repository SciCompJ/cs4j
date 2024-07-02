/**
 * 
 */
package net.sci.array.vector;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.Array2D;
import net.sci.array.generic.GenericArray2D;
import net.sci.array.numeric.Float32Vector;
import net.sci.array.numeric.Float32VectorArray;

/**
 * 
 */
public class Float32VectorArrayWrapperTest
{

    /**
     * Test method for {@link net.sci.array.numeric.Float64VectorArray#wrap(net.sci.array.Array)}.
     */
    @Test
    public final void testWrap()
    {
        // create base 8x6 array
        Float32Vector init = new Float32Vector(new float[] {0,0});
        Array2D<Float32Vector> array = GenericArray2D.create(8, 6, init);
        array.fill((x,y) -> new Float32Vector(new float[] {x, y}));
        
        Float32VectorArray wrap = Float32VectorArray.wrap(array);
        
        assertEquals(2, wrap.dimensionality());
        assertEquals(8, wrap.size(0));
        assertEquals(6, wrap.size(1));

        assertEquals(wrap.getValue(new int[] {7, 5}, 0), 7.0, 0.01);
        assertEquals(wrap.getValue(new int[] {7, 5}, 1), 5.0, 0.01);
    }

}
