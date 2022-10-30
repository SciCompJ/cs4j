/**
 * 
 */
package net.sci.array.scalar;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;

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
        switch (dims.length)
        {
            case 1:
                return new BufferedInt32Array1D(dims[0]);
            case 2:
                return new BufferedInt32Array2D(dims[0], dims[1]);
            case 3:
            {
                fireStatusChanged(this, "Allocating memory");
                if (Array.prod(dims[0], dims[1], dims[2]) < Integer.MAX_VALUE - 8)
                    return new BufferedInt32Array3D(dims[0], dims[1], dims[2]);
                else 
                    return new SlicedInt32Array3D(dims[0], dims[1], dims[2]);
            }
            default:
                return Int32ArrayND.create(dims);
        }
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
