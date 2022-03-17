/**
 * 
 */
package net.sci.array.process.shape;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.Array2D;
import net.sci.array.Array3D;
import net.sci.array.ArrayOperator;

/**
 * Duplicates an array, providing monitoring facilities.
 * 
 * @author dlegland
 *
 */
public class Duplicate extends AlgoStub implements ArrayOperator
{

    /**
     * Default empty constructor.
     */
    public Duplicate()
    {
    }

    @Override
    public <T> Array<?> process(Array<T> array)
    {
        if (array.dimensionality() == 2)
        {
            return process2d(Array2D.wrap(array));
        }
        else if (array.dimensionality() == 3)
        {
            return process3d(Array3D.wrap(array));
        }
        else
        {
            return array.duplicate();
        }
    }
    
    private <T> Array2D<T> process2d(Array2D<T> array)
    {
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        Array2D<T> res = Array2D.wrap(array.newInstance(sizeX, sizeY));
        for (int y = 0; y < sizeY; y++)
        {
            this.fireProgressChanged(this, y, sizeY);
            for (int x = 0; x < sizeX; x++)
            {
                res.set(x, y, array.get(x, y));
            }
        }
        return res;
    }

    private <T> Array3D<T> process3d(Array3D<T> array)
    {
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
        Array3D<T> res = Array3D.wrap(array.newInstance(sizeX, sizeY, sizeZ));
        for (int z = 0; z < sizeZ; z++)
        {
            this.fireProgressChanged(this, z, sizeZ);
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    res.set(x, y, z, array.get(x, y, z));
                }
            }
        }
        return res;
    }
}
