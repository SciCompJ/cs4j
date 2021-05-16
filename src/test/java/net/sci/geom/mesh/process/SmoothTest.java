/**
 * 
 */
package net.sci.geom.mesh.process;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.geom.mesh.Mesh3D;
import net.sci.geom.mesh.Meshes3D;

/**
 * @author dlegland
 *
 */
public class SmoothTest
{
    
    /**
     * Test method for {@link net.sci.geom.mesh.process.Smooth#process(net.sci.geom.mesh.Mesh3D)}.
     */
    @Test
    public final void testProcess()
    {
        Mesh3D mesh = Meshes3D.createOctahedron();
        
        Smooth algo = new Smooth();
        Mesh3D result = algo.process(mesh);
        
        assertEquals(6, result.vertexCount());
        assertEquals(8, result.faceCount());
    }
    
    /**
     * Test method for {@link net.sci.geom.mesh.process.Smooth#process(net.sci.geom.mesh.Mesh3D)}.
     */
    @Test
    public final void testProcessTwice()
    {
        Mesh3D mesh = Meshes3D.createOctahedron();
        
        Smooth algo = new Smooth();
        Mesh3D result = algo.process(mesh);
        result = algo.process(result);
        
        assertEquals(6, result.vertexCount());
        assertEquals(8, result.faceCount());
    }
    
}
