/**
 * 
 */
package net.sci.image.analyze;

import net.sci.array.scalar.IntArray2D;
import net.sci.geom.geom2d.Bounds2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.curve.Ellipse2D;
import net.sci.image.Calibration;
import net.sci.image.analyze.region2d.Centroid2D;
import net.sci.image.analyze.region2d.EquivalentEllipse2D;
import net.sci.image.analyze.region2d.RegionBounds2D;

/**
 * A collections of static methods for the analysis of 2D regions.
 * 
 * @see RegionAnalysis3D
 * 
 * @author dlegland
 *
 */
public class RegionAnalysis2D
{
    /**
     * Computes the centroid of each label in the input image and returns the
     * result as an array of coordinates for each label.
     * 
     * @see net.sci.image.analyze.region2d.Centroid2D
     * 
     * @param labelImage
     *            the input image containing label of particles
     * @param labels
     *            the array of unique labels in image the number of directions
     *            to process, either 2 or 4
     * @param calib
     *            the (spatial) calibration of the image. Can be null, in that
     *            case default calibration will be used.
     * @return an array of Point2D corresponding to the centroid of each label
     *         in pixel coordinates
     */
    public final static Point2D[] centroids(IntArray2D<?> labelImage, int[] labels, Calibration calib)
    {
        return new Centroid2D().analyzeRegions(labelImage, labels, calib);
    }
    
    public final static Bounds2D[] boundingBoxes(IntArray2D<?> image, int[] labels, Calibration calib)
    {
        return new RegionBounds2D().analyzeRegions(image, labels, calib);
    }
    
    /**
     * Computes equivalent ellipse of each region in input label image.
     * 
     * @see net.sci.image.analyze.region2d.EquivalentEllipse2D
     * 
     * @param labelMap
     *            the input image containing label of particles
     * @param labels
     *            the list of labels to process
     * @param calib
     *            the (spatial) calibration of the image. Can be null, in that
     *            case default calibration will be used.
     * @return the equivalent ellipse corresponding to each label, in pixel coordinates
     */
    public final static Ellipse2D[] equivalentEllipses(IntArray2D<?> labelMap, int[] labels, Calibration calib)
    {
        return new EquivalentEllipse2D().analyzeRegions(labelMap, labels, calib);
    }
    
    /**
     * Private constructor to prevent instantiation.
     */
    private RegionAnalysis2D()
    {
    }
}
