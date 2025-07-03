/**
 * 
 */
package net.sci.image.analyze.region2d;

import java.util.Map;

import net.sci.array.Array;
import net.sci.array.numeric.IntArray;
import net.sci.array.numeric.IntArray2D;
import net.sci.image.Calibration;
import net.sci.image.Image;
import net.sci.image.analyze.RegionAnalyzer;
import net.sci.image.label.LabelImages;
import net.sci.table.Table;

/**
 * Base implementation of RegionAnalyzer interface for planar binary/label
 * images.
 *
 * @param T
 *            the type of the data computed for each region. May be a class
 *            instance, or a single Numeric type.
 * @author dlegland
 *           
 */
public abstract class RegionAnalyzer2D<T> extends net.sci.algo.AlgoStub
        implements net.sci.image.analyze.RegionAnalyzer<T>
{
    /**
     * Computes an instance of the generic type T for each region in input label
     * image.
     * 
     * @param array
     *            the input image containing region label of each pixel
     * @param labels
     *            the array of labels within the image
     * @param calib
     *            the spatial calibration of the image
     * @return an array of the type used to represent the analysis result of
     *         each region
     */
    public abstract T[] analyzeRegions(IntArray2D<?> array, int[] labels, Calibration calib);
    
    /**
     * Identifies labels within image and computes an instance of the generic
     * type T for each region in input label image.
     * 
     * @param array
     *            an integer array containing the region labels
     * @param calib
     *            the spatial calibration of the image
     * @return a map between the region label and the result of analysis for
     *         each region
     */
    public Map<Integer, T> analyzeRegions(IntArray2D<?> array, Calibration calib)
    {
        // extract region labels
        fireStatusChanged(this, "Find Labels");
        int[] labels = LabelImages.findAllLabels(array);
        
        // compute analysis result for each label
        fireStatusChanged(this, "Analyze regions");
        T[] results = analyzeRegions(array, labels, calib);
        
        // encapsulate into map
        fireStatusChanged(this, "Convert to map");
        return RegionAnalyzer.createMap(labels, results);
    }
    
    /**
     * Default implementation of the analyzeRegions method, that calls the more
     * specialized {@link #analyzeRegions(IntArray2D<?>, int[], Calibration)}
     * method and transforms the result into a map.
     * 
     * @param labelImage
     *            the input image containing label of regions
     * @return the mapping between region label and result of analysis for each
     *         region
     */
    public Map<Integer, T> analyzeRegions(Image labelImage)
    {
        // get the inner array and cast to appropriate sub-type
        Array<?> array = labelImage.getData();
        if (!(array instanceof IntArray))
        {
            throw new IllegalArgumentException("Label images must contain integer-based arrays");
        }
        IntArray2D<?> array2d = IntArray2D.wrap((IntArray<?>) array);
        
        // extract measures
        int[] labels = LabelImages.findAllLabels(array2d);
        T[] results = analyzeRegions(array2d, labels, labelImage.getCalibration());
        
        // convert the arrays into a map of index-value pairs
        return RegionAnalyzer.createMap(labels, results);
    }
    
    /**
     * Default implementation of computeTable method, using the two other
     * methods {@link #analyzeRegions(Image)} and {@link #createTable(Map)}:
     * 
     * @param labelImage
     *            a label or binary image of region(s)
     * @return an instance of Table containing results presented in a tabular
     *         format.
     */
    public Table computeTable(Image labelImage)
    {
        Table table = createTable(analyzeRegions(labelImage));
        table.setName(appendSuffix(labelImage.getName(), "analysis"));
        return table;
    }
    
    private static final String appendSuffix(String baseName, String suffix)
    {
        if (baseName != null && !baseName.isBlank())
        {
            return baseName + "-" + suffix;
        }
        else
        {
            return suffix;
        }
    }
}
