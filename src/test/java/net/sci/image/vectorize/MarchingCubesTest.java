/**
 * 
 */
package net.sci.image.vectorize;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import net.sci.array.scalar.UInt8Array3D;
import net.sci.geom.mesh.Mesh3D;
import net.sci.geom.mesh.io.OffMeshWriter;

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
        
        assertEquals(6, mesh.vertexCount());
        assertEquals(8, mesh.faceCount());
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
        
        assertEquals(14, mesh.vertexCount());
        assertEquals(24, mesh.faceCount());
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
        
        assertEquals(14, mesh.vertexCount());
        assertEquals(24, mesh.faceCount());
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
        
        assertEquals(14, mesh.vertexCount());
        assertEquals(24, mesh.faceCount());
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
        
        assertEquals(3*14, mesh.vertexCount());
        assertEquals(3*24, mesh.faceCount());
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
        
        assertEquals(54, mesh.vertexCount());
        assertEquals(104, mesh.faceCount());
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
        
        assertEquals(54, mesh.vertexCount());
        assertEquals(104, mesh.faceCount());
    }

    /**
     * Test method for {@link net.sci.image.vectorize.MarchingCubes#process(net.sci.array.scalar.ScalarArray3D)}.
     * @throws IOException 
     */
    @Test
    public final void testProcess_RingXY() throws IOException
    {
        UInt8Array3D array = UInt8Array3D.create(6, 6, 6);
        array.fillValue(0);
        for(int i = 1; i < 5; i++)
        {
            array.setValue(i, 1, 2, 10);
            array.setValue(i, 4, 2, 10);
            array.setValue(1, i, 2, 10);
            array.setValue(4, i, 2, 10);
        }
        
        MarchingCubes mc = new MarchingCubes(5.0);
        Mesh3D mesh = mc.process(array);
        
        new OffMeshWriter(new File("ringXYMarchingCube.off")).writeMesh(mesh);
        
        assertEquals(48, mesh.vertexCount());
        assertEquals(96, mesh.faceCount());
    }

    /**
     * Test method for {@link net.sci.image.vectorize.MarchingCubes#process(net.sci.array.scalar.ScalarArray3D)}.
     * @throws IOException 
     */
    @Test
    public final void testProcess_RingXZ() throws IOException
    {
        UInt8Array3D array = UInt8Array3D.create(6, 6, 6);
        array.fillValue(0);
        for(int i = 1; i < 5; i++)
        {
            array.setValue(i, 2, 1, 10);
            array.setValue(i, 2, 4, 10);
            array.setValue(1, 2, i, 10);
            array.setValue(4, 2, i, 10);
        }
        
        MarchingCubes mc = new MarchingCubes(5.0);
        Mesh3D mesh = mc.process(array);
        
        new OffMeshWriter(new File("ringXZMarchingCube.off")).writeMesh(mesh);
        
        assertEquals(48, mesh.vertexCount());
        assertEquals(96, mesh.faceCount());
    }

    /**
     * Test method for {@link net.sci.image.vectorize.MarchingCubes#process(net.sci.array.scalar.ScalarArray3D)}.
     * @throws IOException 
     */
    @Test
    public final void testProcess_RingYZ() throws IOException
    {
        UInt8Array3D array = UInt8Array3D.create(6, 6, 6);
        array.fillValue(0);
        for(int i = 1; i < 5; i++)
        {
            array.setValue(2, i, 1, 10);
            array.setValue(2, i, 4, 10);
            array.setValue(2, 1, i, 10);
            array.setValue(2, 4, i, 10);
        }
        
        MarchingCubes mc = new MarchingCubes(5.0);
        Mesh3D mesh = mc.process(array);
        
        new OffMeshWriter(new File("ringYZMarchingCube.off")).writeMesh(mesh);
        
        assertEquals(48, mesh.vertexCount());
        assertEquals(96, mesh.faceCount());
    }

    /**
     * Test method for {@link net.sci.image.vectorize.MarchingCubes#process(net.sci.array.scalar.ScalarArray3D)}.
     * @throws IOException 
     */
    @Test
    public final void testProcess_CubeWithoutCorners() throws IOException
    {
        UInt8Array3D array = UInt8Array3D.create(6, 6, 6);
        array.fillValue(0);
        for(int z = 1; z < 5; z++)
        {
            for(int y = 1; y < 5; y++)
            {
                for(int x = 1; x < 5; x++)
                {
                    array.setValue(x, y, z, 10);
                }
            }
        }
        
        // remove corners
        array.setValue(1, 1, 1, 0);
        array.setValue(4, 1, 1, 0);
        array.setValue(1, 4, 1, 0);
        array.setValue(4, 4, 1, 0);
        array.setValue(1, 1, 4, 0);
        array.setValue(4, 1, 4, 0);
        array.setValue(1, 4, 4, 0);
        array.setValue(4, 4, 4, 0);
        
        MarchingCubes mc = new MarchingCubes(5.0);
        Mesh3D mesh = mc.process(array);
        
        new OffMeshWriter(new File("cubeWithoutCornersMarchingCube.off")).writeMesh(mesh);
        
        assertEquals(96, mesh.vertexCount());
        assertEquals(188, mesh.faceCount());
    }

    /**
     * Test method for {@link net.sci.image.vectorize.MarchingCubes#process(net.sci.array.scalar.ScalarArray3D)}.
     * @throws IOException 
     */
    @Test
    public final void testProcess_CubeWithoutEdges() throws IOException
    {
        UInt8Array3D array = UInt8Array3D.create(7, 7, 7);
        array.fillValue(0);
        for(int z = 1; z < 6; z++)
        {
            for(int y = 1; y < 6; y++)
            {
                for(int x = 1; x < 6; x++)
                {
                    array.setValue(x, y, z, 10);
                }
            }
        }
        
        // remove edges
        for(int i = 1; i < 6; i++)
        {
            array.setValue(i, 1, 1, 0);
            array.setValue(i, 5, 1, 0);
            array.setValue(i, 1, 5, 0);
            array.setValue(i, 5, 5, 0);
            array.setValue(1, i, 1, 0);
            array.setValue(5, i, 1, 0);
            array.setValue(1, i, 5, 0);
            array.setValue(5, i, 5, 0);
            array.setValue(1, 1, i, 0);
            array.setValue(5, 1, i, 0);
            array.setValue(1, 5, i, 0);
            array.setValue(5, 5, i, 0);
        }
        
        MarchingCubes mc = new MarchingCubes(5.0);
        Mesh3D mesh = mc.process(array);
        
        new OffMeshWriter(new File("cubeWithoutEdgesMarchingCube.off")).writeMesh(mesh);
        
        assertEquals(126, mesh.vertexCount());
        assertEquals(248, mesh.faceCount());
    }
    
    /**
     * Test method for {@link net.sci.image.vectorize.MarchingCubes#process(net.sci.array.scalar.ScalarArray3D)}.
     * @throws IOException 
     */
    @Test
    public final void testProcess_Config029() throws IOException
    {
        UInt8Array3D array = UInt8Array3D.create(4, 4, 4);
        array.fillValue(0);
        array.setInt(1, 1, 1, 10);
        array.setInt(1, 2, 1, 10);
        array.setInt(2, 2, 1, 10);
        array.setInt(1, 1, 2, 10);
        
        MarchingCubes mc = new MarchingCubes(5.0);
        Mesh3D mesh = mc.process(array);
        
        new OffMeshWriter(new File("config029.off")).writeMesh(mesh);
//        
//        assertEquals(126, mesh.vertexCount());
//        assertEquals(248, mesh.faceCount());
    }
}
