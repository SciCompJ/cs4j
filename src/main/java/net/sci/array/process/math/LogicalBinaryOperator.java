/**
 * 
 */
package net.sci.array.process.math;

import java.util.function.BiFunction;

import net.sci.algo.AlgoStub;
import net.sci.array.Arrays;
import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.binary.BinaryArray3D;

/**
 * Base class for operators that combines the values from two scalar arrays the
 * same size, and put the result in a new scalar array.
 * 
 * The result scalar array can also be specified. 
 * 
 * Example
 * <pre>{@code
    // create operator to compute exclusive or from two arrays 
    LogicalBinaryOperator op = new LogicalBinaryOperator((a,b) -> a ^ b);
    // initialize demo arrays
    BinaryArray2D array1 = BinaryArray2D.create(8, 6);
    array1.fillBooleans((x,y) -> x >= 4);
    BinaryArray2D array2 = BinaryArray2D.create(8, 6);
    array2.fillBooleans((x,y) -> y >= 3);
    // Apply operator and display result
    BinaryArray2D res = (BinaryArray2D) op.process(array1, array2);
    res.print(System.out);
 * }</pre>
 * 
 * @author dlegland
 *
 */
public class LogicalBinaryOperator extends AlgoStub
{
    /**
     * The function to apply to each pair of values.
     */
    BiFunction<Boolean, Boolean, Boolean> fun;
    
    /**
     * Creates a new operator from a function that associates a double to a pair
     * of double.
     * 
     * Example:
     * 
     * <pre>
     * {@codeMathBinaryOperator op = new MathBinaryOperator((a,b) -> a + b);}
     * 
     * @param fun
     *            the function defining this operator.
     */
    public LogicalBinaryOperator(BiFunction<Boolean,Boolean,Boolean> fun)
    {
        this.fun = fun;
    }
    
    public BinaryArray process(BinaryArray array1, BinaryArray array2)
    {
        checkSameSize(array1, array2);
        
        if (array1.dimensionality() == 2)
        {
            BinaryArray2D res = BinaryArray2D.wrap(array1.newInstance(array1.size()));
            return process2d(BinaryArray2D.wrap(array1), BinaryArray2D.wrap(array2), res);
        }
        else if (array1.dimensionality() == 2)
        {
            BinaryArray3D res = BinaryArray3D.wrap(array1.newInstance(array1.size()));
            return process3d(BinaryArray3D.wrap(array1), BinaryArray3D.wrap(array2), res);
        }
        else
        {
            BinaryArray res = array1.newInstance(array1.size());
            for (int[] pos : res.positions())
            {
                res.setBoolean(pos, fun.apply(array1.getBoolean(pos), array2.getBoolean(pos)));
            }
            return res;
        }
    }
    
    public BinaryArray process(BinaryArray array1, BinaryArray array2, BinaryArray output)
    {
        checkSameSize(array1, array2);
        checkSameSize(array1, output);
        
        if (array1.dimensionality() == 2)
        {
            return process2d(BinaryArray2D.wrap(array1), BinaryArray2D.wrap(array2), BinaryArray2D.wrap(output));
        }
        else if (array1.dimensionality() == 2)
        {
            return process3d(BinaryArray3D.wrap(array1), BinaryArray3D.wrap(array2), BinaryArray3D.wrap(output));
        }
        else
        {
            for (int[] pos : output.positions())
            {
                output.setBoolean(pos, fun.apply(array1.getBoolean(pos), array2.getBoolean(pos)));
            }
            return output;
        }
    }

    private BinaryArray2D process2d(BinaryArray2D array1, BinaryArray2D array2, BinaryArray2D res)
    {
        int sizeX = array1.size(0);
        int sizeY = array1.size(1);
        for (int y = 0; y < sizeY; y++)
        {
            this.fireProgressChanged(this, y, sizeY);
            for (int x = 0; x < sizeX; x++)
            {
                res.setBoolean(x, y, fun.apply(array1.getBoolean(x, y), array2.getBoolean(x, y)));
            }
        }
        this.fireProgressChanged(this, 1, 1);
                    
        return res;
    }
    
    private BinaryArray3D process3d(BinaryArray3D array1, BinaryArray3D array2, BinaryArray3D res)
    {
        int sizeX = array1.size(0);
        int sizeY = array1.size(1);
        int sizeZ = array1.size(2);
        for (int z = 0; z < sizeZ; z++)
        {
            this.fireProgressChanged(this, z, sizeZ);
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    res.setBoolean(x, y, z, fun.apply(array1.getBoolean(x, y, z), array2.getBoolean(x, y, z)));
                }
            }
        }
        this.fireProgressChanged(this, 1, 1);
                    
        return res;
    }
    
    private static final void checkSameSize(BinaryArray array1, BinaryArray array2)
    {
        if (!Arrays.isSameDimensionality(array1, array2))
        {
            throw new IllegalArgumentException("Arrays must have same dimension");
        }
        if (!Arrays.isSameSize(array1, array2))
        {
            throw new IllegalArgumentException("Arrays must have same size");
        }
    }
}
