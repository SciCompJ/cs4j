/**
 * 
 */
package net.sci.array.scalar;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;

/**
 * A factory for Int16 arrays that generates dense representation of arrays.
 * The required memory is proportional to the number of elements within the
 * array.
 * 
 * @author dlegland
 *
 */
public class DenseInt16ArrayFactory extends AlgoStub implements Int16Array.Factory
{
    @Override
    public Int16Array create(int... dims)
    {
        switch (dims.length)
        {
        case 2:
            return Int16Array2D.create(dims[0], dims[1]);
        case 3:
        {
            fireStatusChanged(this, "Allocating memory");
            if (Array.prod(dims[0], dims[1], dims[2]) < Integer.MAX_VALUE - 8)
                return new BufferedInt16Array3D(dims[0], dims[1], dims[2]);
            else 
                return new SlicedInt16Array3D(dims[0], dims[1], dims[2]);
        }
        default:
            return Int16ArrayND.create(dims);
        }
    }

    @Override
    public Int16Array create(int[] dims, Int16 value)
    {
        Int16Array array = create(dims);
        fireStatusChanged(this, "Fill default value");
        array.fillInt(value.getInt());
        return array;
    }
}
