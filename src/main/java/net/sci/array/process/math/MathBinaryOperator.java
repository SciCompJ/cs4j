/**
 * 
 */
package net.sci.array.process.math;

import java.util.function.BiFunction;

import net.sci.algo.AlgoStub;
import net.sci.array.Arrays;
import net.sci.array.scalar.ScalarArray;
import net.sci.array.scalar.ScalarArray2D;
import net.sci.array.scalar.ScalarArray3D;

/**
 * Base class for operators that combines the values from two scalar arrays the
 * same size, and put the result in a new scalar array.
 * 
 * The result scalar array can also be specified. 
 * 
 * Example
 * <pre>{@code
    // create operator to add values from two arrays 
    MathBinaryOperator op = new MathBinaryOperator((a,b) -> a + b);
    // initialize demo arrays
    UInt8Array2D array1 = UInt8Array2D.create(8, 6);
    array1.fillValues((x,y) -> (double) x);
    UInt8Array2D array2 = UInt8Array2D.create(8, 6);
    array2.fillValues((x,y) -> (double) y * 10);
    // Apply operator and display result
    UInt8Array2D res = (UInt8Array2D) op.process(array1, array2);
    res.print(System.out);
 * }</pre>
 * 
 * @author dlegland
 *
 */
public class MathBinaryOperator extends AlgoStub
{
    /**
     * The function to apply to each pair of values.
     */
    BiFunction<Double,Double,Double> fun;
    
    /**
     * Creates a new operator from a function that associates a double to a pair
     * of double.
     * 
     * Example:
     * 
     * <pre>
     * {@code MathBinaryOperator op = new MathBinaryOperator((a,b) -> a + b);
     * }</pre>
     * 
     * @param fun
     *            the function defining this operator.
     */
    public MathBinaryOperator(BiFunction<Double,Double,Double> fun)
    {
        this.fun = fun;
    }
    
    public ScalarArray<?> process(ScalarArray<?> array1, ScalarArray<?> array2)
    {
        checkSameSize(array1, array2);
        
        if (array1.dimensionality() == 2)
        {
            ScalarArray2D<?> res = ScalarArray2D.wrapScalar2d(array1.newInstance(array1.size()));
            return process2d(ScalarArray2D.wrapScalar2d(array1), ScalarArray2D.wrapScalar2d(array2), res);
        }
        else if (array1.dimensionality() == 3)
        {
            ScalarArray3D<?> res = ScalarArray3D.wrapScalar3d(array1.newInstance(array1.size()));
            return process3d(ScalarArray3D.wrapScalar3d(array1), ScalarArray3D.wrapScalar3d(array2), res);
        }
        else
        {
            ScalarArray<?> res = array1.newInstance(array1.size());
            res.fillValues(pos -> fun.apply(array1.getValue(pos), array2.getValue(pos)));
            return res;
        }
    }
    
    public ScalarArray<?> process(ScalarArray<?> array1, ScalarArray<?> array2, ScalarArray<?> output)
    {
        checkSameSize(array1, array2);
        checkSameSize(array1, output);
        
        if (array1.dimensionality() == 2)
        {
            return process2d(ScalarArray2D.wrapScalar2d(array1), ScalarArray2D.wrapScalar2d(array2), ScalarArray2D.wrapScalar2d(output));
        }
        else if (array1.dimensionality() == 3)
        {
            return process3d(ScalarArray3D.wrapScalar3d(array1), ScalarArray3D.wrapScalar3d(array2), ScalarArray3D.wrapScalar3d(output));
        }
        else
        {
            output.fillValues(pos -> fun.apply(array1.getValue(pos), array2.getValue(pos)));
            return output;
        }
    }

    private ScalarArray2D<?> process2d(ScalarArray2D<?> array1, ScalarArray2D<?> array2, ScalarArray2D<?> res)
    {
        int sizeX = array1.size(0);
        int sizeY = array1.size(1);
        for (int y = 0; y < sizeY; y++)
        {
            this.fireProgressChanged(this, y, sizeY);
            for (int x = 0; x < sizeX; x++)
            {
                res.setValue(x, y, fun.apply(array1.getValue(x, y), array2.getValue(x, y)));
            }
        }
        this.fireProgressChanged(this, 1, 1);
                    
        return res;
    }
    
    private ScalarArray3D<?> process3d(ScalarArray3D<?> array1, ScalarArray3D<?> array2, ScalarArray3D<?> res)
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
                    res.setValue(x, y, z, fun.apply(array1.getValue(x, y, z), array2.getValue(x, y, z)));
                }
            }
        }
        this.fireProgressChanged(this, 1, 1);
                    
        return res;
    }
    
    private static final void checkSameSize(ScalarArray<?> array1, ScalarArray<?> array2)
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
