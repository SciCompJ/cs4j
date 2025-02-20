/**
 * 
 */
package net.sci.array.numeric.impl;

import net.sci.algo.AlgoStub;
import net.sci.array.numeric.Int16;
import net.sci.array.numeric.Int16Array;
import net.sci.array.numeric.Int16Array3D;
import net.sci.array.numeric.Int16ArrayND;
import net.sci.util.MathUtils;

/**
 * A factory for Int16 arrays that generates dense representation of arrays.
 * The required memory is proportional to the number of elements within the
 * array.
 * 
 * @author dlegland
 *
 */
public class DenseInt16ArrayFactory extends AlgoStub implements Int16Array.Factory
{
    @Override
    public Int16Array create(int... dims)
    {
        return switch (dims.length)
        {
            case 1 -> new BufferedInt16Array1D(dims[0]);
            case 2 -> new BufferedInt16Array2D(dims[0], dims[1]);
            case 3 -> create3d(dims[0], dims[1], dims[2]);
            default -> Int16ArrayND.create(dims);
        };
    }
    
    private Int16Array3D create3d(int dim0, int dim1, int dim2)
    {
        fireStatusChanged(this, "Allocating memory");
        if (MathUtils.prod(dim0, dim1, dim2) < Integer.MAX_VALUE - 8)
            return new BufferedInt16Array3D(dim0, dim1, dim2);
        else
            return new SlicedInt16Array3D(dim0, dim1, dim2);
    }

    @Override
    public Int16Array create(int[] dims, Int16 value)
    {
        Int16Array array = create(dims);
        fireStatusChanged(this, "Fill default value");
        array.fillInt(value.intValue());
        return array;
    }
}
