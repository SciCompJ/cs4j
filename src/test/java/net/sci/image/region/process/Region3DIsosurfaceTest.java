/**
 * 
 */
package net.sci.image.region.process;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.geom.mesh3d.TriMesh3D;
import net.sci.image.region.BinaryRegion3D;

/**
 * 
 */
public class Region3DIsosurfaceTest
{
    /**
     * Test method for {@link net.sci.image.region.process.Region3DIsosurface#process(net.sci.image.region.BinaryRegion3D)}.
     */
    @Test
    public final void test_process_emptyRegion()
    {
        BinaryRegion3D region = new BinaryRegion3D();
        Region3DIsosurface op = new Region3DIsosurface();
        
        TriMesh3D res = op.process(region);
        
        assertEquals(0, res.vertexCount());
        assertEquals(0, res.faceCount());
    }

    /**
     * Test method for {@link net.sci.image.region.process.Region3DIsosurface#process(net.sci.image.region.BinaryRegion3D)}.
     */
    @Test
    public final void test_process_singeVoxel()
    {
        BinaryRegion3D region = new BinaryRegion3D();
        region.set(10, 10, 10, true);
        Region3DIsosurface op = new Region3DIsosurface();
        
        TriMesh3D res = op.process(region);
        
        assertEquals(6, res.vertexCount());
        assertEquals(8, res.faceCount());
    }

    /**
     * Test method for {@link net.sci.image.region.process.Region3DIsosurface#process(net.sci.image.region.BinaryRegion3D)}.
     */
    @Test
    public final void test_process_cube2x2x2()
    {
        BinaryRegion3D region = new BinaryRegion3D();
        region.set(2, 2, 2, true);
        region.set(3, 2, 2, true);
        region.set(2, 3, 2, true);
        region.set(3, 3, 2, true);
        region.set(2, 2, 3, true);
        region.set(3, 2, 3, true);
        region.set(2, 3, 3, true);
        region.set(3, 3, 3, true);
        Region3DIsosurface op = new Region3DIsosurface();
        
        TriMesh3D res = op.process(region);
        
        assertEquals(24, res.vertexCount());
        assertEquals(44, res.faceCount());
    }
}
