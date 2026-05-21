/**
 * 
 */
package net.sci.array.numeric.process;

import java.util.function.BiFunction;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.Arrays;
import net.sci.array.numeric.Scalar;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.ScalarArray3D;
import net.sci.array.numeric.Vector;
import net.sci.array.numeric.VectorArray;

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
    
    public Array<?> process(Array<?> array1, Array<?> array2)
    {
        return process(array1, array2, array1.newInstance(array1.size()));
    }
    
    public Array<?> process(Array<?> array1, Array<?> array2, Array<?> output)
    {
        checkSameSize(array1, array2);
        checkSameSize(array1, output);
        int nd = array1.dimensionality();
        
        // first dispatch processing depending on element class of first array
        Class<?> elementClass1 = array1.elementClass();
        if (Scalar.class.isAssignableFrom(elementClass1))
        {
            // check compatibility of the other arrays
            if (!Scalar.class.isAssignableFrom(array2.elementClass()) || !Scalar.class.isAssignableFrom(output.elementClass())) 
            {
                throw new RuntimeException(
                        "If first array contains scalar values, the second array must contain scalar values as well");
            }
            
            // convert to scalar arrays
            @SuppressWarnings({ "unchecked", "rawtypes" })
            ScalarArray<?> scalarArray1 = ScalarArray.wrap((Array<? extends Scalar>) array1);
            @SuppressWarnings({ "unchecked", "rawtypes" })
            ScalarArray<?> scalarArray2 = ScalarArray.wrap((Array<? extends Scalar>) array2);
            @SuppressWarnings({ "unchecked", "rawtypes" })
            ScalarArray<?> res = ScalarArray.wrap((Array<? extends Scalar>) output);
            
            return switch (nd)
            {
                case 2 -> processScalar2d(
                        ScalarArray2D.wrapScalar2d(scalarArray1), 
                        ScalarArray2D.wrapScalar2d(scalarArray2), 
                        ScalarArray2D.wrapScalar2d(res));
                case 3 -> processScalar3d(
                        ScalarArray3D.wrapScalar3d(scalarArray1), 
                        ScalarArray3D.wrapScalar3d(scalarArray2), 
                        ScalarArray3D.wrapScalar3d(res));
                default -> 
                {
                    res.fillValues(pos -> fun.apply(scalarArray1.getValue(pos), scalarArray2.getValue(pos)));
                    yield res;
                }
            };
            
        }
        else if (Vector.class.isAssignableFrom(elementClass1))
        {
            // check compatibility of the other arrays
            if (!Vector.class.isAssignableFrom(array2.elementClass()) || !Vector.class.isAssignableFrom(output.elementClass())) 
            {
                throw new RuntimeException(
                        "If first array contains vectors, the second array must contain vectors as well");
            }
            
            // convert to arrays of vectors
            @SuppressWarnings({ "unchecked", "rawtypes" })
            VectorArray<?,?> vectorArray1 = VectorArray.wrap((Array<? extends Vector>) array1);
            @SuppressWarnings({ "unchecked", "rawtypes" })
            VectorArray<?,?> vectorArray2 = VectorArray.wrap((Array<? extends Vector>) array2);
            @SuppressWarnings({ "unchecked", "rawtypes" })
            VectorArray<?,?> res = VectorArray.wrap((Array<? extends Vector>) output);
            
            // call specialized method
            return processVector(vectorArray1, vectorArray2, res);
        }
        else
        {
            throw new RuntimeException("Not implemented for arrays with element class: " + elementClass1.getName());
        }
    }
    
    public ScalarArray<?> processScalar(ScalarArray<?> array1, ScalarArray<?> array2)
    {
        checkSameSize(array1, array2);
        
        // dispatch processing according to dimension,
        // making it possible to track progress
        if (array1.dimensionality() == 2)
        {
            ScalarArray2D<?> res = ScalarArray2D.wrapScalar2d(array1.newInstance(array1.size()));
            return processScalar2d(ScalarArray2D.wrapScalar2d(array1), ScalarArray2D.wrapScalar2d(array2), res);
        }
        else if (array1.dimensionality() == 3)
        {
            ScalarArray3D<?> res = ScalarArray3D.wrapScalar3d(array1.newInstance(array1.size()));
            return processScalar3d(ScalarArray3D.wrapScalar3d(array1), ScalarArray3D.wrapScalar3d(array2), res);
        }
        else
        {
            ScalarArray<?> res = array1.newInstance(array1.size());
            res.fillValues(pos -> fun.apply(array1.getValue(pos), array2.getValue(pos)));
            return res;
        }
    }
    
    public ScalarArray<?> processScalar(ScalarArray<?> array1, ScalarArray<?> array2, ScalarArray<?> output)
    {
        checkSameSize(array1, array2);
        checkSameSize(array1, output);
        
        return switch (array1.dimensionality())
        {
            case 2 -> processScalar2d(ScalarArray2D.wrapScalar2d(array1), ScalarArray2D.wrapScalar2d(array2), ScalarArray2D.wrapScalar2d(output));
            case 3 -> processScalar3d(ScalarArray3D.wrapScalar3d(array1), ScalarArray3D.wrapScalar3d(array2), ScalarArray3D.wrapScalar3d(output));
            default -> 
            {
                output.fillValues(pos -> fun.apply(array1.getValue(pos), array2.getValue(pos)));
                yield output;
            }
        };
    }

    private ScalarArray2D<?> processScalar2d(ScalarArray2D<?> array1, ScalarArray2D<?> array2, ScalarArray2D<?> res)
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
    
    private ScalarArray3D<?> processScalar3d(ScalarArray3D<?> array1, ScalarArray3D<?> array2, ScalarArray3D<?> res)
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
    
    public VectorArray<?,?> processVector(VectorArray<?,?> array1, VectorArray<?,?> array2, VectorArray<?,?> output)
    {
        checkSameSize(array1, array2);
        checkSameSize(array1, output);
        
        // iterate over channels of each array
        for (int c = 0; c < array1.channelCount(); c++)
        {
            ScalarArray<?> channel1 = array1.channel(c);
            ScalarArray<?> channel2 = array2.channel(c);
            ScalarArray<?> resChannel = output.channel(c);
            processScalar(channel1, channel2, resChannel);
        }
        this.fireProgressChanged(this, 1, 1);
                    
        return output;
    }

    private static final void checkSameSize(Array<?> array1, Array<?> array2)
    {
        if (!Arrays.isSameDimensionality(array1, array2))
        {
            throw new IllegalArgumentException("Arrays must have same dimensionality");
        }
        if (!Arrays.isSameSize(array1, array2))
        {
            throw new IllegalArgumentException("Arrays must have same size");
        }
    }
}
