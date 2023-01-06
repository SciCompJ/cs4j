/**
 * 
 */
package net.sci.array.process.numeric;

import net.sci.algo.AlgoStub;
import net.sci.array.process.ScalarArrayOperator;
import net.sci.array.scalar.Scalar;
import net.sci.array.scalar.ScalarArray;
import net.sci.array.scalar.ScalarArray2D;
import net.sci.array.scalar.ScalarArray3D;

/**
 * Keep the values within the specified range, and set all other values to zero.
 * 
 * @author dlegland
 *
 */
public class KeepValueRange extends AlgoStub implements ScalarArrayOperator
{
    double minValue;
    double maxValue;
    
    public KeepValueRange(double minValue, double maxValue)
    {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
    
    /**
     * Processes an input array and populates the output array.
     * 
     * @param input
     *            the input array
     * @param output
     *            the output array
     */
    public void processScalar(ScalarArray<? extends Scalar> input,
            ScalarArray<? extends Scalar> output)
    {
        int nd = input.dimensionality();
        if (output.dimensionality() != nd)
        {
            throw new RuntimeException("Input and output arrays must have same dimensionality");
        }
        
        if (nd == 2)
        {
            processScalar2d(ScalarArray2D.wrapScalar2d(input), ScalarArray2D.wrapScalar2d(output));
        }
        else if (nd == 3)
        {
            processScalar3d(ScalarArray3D.wrapScalar3d(input), ScalarArray3D.wrapScalar3d(output));
        }
        else
        {
            processScalarNd(input, output);
        }
    }
    
    public void processScalarNd(ScalarArray<? extends Scalar> input,
            ScalarArray<? extends Scalar> output)
    {
        for (int[] pos : output.positions())
        {
            output.setValue(pos, process(input.getValue(pos)));
        }
    }
    
    private void processScalar2d(ScalarArray2D<? extends Scalar> input,
            ScalarArray2D<? extends Scalar> output)
    {
        int sizeX = input.size(0);
        int sizeY = input.size(1);
        
        for (int y = 0; y < sizeY; y++)
        {
            this.fireProgressChanged(this, y, sizeY);
            for (int x = 0; x < sizeX; x++)
            {
                output.setValue(x, y, process(input.getValue(x, y)));
            }
        }
    }
    
    private void processScalar3d(ScalarArray3D<? extends Scalar> input,
            ScalarArray3D<? extends Scalar> output)
    {
        int sizeX = input.size(0);
        int sizeY = input.size(1);
        int sizeZ = input.size(2);
        
        for (int z = 0; z < sizeZ; z++)
        {
            this.fireProgressChanged(this, z, sizeZ);
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    output.setValue(x, y, z, process(input.getValue(x, y, z)));
                }
            }
        }
    }
    
    /**
     * Replaces value by zero if it is out of range
     * 
     * @param val
     *            the value to process
     * @return the same value if it is comprised between minVal and maxVal, and
     *         zero otherwise.
     */
    private double process(double val)
    {
        if (val < minValue) return 0.0;
        if (val <= maxValue) return val;
        return 0.0;
    }
    
    @Override
    public ScalarArray<?> processScalar(ScalarArray<? extends Scalar> array)
    {
        ScalarArray<? extends Scalar> output = array.newInstance(array.size());
        processScalar(array, output);
        return output;
    }

}
