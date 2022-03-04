/**
 * 
 */
package net.sci.array.process.type;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.binary.Binary;
import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.scalar.ScalarArray;
import net.sci.array.scalar.ScalarArray2D;
import net.sci.array.scalar.ScalarArray3D;

/**
 * Converts a scalar array to a binary array, by setting to <code>true</code>
 * all the elements whose initial value is strictly greater than zero.
 * 
 * <pre>{@code
    // Create and run operator
    ConvertToBinary algo = new ConvertToBinary();
    BinaryArray result = algo.process(array);
 * }</pre>
 * @author dlegland
 *
 */
public class ConvertToBinary extends AlgoStub implements ArrayOperator
{
    @Override
    public <T> BinaryArray process(Array<T> array)
    {
        if (array.dataType().isAssignableFrom(Binary.class))
        {
            if (array instanceof BinaryArray)
            {
                return (BinaryArray) array;
            }
            return convertArrayOfBinary(array);
        }
        
        // check class
        if (!(array instanceof ScalarArray))
        {
            throw new RuntimeException("Requires a scalar array as input");
        }
        ScalarArray<?> scalarArray = (ScalarArray<?>) array;
        
        // use specialized methods for 2D or 3D arrays
        if (scalarArray.dimensionality() == 2)
        {
            return process2d(ScalarArray2D.wrapScalar2d(scalarArray));
        }
        else if (scalarArray.dimensionality() == 3)
        {
            return process3d(ScalarArray3D.wrapScalar3d(scalarArray));
        }

        // Process generic case
        BinaryArray result = BinaryArray.create(array.size());
        result.fillBooleans(pos -> scalarArray.getValue(pos) > 0);
        return result;
    }
    
    private BinaryArray convertArrayOfBinary(Array<?> array)
    {
        BinaryArray res = BinaryArray.create(array.size());
        for (int[] pos : res.positions())
        {
            res.setBoolean(pos, ((Binary) array.get(pos)).getBoolean());
        }
        return res;           
    }
    
    /**
     * Converts a 2D scalar array to a 2D binary array.
     * 
     * @see #process3d(ScalarArray3D)
     * 
     * @param array
     *            the (scalar) array to convert
     * @return the result binary array
     */
    public BinaryArray2D process2d(ScalarArray2D<?> array)
    {
        // retrieve array dimensions
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        
        // create result array
        BinaryArray2D result = BinaryArray2D.create(sizeX, sizeY);
        
        // iterate over array elements, setting result only for true elements
        // (can save time for RLE arrays)
        for (int y = 0; y < sizeY; y++)
        {
            fireProgressChanged(this, y, sizeY);
            for (int x = 0; x < sizeX; x++)
            {
                if (array.getValue(x, y) > 0)
                {
                    result.setBoolean(x, y, true);
                }
            }
        }
        fireProgressChanged(this, sizeY, sizeY);
        
        // return result
        return result;
    }
    
    /**
     * Converts a 3D scalar array to a 3D binary array.
     * 
     * @see #process2d(ScalarArray2D)
     * 
     * @param array
     *            the (scalar) array to convert
     * @return the result binary array
     */
    public BinaryArray3D process3d(ScalarArray3D<?> array)
    {
        // retrieve array dimensions
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
        
        // create result array
        BinaryArray3D result = BinaryArray3D.create(sizeX, sizeY, sizeZ);
        
        // iterate over array elements, setting result only for true elements
        // (can save time for RLE arrays)
        for (int z = 0; z < sizeZ; z++)
        {
            fireProgressChanged(this, z, sizeZ);
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    if (array.getValue(x, y, z) > 0)
                    {
                        result.setBoolean(x, y, z, true);
                    }
                }
            }
        }
        fireProgressChanged(this, sizeZ, sizeZ);
        
        // return result
        return result;
    }
    
    @Override
    public boolean canProcess(Array<?> array)
    {
        return array instanceof ScalarArray;
    }
}
