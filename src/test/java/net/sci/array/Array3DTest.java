/**
 * 
 */
package net.sci.array;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class Array3DTest
{
    @Test
    public final void testNewInstance_String()
    {
        Array3D<String> array = Array3D.create(5, 4, 3, "");
        
        Array<String> tmp = array.newInstance(new int[]{5, 4, 3});
        assertNotNull(tmp);
    }

    /**
     * Test method for {@link net.sci.array.Array3D#populate(net.sci.array.scalar.TriFunction)}.
     */
    @Test
    public final void testPopulate()
    {
        Array3D<String> array = Array3D.create(5, 4, 3, null);
        String[] digits = {"A", "B", "C", "D", "E", "F"};  
        
        array.populate((x,y,z) -> digits[z.intValue()] + digits[y.intValue()] + digits[x.intValue()]);
        
        assertEquals(array.get(0, 0, 0), "AAA");
        assertEquals(array.get(4, 3, 2), "CDE");
    }
}
