/**
 * 
 */
package net.sci.array.binary;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class RunLengthBinaryArrayFactoryTest
{
    /**
     * Test method for {@link net.sci.array.binary.RunLengthBinaryArrayFactory#create(int[])}.
     */
    @Test
    public final void testCreate_Array2D()
    {
        BinaryArray.Factory factory = new RunLengthBinaryArrayFactory();
        int[] dims = new int[] {10, 8};
        
        BinaryArray array = factory.create(dims);
        
        assertTrue(array instanceof RunLengthBinaryArray2D);
    }

    /**
     * Test method for {@link net.sci.array.binary.RunLengthBinaryArrayFactory#create(int[])}.
     */
    @Test
    public final void testCreate_Array3D()
    {
        BinaryArray.Factory factory = new RunLengthBinaryArrayFactory();
        int[] dims = new int[] {10, 8, 6};
        
        BinaryArray array = factory.create(dims);
        
        assertTrue(array instanceof RunLengthBinaryArray3D);
    }

}
