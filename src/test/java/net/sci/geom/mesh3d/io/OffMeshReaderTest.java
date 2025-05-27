/**
 * 
 */
package net.sci.geom.mesh3d.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import net.sci.geom.mesh3d.Mesh3D;

/**
 * @author dlegland
 *
 */
public class OffMeshReaderTest
{
    @Test
    public void testReadMesh_Octaheedron() throws IOException
    {
        String fileName = getClass().getResource("/meshes/octahedron.off").getFile();
        
        MeshReader reader = new OffMeshReader(new File(fileName));
        Mesh3D mesh = reader.readMesh();

        assertNotNull(mesh);
        assertEquals(6, mesh.vertexCount());
        assertEquals(8, mesh.faceCount());
    }
    
}
