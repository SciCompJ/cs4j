/**
 * 
 */
package net.sci.array.binary;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;

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
        switch (dims.length)
        {
            case 1:
                return new BufferedBinaryArray1D(dims[0]);
            case 2:
                return new BufferedBinaryArray2D(dims[0], dims[1]);
            case 3:
            {
                if (Array.prod(dims[0], dims[1], dims[2]) < Integer.MAX_VALUE - 8)
                    return new BufferedBinaryArray3D(dims[0], dims[1], dims[2]);
                else 
                    return new SlicedBinaryArray3D(dims[0], dims[1], dims[2]);
            }
            default:
                return BufferedBinaryArrayND.create(dims);
        }
    }

    @Override
    public BinaryArray create(int[] dims, Binary value)
    {
        BinaryArray array = create(dims);
        array.fillValue(value.getValue());
        return array;
    }
}
