/**
 * 
 */
package net.sci.array.binary.process;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.binary.Binary;
import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.impl.ArrayWrapperStub;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.UInt8;
import net.sci.array.numeric.UInt8Array;
import net.sci.array.numeric.UInt8Array2D;
import net.sci.array.numeric.UInt8Array3D;
import net.sci.array.numeric.process.ScalarArrayOperator;

/**
 * Converts a Binary array into a UInt8 array, by converting <code>false</code>
 * values to 0 and <code>true</code>values to 255.
 * 
 * @author dlegland
 */
public class BinaryToUInt8 extends AlgoStub implements ScalarArrayOperator
{
    public BinaryToUInt8()
    {
    }
    
    @Override
    public UInt8Array processScalar(ScalarArray<?> array)
    {
        if (array.elementClass() != Binary.class)
        {
            throw new IllegalArgumentException("Requires input array to contains Binary values");
        }
        return processBinary(BinaryArray.wrap(array));
    }

    public UInt8Array processBinary(BinaryArray array)
    {
        // Dispatch to specialized methods depending on array dimensionality
        return switch (array.dimensionality())
        {
            case 2 -> processBinary2d(BinaryArray2D.wrap(array));
            case 3 -> processBinary3d(BinaryArray3D.wrap(array));
            default -> processBinaryNd(array);
        };
    }
    
    private UInt8Array2D processBinary2d(BinaryArray2D array)
    {
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        UInt8Array2D res = UInt8Array2D.create(sizeX, sizeY);
        
        for (int y = 0; y < sizeY; y++)
        {
            this.fireProgressChanged(this, y, sizeY);
            for (int x = 0; x < sizeX; x++)
            {
                res.setInt(x, y, array.getBoolean(x,y) ? 255 : 0);
            }
        }
        this.fireProgressChanged(this, sizeY, sizeY);
        return res;
    }
    
    private UInt8Array3D processBinary3d(BinaryArray3D array)
    {
        // retrieve array size
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
        UInt8Array3D res = UInt8Array3D.create(sizeX, sizeY, sizeZ);
        
        // iterate over slices
        for (int z = 0; z < sizeZ; z++)
        {
            this.fireProgressChanged(this, z, sizeZ);
            
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    res.setInt(x, y, z, array.getBoolean(x,y,z) ? 255 : 0);
                }
            }
        }
        this.fireProgressChanged(this, sizeZ, sizeZ);
        return res;
    }
    
    private UInt8Array processBinaryNd(BinaryArray array)
    {
        UInt8Array res = UInt8Array.create(array.size());
        res.fillInts(pos -> array.getBoolean(pos) ? 255 : 0);
        return res;
    }
    
    public static class View extends ArrayWrapperStub<UInt8> implements UInt8Array
    {
        BinaryArray array;
        
        public View(Array<?> array)
        {
            super(array);
            this.array = BinaryArray.wrap(array);
        }

        @Override
        public byte getByte(int[] pos)
        {
            return array.getBoolean(pos) ? (byte) 255 : (byte) 0;
        }

        @Override
        public void setByte(int[] pos, byte value)
        {
            throw new RuntimeException("Non-writable view");
        }
    }
}
