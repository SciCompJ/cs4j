/**
 * 
 */
package net.sci.array.scalar;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;

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
        switch (dims.length)
        {
        case 2:
            return UInt16Array2D.create(dims[0], dims[1]);
        case 3:
        {
            fireStatusChanged(this, "Allocating memory");
            if (Array.prod(dims[0], dims[1], dims[2]) < Integer.MAX_VALUE - 8)
                return new BufferedUInt16Array3D(dims[0], dims[1], dims[2]);
            else 
                return new SlicedUInt16Array3D(dims[0], dims[1], dims[2]);
        }
        default:
            return UInt16ArrayND.create(dims);
        }
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
