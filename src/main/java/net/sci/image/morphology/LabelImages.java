/**
 * 
 */
package net.sci.image.morphology;

import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import net.sci.array.data.IntArray;
import net.sci.array.type.Int;

/**
 * A collection of static methods for processing label images.
 * 
 * @author dlegland
 *
 */
public class LabelImages
{
    /**
     * Private constructor to avoid instantiation.
     */
    private LabelImages()
    {
    }
    
    /**
     * Returns the set of unique labels existing in the given image, excluding 
     * the value zero (used for background).
     * 
     * @param image
     *            a label image
     * @return the list of unique labels present in image (without background)
     */
    public final static int[] findAllLabels(IntArray<?> image)
    {
        TreeSet<Integer> labels = new TreeSet<Integer> ();
        
        // for integer-based images, simply use integer result
        for (Int value : image)
        {
            labels.add(value.getInt());
        }
        
        // remove 0 if it exists
        if (labels.contains(0))
        {
            labels.remove(0);
        }
        
        // convert to array of integers
        int[] array = new int[labels.size()];
        Iterator<Integer> iterator = labels.iterator();
        for (int i = 0; i < labels.size(); i++)
        {
            array[i] = iterator.next();
        }
        
        return array;
    }
    
    /**
     * Create associative array to retrieve the index corresponding each label.
     * 
     * @param labels
     *            an array of labels
     * @return a HashMap instance with each label as key, and the index of the
     *         label in array as value.
     */
    public static final HashMap<Integer, Integer> mapLabelIndices(int[] labels)
    {
        int nLabels = labels.length;
        HashMap<Integer, Integer> labelIndices = new HashMap<Integer, Integer>();
        for (int i = 0; i < nLabels; i++) 
        {
            labelIndices.put(labels[i], i);
        }

        return labelIndices;
    }

}
