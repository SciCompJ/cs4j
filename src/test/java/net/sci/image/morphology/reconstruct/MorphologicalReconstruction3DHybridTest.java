/**
 * 
 */
package net.sci.image.morphology.reconstruct;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.scalar.ScalarArray3D;
import net.sci.array.scalar.UInt8Array3D;
import net.sci.image.Connectivity3D;
import net.sci.image.morphology.MorphologicalReconstruction;

/**
 * @author dlegland
 *
 */
public class MorphologicalReconstruction3DHybridTest
{
    /**
     * Test method for
     * {@link net.sci.image.morphology.reconstruct.MorphologicalReconstruction3DHybrid#process(net.sci.array.scalar.ScalarArray3D, net.sci.array.scalar.ScalarArray3D)}.
     */
    @Test
    public final void testProcess_ByDilation_C6_CubicMesh()
    {
        UInt8Array3D mask = createCubicMeshImage();
        UInt8Array3D marker = UInt8Array3D.create(20, 20, 20);
        marker.setInt(5, 5, 5, 255);
        
        MorphologicalReconstruction3D algo = new MorphologicalReconstruction3DHybrid(Connectivity3D.C6);

        ScalarArray3D<?> result = algo.process(marker, mask);
        
        assertArraysEquals(mask, result);
    }
    
    /**
     * Test method for
     * {@link net.sci.image.morphology.reconstruct.MorphologicalReconstruction3DHybrid#process(net.sci.array.scalar.ScalarArray3D, net.sci.array.scalar.ScalarArray3D)}.
     */
    @Test
    public final void testProcess_ByErosion_C6_CubicMesh()
    {
        UInt8Array3D mask = createCubicMeshImage();
        UInt8Array3D marker = UInt8Array3D.create(20, 20, 20);
        marker.setInt(5, 5, 5, 255);
        invertUInt8Array3D(mask);
        invertUInt8Array3D(marker);
        
        MorphologicalReconstruction3D algo = new MorphologicalReconstruction3DHybrid(
                MorphologicalReconstruction.Type.BY_EROSION, Connectivity3D.C6);
        
        ScalarArray3D<?> result = algo.process(marker, mask);
        
        assertArraysEquals(mask, result);
    }

    /**
     * Test method for
     * {@link net.sci.image.morphology.reconstruct.MorphologicalReconstruction3DHybrid#process(net.sci.array.scalar.ScalarArray3D, net.sci.array.scalar.ScalarArray3D)}.
     */
    @Test
    public final void testProcess_ByDilation_C6_ThinCubicMesh()
    {
        UInt8Array3D mask = createThinCubicMeshImage();
        UInt8Array3D marker = UInt8Array3D.create(5, 5, 5);
        marker.setInt(0, 0, 0, 255);
        
        MorphologicalReconstruction3D algo = new MorphologicalReconstruction3DHybrid(Connectivity3D.C6);

        ScalarArray3D<?> result = algo.process(marker, mask);
        
        assertArraysEquals(mask, result);
    }
    
    /**
     * Test method for
     * {@link net.sci.image.morphology.reconstruct.MorphologicalReconstruction3DHybrid#process(net.sci.array.scalar.ScalarArray3D, net.sci.array.scalar.ScalarArray3D)}.
     */
    @Test
    public final void testProcess_ByDilation_C26_ThinCubicMesh()
    {
        UInt8Array3D mask = createThinCubicMeshImage();
        UInt8Array3D marker = UInt8Array3D.create(5, 5, 5);
        marker.setInt(0, 0, 0, 255);
        
        MorphologicalReconstruction3D algo = new MorphologicalReconstruction3DHybrid(Connectivity3D.C26);

        ScalarArray3D<?> result = algo.process(marker, mask);
        
        assertArraysEquals(mask, result);
    }
    
    /**
     * Test method for
     * {@link net.sci.image.morphology.reconstruct.MorphologicalReconstruction3DHybrid#process(net.sci.array.scalar.ScalarArray3D, net.sci.array.scalar.ScalarArray3D)}.
     */
    @Test
    public final void testProcess_ByErosion_C6_ThinCubicMesh()
    {
        UInt8Array3D mask = createThinCubicMeshImage();
        UInt8Array3D marker = UInt8Array3D.create(5, 5, 5);
        marker.setInt(0, 0, 0, 255);
        invertUInt8Array3D(mask);
        invertUInt8Array3D(marker);
        
        MorphologicalReconstruction3D algo = new MorphologicalReconstruction3DHybrid(
                MorphologicalReconstruction.Type.BY_EROSION, Connectivity3D.C6);
        
        ScalarArray3D<?> result = algo.process(marker, mask);
        
        assertArraysEquals(mask, result);
    }

    /**
     * Test method for
     * {@link net.sci.image.morphology.reconstruct.MorphologicalReconstruction3DHybrid#process(net.sci.array.scalar.ScalarArray3D, net.sci.array.scalar.ScalarArray3D)}.
     */
    @Test
    public final void testProcess_ByErosion_C26_ThinCubicMesh()
    {
        UInt8Array3D mask = createThinCubicMeshImage();
        UInt8Array3D marker = UInt8Array3D.create(5, 5, 5);
        marker.setInt(0, 0, 0, 255);
        invertUInt8Array3D(mask);
        invertUInt8Array3D(marker);
        
        MorphologicalReconstruction3D algo = new MorphologicalReconstruction3DHybrid(
                MorphologicalReconstruction.Type.BY_EROSION, Connectivity3D.C26);
        
        ScalarArray3D<?> result = algo.process(marker, mask);
        
        assertArraysEquals(mask, result);
    }


    private UInt8Array3D createCubicMeshImage() 
    {
        int sizeX = 20;
        int sizeY = 20;
        int sizeZ = 20;
        
        // create empty stack
        UInt8Array3D stack = UInt8Array3D.create(sizeX, sizeY, sizeZ);
        
        // number of voxels between edges and 'tube' borders 
        int gap = 2;

        // First, the edges in the x direction
        for (int z = 5 - gap - 1; z <= 5 + gap + 1; z++)
        {
            for (int y = 5 - gap - 1; y <= 5 + gap + 1; y++)
            {
                for (int x = 5 - gap - 1; x <= 15 + gap + 1; x++)
                {
                    stack.setValue(x, y, z, 255);
                    stack.setValue(x, y, z + 10, 255);
                }
            }
        }

        // then, the edges in the y direction
        for (int z = 5 - gap - 1; z <= 5 + gap + 1; z++)
        {
            for (int x = 5 - gap - 1; x <= 5 + gap + 1; x++)
            {
                for (int y = 5 - gap - 1; y <= 15 + gap + 1; y++)
                {
                    stack.setValue(x + 10, y, z, 255);
                    stack.setValue(x, y, z + 10, 255);
                    stack.setValue(x + 10, y, z + 10, 255);
                }
            }
        }

        // Finally, the edges in the z direction
        for (int y = 5 - gap - 1; y <= 5 + gap + 1; y++)
        {
            for (int x = 5 - gap - 1; x <= 5 + gap + 1; x++)
            {
                for (int z = 5 - gap - 1; z <= 15 + gap + 1; z++)
                {
                    stack.setValue(x, y + 10, z, 255);
                    stack.setValue(x + 10, y + 10, z, 255);
                }               
            }
        }
        
        return stack;
    }
    
    /**
     * Creates an image of cube edges, similar to this:  
     * 
     *    *---------*
     *   **        **
     *  * *       * *
     * ***********  *
     * |  *      |  *
     * |  *------|--* 
     * | /       | *
     * |/        |*
     * ***********
     * 
     * Typical planes are as follow:
     *   z = 0        z=1,2,3       z = 4
     *  X * * * *    . . . . .    * * * * *
     *  . . . . *    . . . . .    * . . . *
     *  . . . . *    . . . . .    * . . . *
     *  . . . . *    . . . . .    * . . . *
     *  Z . . . *    * . . . *    * . . . *
     *  
     *  (reconstruction starts from the X, and terminates at the Z)
     */
    private UInt8Array3D createThinCubicMeshImage() {
        int sizeX = 5;
        int sizeY = 5;
        int sizeZ = 5;
        
        // create empty stack
        UInt8Array3D stack = UInt8Array3D.create(sizeX, sizeY, sizeZ);
        
        // First, the two edges in the x direction
        for (int x = 0; x < 5; x++) {
            stack.setValue(x, 0, 0, 255);
            stack.setValue(x, 0, 4, 255);
        }               
        
        // then, the three edges in the y direction
        for (int y = 0; y < 5; y++) {
            stack.setValue(4, y, 0, 255);
            stack.setValue(0, y, 4, 255);
            stack.setValue(4, y, 4, 255);
        }               

        // Finally, the two edges in the z direction
        for (int z = 0; z < 5; z++) {
            stack.setValue(0, 4, z, 255);
            stack.setValue(4, 4, z, 255);
        }
        
        return stack;
    }
    
    private final void invertUInt8Array3D(UInt8Array3D image)
    {
        int sizeX = image.size(0);
        int sizeY = image.size(1);
        int sizeZ = image.size(2);
        
        for (int z = 0; z < sizeZ; z++)
        {
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    image.setInt(x, y, z, 255 - image.getInt(x, y, z));
                }
            }
        }
    }
    
    private final void assertArraysEquals(ScalarArray3D<?> image, ScalarArray3D<?> image2)
    {
        int sizeX = image.size(0);
        int sizeY = image.size(1);
        int sizeZ = image.size(2);
        
        assertEquals(sizeX, image2.size(0));
        assertEquals(sizeY, image2.size(1));
        assertEquals(sizeZ, image2.size(2));
        
        for (int z = 0; z < sizeZ; z++)
        {
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    assertEquals(image.getValue(x, y, z), image2.getValue(x, y, z), .01);
                }
            }
        }
    }
    
}
