/**
 * 
 */
package net.sci.image.morphology.reconstruction;

import java.util.HashMap;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.binary.BinaryRow;
import net.sci.array.binary.Run;
import net.sci.array.binary.RunLengthBinaryArray2D;

/**
 * Fill holes algorithm, based on morphological reconstruction, but
 * reconstructing the background.
 * 
 * @author dlegland
 *
 */
public class BinaryFillHoles2D extends AlgoStub implements ArrayOperator
{
    int conn = 4;
    
    public BinaryArray2D processBinary2d(BinaryArray2D mask)
    {
        // Ensure input array is run-length encoded
        this.fireStatusChanged(this, "convert mask image");
        RunLengthBinaryArray2D mask2 = RunLengthBinaryArray2D.convert(mask);
        mask2 = mask2.complement();
        
        // create marker
        this.fireStatusChanged(this, "create marker");
        RunLengthBinaryArray2D marker = createMarkerImage(mask.size());
        
        this.fireStatusChanged(this, "reconstruct background");
        BinaryArray2D res = new RunLengthBinaryReconstruction2D().processBinary2d(marker, mask2);
        
        this.fireStatusChanged(this, "complement");
        return res.complement();
    }

    private RunLengthBinaryArray2D createMarkerImage(int[] dims)
    {
        // retrieve size
        int sizeX = dims[0];
        int sizeY = dims[1];
        
        // create marker
        HashMap<Integer, BinaryRow> rows = new HashMap<>(sizeY);
        rows.put(0, new BinaryRow(new Run(0, sizeX - 1)));
        for (int y = 1; y < sizeY-1; y++)
        {
            rows.put(y, makeMarkerRow(sizeX));
        }
        rows.put(sizeY - 1, new BinaryRow(new Run(0, sizeX - 1)));
        
        return new RunLengthBinaryArray2D(sizeX, sizeY, rows);
    }
    
    private BinaryRow makeMarkerRow(int size)
    {
        BinaryRow row = new BinaryRow();
        row.set(0, true);
        row.set(size - 1, true);
        return row;
    }

    @Override
    public <T> Array<?> process(Array<T> array)
    {
        if (array instanceof BinaryArray)
        {
            return processBinary2d(BinaryArray2D.wrap((BinaryArray) array));
        }
        else
        {
            throw new RuntimeException(
                    "Requires an instance of BinaryArray2D");
        }
    }
    
    public boolean canProcess(Array<?> array)
    {
        return array instanceof BinaryArray && array.dimensionality() == 3;
    }
}
