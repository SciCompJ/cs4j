/**
 * 
 */
package net.sci.array.scalar;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;

/**
 * A factory for Float64 arrays that generates dense representation of arrays.
 * The required memory is proportional to the number of elements within the
 * array.
 * 
 * @author dlegland
 *
 */
public class DenseFloat64ArrayFactory extends AlgoStub implements Float64Array.Factory
{
    @Override
    public Float64Array create(int... dims)
    {
        switch (dims.length)
        {
        case 2:
            return new BufferedFloat64Array2D(dims[0], dims[1]);
        case 3:
        {
            fireStatusChanged(this, "Allocating memory");
            if (Array.prod(dims[0], dims[1], dims[2]) < Integer.MAX_VALUE - 8)
                return new BufferedFloat64Array3D(dims[0], dims[1], dims[2]);
            else 
                return new SlicedFloat64Array3D(dims[0], dims[1], dims[2]);
        }
        default:
            return Float64ArrayND.create(dims);
        }
    }

    @Override
    public Float64Array create(int[] dims, Float64 value)
    {
        Float64Array array = create(dims);
        fireStatusChanged(this, "Fill default value");
        array.fillValue(value.getValue());
        return array;
    }
}
