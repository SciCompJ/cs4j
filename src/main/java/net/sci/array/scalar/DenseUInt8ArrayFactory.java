/**
 * 
 */
package net.sci.array.scalar;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;

/**
 * A factory for UInt8 arrays that generates dense representation of arrays.
 * The required memory is proportional to the number of elements within the
 * array.
 * 
 * @author dlegland
 *
 */
public class DenseUInt8ArrayFactory extends AlgoStub implements UInt8Array.Factory
{
    @Override
    public UInt8Array create(int... dims)
    {
        switch (dims.length)
        {
        case 2:
            return UInt8Array2D.create(dims[0], dims[1]);
        case 3:
        {
            fireStatusChanged(this, "Allocating memory");
            if (Array.prod(dims[0], dims[1], dims[2]) < Integer.MAX_VALUE - 8)
                return new BufferedUInt8Array3D(dims[0], dims[1], dims[2]);
            else 
                return new SlicedUInt8Array3D(dims[0], dims[1], dims[2]);
        }
        default:
            return UInt8ArrayND.create(dims);
        }
    }

    @Override
    public UInt8Array create(int[] dims, UInt8 value)
    {
        UInt8Array array = create(dims);
        fireStatusChanged(this, "Fill default value");
        array.fillInt(value.getInt());
        return array;
    }
}
