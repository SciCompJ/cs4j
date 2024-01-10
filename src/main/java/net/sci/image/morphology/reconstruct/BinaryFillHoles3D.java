/**
 * 
 */
package net.sci.image.morphology.reconstruct;

import java.util.HashMap;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.binary.BinaryRow;
import net.sci.array.binary.Run;
import net.sci.array.binary.RunLengthBinaryArray3D;
import net.sci.image.Connectivity3D;

/**
 * Fill holes algorithm, based on morphological reconstruction, but
 * reconstructing the background.
 * 
 * @author dlegland
 *
 */
public class BinaryFillHoles3D extends AlgoStub implements ArrayOperator
{
    int conn = 6;
    
    public BinaryFillHoles3D()
    {
    }
    
    public BinaryFillHoles3D(int conn)
    {
        this.conn = conn;
    }
    
    public BinaryFillHoles3D(Connectivity3D conn)
    {
        if (conn == Connectivity3D.C6)
        {
            this.conn = 6;
        }
        else if (conn == Connectivity3D.C26)
        {
            this.conn = 26;
        }
        else
        {
            throw new RuntimeException("Can only manage connectivities 6 or 26");
        }
    }
    
    public BinaryArray3D processBinary3d(BinaryArray3D mask)
    {
        // Ensure input array is run-length encoded
        this.fireStatusChanged(this, "convert mask image");
        RunLengthBinaryArray3D mask2 = RunLengthBinaryArray3D.convert(mask);
        mask2 = mask2.complement();
        
        this.fireStatusChanged(this, "create marker");
        RunLengthBinaryArray3D marker= createMarkerImage(mask.size());
        
        this.fireStatusChanged(this, "reconstruct background");
        BinaryArray3D res = new RunLengthBinaryReconstruction3D(conn).processBinary3d(marker, mask2);
        
        this.fireStatusChanged(this, "complement");
        return res.complement();
    }
    
    private RunLengthBinaryArray3D createMarkerImage(int[] dims)
    {
        // check input sizes, based on the size of the mask
        int sizeX = dims[0];
        int sizeY = dims[1];
        int sizeZ = dims[2];
        
        // create slice array
        HashMap<Integer, HashMap<Integer, BinaryRow>> slices = new HashMap<>(sizeZ);
        
        // first slice
        slices.put(0, makeFilledSlice(sizeX, sizeY));

        // middle slices
        for (int z = 1; z < sizeZ-1; z++)
        {
            slices.put(z, makeMarkerSlice(sizeX, sizeY));
        }
        
        // last slice
        slices.put(sizeZ - 1, makeFilledSlice(sizeX, sizeY));
        
        return new RunLengthBinaryArray3D(sizeX, sizeY, sizeZ, slices);
    }
    
    private HashMap<Integer, BinaryRow> makeFilledSlice(int sizeX, int sizeY)
    {
        HashMap<Integer, BinaryRow> slice = new HashMap<>(sizeY);
        
        slice = new HashMap<>(sizeY);
        for (int y = 0; y < sizeY; y++)
        {
            slice.put(y, new BinaryRow(new Run(0, sizeX - 1)));
        }
        return slice;
    }
    
    private HashMap<Integer, BinaryRow> makeMarkerSlice(int sizeX, int sizeY)
    {
        HashMap<Integer, BinaryRow> slice = new HashMap<>(sizeY);
        
        slice = new HashMap<>(sizeY);
        slice.put(0, new BinaryRow(new Run(0, sizeX - 1)));
        for (int y = 1; y < sizeY-1; y++)
        {
            slice.put(y, makeMarkerRow(sizeX));
        }
        slice.put(sizeY - 1, new BinaryRow(new Run(0, sizeX - 1)));
        
        return slice;
    }
    
    private BinaryRow makeMarkerRow(int size)
    {
        BinaryRow row = new BinaryRow();
        row.set(0, true);
        row.set(size - 1, true);
        return row;
    }

    @Override
    public <T> BinaryArray process(Array<T> array)
    {
        if (array instanceof BinaryArray)
        {
            return processBinary3d(BinaryArray3D.wrap((BinaryArray) array));
        }
        else
        {
            throw new RuntimeException(
                    "Requires an instance of BinaryArray");
        }
    }
    
    public boolean canProcess(Array<?> array)
    {
        return array instanceof BinaryArray && array.dimensionality() == 3;
    }
}
