/**
 * 
 */
package net.sci.image.analyze;

import java.util.HashMap;
import java.util.Map;

import net.sci.image.Image;
import net.sci.table.Table;

/**
 * Common interface for classes devoted to the extraction of information from
 * each region of label or binary image.
 *
 * @param T
 *            the type of the data computed for each region. May be a class
 *            instance, or a single Numeric type.
 *            
 * @author dlegland
 *
 */
public interface RegionAnalyzer<T>
{
    /**
     * Converts a pair of array, containing region labels and region data, into
     * a map that index data with region label.
     * 
     * @param <T>
     *            the type of data computed for each region
     * @param labels
     *            the array of region labels
     * @param data
     *            the array of region data (same size as <code>labels</code>
     *            array)
     * @return a map that index each region data with the region label
     */
    public static <T> Map<Integer, T> createMap(int[] labels,  T[] data)
    {
        // allocate result
        int nLabels = labels.length;
        Map<Integer, T> map = new HashMap<Integer, T>(nLabels);
        
        // populate data
        for (int i = 0; i < labels.length; i++)
        {
            map.put(labels[i], data[i]);
        } 
        return map;
    }
    
	/**
	 * Generic method to compute the result of an analysis on each region of a
	 * label or binary image.
	 * 
	 * @param image
	 *            a label or binary image of region(s)
	 * @return the mapping between each region label within the image and the
	 *         result of the analysis for the regions
	 */
	public Map<Integer, T> analyzeRegions(Image image);
	
	/**
     * <p>
     * Returns the result of the analysis in the form of a Table, to facilitate
     * concatenation of results obtained from several instances of
     * RegionAnalyzer.
     * </p>
     * 
     * <p>
     * This method can be quickly implemented by using the two other methods
     * {@link #analyzeRegions(Image)} and {@link #createTable(Map)}:
     * 
     * <pre>
     * {@code
     *  public Table computeTable(Image labelImage)
     *  {
     *      return createTable(analyzeRegions(labelImages));
     *  }
     * }
     * </pre>
     * 
     * 
     * @param image
     *            a label or binary image of region(s)
     * @return an instance of Table containing results presented in a tabular
     *         format.
     */
	public Table computeTable(Image image);

	/**
     * Utility method that converts the detailed results of the
     * {@link #analyzeRegions(Image)} method into an instance of Table to
     * facilitate display within a GUI.
     * 
     * @param results
     *            the mapping between each region label and the result of the
     *            analysis
     * @return an instance of Table containing results presented in a tabular
     *         format.
     */
	public Table createTable(Map<Integer, T> results);
}
