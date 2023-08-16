/**
 * 
 */
package net.sci.image.binary.distmap;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.binary.Binary;
import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.scalar.Float32Array;
import net.sci.array.scalar.Scalar;
import net.sci.array.scalar.ScalarArray;
import net.sci.array.scalar.ScalarArray2D;

/**
 * @author dlegland
 *
 */
public class DepthMap3D extends AlgoStub implements ArrayOperator
{
    int dirIndex;
    
    /**
     * The factory used to create output array. Initialized with a Float32 array factory.
     */
    ScalarArray.Factory<? extends Scalar> factory = Float32Array.defaultFactory;

    
    public DepthMap3D()
    {
        this(0);
    }
    
    public DepthMap3D(int dirIndex)
    {
        this.dirIndex = dirIndex;
    }

    @Override
    public <T> ScalarArray<?> process(Array<T> array)
    {
        // check conditions
        if (array.dimensionality() != 3)
        {
            throw new RuntimeException("Requires array with dimensionality 3");
        }
        if (array.dataType() != Binary.class)
        {
            throw new RuntimeException("Requires a binary array");
        }
        
        BinaryArray3D binary = BinaryArray3D.wrap(BinaryArray.wrap(array));
        
        // retrieve image size
        int sizeX = binary.size(0);
        int sizeY = binary.size(1);
        int sizeZ = binary.size(2);

        ScalarArray2D<?> res;
        switch (dirIndex)
        {
            case 0:
            {
                res = ScalarArray2D.wrapScalar2d(this.factory.create(sizeY, sizeZ));
                for (int z = 0; z < sizeZ; z++)
                {
                    for (int y = 0; y < sizeY; y++)
                    {
                        double val = Double.POSITIVE_INFINITY;
                        for (int x = 0; x < sizeX; x++)
                        {
                            if (binary.getBoolean(x, y, z))
                            {
                                val = x;
                                break;
                            }
                        }
                        res.setValue(y, z, val);
                    }
                }
                return res;
            }   
            case 1:
            {
                res = ScalarArray2D.wrapScalar2d(this.factory.create(sizeX, sizeZ));
                for (int z = 0; z < sizeZ; z++)
                {
                    for (int x = 0; x < sizeX; x++)
                    {
                        double val = Double.POSITIVE_INFINITY;
                        for (int y = 0; y < sizeY; y++)
                        {
                            if (binary.getBoolean(x, y, z))
                            {
                                val = y;
                                break;
                            }
                        }
                        res.setValue(x, z, val);
                    }
                }
                return res;
            }
            case 2:
            {
                res = ScalarArray2D.wrapScalar2d(this.factory.create(sizeX, sizeY));
                for (int y = 0; y < sizeY; y++)
                {
                    for (int x = 0; x < sizeX; x++)
                    {
                        double val = Double.POSITIVE_INFINITY;
                        for (int z = 0; z < sizeZ; z++)
                        {
                            if (binary.getBoolean(x, y, z))
                            {
                                val = z;
                                break;
                            }
                        }
                        res.setValue(x, y, val);
                    }
                }
                return res;
            }
            default:
                throw new RuntimeException("Direction index not managed: " + dirIndex);
        }
        
    }
    
    public boolean canProcess(Array<?> array)
    {
        if (array.dimensionality() != 3) return false;
        if (array.dataType() != Binary.class) return false;
        return true;
    }
}
