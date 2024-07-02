/**
 * 
 */
package net.sci.image.morphology.strel;

import net.sci.algo.AlgoStub;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.ScalarArray3D;

/**
 * Utility class to wrap a 2D structuring element into a 2D structuring element.
 * This allows to apply morphological operations based on 2D structuring
 * elements also on 3D images.
 * 
 * @author dlegland
 *
 */
public class Strel2DWrapper extends AlgoStub implements Strel3D
{
    Strel2D strel2d;

    public Strel2DWrapper(Strel2D strel2d)
    {
        this.strel2d = strel2d;
    }
    
    
    @Override
    public int[] size()
    {
        int[] size2d = strel2d.size();
        int[] size = new int[3];
        size[0] = size2d[0];
        size[1] = size2d[1];
        size[2] = 1;
        return size;
    }

    @Override
    public BinaryArray3D binaryMask()
    {
        int[] size = strel2d.size();
        BinaryArray2D mask2d = strel2d.binaryMask();
        BinaryArray3D mask = BinaryArray3D.create(size[0], size[1], 1);
        for (int y = 0; y < size[1]; y++)
        {
            for (int x = 0; x < size[0]; x++)
            {
                mask.setBoolean(x, y, 0, mask2d.getBoolean(x,y));
            }
        }
        return mask;
    }

    @Override
    public int[] maskOffset()
    {
        int[] offset2d = strel2d.maskOffset();
        int[] offset = new int[3];
        offset[0] = offset2d[0];
        offset[1] = offset2d[1];
        offset[2] = 0;
        return offset;
    }

    @Override
    public int[][] shifts()
    {
        int[][] shifts2d = strel2d.shifts();
        int n = shifts2d.length;
        int[][] shifts = new int[n][3];
        for (int i = 0; i < n; i++)
        {
            shifts[i][0] = shifts2d[i][0];
            shifts[i][1] = shifts2d[i][1];
            shifts[i][0] = 0;
        }
        return shifts;
    }

    @Override
    public Strel3D reverse()
    {
        return new Strel2DWrapper(strel2d.reverse());
    }

    @Override
    public ScalarArray3D<?> dilation(ScalarArray3D<?> array)
    {
        ScalarArray3D<?> result = array.duplicate();
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
        for (int z = 0; z < sizeZ; z++)
        {
            // perform operation on current slice
            this.fireProgressChanged(this, z, sizeZ);
            ScalarArray2D<?> resZ = strel2d.dilation(array.slice(z));
            
            // copy 2D result into 3D array
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    result.setValue(x, y, z, resZ.getValue(x, y));
                }
            }
        }
        
        this.fireProgressChanged(this, 1, 1);
        return result;
    }

    @Override
    public ScalarArray3D<?> erosion(ScalarArray3D<?> array)
    {
        ScalarArray3D<?> result = array.duplicate();
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
        for (int z = 0; z < sizeZ; z++)
        {
            // perform operation on current slice
            this.fireProgressChanged(this, z, sizeZ);
            ScalarArray2D<?> resZ = strel2d.erosion(array.slice(z));
            
            // copy 2D result into 3D array
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    result.setValue(x, y, z, resZ.getValue(x, y));
                }
            }
        }
        
        this.fireProgressChanged(this, 1, 1);
        return result;
    }
}
