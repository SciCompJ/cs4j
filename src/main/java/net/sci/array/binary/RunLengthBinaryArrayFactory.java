/**
 * 
 */
package net.sci.array.binary;

import net.sci.algo.AlgoStub;

/**
 * A Factory for BinaryArray instances that preferentially returns instances of
 * Run-Length encoded arrays.
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
            return RunLengthBinaryArray2D.create(dims[0], dims[1]);
        case 3:
            return RunLengthBinaryArray3D.create(dims[0], dims[1], dims[2]);
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
