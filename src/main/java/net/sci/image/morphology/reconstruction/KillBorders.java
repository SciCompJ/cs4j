/**
 * 
 */
package net.sci.image.morphology.reconstruction;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.ScalarArray3D;
import net.sci.image.morphology.MorphologicalReconstruction;

/**
 * Kill Borders of (scalar) array using reconstruction (by dilation) algorithm.
 *
 * @see BinaryKillBorders
 * 
 * @author dlegland
 *
 */
public class KillBorders extends AlgoStub implements ArrayOperator
{
    /**
     * Removes the border of the input 2D array. The principle is to perform a
     * morphological reconstruction by dilation initialized with image boundary.
     * 
     * @param array
     *            the image to process
     * @return a new image with borders removed
     */
    public ScalarArray2D<?> processScalar2d(ScalarArray2D<?> array) 
    {
        // Image size
        int sizeX = array.size(0);
        int sizeY = array.size(1);

        // Initialize marker image with zeros everywhere except at borders
        this.fireStatusChanged(this, "Initialize marker");
        ScalarArray2D<?> marker = array.duplicate();
        for (int y = 1; y < sizeY - 1; y++)
        {
            for (int x = 1; x < sizeX - 1; x++)
            {
                marker.setValue(x, y, Double.NEGATIVE_INFINITY);
            }
        }
        
        // Reconstruct image from borders to find touching structures
        this.fireStatusChanged(this, "Reconstruction");
        ScalarArray2D<?> result = MorphologicalReconstruction.reconstructByDilation(marker, array);
        
        // removes result from original image
        this.fireStatusChanged(this, "Finalize result");
        for (int y = 0; y < sizeY; y++) 
        {
            for (int x = 0; x < sizeX; x++) 
            {
                double val = array.getValue(x, y) - result.getValue(x, y);
                result.setValue(x, y, Math.max(val, 0));
            }
        }
        
        return result;
    }

    /**
     * Removes the border of the input 3D array. The principle is to perform a
     * morphological reconstruction by dilation initialized with image boundary.
     * 
     * @see #fillHoles(ScalarArray3D)
     * @see #processScalar2d(ScalarArray2D)
     * 
     * @param array
     *            the image to process
     * @return a new image with borders removed
     */
    public ScalarArray3D<?> processScalar3d(ScalarArray3D<?> array) 
    {
        // Image size
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);

        // Initialize marker image with zeros everywhere except at borders
        this.fireStatusChanged(this, "Initialize marker");
        ScalarArray3D<?> marker = array.duplicate();
        for (int z = 1; z < sizeZ-1; z++) 
        {
            for (int y = 1; y < sizeY-1; y++) 
            {
                for (int x = 1; x < sizeX-1; x++) 
                {
                    marker.setValue(x, y, z, Double.NEGATIVE_INFINITY);
                }
            }
        }
        
        // Reconstruct image from borders to find touching structures
        this.fireStatusChanged(this, "Reconstruction");
        ScalarArray3D<?> result = MorphologicalReconstruction.reconstructByDilation(marker, array);
        
        // removes result from original image
        this.fireStatusChanged(this, "Finalize result");
        for (int z = 0; z < sizeZ; z++) 
        {
            for (int y = 0; y < sizeY; y++) 
            {
                for (int x = 0; x < sizeX; x++) 
                {
                    double val = array.getValue(x, y, z) - result.getValue(x, y, z);
                    result.setValue(x, y, z, Math.max(val, 0));
                }
            }
        }       
        return result;
    }

    @Override
    public <T> ScalarArray<?> process(Array<T> array)
    {
        if (array instanceof ScalarArray && array.dimensionality() == 2)
        {
            return processScalar2d(ScalarArray2D.wrap((ScalarArray<?>) array));
        }
        else if (array instanceof ScalarArray && array.dimensionality() == 3)
        {
            return processScalar3d(ScalarArray3D.wrap((ScalarArray<?>) array));
        }
        else
        {
            throw new RuntimeException("Requires an instance of BinaryArray2D");
        }
    }
    
    public boolean canProcess(Array<?> array)
    {
        return array instanceof ScalarArray<?> && (array.dimensionality() == 2 || array.dimensionality() == 3);
    }
}
