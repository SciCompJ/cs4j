/**
 * 
 */
package net.sci.array.scalar;

import net.sci.algo.AlgoStub;
import net.sci.util.MathUtils;

/**
 * A factory for Int32 arrays that generates dense representation of arrays.
 * The required memory is proportional to the number of elements within the
 * array.
 * 
 * @author dlegland
 *
 */
public class DenseInt32ArrayFactory extends AlgoStub implements Int32Array.Factory
{
    @Override
    public Int32Array create(int... dims)
    {
        return switch (dims.length)
        {
            case 1 -> new BufferedInt32Array1D(dims[0]);
            case 2 -> new BufferedInt32Array2D(dims[0], dims[1]);
            case 3 -> create3d(dims[0], dims[1], dims[2]);
            default -> Int32ArrayND.create(dims);
        };
    }

    private Int32Array3D create3d(int dim0, int dim1, int dim2)
    {
        fireStatusChanged(this, "Allocating memory");
        if (MathUtils.prod(dim0, dim1, dim2) < Integer.MAX_VALUE - 8)
            return new BufferedInt32Array3D(dim0, dim1, dim2);
        else
            return new SlicedInt32Array3D(dim0, dim1, dim2);
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
