/**
 * 
 */
package net.sci.image.label.filters;

import java.util.HashSet;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.numeric.Int;
import net.sci.array.numeric.IntArray;
import net.sci.image.ImageArrayOperator;
import net.sci.image.label.LabelImages;

/**
 * Applies size opening on a label map: creates a new label map that contains
 * only regions with at least the specified number of elements.
 */
public class LabelMapSizeOpening extends AlgoStub implements ImageArrayOperator
{
    int nPixels;
    
    public LabelMapSizeOpening(int minElementCount)
    {
        this.nPixels = minElementCount;
    }
    
    public <I extends Int<I>> Array<I> processInt(Array<I> array)
    {
        IntArray<I> array1 = IntArray.wrap(array);
        int[] labels = LabelImages.findAllLabels(array1);
        int[] counts = LabelImages.elementCounts(array1, labels);
        
        // identify the labels with sufficient element count
        HashSet<Integer> labelsToKeep = new HashSet<Integer>(labels.length);
        for (int i = 0; i < labels.length; i++) 
        {
            if (counts[i] >= nPixels) 
            {
                labelsToKeep.add(labels[i]);
            }
        }
        
        return LabelImages.keepLabels(array1, labelsToKeep);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <T> Array<?> process(Array<T> array)
    {
        if (Int.class.isAssignableFrom(array.elementClass()))
        {
            return processInt((Array<? extends Int>) array);
        }
        
        throw new IllegalArgumentException("Requires an array of Int as input");
    }

}
