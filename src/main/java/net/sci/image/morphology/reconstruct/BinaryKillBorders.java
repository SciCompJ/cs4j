/**
 * 
 */
package net.sci.image.morphology.reconstruct;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.binary.BinaryArray3D;

/**
 * Specialization of KillBorders algorithm for binary images.
 * 
 * @see KillBorders
 * 
 * @author dlegland
 *
 */
public class BinaryKillBorders extends AlgoStub implements ArrayOperator
{
    public BinaryArray2D processBinary2d(BinaryArray2D array)
    {
        // Image size
        int sizeX = array.size(0);
        int sizeY = array.size(1);

        // copy borders of mask array into marker array
        BinaryArray2D marker = BinaryArray2D.wrap(array.newInstance(sizeX, sizeY));
        for (int x = 0; x < sizeX; x++)
        {
            marker.setBoolean(x, 0, array.getBoolean(x, 0));
        }
        for (int y = 1; y < sizeY - 1; y++)
        {
            marker.setBoolean(0, y, array.getBoolean(0, y));
            marker.setBoolean(sizeX - 1, y, array.getBoolean(sizeX - 1, y));
        }
        for (int x = 0; x < sizeX; x++)
        {
            marker.setBoolean(x, sizeY - 1, array.getBoolean(x, sizeY - 1));
        }
        
        // Reconstruct image from borders to find touching structures
        RunLengthBinaryReconstruction2D algo = new RunLengthBinaryReconstruction2D(); 
        BinaryArray2D result = algo.processBinary2d(marker, array);
        
        // removes result from original image
        for (int y = 0; y < sizeY; y++) 
        {
            for (int x = 0; x < sizeX; x++) 
            {
                boolean b = array.getBoolean(x, y) && !result.getBoolean(x, y);
                result.setBoolean(x, y, b);
            }
        }
        
        return result;
    }
    
    public BinaryArray3D processBinary3d(BinaryArray3D array)
    {
        // Image size
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);

        BinaryArray3D marker = BinaryArray3D.wrap(array.newInstance(sizeX, sizeY, sizeZ));
        // copy borders of mask array into marker array
        // first plane
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                marker.setBoolean(x, y, 0, array.getBoolean(x, y, 0));
            }
        }
        // "middle" planes
        for (int z = 1; z < sizeZ - 1; z++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                marker.setBoolean(x, 0, z, array.getBoolean(x, 0, z));
            }
            for (int y = 1; y < sizeY - 1; y++)
            {
                marker.setBoolean(0, y, z, array.getBoolean(0, y, z));
                marker.setBoolean(sizeX - 1, y, z, array.getBoolean(sizeX - 1, y, z));
            }
            for (int x = 0; x < sizeX; x++)
            {
                marker.setBoolean(x, sizeY - 1, z, array.getBoolean(x, sizeY - 1, z));
            }
        }
        // last plane
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                marker.setBoolean(x, y, sizeZ - 1, array.getBoolean(x, y, sizeZ - 1));
            }
        }

        // Reconstruct image from borders to find touching structures
        RunLengthBinaryReconstruction3D algo = new RunLengthBinaryReconstruction3D(); 
        BinaryArray3D result = algo.processBinary3d(marker, array);
        
        // removes result from original image
        for (int z = 0; z < sizeZ; z++) 
        {
            for (int y = 0; y < sizeY; y++) 
            {
                for (int x = 0; x < sizeX; x++) 
                {
                    boolean b = array.getBoolean(x, y, z) && !result.getBoolean(x, y, z);
                    result.setBoolean(x, y, z, b);
                }
            }
        }        
        return result;
    }

    @Override
    public <T> BinaryArray process(Array<T> array)
    {
        if (array instanceof BinaryArray && array.dimensionality() == 2)
        {
            return processBinary2d(BinaryArray2D.wrap((BinaryArray) array));
        }
        else if (array instanceof BinaryArray && array.dimensionality() == 3)
        {
            return processBinary3d(BinaryArray3D.wrap((BinaryArray) array));
        }
        else
        {
            throw new RuntimeException("Requires an instance of BinaryArray2D");
        }
    }
    
    public boolean canProcess(Array<?> array)
    {
        return array instanceof BinaryArray && (array.dimensionality() == 2 || array.dimensionality() == 3);
    }
}
