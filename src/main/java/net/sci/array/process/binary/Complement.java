/**
 * 
 */
package net.sci.array.process.binary;

import net.sci.algo.AlgoStub;
import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.binary.BinaryRow;
import net.sci.array.binary.Run;
import net.sci.array.binary.RunLengthBinaryArray2D;
import net.sci.array.binary.RunLengthBinaryArray3D;

/**
 * Computes the complement of a binary array.
 * 
 * @author dlegland
 */
public class Complement extends AlgoStub
{
    /**
     * Creates the processor class. 
     */
    public Complement()
    {
    }
    
    public BinaryArray process(BinaryArray array)
    {
        if (array.dimensionality() == 2)
        {
            return process2d(BinaryArray2D.wrap(array));
        }
        else if (array.dimensionality() == 3)
        {
            return process3d(BinaryArray3D.wrap(array));
        }
        
        return processNd(array);
    }
    
    private BinaryArray2D process2d(BinaryArray2D array)
    {
        if (array instanceof RunLengthBinaryArray2D)
        {
            return process2d_RunLength((RunLengthBinaryArray2D) array);
        }
        
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        
        BinaryArray2D res = BinaryArray2D.create(sizeX, sizeY);
        for (int y = 0; y < sizeY; y++)
        {
            this.fireProgressChanged(this, y, sizeY);
            for (int x = 0; x < sizeX; x++)
            {
                res.setBoolean(x, y, !array.getBoolean(x, y));
            }
        }
        this.fireProgressChanged(this, 1, 1);
        return res;
    }

    private BinaryArray2D process2d_RunLength(RunLengthBinaryArray2D array)
    {
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        
        RunLengthBinaryArray2D res = new RunLengthBinaryArray2D(sizeX, sizeY);
        
        for (int y = 0; y < sizeY; y++)
        {
            this.fireProgressChanged(this, y, sizeY);
            
            BinaryRow row = array.getRow(y);
            row = row != null ? row.complement(0, sizeX - 1) : new BinaryRow(new Run(0, sizeX-1));
            res.setRow(y, row);
        }
        this.fireProgressChanged(this, 1, 1);
        return res;
    }
    
    private BinaryArray3D process3d(BinaryArray3D array)
    {
        if (array instanceof RunLengthBinaryArray3D)
        {
            return process3d_RunLength((RunLengthBinaryArray3D) array);
        }
        
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
        
        BinaryArray3D res = BinaryArray3D.create(sizeX, sizeY, sizeZ);
        for (int z = 0; z < sizeZ; z++)
        {
            this.fireProgressChanged(this, z, sizeZ);
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    res.setBoolean(x, y, z, !array.getBoolean(x, y, z));
                }
            }
        }
        this.fireProgressChanged(this, 1, 1);
        return res;
    }

    private BinaryArray3D process3d_RunLength(RunLengthBinaryArray3D array)
    {
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
        
        RunLengthBinaryArray3D res = new RunLengthBinaryArray3D(sizeX, sizeY, sizeZ);
        for (int z = 0; z < sizeZ; z++)
        {
            this.fireProgressChanged(this, z, sizeZ);
            for (int y = 0; y < sizeY; y++)
            {
                BinaryRow row = array.getRow(y, z);
                row = row != null ? row.complement(0, sizeX - 1) : new BinaryRow(new Run(0, sizeX-1));
                res.setRow(y, z, row);
            }
        }
        this.fireProgressChanged(this, 1, 1);
        return res;
    }

    private BinaryArray processNd(BinaryArray array)
    {
        BinaryArray res = BinaryArray.create(array.size());
        for (int[] pos : res.positions())
        {
            res.setBoolean(pos, !array.getBoolean(pos));
        }
        return res;
    }
}
