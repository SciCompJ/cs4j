/**
 * 
 */
package net.sci.array.color;

import net.sci.algo.AlgoStub;

/**
 * A factory for RGB8 arrays that generates dense representation of arrays.
 * The required memory is proportional to the number of elements within the
 * array.
 * 
 * @author dlegland
 *
 */
public class DenseRGB8ArrayFactory extends AlgoStub implements RGB8Array.Factory
{
    @Override
    public RGB8Array create(int[] dims)
    {
        return switch (dims.length)
        {
            case 2 -> new Int32EncodedRGB8Array2D(dims[0], dims[1]);
            case 3 -> {
                fireStatusChanged(this, "Allocating memory");
                yield new Int32EncodedRGB8Array3D(dims[0], dims[1], dims[2]);
            }
            default -> new Int32EncodedRGB8ArrayND(dims);
        };
    }

    @Override
    public RGB8Array create(int[] dims, RGB8 value)
    {
        RGB8Array array = create(dims);
        fireStatusChanged(this, "Fill default value");
        array.fill(value);
        return array;
    }
}
