/**
 * 
 */
package net.sci.array;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import net.sci.array.impl.BufferedGenericArrayND;

/**
 * @author dlegland
 *
 */
public class ArrayNDTest
{
    @Test
    public final void test_newInstance_String()
    {
        int[] dims = new int[]{5, 4, 3, 2};
        ArrayND<String> array = new BufferedGenericArrayND<String>(dims, "");
        
        int[] dims2 = new int[]{6, 5, 4, 3};
        Array<String> res = array.newInstance(dims2);
        assertNotNull(res);
        assertEquals(4, res.dimensionality());
        for (int i = 0; i < 4; i++)
        {
            assertEquals(dims2[i], res.size(i));
        }
    }
}
