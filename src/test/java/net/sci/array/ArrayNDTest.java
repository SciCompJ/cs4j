/**
 * 
 */
package net.sci.array;

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
    public final void testNewInstance_String()
    {
        ArrayND<String> array = BufferedGenericArrayND.create(new int[]{5, 4, 3, 2}, "");
        
        Array<String> tmp = array.newInstance(new int[]{5, 4, 3, 2});
        assertNotNull(tmp);
    }

}
