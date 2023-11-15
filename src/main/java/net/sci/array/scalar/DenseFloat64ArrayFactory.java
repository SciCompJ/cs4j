/**
 * 
 */
package net.sci.array.scalar;

import net.sci.algo.AlgoStub;
import net.sci.util.MathUtils;

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
        return switch (dims.length)
        {
            case 1 -> new BufferedFloat64Array1D(dims[0]);
            case 2 -> new BufferedFloat64Array2D(dims[0], dims[1]);
            case 3 -> create3d(dims[0], dims[1], dims[2]);
            default -> Float64ArrayND.create(dims);
        };
    }

    private Float64Array3D create3d(int dim0, int dim1, int dim2)
    {
        fireStatusChanged(this, "Allocating memory");
        if (MathUtils.prod(dim0, dim1, dim2) < Integer.MAX_VALUE - 8)
            return new BufferedFloat64Array3D(dim0, dim1, dim2);
        else
            return new SlicedFloat64Array3D(dim0, dim1, dim2);
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
