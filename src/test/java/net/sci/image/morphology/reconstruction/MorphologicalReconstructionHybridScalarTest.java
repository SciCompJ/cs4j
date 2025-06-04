/**
 * 
 */
package net.sci.image.morphology.reconstruction;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.numeric.Float32Array2D;
import net.sci.array.numeric.ScalarArray3D;
import net.sci.array.numeric.UInt8Array2D;
import net.sci.array.numeric.UInt8Array3D;
import net.sci.image.connectivity.Connectivity2D;
import net.sci.image.connectivity.Connectivity3D;
import net.sci.image.morphology.MorphologicalReconstruction;

/**
 * 
 */
public class MorphologicalReconstructionHybridScalarTest
{
    @Test
    public void testReconstructByDilation_C4_SShape()
    {
        UInt8Array2D mask = create_UInt8Array2D_SShape();
        UInt8Array2D marker = UInt8Array2D.create(mask.size(0), mask.size(1));
        marker.setInt(2, 3, 255);

        MorphologicalReconstructionHybridScalar algo = new MorphologicalReconstructionHybridScalar(
                MorphologicalReconstruction.Type.BY_DILATION, Connectivity2D.C4);

        UInt8Array2D result = (UInt8Array2D) algo.process(marker, mask);
//        result.printContent();

        assertEquals(mask.size(0), result.size(0));
        assertEquals(mask.size(1), result.size(1));

        assertEquals(255, result.getInt(2, 8));
        assertEquals(255, result.getInt(8, 8));
        assertEquals(255, result.getInt(8, 5));
        assertEquals(255, result.getInt(14, 8));

        assertEquals(0, result.getInt(15, 9));
        assertEquals(0, result.getInt(0, 0));
        assertEquals(0, result.getInt(5, 3));
        assertEquals(0, result.getInt(11, 5));
    }

    @Test
    public void testReconstructByDilation_C8_SShape()
    {
        UInt8Array2D mask = create_UInt8Array2D_SShape();
        UInt8Array2D marker = UInt8Array2D.create(mask.size(0), mask.size(1));
        marker.setInt(2, 3, 255);

        MorphologicalReconstructionHybridScalar algo = new MorphologicalReconstructionHybridScalar(
                MorphologicalReconstruction.Type.BY_DILATION, Connectivity2D.C8);

        UInt8Array2D result = (UInt8Array2D) algo.process(marker, mask);
//        result.printContent();

        assertEquals(mask.size(0), result.size(0));
        assertEquals(mask.size(1), result.size(1));

        assertEquals(255, result.getInt(2, 8));
        assertEquals(255, result.getInt(8, 8));
        assertEquals(255, result.getInt(8, 5));
        assertEquals(255, result.getInt(14, 8));

        assertEquals(0, result.getInt(15, 9));
        assertEquals(0, result.getInt(0, 0));
        assertEquals(0, result.getInt(5, 3));
        assertEquals(0, result.getInt(11, 5));
    }

    @Test
    public void testReconstructByErosion_C4_SShape()
    {
        UInt8Array2D mask = create_UInt8Array2D_SShape();
        mask.fillInts((x,y) -> 255 - mask.getInt(x,y));
        UInt8Array2D marker = UInt8Array2D.create(mask.size(0), mask.size(1));
        marker.fillInt(255);
        marker.setInt(2, 3, 0);

        MorphologicalReconstructionHybridScalar algo = new MorphologicalReconstructionHybridScalar(
                MorphologicalReconstruction.Type.BY_EROSION, Connectivity2D.C4);

        UInt8Array2D result = (UInt8Array2D) algo.process(marker, mask);
//        result.printContent();

        assertEquals(mask.size(0), result.size(0));
        assertEquals(mask.size(1), result.size(1));

        assertEquals(0, result.getInt(2, 8));
        assertEquals(0, result.getInt(8, 8));
        assertEquals(0, result.getInt(8, 5));
        assertEquals(0, result.getInt(14, 8));
        
        assertEquals(255, result.getInt(15, 9));
        assertEquals(255, result.getInt(0, 0));
        assertEquals(255, result.getInt(5, 3));
        assertEquals(255, result.getInt(11, 5));
    }

    @Test
    public void testReconstructByErosion_C8_SShape()
    {
        UInt8Array2D mask = create_UInt8Array2D_SShape();
        mask.fillInts((x,y) -> 255 - mask.getInt(x,y));
        UInt8Array2D marker = UInt8Array2D.create(mask.size(0), mask.size(1));
        marker.fillInt(255);
        marker.setInt(2, 3, 0);

        MorphologicalReconstructionHybridScalar algo = new MorphologicalReconstructionHybridScalar(
                MorphologicalReconstruction.Type.BY_EROSION, Connectivity2D.C8);

        UInt8Array2D result = (UInt8Array2D) algo.process(marker, mask);
//        result.printContent();

        assertEquals(mask.size(0), result.size(0));
        assertEquals(mask.size(1), result.size(1));

        assertEquals(0, result.getInt(2, 8));
        assertEquals(0, result.getInt(8, 8));
        assertEquals(0, result.getInt(8, 5));
        assertEquals(0, result.getInt(14, 8));
        
        assertEquals(255, result.getInt(15, 9));
        assertEquals(255, result.getInt(0, 0));
        assertEquals(255, result.getInt(5, 3));
        assertEquals(255, result.getInt(11, 5));
    }

    @Test
    public void testReconstructByDilation_Float32_C4_SShape()
    {
        UInt8Array2D mask8 = create_UInt8Array2D_SShape();
        Float32Array2D mask = Float32Array2D.create(mask8.size(0), mask8.size(1));
        mask.fillValues((x,y) -> mask8.getInt(x,y) > 0 ? 15.5 : -12.2);
        
        Float32Array2D marker = Float32Array2D.create(mask.size(0), mask.size(1));
        marker.fillValue(Double.NEGATIVE_INFINITY);
        marker.setValue(2, 3, 15.5);

        MorphologicalReconstructionHybridScalar algo = new MorphologicalReconstructionHybridScalar(
                MorphologicalReconstruction.Type.BY_DILATION, Connectivity2D.C4);

        Float32Array2D result = (Float32Array2D) algo.process(marker, mask);
//        result.printContent(System.out, "%6.2f");

        assertEquals(mask.size(0), result.size(0));
        assertEquals(mask.size(1), result.size(1));

        assertEquals(15.5, result.getValue(2, 8), 0.01);
        assertEquals(15.5, result.getValue(8, 8), 0.01);
        assertEquals(15.5, result.getValue(8, 5), 0.01);
        assertEquals(15.5, result.getValue(14, 8), 0.01);

        assertEquals(-12.2, result.getValue(15, 9), 0.01);
        assertEquals(-12.2, result.getValue(0, 0), 0.01);
        assertEquals(-12.2, result.getValue(5, 3), 0.01);
        assertEquals(-12.2, result.getValue(11, 5), 0.01);
    }

    @Test
    public void testReconstructByErosion_Float32_C4_SShape()
    {
        UInt8Array2D mask8 = create_UInt8Array2D_SShape();
        Float32Array2D mask = Float32Array2D.create(mask8.size(0), mask8.size(1));
        mask.fillValues((x,y) -> mask8.getInt(x,y) > 0 ? -12.2 : 15.5);
        
        Float32Array2D marker = Float32Array2D.create(mask.size(0), mask.size(1));
        marker.fillValue(Double.POSITIVE_INFINITY);
        marker.setValue(2, 3, -12.2);

        MorphologicalReconstructionHybridScalar algo = new MorphologicalReconstructionHybridScalar(
                MorphologicalReconstruction.Type.BY_EROSION, Connectivity2D.C4);

        Float32Array2D result = (Float32Array2D) algo.process(marker, mask);
//        result.printContent(System.out, "%6.2f");

        assertEquals(mask.size(0), result.size(0));
        assertEquals(mask.size(1), result.size(1));

        assertEquals(-12.2, result.getValue(2, 8), 0.01);
        assertEquals(-12.2, result.getValue(8, 8), 0.01);
        assertEquals(-12.2, result.getValue(8, 5), 0.01);
        assertEquals(-12.2, result.getValue(14, 8), 0.01);

        assertEquals(15.5, result.getValue(15, 9), 0.01);
        assertEquals(15.5, result.getValue(0, 0), 0.01);
        assertEquals(15.5, result.getValue(5, 3), 0.01);
        assertEquals(15.5, result.getValue(11, 5), 0.01);
    }
    
    @Test
    public void testProcess_ByDilation_C6_ThinCubicMesh()
    {
        UInt8Array3D mask = create_UInt8Array3D_thinCubicMesh();
        UInt8Array3D marker = UInt8Array3D.create(5, 5, 5);
        marker.setInt(0, 0, 0, 255);
        
        MorphologicalReconstruction3D algo = new MorphologicalReconstruction3DHybrid(Connectivity3D.C6);

        ScalarArray3D<?> result = algo.process(marker, mask);
        
        assertArraysEquals(mask, result);
    }
    
    @Test
    public void testProcess_ByDilation_C26_ThinCubicMesh()
    {
        UInt8Array3D mask = create_UInt8Array3D_thinCubicMesh();
        UInt8Array3D marker = UInt8Array3D.create(5, 5, 5);
        marker.setInt(0, 0, 0, 255);
        
        MorphologicalReconstruction3D algo = new MorphologicalReconstruction3DHybrid(Connectivity3D.C26);

        ScalarArray3D<?> result = algo.process(marker, mask);
        
        assertArraysEquals(mask, result);
    }
    
    private static final void assertArraysEquals(ScalarArray3D<?> image, ScalarArray3D<?> image2)
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

    private static final UInt8Array2D create_UInt8Array2D_SShape()
    {
        int BG = 0;
        int FG = 255;
        int[][] data = new int[][] {
            { BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG },
            { BG, FG, FG, BG, FG, FG, BG, FG, FG, FG, FG, FG, FG, FG, FG, BG },
            { BG, FG, FG, BG, FG, FG, BG, FG, FG, FG, FG, FG, FG, FG, FG, BG },
            { BG, FG, FG, BG, FG, FG, BG, FG, FG, BG, BG, BG, BG, FG, FG, BG },
            { BG, FG, FG, BG, FG, FG, BG, FG, FG, BG, FG, FG, BG, FG, FG, BG },
            { BG, FG, FG, BG, FG, FG, BG, FG, FG, BG, FG, FG, BG, FG, FG, BG },
            { BG, FG, FG, BG, BG, BG, BG, FG, FG, BG, FG, FG, BG, FG, FG, BG },
            { BG, FG, FG, FG, FG, FG, FG, FG, FG, BG, FG, FG, BG, FG, FG, BG },
            { BG, FG, FG, FG, FG, FG, FG, FG, FG, BG, FG, FG, BG, FG, FG, BG },
            { BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG }
        };
        return UInt8Array2D.fromIntArray(data);
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
    private static final UInt8Array3D create_UInt8Array3D_thinCubicMesh()
    {
        int sizeX = 5;
        int sizeY = 5;
        int sizeZ = 5;
        // create empty stack
        UInt8Array3D stack = UInt8Array3D.create(sizeX, sizeY, sizeZ);

        for (int i = 0; i < 5; i++)
        {
            // First, the two edges in the x direction
            stack.setValue(i, 0, 0, 255);
            stack.setValue(i, 0, 4, 255);
            
            // then, the three edges in the y direction
            stack.setValue(4, i, 0, 255);
            stack.setValue(0, i, 4, 255);
            stack.setValue(4, i, 4, 255);

            // Finally, the two edges in the z direction
            stack.setValue(0, 4, i, 255);
            stack.setValue(4, 4, i, 255);
        }

        return stack;
    }

}
