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
import net.sci.image.Image;
import net.sci.image.ImageArrayOperator;
import net.sci.image.ImageType;
import net.sci.table.Table;
import net.sci.table.cluster.KMeans;

/**
 * Segment an image by applying k-means algorithms on image's values.
 * 
 * Result values start at 1, making it possible to use specific processing for 0
 * value, associated to background, or un-classified data.
 * 
 * @author dlegland
 */
public class KMeansSegmentation extends AlgoStub implements ImageArrayOperator
{
    /**
     * The number of classes to generate.
     */
    int nClasses;
    
    /**
     * Default constructor for k-means image segmentation operator.
     * 
     * @param nClasses
     *            the number of classes to generate
     */
    public KMeansSegmentation(int nClasses)
    {
        this.nClasses = nClasses;
    }

    @Override
    public Image process(Image image)
    {
        Array<?> array = image.getData();
        Array<?> result = process(array);
        
        Image resImage = new Image(result, ImageType.LABEL, image);
        
        return resImage;
    }
    
    @Override
    public <T> IntArray<?> process(Array<T> array)
    {
        this.fireStatusChanged(this, "create data table");
        Table table = createTable(array);
        
        // Compute k-means on the data table
        this.fireStatusChanged(this, "compute k-means");
        KMeans algo = new KMeans(nClasses);
        Table classes = algo.process(table);
        
        // create array of labels
        this.fireStatusChanged(this, "create result array");
        UInt8Array labelMap = UInt8Array.create(array.size());
        
        // populate label map with class indices, 
        // keeping value zero for background
        int i = 0;
        for (int[] pos : array.positions())
        {
            labelMap.setValue(pos, classes.getValue(i++, 0) + 1);
        }
        
        return labelMap;
    }

    private Table createTable(Array<?> array)
    {
        // check table can be created
        long nElems = array.elementCount();
        if (nElems > Integer.MAX_VALUE)
        {
            throw new RuntimeException("Array contains too many elements to be transformed into a Table");
        }
        int nRows = (int) nElems;
        
        switch (array)
        {
            case ScalarArray<?> scalarArray -> 
            {
                // create table
                Table table = Table.create(nRows, 1);

                // fill with image values
                int i = 0;
                for (int[] pos : scalarArray.positions())
                {
                    table.setValue(i++, 0, scalarArray.getValue(pos));
                }
                return table;
            }
            case VectorArray<?,?> vectorArray -> 
            {
                // create table
                int nCols = vectorArray.channelCount();
                Table table = Table.create(nRows, nCols);

                // fill with image values
                int i = 0;
                for (int[] pos : vectorArray.positions())
                {
                    int j = 0;
                    for (double v : vectorArray.getValues(pos))
                    {
                        table.setValue(i, j++, v);
                    }
                    i++;
                }
                return table;
            }

            default -> throw new RuntimeException("Requires input array to contain either scalar or vector elements");
        }
    }
}
