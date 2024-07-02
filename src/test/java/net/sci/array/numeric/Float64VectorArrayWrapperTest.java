/**
 * 
 */
package net.sci.array.numeric;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.Array2D;
import net.sci.array.impl.GenericArray2D;

/**
 * 
 */
public class Float64VectorArrayWrapperTest
{

    /**
     * Test method for {@link net.sci.array.numeric.Float64VectorArray#wrap(net.sci.array.Array)}.
     */
    @Test
    public final void testWrap()
    {
        // create base 8x6 array
        Float64Vector init = new Float64Vector(new double[] {0,0});
        Array2D<Float64Vector> array = GenericArray2D.create(8, 6, init);
        array.fill((x,y) -> new Float64Vector(new double[] {x, y}));
        
        Float64VectorArray wrap = Float64VectorArray.wrap(array);
        
        assertEquals(2, wrap.dimensionality());
        assertEquals(8, wrap.size(0));
        assertEquals(6, wrap.size(1));

        assertEquals(wrap.getValue(new int[] {7, 5}, 0), 7.0, 0.01);
        assertEquals(wrap.getValue(new int[] {7, 5}, 1), 5.0, 0.01);
    }

}
