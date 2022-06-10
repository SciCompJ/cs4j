/**
 * 
 */
package net.sci.array.color;

import net.sci.algo.AlgoStub;

/**
 * A factory for RGB16 arrays that generates dense representation of arrays.
 * The required memory is proportional to the number of elements within the
 * array.
 * 
 * @author dlegland
 *
 */
public class DenseRGB16ArrayFactory extends AlgoStub implements RGB16Array.Factory
{
    @Override
    public RGB16Array create(int... dims)
    {
        fireStatusChanged(this, "Allocating memory");
            switch (dims.length)
        {
        case 2:
            return new BufferedPackedShortRGB16Array2D(dims[0], dims[1]);
        case 3:
        {
            return new BufferedPackedShortRGB16Array3D(dims[0], dims[1], dims[2]);
        }
        default:
            return new BufferedPackedShortRGB16ArrayND(dims);
        }
    }

    @Override
    public RGB16Array create(int[] dims, RGB16 value)
    {
        RGB16Array array = create(dims);
        fireStatusChanged(this, "Fill default value");
        array.fill(value);
        return array;
    }
}
