/**
 * 
 */
package net.sci.image.morphology.reconstruct;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.binary.BinaryArray3D;
import net.sci.array.binary.RunLengthBinaryArray3D;

/**
 * @author dlegland
 *
 */
public class RunLengthBinaryReconstruction3DTest
{
    /**
     * Test method for
     * {@link net.sci.image.morphology.reconstruct.MorphologicalReconstruction3DHybrid#process(net.sci.array.scalar.ScalarArray3D, net.sci.array.scalar.ScalarArray3D)}.
     */
    @Test
    public final void testProcess_ByDilation_C6_CubicMesh()
    {
        BinaryArray3D mask = createCubicMeshImage();
        BinaryArray3D marker = BinaryArray3D.create(20, 20, 20);
        marker.setBoolean(5, 5, 5, true);
        
        RunLengthBinaryReconstruction3D algo = new RunLengthBinaryReconstruction3D(6);

        BinaryArray3D result = algo.processBinary3d(marker, mask);
        
        assertBinaryArraysEquals(mask, result);
    }
    
    /**
     * Test method for
     * {@link net.sci.image.morphology.reconstruct.MorphologicalReconstruction3DHybrid#process(net.sci.array.scalar.ScalarArray3D, net.sci.array.scalar.ScalarArray3D)}.
     */
    @Test
    public final void testProcess_ByDilation_C26_CubicMesh()
    {
        BinaryArray3D mask = createCubicMeshImage();
        BinaryArray3D marker = BinaryArray3D.create(20, 20, 20);
        marker.setBoolean(5, 5, 5, true);
        
        RunLengthBinaryReconstruction3D algo = new RunLengthBinaryReconstruction3D(26);

        BinaryArray3D result = algo.processBinary3d(marker, mask);
        
        assertBinaryArraysEquals(mask, result);
    }
    
    /**
     * Test method for
     * {@link net.sci.image.morphology.reconstruct.MorphologicalReconstruction3DHybrid#process(net.sci.array.scalar.ScalarArray3D, net.sci.array.scalar.ScalarArray3D)}.
     */
    @Test
    public final void testProcess_ByDilation_C6_ThinCubicMesh()
    {
        BinaryArray3D mask = createThinCubicMeshImage();
        BinaryArray3D marker = BinaryArray3D.create(5, 5, 5);
        marker.setInt(0, 0, 0, 255);
        
        RunLengthBinaryReconstruction3D algo = new RunLengthBinaryReconstruction3D();

        BinaryArray3D result = algo.processBinary3d(marker, mask);
        
        assertBinaryArraysEquals(mask, result);
    }
    
    /**
     * Test method for
     * {@link net.sci.image.morphology.reconstruct.MorphologicalReconstruction3DHybrid#process(net.sci.array.scalar.ScalarArray3D, net.sci.array.scalar.ScalarArray3D)}.
     */
    @Test
    public final void testProcess_ByDilation_C26_ThinCubicMesh()
    {
        BinaryArray3D mask = createThinCubicMeshImage();
        BinaryArray3D marker = BinaryArray3D.create(5, 5, 5);
        marker.setInt(0, 0, 0, 255);
        
        RunLengthBinaryReconstruction3D algo = new RunLengthBinaryReconstruction3D(26);

        BinaryArray3D result = algo.processBinary3d(marker, mask);
        
        assertBinaryArraysEquals(mask, result);
    }
    
    /**
     * Test method for
     * {@link net.sci.image.morphology.reconstruct.MorphologicalReconstruction3DHybrid#process(net.sci.array.scalar.ScalarArray3D, net.sci.array.scalar.ScalarArray3D)}.
     */
    @Test
    public final void testProcess_ByDilation_C26_XShapeC26()
    {
        // a marker with a single voxel in the middle
        BinaryArray3D marker = RunLengthBinaryArray3D.create(7, 7, 7);
        marker.setBoolean(3, 3, 3, true);
        // a mask composed of four diagonal lines through the (3,3,3) center
        BinaryArray3D mask = RunLengthBinaryArray3D.create(7, 7, 7);
        for (int z = 1; z <= 5; z++)
        {
            mask.setBoolean(  z,   z, z, true);
            mask.setBoolean(6-z,   z, z, true);
            mask.setBoolean(  z, 6-z, z, true);
            mask.setBoolean(6-z, 6-z, z, true);
        }
        RunLengthBinaryReconstruction3D algo = new RunLengthBinaryReconstruction3D(26);

        BinaryArray3D result = algo.processBinary3d(marker, mask);
        
        assertBinaryArraysEquals(mask, result);
    }
    

    private BinaryArray3D createCubicMeshImage() 
    {
        int sizeX = 20;
        int sizeY = 20;
        int sizeZ = 20;
        
        // create empty stack
        BinaryArray3D stack = BinaryArray3D.create(sizeX, sizeY, sizeZ);
        
        // number of voxels between edges and 'tube' borders 
        int gap = 2;

        // First, the edges in the x direction
        for (int z = 5 - gap - 1; z <= 5 + gap + 1; z++)
        {
            for (int y = 5 - gap - 1; y <= 5 + gap + 1; y++)
            {
                for (int x = 5 - gap - 1; x <= 15 + gap + 1; x++)
                {
                    stack.setBoolean(x, y, z, true);
                    stack.setBoolean(x, y, z + 10, true);
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
                    stack.setBoolean(x + 10, y, z, true);
                    stack.setBoolean(x, y, z + 10, true);
                    stack.setBoolean(x + 10, y, z + 10, true);
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
                    stack.setBoolean(x, y + 10, z, true);
                    stack.setBoolean(x + 10, y + 10, z, true);
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
    private BinaryArray3D createThinCubicMeshImage() 
    {
        int sizeX = 5;
        int sizeY = 5;
        int sizeZ = 5;
        
        // create empty stack
        BinaryArray3D stack = BinaryArray3D.create(sizeX, sizeY, sizeZ);
        
        // First, the two edges in the x direction
        for (int x = 0; x < 5; x++) 
        {
            stack.setBoolean(x, 0, 0, true);
            stack.setBoolean(x, 0, 4, true);
        }               
        
        // then, the three edges in the y direction
        for (int y = 0; y < 5; y++) 
        {
            stack.setBoolean(4, y, 0, true);
            stack.setBoolean(0, y, 4, true);
            stack.setBoolean(4, y, 4, true);
        }               

        // Finally, the two edges in the z direction
        for (int z = 0; z < 5; z++) 
        {
            stack.setBoolean(0, 4, z, true);
            stack.setBoolean(4, 4, z, true);
        }
        
        return stack;
    }
        
    private final void assertBinaryArraysEquals(BinaryArray3D array1, BinaryArray3D array2)
    {
        int sizeX = array1.size(0);
        int sizeY = array1.size(1);
        int sizeZ = array1.size(2);
        
        assertEquals(sizeX, array2.size(0));
        assertEquals(sizeY, array2.size(1));
        assertEquals(sizeZ, array2.size(2));
        
        for (int z = 0; z < sizeZ; z++)
        {
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    assertEquals(array1.getBoolean(x, y, z), array2.getBoolean(x, y, z));
                }
            }
        }
    }
    
}
