/**
 * 
 */
package net.sci.array.scalar.impl;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.scalar.BufferedUInt8Array3D;
import net.sci.array.scalar.SlicedUInt8Array3D;
import net.sci.array.scalar.UInt8;
import net.sci.array.scalar.UInt8Array;
import net.sci.array.scalar.UInt8Array2D;
import net.sci.array.scalar.UInt8ArrayND;

/**
 * @author dlegland
 *
 */
public class UInt8ArrayDefaultFactory extends AlgoStub implements UInt8Array.Factory
{
    @Override
    public UInt8Array create(int... dims)
    {
        switch (dims.length)
        {
        case 2:
            return UInt8Array2D.create(dims[0], dims[1]);
        case 3:
            fireStatusChanged(this, "Allocating memory");
            if (Array.prod(dims[0], dims[1], dims[2]) < Integer.MAX_VALUE - 8)
                return new BufferedUInt8Array3D(dims[0], dims[1], dims[2]);
            else 
                return new SlicedUInt8Array3D(dims[0], dims[1], dims[2]);
//            return UInt8Array3D.create(dims[0], dims[1], dims[2]);
        default:
            return UInt8ArrayND.create(dims);
        }
    }

    @Override
    public UInt8Array create(int[] dims, UInt8 value)
    {
        UInt8Array array = create(dims);
        fireStatusChanged(this, "Fill default value");
        array.fillInt(value.getInt());
        return array;
    }
}
