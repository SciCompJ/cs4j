/**
 * 
 */
package net.sci.array.binary;

import net.sci.algo.AlgoStub;

/**
 * A Factory for BinaryArray instances that preferentially returns instances of
 * Run-Length encoded arrays.
 * 
 * If number of dimensions is different from 2 and 3, a dense (i.e. buffered or
 * sliced) binary array is returned.
 * 
 * @author dlegland
 */
public class RunLengthBinaryArrayFactory extends AlgoStub implements BinaryArray.Factory
{
    @Override
    public BinaryArray create(int... dims)
    {
        switch (dims.length)
        {
        case 2:
            return new RunLengthBinaryArray2D(dims[0], dims[1]);
        case 3:
            return new RunLengthBinaryArray3D(dims[0], dims[1], dims[2]);
        default:
            return BinaryArrayND.create(dims);
        }
    }

    @Override
    public BinaryArray create(int[] dims, Binary value)
    {
        BinaryArray array = create(dims);
        array.fill(value.getBoolean());
        return array;
    }
}
