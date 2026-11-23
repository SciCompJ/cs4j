/**
 * 
 */
package net.sci.image.vectorize;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.binary.BinaryArray3D;
import net.sci.geom.mesh3d.Mesh3D;

/**
 * 
 */
public class BinaryImage3DBoundaryMeshTest
{

    /**
     * Test method for {@link net.sci.image.vectorize.BinaryImage3DBoundaryMesh#process(net.sci.array.Array)}.
     */
    @Test
    public final void test_process_singleVoxel()
    {
        BinaryArray3D array = BinaryArray3D.create(3, 3, 3);
        array.setBoolean(1, 1, 1, true);
        
        BinaryImage3DBoundaryMesh op = new BinaryImage3DBoundaryMesh();
        
        Mesh3D mesh = op.process(array);
        assertNotNull(mesh);
        
        assertEquals(8, mesh.vertexCount());
        assertEquals(6, mesh.faceCount());
    }

    /**
     * Test method for {@link net.sci.image.vectorize.BinaryImage3DBoundaryMesh#process(net.sci.array.Array)}.
     */
    @Test
    public final void test_process_singleCross()
    {
        BinaryArray3D array = BinaryArray3D.create(5, 5, 5);
        array.setBoolean(3, 3, 3, true);
        array.setBoolean(2, 3, 3, true);
        array.setBoolean(4, 3, 3, true);
        array.setBoolean(3, 2, 3, true);
        array.setBoolean(3, 4, 3, true);
        array.setBoolean(3, 3, 2, true);
        array.setBoolean(3, 3, 4, true);
        
        BinaryImage3DBoundaryMesh op = new BinaryImage3DBoundaryMesh();
        
        Mesh3D mesh = op.process(array);
        assertNotNull(mesh);
        
        assertEquals(4+12+12+4, mesh.vertexCount());
        assertEquals(1+4+4+12+4+4+1, mesh.faceCount());
    }

}
