/**
 * 
 */
package net.sci.array;

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

}
