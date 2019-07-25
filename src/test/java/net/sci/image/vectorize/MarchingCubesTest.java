/**
 * 
 */
package net.sci.image.vectorize;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import net.sci.array.scalar.UInt8Array3D;
import net.sci.geom.mesh.Mesh3D;

/**
 * @author dlegland
 *
 */
public class MarchingCubesTest
{
    /**
     * Test method for {@link net.sci.image.vectorize.MarchingCubes#process(net.sci.array.scalar.ScalarArray3D)}.
     */
    @Test
    public final void testProcess_SingleVoxel()
    {
        UInt8Array3D array = UInt8Array3D.create(3, 3, 3);
        array.fillValue(0);
        array.setValue(1, 1, 1, 10);
        
        MarchingCubes mc = new MarchingCubes(5.0);
        Mesh3D mesh = mc.process(array);
        
        assertEquals(6, mesh.vertexNumber());
        assertEquals(8, mesh.faceNumber());
    }
    
    /**
     * Test method for {@link net.sci.image.vectorize.MarchingCubes#process(net.sci.array.scalar.ScalarArray3D)}.
     * @throws IOException 
     */
    @Test
    public final void testProcess_LineX() throws IOException
    {
        UInt8Array3D array = UInt8Array3D.create(5, 3, 3);
        array.fillValue(0);
        for(int i = 1; i < 4; i++)
        {
            array.setValue(i, 1, 1, 10);
        }
        
        MarchingCubes mc = new MarchingCubes(5.0);
        Mesh3D mesh = mc.process(array);
        
        assertEquals(14, mesh.vertexNumber());
        assertEquals(24, mesh.faceNumber());
    }

    /**
     * Test method for {@link net.sci.image.vectorize.MarchingCubes#process(net.sci.array.scalar.ScalarArray3D)}.
     * @throws IOException 
     */
    @Test
    public final void testProcess_LineY() throws IOException
    {
        UInt8Array3D array = UInt8Array3D.create(3, 5, 3);
        array.fillValue(0);
        for(int i = 1; i < 4; i++)
        {
            array.setValue(1, i, 1, 10);
        }
        
        MarchingCubes mc = new MarchingCubes(5.0);
        Mesh3D mesh = mc.process(array);
        
        assertEquals(14, mesh.vertexNumber());
        assertEquals(24, mesh.faceNumber());
    }

    /**
     * Test method for {@link net.sci.image.vectorize.MarchingCubes#process(net.sci.array.scalar.ScalarArray3D)}.
     * @throws IOException 
     */
    @Test
    public final void testProcess_LineZ() throws IOException
    {
        UInt8Array3D array = UInt8Array3D.create(3, 3, 5);
        array.fillValue(0);
        for(int i = 1; i < 4; i++)
        {
            array.setValue(1, 1, i, 10);
        }
        
        MarchingCubes mc = new MarchingCubes(5.0);
        Mesh3D mesh = mc.process(array);
        
        assertEquals(14, mesh.vertexNumber());
        assertEquals(24, mesh.faceNumber());
    }

    /**
     * Test method for {@link net.sci.image.vectorize.MarchingCubes#process(net.sci.array.scalar.ScalarArray3D)}.
     * @throws IOException 
     */
    @Test
    public final void testProcess_LinearStructures()
    {
        UInt8Array3D array = UInt8Array3D.create(5, 5, 5);
        array.fillValue(0);
        for(int i = 1; i < 4; i++)
        {
            array.setValue(3, 1, i, 10);
            array.setValue(i, 3, 1, 10);
            array.setValue(1, i, 3, 10);
        }
        
        MarchingCubes mc = new MarchingCubes(5.0);
        Mesh3D mesh = mc.process(array);
        
        assertEquals(3*14, mesh.vertexNumber());
        assertEquals(3*24, mesh.faceNumber());
    }
    
    /**
     * Test method for {@link net.sci.image.vectorize.MarchingCubes#process(net.sci.array.scalar.ScalarArray3D)}.
     * @throws IOException 
     */
    @Test
    public final void testProcess_Cube() throws IOException
    {
        UInt8Array3D array = UInt8Array3D.create(5, 5, 5);
        array.fillValue(0);
        for(int z = 1; z < 4; z++)
        {
            for(int y = 1; y < 4; y++)
            {
                for(int x = 1; x < 4; x++)
                {
                    array.setValue(x, y, z, 10);
                }
            }
        }
        
        MarchingCubes mc = new MarchingCubes(5.0);
        Mesh3D mesh = mc.process(array);
        
        assertEquals(54, mesh.vertexNumber());
        assertEquals(104, mesh.faceNumber());
    }

    /**
     * Test method for {@link net.sci.image.vectorize.MarchingCubes#process(net.sci.array.scalar.ScalarArray3D)}.
     * @throws IOException 
     */
    @Test
    public final void testProcess_Cross() throws IOException
    {
        UInt8Array3D array = UInt8Array3D.create(7, 7, 7);
        array.fillValue(0);
        for(int i = 1; i < 6; i++)
        {
            array.setValue(i, 3, 3, 10);
            array.setValue(3, i, 3, 10);
            array.setValue(3, 3, i, 10);
        }
        
        MarchingCubes mc = new MarchingCubes(5.0);
        Mesh3D mesh = mc.process(array);
        
        assertEquals(54, mesh.vertexNumber());
        assertEquals(104, mesh.faceNumber());
    }

    /**
     * Test method for {@link net.sci.image.vectorize.MarchingCubes#process(net.sci.array.scalar.ScalarArray3D)}.
     * @throws IOException 
     */
    @Test
    public final void testProcess_CubeWithoutCorner() throws IOException
    {
        UInt8Array3D array = UInt8Array3D.create(5, 5, 5);
        array.fillValue(0);
        for(int z = 1; z < 4; z++)
        {
            for(int y = 1; y < 4; y++)
            {
                for(int x = 1; x < 4; x++)
                {
                    array.setValue(x, y, z, 10);
                }
            }
        }
        // remove corner
        array.setValue(3, 3, 3, 0);
        
        MarchingCubes mc = new MarchingCubes(5.0);
        Mesh3D mesh = mc.process(array);
        
        assertEquals(54, mesh.vertexNumber());
        assertEquals(104, mesh.faceNumber());
    }
}
