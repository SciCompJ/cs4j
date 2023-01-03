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
        return switch (dims.length)
        {
            case 1 -> new BufferedFloat32Array1D(dims[0]);
            case 2 -> new BufferedFloat32Array2D(dims[0], dims[1]);
            case 3 -> create3d(dims[0], dims[1], dims[2]);
            default -> Float32ArrayND.create(dims);
        };
    }

    private Float32Array3D create3d(int dim0, int dim1, int dim2)
    {
        fireStatusChanged(this, "Allocating memory");
        if (Array.prod(dim0, dim1, dim2) < Integer.MAX_VALUE - 8)
            return new BufferedFloat32Array3D(dim0, dim1, dim2);
        else
            return new SlicedFloat32Array3D(dim0, dim1, dim2);
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
