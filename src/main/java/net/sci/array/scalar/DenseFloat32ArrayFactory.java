/**
 * 
 */
package net.sci.array.scalar;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;

/**
 * A factory for Float32 arrays that generates dense representation of arrays.
 * The required memory is proportional to the number of elements within the
 * array.
 * 
 * @author dlegland
 *
 */
public class DenseFloat32ArrayFactory extends AlgoStub implements Float32Array.Factory
{
    @Override
    public Float32Array create(int... dims)
    {
        switch (dims.length)
        {
        case 2:
            return Float32Array2D.create(dims[0], dims[1]);
        case 3:
        {
            fireStatusChanged(this, "Allocating memory");
            if (Array.prod(dims[0], dims[1], dims[2]) < Integer.MAX_VALUE - 8)
                return new BufferedFloat32Array3D(dims[0], dims[1], dims[2]);
            else 
                return new SlicedFloat32Array3D(dims[0], dims[1], dims[2]);
        }
        default:
            return Float32ArrayND.create(dims);
        }
    }

    @Override
    public Float32Array create(int[] dims, Float32 value)
    {
        Float32Array array = create(dims);
        fireStatusChanged(this, "Fill default value");
        array.fillValue(value.getValue());
        return array;
    }
}
