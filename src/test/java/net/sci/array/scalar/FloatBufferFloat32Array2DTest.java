/**
 * 
 */
package net.sci.array.scalar;

import static org.junit.Assert.*;

import java.nio.FloatBuffer;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class FloatBufferFloat32Array2DTest
{
    /**
     * Test method for {@link net.sci.array.scalar.FloatBufferFloat32Array2D#FloatBufferFloat32Array2D(int, int, java.nio.FloatBuffer)}.
     */
    @Test
    public void testCreate()
    {
        FloatBufferFloat32Array2D array = createGradientArray();
        assertEquals(array.size(0), 10);
        assertEquals(array.size(1),  8);
        assertEquals(array.getValue(9, 7), 79.0, 0.01);
    }

    /**
     * Test method for {@link net.sci.array.scalar.FloatBufferFloat32Array2D#getFloat(int[])}.
     */
    @Test
    public void testGetFloat()
    {
        FloatBufferFloat32Array2D array = createGradientArray();
        assertEquals(array.size(0), 10);
        assertEquals(array.size(1),  8);
        assertEquals(array.getValue(9, 7), 79.0, 0.01); 
        assertEquals(array.getFloat(9, 7), 79.0f, 0.01);
    }
    
    /**
     * Test method for {@link net.sci.array.scalar.FloatBufferFloat32Array2D#setFloat(int, int, float)}.
     */
    @Test
    public void testSetFloatIntIntFloat()
    {
        FloatBufferFloat32Array2D array = createGradientArray();
        array.setFloat(6, 5, 314.15f);
        assertEquals(array.getValue(6, 5), 314.15, 0.001);
    }

    /**
     * Test method for {@link net.sci.array.scalar.FloatBufferFloat32Array2D#duplicate()}.
     */
    @Test
    public void testDuplicate()
    {
        FloatBufferFloat32Array2D array = createGradientArray();
        Float32Array2D dup = array.duplicate();
        assertEquals(dup.size(0), 10);
        assertEquals(dup.size(1),  8);
        assertEquals(dup.getValue(9, 7), 79.0, 0.01);  
    }


    private FloatBufferFloat32Array2D createGradientArray()
    {
        float[] floatArray = new float[80];
        FloatBuffer buffer = FloatBuffer.wrap(floatArray);
        FloatBufferFloat32Array2D array = new FloatBufferFloat32Array2D(10, 8, buffer);
        array.populateValues((x,y) -> x + 10.0 * y);
        return array;
    }

}
