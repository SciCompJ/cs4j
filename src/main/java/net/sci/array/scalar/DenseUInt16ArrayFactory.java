/**
 * 
 */
package net.sci.array.scalar;

import net.sci.algo.AlgoStub;
import net.sci.util.MathUtils;

/**
 * A factory for UInt16 arrays that generates dense representation of arrays.
 * The required memory is proportional to the number of elements within the
 * array.
 * 
 * @author dlegland
 *
 */
public class DenseUInt16ArrayFactory extends AlgoStub implements UInt16Array.Factory
{
    @Override
    public UInt16Array create(int... dims)
    {
        return switch (dims.length)
        {
            case 1 -> new BufferedUInt16Array1D(dims[0]);
            case 2 -> new BufferedUInt16Array2D(dims[0], dims[1]);
            case 3-> create3d(dims[0], dims[1], dims[2]);
            default -> UInt16ArrayND.create(dims);
        };
    }

    private UInt16Array3D create3d(int dim0, int dim1, int dim2)
    {
        fireStatusChanged(this, "Allocating memory");
        if (MathUtils.prod(dim0, dim1, dim2) < Integer.MAX_VALUE - 8)
            return new BufferedUInt16Array3D(dim0, dim1, dim2);
        else
            return new SlicedUInt16Array3D(dim0, dim1, dim2);
    }

    @Override
    public UInt16Array create(int[] dims, UInt16 value)
    {
        UInt16Array array = create(dims);
        fireStatusChanged(this, "Fill default value");
        array.fillInt(value.getInt());
        return array;
    }
}
