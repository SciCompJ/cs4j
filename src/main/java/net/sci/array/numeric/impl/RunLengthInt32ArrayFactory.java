/**
 * 
 */
package net.sci.array.numeric.impl;

import net.sci.algo.AlgoStub;
import net.sci.array.numeric.Int32;
import net.sci.array.numeric.Int32Array;
import net.sci.array.numeric.Int32ArrayND;

/**
 * A factory for Int32 arrays that generates run-length encoded representation
 * of arrays. If implementation for a specific dimension is not available, an
 * alternative representation is provided instead.
 * 
 * @author dlegland
 *
 */
public class RunLengthInt32ArrayFactory extends AlgoStub implements Int32Array.Factory
{
    @Override
    public Int32Array create(int... dims)
    {
        return switch (dims.length)
        {
            case 1 -> new BufferedInt32Array1D(dims[0]);
            case 2 -> new RunLengthInt32Array2D(dims[0], dims[1]);
            case 3 -> new RunLengthInt32Array3D(dims[0], dims[1], dims[2]);
            default -> Int32ArrayND.create(dims);
        };
    }

    @Override
    public Int32Array create(int[] dims, Int32 value)
    {
        Int32Array array = create(dims);
        fireStatusChanged(this, "Fill default value");
        array.fillInt(value.getInt());
        return array;
    }
}
