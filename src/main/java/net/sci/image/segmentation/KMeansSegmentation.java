/**
 * 
 */
package net.sci.image.segmentation;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.numeric.IntArray;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.UInt8Array;
import net.sci.array.numeric.VectorArray;
import net.sci.image.ImageArrayOperator;
import net.sci.table.Table;
import net.sci.table.cluster.KMeans;

/**
 * Segment an image using k-means on image's values. 
 * 
 * 
 * 
 * @author dlegland
 */
public class KMeansSegmentation extends AlgoStub implements ImageArrayOperator
{
    int nClasses;
    
    public KMeansSegmentation(int nClasses)
    {
        this.nClasses = nClasses;
    }

    @Override
    public <T> IntArray<?> process(Array<T> array)
    {
        // check table can be created
        long nElems = array.elementCount();
        if (nElems > Integer.MAX_VALUE)
        {
            throw new RuntimeException("Array has too many elements to be transformed as Table");
        }
        int nRows = (int) nElems;
        
        Table table;
        if (array instanceof ScalarArray)
        {
            // type cast
            ScalarArray<?> array2 = (ScalarArray<?>) array;
            
            // create table
            table = Table.create(nRows, 1);

            // fill with image values
            int i = 0;
            for (int[] pos : array2.positions())
            {
                table.setValue(i++, 0, array2.getValue(pos));
            }
        }
        else if (array instanceof VectorArray)
        {
            // type cast
            VectorArray<?,?> array2 = (VectorArray<?,?>) array;
            int nCols = array2.channelCount();
            
            // create table
            table = Table.create(nRows, nCols);

            // fill with image values
            int i = 0;
            for (int[] pos : array2.positions())
            {
                int j = 0;
                for (double v : array2.getValues(pos))
                {
                    table.setValue(i, j++, v);
                }
                i++;
            }
        }
        else
        {
            throw new RuntimeException("Requires input array to be either scalar or vector");
        }
        
        // Compute k-means on the data table
        KMeans algo = new KMeans(nClasses);
        Table classes = algo.process(table);
        
        // create array of labels
        UInt8Array labelMap = UInt8Array.create(array.size());
        int i = 0;
        for (int[] pos : array.positions())
        {
            labelMap.setValue(pos, classes.getValue(i++, 0));
        }
        
        return labelMap;
    }

	
}
