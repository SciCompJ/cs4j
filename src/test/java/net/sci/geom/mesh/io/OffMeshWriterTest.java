/**
 * 
 */
package net.sci.geom.mesh.io;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import net.sci.geom.mesh.Mesh3D;
import net.sci.geom.mesh.Meshes3D;

/**
 * @author dlegland
 *
 */
public class OffMeshWriterTest
{
    
    /**
     * Test method for {@link net.sci.geom.mesh.io.OffMeshWriter#writeMesh(net.sci.geom.mesh.Mesh3D)}.
     * @throws IOException 
     */
    @Test
    public final void testWriteMesh() throws IOException
    {
        Mesh3D mesh = Meshes3D.createOctahedron();
        
        File file = new File("test_mesh_write_off_octahedron.off");
        MeshWriter writer = new OffMeshWriter(file);
        
        writer.writeMesh(mesh);
    }

    /**
     * Test method for {@link net.sci.geom.mesh.io.OffMeshWriter#writeMesh(net.sci.geom.mesh.Mesh3D)}.
     * @throws IOException 
     */
    @Test
    public final void testWriteAndReadMesh() throws IOException
    {
        Mesh3D mesh = Meshes3D.createOctahedron();
        
        File file = new File("test_mesh_write_off_octahedron.off");
        MeshWriter writer = new OffMeshWriter(file);
        
        writer.writeMesh(mesh);
        
        MeshReader reader = new OffMeshReader(file);
        Mesh3D mesh2 = reader.readMesh();
        assertEquals(mesh.vertexCount(), mesh2.vertexCount());
        assertEquals(mesh.faceCount(), mesh2.faceCount());
    }
}
