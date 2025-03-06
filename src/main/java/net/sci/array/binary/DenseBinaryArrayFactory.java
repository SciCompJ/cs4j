/**
 * 
 */
package net.sci.array.binary;

import net.sci.algo.AlgoStub;
import net.sci.util.MathUtils;

/**
 * A factory for binary arrays that generates dense representation of arrays.
 * The required memory is proportional to the number of elements within the
 * array.
 * 
 * @author dlegland
 *
 */
public class DenseBinaryArrayFactory extends AlgoStub implements BinaryArray.Factory
{
    @Override
    public BinaryArray create(int... dims)
    {
        return switch (dims.length)
        {
            case 1 -> new BufferedBinaryArray1D(dims[0]);
            case 2 -> new BufferedBinaryArray2D(dims[0], dims[1]);
            case 3 -> create3d(dims[0], dims[1], dims[2]);
            default -> BufferedBinaryArrayND.create(dims);
        };
    }
    
    private BinaryArray3D create3d(int dim0, int dim1, int dim2)
    {
        fireStatusChanged(this, "Allocating memory");
        if (MathUtils.prod(dim0, dim1, dim2) < Integer.MAX_VALUE - 8)
            return new BufferedBinaryArray3D(dim0, dim1, dim2);
        else
            return new SlicedBinaryArray3D(dim0, dim1, dim2);
    }

    @Override
    public BinaryArray create(int[] dims, Binary value)
    {
        BinaryArray array = create(dims);
        array.fillValue(value.value());
        return array;
    }
}
