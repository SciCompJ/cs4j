/**
 * 
 */
package net.sci.image.morphology.strel;

import net.sci.algo.AlgoStub;
import net.sci.array.scalar.ScalarArray2D;
import net.sci.array.scalar.ScalarArray3D;

/**
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
    public int[][][] getMask()
    {
        int[] size = strel2d.size();
        int[][] mask2d = strel2d.getMask();
        int[][][] mask = new int[1][size[1]][size[0]];
        for (int y = 0; y < size[1]; y++)
        {
            for (int x = 0; x < size[0]; x++)
            {
                mask[0][y][x] = mask2d[y][x];  
            }
        }
        return mask;
    }

    @Override
    public int[] getOffset()
    {
        int[] offset2d = strel2d.getOffset();
        int[] offset = new int[3];
        offset[0] = offset2d[0];
        offset[1] = offset2d[1];
        offset[2] = 0;
        return offset;
    }

    @Override
    public int[][] getShifts()
    {
        int[][] shifts2d = strel2d.getShifts();
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
                    result.setValue(resZ.getValue(x, y), x, y, z);
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
                    result.setValue(resZ.getValue(x, y), x, y, z);
                }
            }
        }
        
        this.fireProgressChanged(this, 1, 1);
        return result;
    }
}
